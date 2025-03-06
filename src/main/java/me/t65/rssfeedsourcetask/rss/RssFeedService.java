package me.t65.rssfeedsourcetask.rss;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import me.t65.rssfeedsourcetask.config.Config;
import me.t65.rssfeedsourcetask.db.DBService;
import me.t65.rssfeedsourcetask.db.mongo.ArticleContentEntity;
import me.t65.rssfeedsourcetask.db.postgres.entities.ArticlesEntity;
import me.t65.rssfeedsourcetask.dedupe.DetectDuplicateService;
import me.t65.rssfeedsourcetask.dedupe.NormalizeLinks;
import me.t65.rssfeedsourcetask.dto.Article;
import me.t65.rssfeedsourcetask.dto.ArticlePrimary;
import me.t65.rssfeedsourcetask.dto.Edges;
import me.t65.rssfeedsourcetask.dto.NodeArticle;
import me.t65.rssfeedsourcetask.dto.RelatedLink;
import me.t65.rssfeedsourcetask.feed.*;
import me.t65.rssfeedsourcetask.utils.DateUtilsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;
import reactor.util.retry.RetryBackoffSpec;
import reactor.util.retry.RetrySpec;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/** Feed service that reads articles from an RSS source */
@Service
public class RssFeedService implements FeedService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RssFeedService.class);

    //private final RestTemplate restTemplate;
    private final WebClient webClient;
    private final Config config;
    private final RssIdGenerator rssIdGenerator;
    private final DateUtilsService dateUtilsService;

    private final DetectDuplicateService detectDuplicateService;
    private final DBService dbService;
    private final Scheduler scheduler;

    private final ObjectMapper objectMapper;


    @Autowired
    public RssFeedService(
            WebClient webClient,
            Config config,
            RssIdGenerator rssIdGenerator,
            DateUtilsService dateUtilsService,
            DBService dbService,
            DetectDuplicateService detectDuplicateService,
            Scheduler scheduler,
            ObjectMapper objectMapper) {
        //this.restTemplate = restTemplate;
        this.webClient = webClient;
        this.config = config;
        this.rssIdGenerator = rssIdGenerator;
        this.dateUtilsService = dateUtilsService;
        this.dbService = dbService;
        this.detectDuplicateService = detectDuplicateService;
        this.scheduler = scheduler;
        this.objectMapper = objectMapper;

    }

//     @Override
//     public Flux<ArticleData> getFeed(FeedUrlPair feedUrlPair) {
//         // Count of articles/data points for a given article
//         final AtomicInteger feedCount = new AtomicInteger();
//         final AtomicInteger failureCount = new AtomicInteger();

//         return Mono.just(feedUrlPair.url())
//                 .doOnNext(
//                         url -> LOGGER.info("Started feed {}: url: '{}'", feedUrlPair.source(), url))
//                 .map(URI::create) // Convert String URL to URI Object,   url -> URI.create(url)
//                 .map(url -> getFeed(url, feedUrlPair.lastUpdate())) // Generate Feed from URI
//                 .doOnError(
//                         e ->
//                                 LOGGER.error(
//                                         "Error reading feed.",
//                                         e)) // if error reading feed, log error
//                 .retryWhen(
//                         getRetrySpec(
//                                 feedUrlPair)) // if an error reading feed, retry according to spec
//                 .onErrorComplete() // if failed to read from feed, send complete signal to not
//                 // trigger error handling downstream
//                 .filter(
//                         feedTuple ->
//                                 feedTuple.getT3()) // If last build date is after last update time, if true emit that stream, if false dont emit anything
//                 .doOnNext(// if we need to repull stuff from the rss feed
//                         feedTuple ->
//                                 dbService.saveLastUpdateToDatabase(
//                                         feedUrlPair.sourceId())) // Save the new last update time
//                 .flatMapMany(this::getEntries) // Split Feed into individual articles  // we have not a flux of feed entries (aka articles)
//                 .filter(
//                         entry ->
//                                 !detectDuplicateService.isDuplicateArticle(
//                                         entry.getLink())) // Check if the article is a duplicate
//                 .flatMap(// just the feed entries that are new
//                         entry ->
//                                 transformEntry(
//                                         entry,
//                                         feedUrlPair.sourceId(),
//                                         failureCount)) // Transform articles into db objects
//                 .doOnError(
//                         throwable -> {
//                             LOGGER.error("Unable to complete reading from feeds", throwable);
//                         })
//                 .onErrorComplete() // If unstopped error, report it. and end stream
//                 .doOnNext(entry -> feedCount.incrementAndGet()) // add to feed count when successful
//                 .doOnComplete(
//                         () -> // When feed reading complete, log results
//                         LOGGER.info(
//                                         "Completed {}: '{}' feed with {} entries. Failed to read"
//                                                 + " entries: {}",
//                                         feedUrlPair.source(),
//                                         feedUrlPair.url(),
//                                         feedCount.get(),
//                                         failureCount.get()))
//                 .subscribeOn(scheduler);
//     }

    /**
     * Reads RSS Feed for a given URL. Returns both the received feed and Reader to be closed. Any
     * errors will generate Error signal
     *
     * @param url the url to read as a URI Object
     * @return Tuple containing Received feed and XML Reader to be closed
     */
