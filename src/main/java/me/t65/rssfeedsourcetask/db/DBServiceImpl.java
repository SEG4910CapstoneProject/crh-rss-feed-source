package me.t65.rssfeedsourcetask.db;

import me.t65.rssfeedsourcetask.db.mongo.repository.ArticleContentRepository;
import me.t65.rssfeedsourcetask.db.postgres.entities.ArticlesEntity;
import me.t65.rssfeedsourcetask.db.postgres.entities.OpenCtiSourcesEntity;
import me.t65.rssfeedsourcetask.db.postgres.entities.SourcesEntity;
import me.t65.rssfeedsourcetask.db.postgres.entities.VersionsEntity;
import me.t65.rssfeedsourcetask.db.postgres.repository.ArticlesRepository;
import me.t65.rssfeedsourcetask.db.postgres.repository.OpenCtiRepository;
import me.t65.rssfeedsourcetask.db.postgres.repository.SourcesRepository;
import me.t65.rssfeedsourcetask.db.postgres.repository.VersionsRepository;
import me.t65.rssfeedsourcetask.dedupe.NormalizeLinks;
import me.t65.rssfeedsourcetask.dto.Article;
import me.t65.rssfeedsourcetask.feed.ArticleData;
import me.t65.rssfeedsourcetask.utils.DateUtilsService;
import reactor.core.publisher.Mono;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Optional;

@Service
public class DBServiceImpl implements DBService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBServiceImpl.class);

    private final ArticlesRepository articlesRepository;
    private final ArticleContentRepository articleContentRepository;

    private final SourcesRepository sourcesRepository;

    private final DateUtilsService dateUtilsService;
    private final VersionsRepository versionsRepository;
    private final OpenCtiRepository openCtiRepository;

    public DBServiceImpl(
            ArticlesRepository articlesRepository,
            ArticleContentRepository articleContentRepository,
            SourcesRepository sourcesRepository,
            DateUtilsService dateUtilsService,
            VersionsRepository versionsRepository,
            OpenCtiRepository openCtiRepository ) {
        this.articlesRepository = articlesRepository;
        this.articleContentRepository = articleContentRepository;
        this.sourcesRepository = sourcesRepository;
        this.dateUtilsService = dateUtilsService;
        this.versionsRepository = versionsRepository;
        this.openCtiRepository = openCtiRepository;
    }

    @Override
    public boolean save(ArticleData articleData) {
        try {
            saveRaw(articleData);
            return true;
        } catch (Exception e) {
            LOGGER.error("Failed to write to db", e);
            throw e;
        }
    }

    private void saveRaw(ArticleData articleData) {
        articlesRepository.save(articleData.getArticlesEntity());
        articleContentRepository.save(articleData.getArticleContentEntity());
    }

    /**
     * Saves the last update time to the database for the given source ID.
     *
     * @param sourceId the ID of the source
     */
    @Override
    public void saveLastUpdateToDatabase(int sourceId) {
        Optional<SourcesEntity> sourceEntityOptional = sourcesRepository.findById(sourceId);// this is always gonna be present, why make it optional then.
        if (sourceEntityOptional.isPresent()) {
            SourcesEntity sourcesEntity = sourceEntityOptional.get();

            Date currentDate = dateUtilsService.getCurrentDate();
            sourcesEntity.setLastUpdate(currentDate);
            sourcesRepository.save(sourcesEntity);
        }
    }




    public String getLastVersionUpdateTime() {
        VersionsEntity res = versionsRepository.findFirstByOrderByVersionIdDesc();
        //LOGGER.info("the res after query is: {}",res);
        if (res != null) {
            // meaning we did build the system before
            return res.getTimeUpdate().toString();
        } else {
            // meaning the system was never built before, in this case we need to pull all the data from open cti
            return "";
        }
    }


    public boolean saveVersion(Instant utcDate) {
        try {
            VersionsEntity newVersion = new VersionsEntity();
            newVersion.setTimeUpdate(utcDate);
            versionsRepository.save(newVersion);
            return true;
        } catch (Exception e) {
            LOGGER.error("Failed to write to db to save current version", e);
            throw e;
        }
    }

    public Mono<ArticleData> transformIntoDbObjects(Article arc) {
        // first, I need to get the source of this article and go 
        // to the open cti sources to actually get the source id
        OpenCtiSourcesEntity src_entity = openCtiRepository.findIdBySourceName(arc.getSource());

        int src_id = src_entity.getSourceId();
        //LOGGER.info("the src is is: {}",src_id);
        Date date_ingested = dateUtilsService.getCurrentDate();
        // TODO, in the database, the date_published is a date, how will this translate to a date (I have a timestamp with zone type)
        long hashLink = NormalizeLinks.normalizeAndHashLink(arc.getLinkPrimary());
        Instant inst_published_date = Instant.parse(arc.getDatePublished());
        ArticlesEntity articleEntity = new ArticlesEntity(arc.getId(),src_id,date_ingested,Date.from(inst_published_date),false,false,hashLink);
        articlesRepository.save(articleEntity);

        return Mono.empty();


        //ArticlesEntity articlesEntity = 


    }
}
