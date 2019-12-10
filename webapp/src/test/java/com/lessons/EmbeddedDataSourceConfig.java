package com.lessons;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.io.IOException;

@Configuration
public class EmbeddedDataSourceConfig {
    private static final Logger logger = LoggerFactory.getLogger(EmbeddedDataSourceConfig.class);

    private EmbeddedPostgres embeddedPostgres;

    @Bean
    @Profile( "test" )           // This spring bean is only created when the profile is "test" -- so it will be included when running unit tests
    public DataSource getDataSource() throws Exception {
        logger.debug("getDataSource() started.");

        // Start-up the embedded database
        // Note, that the embdded data source is running, we can get the jdbc url information
        this.embeddedPostgres = EmbeddedPostgres.start();

        // Get the jdbcUrl from the running postgres database
        String jdbcUrl = embeddedPostgres.getJdbcUrl("postgres", "postgres");

        // Build a HikariConfig object
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername("postgres");
        hikariConfig.setPassword("postgres");

        // Build a HikariDataSource object (using the hikariConfig)
        HikariDataSource dataSource = new HikariDataSource(hikariConfig);

        logger.debug("getDataSource() finished");

        return dataSource;
    }


    /**
     * Shutdown the embedded Postgres server when spring shuts-down this class
     */
    @PreDestroy
    public void preDestroy() {
        if (this.embeddedPostgres != null) {
            try {
                logger.debug("Stopping Embedded Postgres....");
                this.embeddedPostgres.close();
            } catch (IOException e) {
                logger.warn("Ignoring exception attempting to stop embedded postgres");
            }
        }
    }
}