//     private Tuple3<SyndFeed, XmlReader, Boolean> getFeed(URI url, Date lastUpdate) {
//         return restTemplate.execute(
//                 url, HttpMethod.GET, null, response -> processFeed(response, url, lastUpdate));
//     }

    private Tuple3<SyndFeed, XmlReader, Boolean> processFeed(
            ClientHttpResponse response, URI url, Date lastUpdate) throws IOException {
        try {
            XmlReader reader = new XmlReader(response.getBody());
            SyndFeed rssFeed = new SyndFeedInput().build(reader);
            Date buildDate = rssFeed.getPublishedDate();
            System.out.println("The build date is: "+buildDate.toString());
            LOGGER.info("Build Date {}:", buildDate);
            if (shouldFetchFeed(
                    buildDate, lastUpdate)) { // Compare the build date with the last update time. They are alway gonna be different no?
                return Tuples.of(rssFeed, reader, true);
            } else {
                LOGGER.info("Skipping feed '{}'. Last build date is before last update time.", url);
                reader.close(); // Close the reader if feed is not fetched
                return Tuples.of(rssFeed, reader, false);
            }
        } catch (FeedException e) {
            throw Exceptions.propagate(e);
        }
    }

    /**
     * Gets each individual entry for a given feed. The provided XML Reader will be closed.
     *
     * @param feedReaderTuple Tuple containing Received feed and XML Reader to be closed
     * @return Flux containing each individual entry.
     */
    private Flux<SyndEntry> getEntries(Tuple2<SyndFeed, XmlReader> feedReaderTuple) {// What did we do to the third value
        Flux<SyndEntry> entries = Flux.fromIterable(feedReaderTuple.getT1().getEntries());
        try {
            feedReaderTuple.getT2().close();
        } catch (IOException e) {
            LOGGER.warn("Failed to close XML Reader!", e);
        }
        return entries;
    }

    /**
     * Transforms a SyndEntry to a database object for storage
     *
     * @param syndEntry Entry to transform
     * @return transformed database object for entry
     */
    private Mono<ArticleData> transformEntry(
            SyndEntry syndEntry, int sourceId, AtomicInteger failCount) {
        try {
            UUID uuid = rssIdGenerator.generateId();
            Date currentDate = dateUtilsService.getCurrentDate();
            SyndContent description = syndEntry.getDescription();

            ArticlesEntity articlesEntity =
                    new ArticlesEntity(// saving directly to database from here? Kinda messy, should go throu db service impl
                            uuid,
                            sourceId,
                            currentDate,
                            syndEntry.getPublishedDate(),
                            false,
                            false,
                            NormalizeLinks.normalizeAndHashLink(syndEntry.getLink()));
            ArticleContentEntity articleContentEntity =
                    new ArticleContentEntity(
                            uuid,
                            syndEntry.getLink(),
                            syndEntry.getTitle(),
                            syndEntry.getPublishedDate(),
                            (description != null ? description.getValue() : null));
            

            return Mono.just(new ArticleData(articlesEntity, articleContentEntity));
        } catch (Exception e) {
            // if any exception occurs during transformation
            failCount.incrementAndGet();
            // ignore and return nothing
            return Mono.empty();
        }
    }

    /**
     * Generates retry spec to log status of retries
     *
     * @param reason The reason why we are retrying
     * @return Retry Spec that logs before every retry
     */
    private RetryBackoffSpec getRetrySpec(String reason) {
        return RetrySpec.fixedDelay(
                        config.getFeedMaxAttempts(),
                        Duration.ofMillis(config.getFeedRetryBackoffMillis()))
                .doBeforeRetry(
                        retrySignal ->
                                LOGGER.warn(
                                        "Retrying when {}. Retry: {}"
                                                + " Backoff: {}ms",
                                        reason,
                                        retrySignal.totalRetriesInARow(),
                                        config.getFeedRetryBackoffMillis()))
                .onRetryExhaustedThrow(
                        (spec, retrySignal) -> {
                            LOGGER.error(
                                    "Retries exhausted. Failed to {}. {} retries"
                                            + " attempted.",
                                    reason,
                                    retrySignal.totalRetriesInARow());
                            return retrySignal.failure();
                        });
    }

    /**
     * Checks if the feed should be fetched based on its build date and the last update time.
     *
     * @param buildDate The build date of the feed.
     * @param lastUpdate The last update time from the database.
     * @return true if the feed should be fetched, false otherwise.
     */
    private boolean shouldFetchFeed(Date buildDate, Date lastUpdate) {
        // Ensure consistent time zone for comparison
        TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");

        // Convert last update time to UTC
        Calendar lastUpdateUTC = Calendar.getInstance(utcTimeZone);
        if (lastUpdate != null) {
            lastUpdateUTC.setTime(lastUpdate);
        }
        // Convert RSS feed date to UTC
        Calendar buildDateUTC = Calendar.getInstance(utcTimeZone);
        if (buildDate != null) {
            buildDateUTC.setTime(buildDate);
        }

        // If the last update time is null or the build date is after the last update then fetch the
        // feed
        return lastUpdate == null || buildDateUTC.after(lastUpdateUTC);
    }


   // might need to subscribe, will see

    public Flux<ArticleData> fetchArticlesFromOpenCti(String s){
        LOGGER.info("in fetchArticlesFromOpenCti");
        String openCtiEndpoint = "/graphql";
        return 
            Flux.just(openCtiEndpoint)
                .flatMap(url->sendQuery(url))
                .flatMap(this::transformArticle)
                .doOnNext(article->printArticle(article))// new object creation here
                .filter(  // I have a flux of Article, I need to make sure the links aren't duplicated
                    article -> {
                        return !detectDuplicateService.isDuplicateArticle(article.getLinkPrimary());// TODO maybe I need here the hashlink instead
                })
                .flatMap(article -> 
                    dbService.transformIntoDbObjects(article)
                );
                    
    }

    private void printArticle(Article arc) {
        LOGGER.info("Converted NEW article with id {}",arc.getId());
        LOGGER.info("The source is {}",arc.getSource());
        LOGGER.info("The link primary is {}",arc.getLinkPrimary());
        LOGGER.info("The external references are: ");


        List<String> relLinks = arc.getRelLinks().stream()
            .map((e)-> {
                LOGGER.info("The source is: {}, The url is: {}",e.getSource(),e.getRelatedLink());
                return e.getSource() + " - " + e.getRelatedLink();   
            }).toList();

        LOGGER.info("The object labels are: ");

        // List<String> labels = arc.getLabels().stream().map((label)->{
        //     LOGGER.info("The label is: {}",label);
        //     return label;
        // }).toList();

        arc.getLabels().stream().map((label)->{
            LOGGER.info("The label is: {}",label);
            return label;
        }).toList();


        LOGGER.info("The name is: {}",arc.getName());

        LOGGER.info("The description is: {}",arc.getDescription());

        LOGGER.info("The date published is: {}",arc.getDatePublished());


    }

    // this method need to split the externalReferences array into single articles (each article need to have exactly one link, the res of links would go to 
    // another object called RelatedLinks)
    // this method also writes the correct source name for articles coming from BleepingComputer (scan the link and overwrite the source by BleepingComputer instead of title of article)
    public Flux<Article> transformArticle(ArticlePrimary article) {
        Article arc;
        UUID id = rssIdGenerator.generateId();
        //String src;
        //String linkPrimary;
        List<Edges> resources = article.getExternalReferences().getEdges();
        int indexPrimary = detectPrimaryLink(resources);
        String src = resources.get(indexPrimary).getNode().getSource();// the source of the primary article
        String linkPrimary = resources.get(indexPrimary).getNode().getUrl();
        List<RelatedLink> relLinks = new ArrayList<>();

        IntStream.range(0,resources.size())
            .forEach(i -> {
                if (i != indexPrimary) {
                    RelatedLink r = new RelatedLink(resources.get(i).getNode().getSource(), resources.get(i).getNode().getUrl());
                    relLinks.add(r);
                }
            });
        // until now we gathered all related links
        List<String> labels = new ArrayList<>();
        for (int i = 0; i < article.getLabels().size();i++) {
            labels.add(article.getLabels().get(i).getValue());
        }

        //LOGGER.info("the source is inside transformArticle: {}, index P {}",src,indexPrimary);

        arc = new Article(id,src,linkPrimary,relLinks,labels,article.getName(),article.getDescription(),article.getDatePublished());
        
        return Flux.just(arc);
    }

    // this should detect whats the primary article link, serves as a black list to remove links like github and .txt files; that are relevant but that are not primary links. 
    private int detectPrimaryLink(List<Edges> resources) {
        int indexRes = -1; // index of the primary link
        int probableIndex = -1;

        for (int i = 0; i < resources.size();i++) {
            Edges edge = resources.get(i);
            cleanSourceName(edge.getNode());
            if (!(edge.getNode().getUrl().contains("github.com") 
                || edge.getNode().getUrl().contains("githubusercontent.com") 
                    || edge.getNode().getUrl().endsWith(".txt") 
                        || edge.getNode().getUrl().endsWith(".csv")
                            || edge.getNode().getUrl().contains("https://twitter.com")
                            || edge.getNode().getUrl().contains("https://otx.alienvault.com"))) {
                            // then this is a primary link article
                            // TODO: more restrictions might get added
                            indexRes = i;
            } else if (edge.getNode().getUrl().contains("https://otx.alienvault.com")) {
                probableIndex = i;
            }

            if (indexRes != -1) {
                break;
            }

        }

        if (indexRes == -1) {
            if (probableIndex != -1) {
                indexRes = probableIndex;
            } else {
                indexRes = 0;
            }
        }

        return indexRes;

    }

    private NodeArticle cleanSourceName(NodeArticle pair) {
        if (pair.getUrl().contains("www.bleepingcomputer.com")) {
            // ok the url is a bleeping computer one
            if (!pair.getSource().equals("AlienVault")) {
                // it's not AlienVault as a source name
                // so overwrite the source name by Bleeping Computer
                pair.setSource("Bleeping Computer");
            }
        }
        return pair;
    }

    public Flux<ArticlePrimary> sendQuery(String url) {
        LOGGER.info("in send Query");
        Map<String, String> requestBody = new HashMap<>();
        String last_system_update = dbService.getLastVersionUpdateTime();

        // first, check the versions table and see from where we need to start importing
        if (last_system_update != "") {
            // we have a date to start fetching from
            requestBody.put("query", "{reports(filters: {mode: and filters: {key: \"published\",values:\""+last_system_update+"\",operator: gt}filterGroups:[]})"+
            "{edges{node{standard_id externalReferences{edges{node{source_name url}}} objectLabel {value} name description published}}}}");
            LOGGER.info("THE VALUE OF QUERY IS: {}",requestBody.get("query"));
        } else {
            // we dont have a date to start fetching from; pull all the stuff that open cti offers
            requestBody.put("query", "{reports{edges{node{standard_id externalReferences{edges{node{source_name url}}} objectLabel {value} name description published}}}}");
            LOGGER.info("Initial system build. All information should be pulled from open cti. ");// here the format is good
        }


        LOGGER.info("QUERY HERE: {}",requestBody.get("query"));

        // changes come here; best of cases here we return a dto object of whats important from db
        // return webClient
        // .post()
        // .uri(url)
        // .bodyValue(requestBody)
        // .retrieve()
        // .bodyToMono(String.class)// better to stream, than buffering evth in memory TODO
        // .retryWhen(getRetrySpec("pulling articles from Open CTI"))
        // .doOnNext(response -> LOGGER.info("Response: "+response))
        // .then(saveVersionDate())
        // .doOnError(e->LOGGER.error("Error getting reports from open cti",e));

        return webClient
        .post()
        .uri(url)
        .bodyValue(requestBody)
        .retrieve()
        .bodyToFlux(JsonNode.class)// ArticlePrimary is NOT the object we save to the database, it just primarly
        .flatMap(json->Flux.fromIterable(json.get("data").get("reports").get("edges")))
        .map(edge->objectMapper.convertValue(edge.get("node"),ArticlePrimary.class))
        //.retryWhen(getRetrySpec("pulling articles from Open CTI"))
        //.then(saveVersionDate())
        .doOnNext(res->printArticles(res));// apparently this operates on every single article emitted by the flux, need to save this object now to the db instead of printing it
        //.doOnError(e->LOGGER.error("Error getting reports from open cti",e));
    }

    public void printArticles(ArticlePrimary article) {
        LOGGER.info("Converted article with id {}",article.getStandardId());
        //LOGGER.info("The external references are: {}",
        List<String> links = article.getExternalReferences()
            .getEdges().stream()
            .map((e)-> {
                LOGGER.info("The source is: {}, The url is: {}",e.getNode().getSource(),e.getNode().getUrl());
                return e.getNode().getSource() + " - " + e.getNode().getUrl();   
            }).toList();

        LOGGER.info("The external references are: {}", links);

        List<String> labels = article.getLabels().stream().map((label)->{
            return label.getValue();
        }).toList();

        LOGGER.info("The object labels are: {}",labels);

        LOGGER.info("The name is: {}",article.getName());

        LOGGER.info("The description is: {}",article.getDescription());

        LOGGER.info("The date published is: {}",article.getDatePublished());
    }



    private Flux<String> saveVersionDate() {
        // save todays date; TODO: as for now I save todays date, but afterward we should save starting the last published date we find in the response of whats before.
        // to not loose important articles

        Instant todaysUTC = Instant.now();
        LOGGER.info("Saving {} for future updates.",todaysUTC.toString());
        if (dbService.saveVersion(todaysUTC)) {
            return Flux.just("ok");
        } else {
            return Flux.just("a problem happened");
        }


    }


    public Flux<Object> transformToJsonObj(Object answer) {
        return Flux.empty();
    }
}
