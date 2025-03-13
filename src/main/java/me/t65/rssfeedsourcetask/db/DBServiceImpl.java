package me.t65.rssfeedsourcetask.db;

import me.t65.rssfeedsourcetask.db.mongo.repository.ArticleContentRepository;
import me.t65.rssfeedsourcetask.db.postgres.entities.ArticleRelatedLinkEntity;
import me.t65.rssfeedsourcetask.db.postgres.entities.ArticlesEntity;
import me.t65.rssfeedsourcetask.db.postgres.entities.LabelsEntity;
import me.t65.rssfeedsourcetask.db.postgres.entities.OpenCtiSourcesEntity;
import me.t65.rssfeedsourcetask.db.postgres.entities.RelatedLinkEntity;
import me.t65.rssfeedsourcetask.db.postgres.entities.SourcesEntity;
import me.t65.rssfeedsourcetask.db.postgres.entities.VersionsEntity;
import me.t65.rssfeedsourcetask.db.postgres.repository.ArticlesRepository;
import me.t65.rssfeedsourcetask.db.postgres.repository.LabelsRepository;
import me.t65.rssfeedsourcetask.db.postgres.repository.OpenCtiRepository;
import me.t65.rssfeedsourcetask.db.postgres.repository.SourcesRepository;
import me.t65.rssfeedsourcetask.db.postgres.repository.VersionsRepository;
import me.t65.rssfeedsourcetask.dedupe.NormalizeLinks;
import me.t65.rssfeedsourcetask.dto.Article;
import me.t65.rssfeedsourcetask.dto.RelatedLink;
import me.t65.rssfeedsourcetask.emitter.DBEmitter;
import me.t65.rssfeedsourcetask.feed.ArticleData;
import me.t65.rssfeedsourcetask.utils.DateUtilsService;
import reactor.core.publisher.Mono;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import aj.org.objectweb.asm.Label;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class DBServiceImpl implements DBService {

    private final DBEmitter DBEmitter;
    private static final Logger LOGGER = LoggerFactory.getLogger(DBServiceImpl.class);

    private final ArticlesRepository articlesRepository;
    private final ArticleContentRepository articleContentRepository;

    private final SourcesRepository sourcesRepository;

    private final DateUtilsService dateUtilsService;
    private final VersionsRepository versionsRepository;
    private final OpenCtiRepository openCtiRepository;
    private final LabelsRepository labelsRepository;

    public DBServiceImpl(
            ArticlesRepository articlesRepository,
            ArticleContentRepository articleContentRepository,
            SourcesRepository sourcesRepository,
            DateUtilsService dateUtilsService,
            VersionsRepository versionsRepository,
            OpenCtiRepository openCtiRepository,
            LabelsRepository labelsRepository, DBEmitter DBEmitter) {
        this.articlesRepository = articlesRepository;
        this.articleContentRepository = articleContentRepository;
        this.sourcesRepository = sourcesRepository;
        this.dateUtilsService = dateUtilsService;
        this.versionsRepository = versionsRepository;
        this.openCtiRepository = openCtiRepository;
        this.labelsRepository = labelsRepository;
        this.DBEmitter = DBEmitter;
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
        OpenCtiSourcesEntity src_related_link_entity;
        int src_related_link_id;
        RelatedLinkEntity arcRelLinkEntity = new RelatedLinkEntity();
        ArticleRelatedLinkEntity articleRelatedLinkEntity = new ArticleRelatedLinkEntity();
        List<ArticleRelatedLinkEntity> article_rel_links_mappings = new ArrayList<>();
        List<RelatedLinkEntity> rel_links = new ArrayList<>();

        for (RelatedLink relLink : arc.getRelLinks()) {
            // construct the object RelatedLinkEntity first
            src_related_link_entity = openCtiRepository.findIdBySourceName(relLink.getSource());
            src_related_link_id = src_related_link_entity.getSourceId();

            arcRelLinkEntity.setRelatedHashLink(NormalizeLinks.normalizeAndHashLink(relLink.getRelatedLink()));
            arcRelLinkEntity.setSourceId(src_related_link_id);// here we are done constructing the RelatedLinkEntity

            articleRelatedLinkEntity.setArticleId(arc.getId());
            articleRelatedLinkEntity.setRelLinkId(src_related_link_id);// here we are done constructing the articleRelatedLinkEntity

            // Now you just have to make a big object that tracks all the objects created so far, and add the arcRelLinkEntity and articleRelatedLinkEntity to a list in there.
            article_rel_links_mappings.add(articleRelatedLinkEntity);
            rel_links.add(arcRelLinkEntity);    
        }

        int label_id;

        for (String label : arc.getLabels()) {
            LabelsEntity label_entity = labelsRepository.findByLabelName(label);
            if ( label_entity != null ) {
                // meaning the label is alr present
                label_id = label_entity.getLabelId();
                
                

            }
            
        }


        //articlesRepository.save(articleEntity);

        return Mono.empty();


        //ArticlesEntity articlesEntity = 


    }
}
