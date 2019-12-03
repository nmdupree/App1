package com.lessons;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static junit.framework.TestCase.assertTrue;


@RunWith(SpringRunner.class)    // Required to work with JUnit 4
@SpringBootTest                 // Start up a Spring Application Context
public class AppTest {

    private static final Logger logger = LoggerFactory.getLogger(AppTest.class);

    @Value("${local.server.port}")
    private String appServerPort;       // Holds the port that the test app server is listening on

    @Test
    public void testDashboardControllerGetTime2() {
        RestTemplate restTemplate = new RestTemplate();

        // Construct the URL to connect to this REST endpoint
        String url = "http://localhost:" + appServerPort + "/app1/api/dashboard/time";

        // Invoke the REST endpoint and get the response
        String dateTime = restTemplate.getForObject(url, String.class);

        assertTrue("Expected dateTime to be non-null", (dateTime != null));


    }
}