package me.t65.rssfeedsourcetask.dedupe;

import me.t65.rssfeedsourcetask.db.mongo.ArticleContentEntity;
import me.t65.rssfeedsourcetask.db.mongo.repository.ArticleContentRepository;
import me.t65.rssfeedsourcetask.db.postgres.repository.ArticlesRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DetectDuplicateServiceImpl implements DetectDuplicateService {

    private final ArticlesRepository articlesRepository;
    private final ArticleContentRepository articleContentRepository;

    @Autowired
    public DetectDuplicateServiceImpl(
            ArticlesRepository articlesRepository,
            ArticleContentRepository articleContentRepository) {
        this.articlesRepository = articlesRepository;
        this.articleContentRepository = articleContentRepository;
    }

    @Override
    public Boolean isDuplicateArticle(String link) {
        // Normalize and hash the link
        long hashedLink = NormalizeLinks.normalizeAndHashLink(link);
        // Check if the hashed link exists in Postgres
        boolean existsInPostgres = articlesRepository.existsHashlink(hashedLink);
        if (existsInPostgres) {
            // Check Mongo for the link
            ArticleContentEntity entity = articleContentRepository.findByLink(link);
            return entity != null; // Return true if the link exists in Mongo
        }
        return false; // return false link is not a duplicate
    }
}
