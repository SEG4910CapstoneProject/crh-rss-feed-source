package me.t65.rssfeedsourcetask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;

@SpringBootApplication
@EnableTask
public class RssFeedSourceTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(RssFeedSourceTaskApplication.class, args);
    }
}
