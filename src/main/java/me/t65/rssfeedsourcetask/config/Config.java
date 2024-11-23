package me.t65.rssfeedsourcetask.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.RetryBackoffSpec;
import reactor.util.retry.RetrySpec;

import java.time.Duration;

/** Configuration Data for application */
@Configuration
@lombok.Getter
public class Config {

    @Value("${rss-feed.retries.max-attempts}")
    private int feedMaxAttempts;

    @Value("${rss-feed.retries.retry-backoff-millis}")
    private int feedRetryBackoffMillis;

    /** Bean for building a default rest template */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
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
