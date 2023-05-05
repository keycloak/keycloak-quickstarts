package org.keycloak.quickstart.event.listener;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import org.jboss.logging.Logger;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class LogReaderHelper implements Closeable {

    private static final int INTERVAL_MS = 2000;

    private static final Logger logger = Logger.getLogger(LogReaderHelper.class);

    private final BufferedReader reader;
    private final WatchService watchService;
    private final String filePath;

    private boolean closed = false;

    private final LinkedList<String> lines = new LinkedList<>();

    public LogReaderHelper(String filePath) {
        try {
            this.filePath = filePath;
            this.reader = new BufferedReader(new FileReader(filePath));
            this.watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException ioe) {
            throw new IllegalArgumentException("Exception during initialization of FileReaderHelper", ioe);
        }
    }

    private Path getDirPath() {
        return Paths.get(filePath).getParent();
    }

    private String getFileName() {
        return Paths.get(filePath).getFileName().toString();
    }

    public void start() {
        // Initial read of all existing lines in the file
        readLines();
        this.lines.clear();

        try {
            getDirPath().register(watchService, ENTRY_MODIFY);
        } catch (IOException ioe) {
            throw new RuntimeException("Exception when register watchService", ioe);
        }
    }

    private void readLines() {
        if (closed) {
            logger.warn("Reader is closed. Not possible to return any new lines");
            return;
        }

        try {
            boolean available = true;
            while (!closed && available) {
                String line = this.reader.readLine();
                if (line != null) {
                    lines.add(line);
                } else {
                    available = false;
                }
            }
        } catch (IOException ioe) {
            logger.warn("Error when reading file", ioe);
        }
    }


    /**
     * Read the first line, which was added to the file since last read and removes this line from the "stack" of read lines.
     * In case, there is not line present, this method will block for INTERVAL_MS milliseconds until new line is present or return null if nothing is present
     * until specified timeout
     *
     * @return
     */
    public String pollLine() {
        if (!lines.isEmpty()) {
            return lines.pollFirst();
        } else {
            boolean linePresent = waitUntilNewLinePresent(true);
            if (linePresent) {
                return lines.pollFirst();
            } else {
                logger.warn("No line present");
                return null;
            }
        }
    }

    /**
     * Clear the previously read lines
     */
    public void clear() {
        // Consume all existing lines (if any)
        while (waitUntilNewLinePresent(false)) {
        }
        this.lines.clear();
    }

    private boolean waitUntilNewLinePresent(boolean wait) {
        if (closed) {
            logger.warn("Reader is closed. Not possible to return any new lines");
            return false;
        }

        try {
            WatchKey key;
            if (wait) {
                key = watchService.poll(INTERVAL_MS, TimeUnit.MILLISECONDS);
            } else {
                // Just check if something is present - without waiting
                key = watchService.poll();
            }
            if (key == null) return false;

            for (WatchEvent<?> event : key.pollEvents()) {
                if (getFileName().equals(event.context().toString())) {
                    readLines();
                } else {
                    logger.infof("Detected change in different file. Our fileName: %s, event context: %s", getFileName(), event.context().toString());
                }
            }

            key.reset();
            return true;
        } catch (ClosedWatchServiceException cwse) {
            logger.error("Closed watch service when trying to reading file", cwse);
            return false;
        } catch (InterruptedException ie) {
            logger.error("Interrupted", ie);
            return false;
        }
    }

    @Override
    public void close() {
        try {
            closed = true;
            reader.close();
            watchService.close();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
}
