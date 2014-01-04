package cz.zcu.kiv.crce.it;

import java.io.IOException;

/**
 *
 * @author Jiri Kucera (jiri.kucera@kalwi.eu)
 */
public class Configuration {

    public static void metadataDao(IntegrationTestBase integrationTestBase) throws IOException {
        integrationTestBase.configure(
                cz.zcu.kiv.crce.metadata.dao.internal.Activator.PID,
                "jdbc.driver", "org.h2.Driver",
                "jdbc.url", "jdbc:h2:mem:it;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
                "jdbc.username", "sa",
                "jdbc.password", "");
    }

    public static void repository(IntegrationTestBase integrationTestBase) throws IOException {
        integrationTestBase.configure(
                cz.zcu.kiv.crce.repository.internal.Activator.PID,
                cz.zcu.kiv.crce.repository.internal.Activator.STORE_URI, "target/store"
        );
    }
}
