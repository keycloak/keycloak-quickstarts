package org.keycloak.quickstart.util;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * @author <a href="mailto:bruno@abstractj.org">Bruno Oliveira</a>
 */
public class StorageManager {

    private static final String TMP_DIR_PREFIX = "user_storage_";
    private static final Properties props = new Properties();
    private static Path file;

    public static String getPropertyFile() {
        return file.toFile().getAbsoluteFile().toString();
    }

    public static void createStorage() {
        try {
            Path path = Files.createTempDirectory(TMP_DIR_PREFIX);
            file = path.resolve("example-users.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addUser(String user, String password) {
        try {
            props.setProperty(user, password);
            FileWriter writer = new FileWriter(file.toFile());
            props.store(writer, "Added user");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteStorage() {
        try {
            Files.delete(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
