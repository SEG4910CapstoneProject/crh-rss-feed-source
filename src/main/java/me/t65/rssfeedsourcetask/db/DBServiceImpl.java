package me.t65.rssfeedsourcetask.db;

import me.t65.rssfeedsourcetask.db.mongo.repository.ArticleContentRepository;
import me.t65.rssfeedsourcetask.db.postgres.entities.SourcesEntity;
import me.t65.rssfeedsourcetask.db.postgres.entities.VersionsEntity;
import me.t65.rssfeedsourcetask.db.postgres.repository.ArticlesRepository;
import me.t65.rssfeedsourcetask.db.postgres.repository.SourcesRepository;
import me.t65.rssfeedsourcetask.db.postgres.repository.VersionsRepository;
import me.t65.rssfeedsourcetask.feed.ArticleData;
import me.t65.rssfeedsourcetask.utils.DateUtilsService;

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

    public DBServiceImpl(
            ArticlesRepository articlesRepository,
            ArticleContentRepository articleContentRepository,
            SourcesRepository sourcesRepository,
            DateUtilsService dateUtilsService,
            VersionsRepository versionsRepository) {
        this.articlesRepository = articlesRepository;
        this.articleContentRepository = articleContentRepository;
        this.sourcesRepository = sourcesRepository;
        this.dateUtilsService = dateUtilsService;
        this.versionsRepository = versionsRepository;
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
}
