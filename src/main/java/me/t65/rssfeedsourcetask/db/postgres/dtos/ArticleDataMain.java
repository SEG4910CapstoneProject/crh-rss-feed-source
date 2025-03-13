package me.t65.rssfeedsourcetask.db.postgres.dtos;

import java.util.List;

import me.t65.rssfeedsourcetask.db.postgres.entities.ArticleRelatedLinkEntity;
import me.t65.rssfeedsourcetask.db.postgres.entities.ArticlesEntity;
import me.t65.rssfeedsourcetask.db.postgres.entities.RelatedLinkEntity;

public class ArticleDataMain {
    private ArticlesEntity article;
    private List<ArticleRelatedLinkEntity> article_rel_links_mappings;
    private List<RelatedLinkEntity> related_links;
    
    public ArticleDataMain(ArticlesEntity article,List<ArticleRelatedLinkEntity> article_rel_links_mappings,List<RelatedLinkEntity> related_links) {
        this.article = article;
        this.article_rel_links_mappings = article_rel_links_mappings;
        this.related_links = related_links;
    }

}
