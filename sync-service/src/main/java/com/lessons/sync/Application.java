package com.lessons.sync;


import com.lessons.sync.services.RefreshService;
import com.lessons.sync.utilities.SpringAppContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;


@SpringBootApplication
@EnableScheduling
public class Application implements CommandLineRunner
{
    private static final Logger logger = LoggerFactory.getLogger(Application.class);


    public static void main( String[] args ) throws Exception {
        logger.debug("main() started.");

        // Start up Spring Boot but disable the banner
        SpringApplication app = new SpringApplication(Application.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);

        // Refresh the ES indexes on startup
        RefreshService refreshService = (RefreshService) SpringAppContextUtils.getBean("com.lessons.sync.services.RefreshService");
        refreshService.refreshAllMappings();


        logger.debug("Sync Service is up.");
    }

    /**
     * Run this method every day at 0500 server time
     */
    @Scheduled(cron = "${sync.refresh.cron:0 0 5 * * ?}")
    private void sync() throws Exception {

        logger.info("Scheduled call");
    }


    @Override
    public void run(String... args) throws Exception {
        // Tell Springboot application to not shutdown
        /*
        logger.debug("Sync Service is up.");
        Thread.currentThread().join();
         */
    }
}
