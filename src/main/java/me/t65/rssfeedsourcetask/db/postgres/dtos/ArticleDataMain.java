package me.t65.rssfeedsourcetask.db.postgres.dtos;

import java.util.List;

import me.t65.rssfeedsourcetask.db.mongo.ArticleContentEntity;
import me.t65.rssfeedsourcetask.db.mongo.RelatedLinkEntityContent;
import me.t65.rssfeedsourcetask.db.postgres.entities.ArticleLabelEntity;
import me.t65.rssfeedsourcetask.db.postgres.entities.ArticleRelatedLinkEntity;
import me.t65.rssfeedsourcetask.db.postgres.entities.ArticlesEntity;
import me.t65.rssfeedsourcetask.db.postgres.entities.LabelsEntity;
import me.t65.rssfeedsourcetask.db.postgres.entities.RelatedLinkEntity;

public class ArticleDataMain {
    private ArticlesEntity article;
    private List<ArticleRelatedLinkEntity> article_rel_links_mappings;
    private List<RelatedLinkEntity> related_links;
    private List<ArticleLabelEntity> article_label_mappings;
    private List<LabelsEntity> labels;
    private ArticleContentEntity article_content;
    private RelatedLinkEntityContent rel_links_content;
    
    public ArticleDataMain(ArticlesEntity article,List<ArticleRelatedLinkEntity> article_rel_links_mappings,List<RelatedLinkEntity> related_links,
    List<ArticleLabelEntity> article_label_mappings,List<LabelsEntity> labels,ArticleContentEntity article_content,RelatedLinkEntityContent rel_links_content) {
        this.article = article;
        this.article_rel_links_mappings = article_rel_links_mappings;
        this.related_links = related_links;
        this.article_label_mappings = article_label_mappings;
        this.labels = labels;
        this.article_content = article_content;
        this.rel_links_content = rel_links_content;
    }

}
