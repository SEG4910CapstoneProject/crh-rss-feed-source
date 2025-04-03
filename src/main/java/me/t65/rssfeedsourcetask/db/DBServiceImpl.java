package me.t65.rssfeedsourcetask.db;
import me.t65.rssfeedsourcetask.config.Config;
import me.t65.rssfeedsourcetask.db.mongo.ArticleContentEntity;
import me.t65.rssfeedsourcetask.db.mongo.RelatedLinkContentEntity;
import me.t65.rssfeedsourcetask.db.mongo.repository.ArticleContentRepository;
import me.t65.rssfeedsourcetask.db.mongo.repository.RelatedLinkContentRepository;
import me.t65.rssfeedsourcetask.db.postgres.composite_keys.ArticleLabelId;
import me.t65.rssfeedsourcetask.db.postgres.composite_keys.ArticleRelatedLinkId;
import me.t65.rssfeedsourcetask.db.postgres.dtos.ArticleDataMain;
import me.t65.rssfeedsourcetask.db.postgres.entities.ArticleLabelEntity;
import me.t65.rssfeedsourcetask.db.postgres.entities.ArticleRelatedLinkEntity;
import me.t65.rssfeedsourcetask.db.postgres.entities.ArticlesEntity;
import me.t65.rssfeedsourcetask.db.postgres.entities.LabelsEntity;
import me.t65.rssfeedsourcetask.db.postgres.entities.OpenCtiSourcesEntity;
import me.t65.rssfeedsourcetask.db.postgres.entities.RelatedLinkEntity;
import me.t65.rssfeedsourcetask.db.postgres.entities.SourcesEntity;
import me.t65.rssfeedsourcetask.db.postgres.entities.VersionsEntity;
import me.t65.rssfeedsourcetask.db.postgres.repository.ArticleLabelRepository;
import me.t65.rssfeedsourcetask.db.postgres.repository.ArticleRelatedLinkRepository;
import me.t65.rssfeedsourcetask.db.postgres.repository.ArticlesRepository;
import me.t65.rssfeedsourcetask.db.postgres.repository.LabelsRepository;
import me.t65.rssfeedsourcetask.db.postgres.repository.OpenCtiRepository;
import me.t65.rssfeedsourcetask.db.postgres.repository.RelatedLinkRepository;
import me.t65.rssfeedsourcetask.db.postgres.repository.SourcesRepository;
import me.t65.rssfeedsourcetask.db.postgres.repository.VersionsRepository;
import me.t65.rssfeedsourcetask.dedupe.DetectDuplicateServiceImpl;
import me.t65.rssfeedsourcetask.dedupe.NormalizeLinks;
import me.t65.rssfeedsourcetask.dto.Article;
import me.t65.rssfeedsourcetask.dto.RelatedLink;
import me.t65.rssfeedsourcetask.utils.DateUtilsService;
import me.t65.rssfeedsourcetask.utils.DateUtilsServiceImpl;
import reactor.core.publisher.Mono;
import reactor.util.retry.RetryBackoffSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class DBServiceImpl implements DBService {

    private final RetryBackoffSpec feedRetrySpec;

    private final DetectDuplicateServiceImpl detectDuplicateServiceImpl;

    private final DateUtilsServiceImpl dateUtilsServiceImpl;

    private final Config config;

    //private final DBEmitter DBEmitter;
    private static final Logger LOGGER = LoggerFactory.getLogger(DBServiceImpl.class);

    private final ArticlesRepository articlesRepository;
    private final ArticleContentRepository articleContentRepository;

    private final SourcesRepository sourcesRepository;

    private final DateUtilsService dateUtilsService;
    private final VersionsRepository versionsRepository;
    private final OpenCtiRepository openCtiRepository;
    private final LabelsRepository labelsRepository;
    private final RelatedLinkRepository relatedLinkRepository;
    private final ArticleRelatedLinkRepository articleRelatedLinkRepository;
    private final ArticleLabelRepository articleLabelRepository;
    private final RelatedLinkContentRepository relatedLinkContentRepository;

    public DBServiceImpl(
            ArticlesRepository articlesRepository,
            ArticleContentRepository articleContentRepository,
            SourcesRepository sourcesRepository,
            DateUtilsService dateUtilsService,
            VersionsRepository versionsRepository,
            OpenCtiRepository openCtiRepository,
            LabelsRepository labelsRepository, 
            RelatedLinkRepository relatedLinkRepository,
            ArticleRelatedLinkRepository articleRelatedLinkRepository,
            ArticleLabelRepository articleLabelRepository,
            RelatedLinkContentRepository relatedLinkContentRepository,
            //DBEmitter DBEmitter,
             Config config, DateUtilsServiceImpl dateUtilsServiceImpl, DetectDuplicateServiceImpl detectDuplicateServiceImpl, RetryBackoffSpec feedRetrySpec) {
        this.articlesRepository = articlesRepository;
        this.articleContentRepository = articleContentRepository;
        this.sourcesRepository = sourcesRepository;
        this.dateUtilsService = dateUtilsService;
        this.versionsRepository = versionsRepository;
        this.openCtiRepository = openCtiRepository;
        this.labelsRepository = labelsRepository;
        this.relatedLinkRepository = relatedLinkRepository;
        this.articleRelatedLinkRepository = articleRelatedLinkRepository;
        this.articleLabelRepository = articleLabelRepository;
        this.relatedLinkContentRepository = relatedLinkContentRepository;
        //this.DBEmitter = DBEmitter;
        this.config = config;
        this.dateUtilsServiceImpl = dateUtilsServiceImpl;
        this.detectDuplicateServiceImpl = detectDuplicateServiceImpl;
        this.feedRetrySpec = feedRetrySpec;
    }

    @Override
    public boolean save(ArticleDataMain articleData) {
        try {
            saveRaw(articleData);
            return true;
        } catch (Exception e) {
            LOGGER.error("Failed to write to db", e);
            throw e;
        }
    }

    private void saveRaw(ArticleDataMain articleData) {
        // 1- save the article entity to the articles table
        articlesRepository.save(articleData.getArticle());

        // 2- save the related link
        
        for (RelatedLinkEntity relatedLinkEntity : articleData.getRelated_links()) {
            relatedLinkRepository.save(relatedLinkEntity);
        }


        // 3- save the article_related_link mapping
        for (ArticleRelatedLinkEntity article_related_link : articleData.getArticle_rel_links_mappings()) {
            articleRelatedLinkRepository.save(article_related_link);
        }

        // 4- save the labels

        for (LabelsEntity label : articleData.getLabels()) {
            labelsRepository.save(label);
        }

        // TODO ANA

        // 5- save the article_label_mappings

        // try {
        //     for (ArticleLabelEntity article_label_mapping : articleData.getArticle_label_mappings()) {
        //         articleLabelRepository.save(article_label_mapping);
        //         LOGGER.info("the article label is: {},{}",article_label_mapping.getId().getArticleId(),article_label_mapping.getId().getLabelId());
        //     }
        // } catch(Exception e) {
        //     LOGGER.info("HERE ERROR");
        //     System.exit(1);
        // }

 
        // 6- save the article content entity

        articleContentRepository.save(articleData.getArticle_content());
        // // 7- save the RelatedLinkContent
        // relatedLinkContentRepository.save(articleData.getRel_links_content());
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

    public Mono<ArticleDataMain> transformIntoDbObjects(Article arc) {
        // first, I need to get the source of this article and go 
        // to the open cti sources to actually get the source id
        OpenCtiSourcesEntity src_entity = openCtiRepository.findIdBySourceName(arc.getSource());

        int src_id = src_entity.getSourceId();
        //LOGGER.info("the src is is: {}",src_id);
        Date date_ingested = dateUtilsService.getCurrentDate();
        // TODO, in the database, the date_published is a date, how will this translate to a date (I have a timestamp with zone type)
        long hashLink = NormalizeLinks.normalizeAndHashLink(arc.getLinkPrimary());
        Instant inst_published_date = Instant.parse(arc.getDatePublished());
        ArticlesEntity articleEntity = new ArticlesEntity(arc.getId(),src_id,date_ingested,Date.from(inst_published_date),false,false,hashLink); // 1- article
        OpenCtiSourcesEntity src_related_link_entity;
        int src_related_link_id;
        RelatedLinkEntity arcRelLinkEntity = new RelatedLinkEntity();
        ArticleRelatedLinkEntity articleRelatedLinkEntity = new ArticleRelatedLinkEntity();
        List<ArticleRelatedLinkEntity> article_rel_links_mappings = new ArrayList<>(); // 2- Article-RelatedLink
        List<RelatedLinkEntity> rel_links = new ArrayList<>(); // 3- RelatedLinks
        List<ArticleLabelEntity> article_label_mappings = new ArrayList<>();// 4- Article-Label-Entity
        List<LabelsEntity> labels = new ArrayList<>();// 5- labels

        Date date_of_publication = dateUtilsService.transformStringToDate(arc.getDatePublished());

        ArticleContentEntity articleContentEntity = new ArticleContentEntity(arc.getId(),arc.getLinkPrimary(),arc.getName(),date_of_publication,arc.getDescription()); // 6- articleContent
        RelatedLinkContentEntity related_link_content = new RelatedLinkContentEntity();// 7- RelatedLinkEntityContent
        related_link_content.setId(arc.getId());
        List<String> relLinksContent = new ArrayList<>();// this is to set the related_link_content array

        ArticleRelatedLinkId article_related_link_id;
        

        for (RelatedLink relLink : arc.getRelLinks()) {
            // construct the object RelatedLinkEntity first
            src_related_link_entity = openCtiRepository.findIdBySourceName(relLink.getSource());
            src_related_link_id = src_related_link_entity.getSourceId();

            arcRelLinkEntity.setRelatedHashLink(NormalizeLinks.normalizeAndHashLink(relLink.getRelatedLink()));
            arcRelLinkEntity.setSourceId(src_related_link_id);// here we are done constructing the RelatedLinkEntity
            relLinksContent.add(relLink.getRelatedLink());

            article_related_link_id = new ArticleRelatedLinkId(arc.getId(),src_related_link_id);
            articleRelatedLinkEntity.setId(article_related_link_id);// here we are done constructing the articleRelatedLinkEntity
            // articleRelatedLinkEntity.setArticleId();
            // articleRelatedLinkEntity.setRelLinkId();

            // Now you just have to make a big object that tracks all the objects created so far, and add the arcRelLinkEntity and articleRelatedLinkEntity to a list in there.
            article_rel_links_mappings.add(articleRelatedLinkEntity);
            rel_links.add(arcRelLinkEntity);    
        }

        int label_id;
        LabelsEntity label_entity;
        ArticleLabelEntity article_label_entity;

        ArticleLabelId article_label_id;

        //TODO ANA
        // for (String label : arc.getLabels()) {
        //     LOGGER.info("the label is: {}",label);
        //     label_entity = labelsRepository.findByLabelName(label);
        //     LOGGER.info("the label entity is: {}",label_entity);
        //     if ( label_entity != null ) {
        //         LOGGER.info("the label entity is 1: {}",label_entity.getLabelName());

        //         // meaning the label is alr present in the table
        //         label_id = label_entity.getLabelId();
        //         article_label_id = new ArticleLabelId(arc.getId(),label_id);
        //         article_label_entity = new ArticleLabelEntity(article_label_id);// we just need to construct the ArticleLabelEntity in this case
        //         article_label_mappings.add(article_label_entity);
        //     } else {

        //         // meaning a new label was encountered
        //         // first, create the labelEntity
        //         label_entity = new LabelsEntity();
        //         label_entity.setLabelName(label);
        //         LOGGER.info("the label entity is 2: {}",label_entity.getLabelName());

        //         labels.add(label_entity);
        //         // then go construct the Article_Label_entity
        //         article_label_id = new ArticleLabelId(arc.getId(), label_entity.getLabelId());

        //         article_label_entity = new ArticleLabelEntity(article_label_id);
        //         article_label_mappings.add(article_label_entity);
        //     }
            
        // }

        related_link_content.setLinks(relLinksContent);

        ArticleDataMain mainEntity = new  ArticleDataMain(articleEntity, article_rel_links_mappings, rel_links, article_label_mappings, labels, articleContentEntity,related_link_content);


        //articlesRepository.save(articleEntity);

        return Mono.just(mainEntity);


        //ArticlesEntity articlesEntity = 


    }
}
