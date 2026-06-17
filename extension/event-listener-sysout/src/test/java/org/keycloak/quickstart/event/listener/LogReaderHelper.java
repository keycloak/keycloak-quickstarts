package org.keycloak.quickstart.event.listener;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import org.jboss.logging.Logger;

/**
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class LogReaderHelper implements Closeable {

    private static final int INTERVAL_MS = 2000;

    private static final Logger logger = Logger.getLogger(LogReaderHelper.class);

    private final BufferedReader reader;
    private final File file;
    private long length;

    private boolean closed = false;

    private final LinkedList<String> lines = new LinkedList<>();

    public LogReaderHelper(String filePath) {
        try {
            this.file = new File(filePath);
            this.reader = new BufferedReader(new FileReader(this.file));
        } catch (IOException ioe) {
            throw new IllegalArgumentException("Exception during initialization of FileReaderHelper", ioe);
        }
    }

    private Path getDirPath() {
        return file.toPath().getParent();
    }

    private String getFileName() {
        return file.getName();
    }

    public void start() {
        // Initial read of all existing lines in the file
        readLines();
        this.lines.clear();

        length = file.length();
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

    private boolean waitUtilModified(boolean wait) throws InterruptedException {
        long newLength = file.length();
        int i = 0;
        int steps = 4;
        while (wait && this.length == newLength && i < steps) {
            TimeUnit.MILLISECONDS.sleep(INTERVAL_MS / steps);
            newLength = file.length();
            i++;
        }
        if (newLength > this.length) {
            this.length = newLength;
            return true;
        }
        return false;
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
            if (waitUtilModified(wait)) {
                readLines();
                return true;
            }
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
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
}
