package me.t65.rssfeedsourcetask.dedupe;

public interface DetectDuplicateService {

    Boolean isDuplicateArticle(String Link);
}
