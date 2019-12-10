package com.lessons;

import org.apache.commons.lang3.StringUtils;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

public class FlywayUtils {

    private static final Logger logger = LoggerFactory.getLogger(FlywayUtils.class);



    public static void runCleanMigrate(DataSource aDataSource, String aDirectoryPathToSampleData ) {
        logger.debug( "runFlywayCleanMigrate() started." );

        Flyway flyway = new Flyway();

        // Have flyway code point to our embedded database
        flyway.setDataSource(aDataSource);

        if (StringUtils.isEmpty(aDirectoryPathToSampleData)) {
            flyway.setLocations("db");
        }
        else {
            flyway.setLocations("db", aDirectoryPathToSampleData);
        }

        // Run flyway clean
        flyway.clean();

        // Run flyway migrate
        flyway.migrate();

        logger.debug( "runFlywayCleanMigrate() finished." );
    }

}