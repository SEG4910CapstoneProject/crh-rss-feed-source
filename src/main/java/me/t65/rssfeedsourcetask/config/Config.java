package me.t65.rssfeedsourcetask.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import me.t65.rssfeedsourcetask.rss.RssFeedService;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.RetryBackoffSpec;
import reactor.util.retry.RetrySpec;

import java.time.Duration;

/** Configuration Data for application */
@Configuration   
@lombok.Getter   // this makes a getter for it
public class Config {
    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);


    @Value("${rss-feed.retries.max-attempts}")  //being read from application.yml
    private int feedMaxAttempts;

    @Value("${rss-feed.retries.retry-backoff-millis}")  // same,being read from there as well
    private int feedRetryBackoffMillis;

    /** Bean for building a default rest template */
    // @Bean
    // public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
    //     return restTemplateBuilder.build();
    // }

    /**
     * Bean for building web client . This is going to replace rest template    */
    @Bean
    public WebClient webClient() {
        LOGGER.info("Configuring: {}",System.getenv("OPEN_CTI_PRIVATE_KEY"));
        return WebClient.builder().exchangeStrategies(ExchangeStrategies.builder()
        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024*1024)).build())
        .baseUrl("http://opencti:8080")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader("Authorization","Bearer "+System.getenv("OPEN_CTI_PRIVATE_KEY"))
        .build();
    }

    /**
     * Bean for feed retrying. Creates fixed delay retry backoff spec. Exponential backoff is not
     * used.
     */
    @Bean("feedRetrySpec")
    public RetryBackoffSpec feedRetrySpec() {
        return RetrySpec.fixedDelay(feedMaxAttempts, Duration.ofMillis(feedRetryBackoffMillis));
    }

    @Bean
    public Scheduler scheduler() {
        return Schedulers.boundedElastic();
    }
}
