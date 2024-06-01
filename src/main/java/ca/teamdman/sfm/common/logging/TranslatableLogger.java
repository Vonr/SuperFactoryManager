package ca.teamdman.sfm.common.logging;

import ca.teamdman.sfm.SFM;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.time.Instant;
import org.apache.logging.log4j.core.time.MutableInstant;

import java.util.*;
import java.util.function.Consumer;

public class TranslatableLogger {
    private static final LoggerContext CONTEXT = new LoggerContext(SFM.MOD_ID);
    private final Logger logger;
    private boolean active = false;
    private MutableInstant lastSentMarker = new MutableInstant();

    public TranslatableLogger(String name) {
        // Create logger
        this.logger = CONTEXT.getLogger(name);


        // Register the config to the logger
        Configuration configuration = CONTEXT.getConfiguration();
        configuration.removeLogger(name);
        LoggerConfig config = new LoggerConfig(name, Level.OFF, false);
        configuration.addLogger(name, config);

        // Create appender
        TranslatableAppender appender = TranslatableAppender.createAppender(name);

        // Unregister any previous appenders that may have been created for this location
        config.removeAppender(name);

        // Attach the appender to the logger
        config.addAppender(appender, Level.TRACE, null);

        // Start the appender
        appender.start();
    }

    public static boolean comesAfter(Instant a, Instant b) {
        return a.getEpochSecond() > b.getEpochSecond() || (
                a.getEpochSecond() == b.getEpochSecond()
                && a.getNanoOfSecond() > b.getNanoOfSecond()
        );
    }

    public static List<TranslatableLogEvent> decode(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        List<TranslatableLogEvent> contents = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            contents.add(TranslatableLogEvent.decode(buf));
        }
        Collections.reverse(contents); // Reverse the list to restore original order
        return contents;
    }

    public Level getLogLevel() {
        return CONTEXT.getConfiguration().getLoggerConfig(logger.getName()).getLevel();
    }

    public void setLogLevel(Level level) {
        LoggerConfig found = CONTEXT.getConfiguration().getLoggerConfig(logger.getName());
        found.setLevel(level);

        this.active = level != Level.OFF;
        CONTEXT.updateLoggers();
    }

    private List<TranslatableLogEvent> getContents() {
        var appenders = CONTEXT.getConfiguration().getLoggerConfig(logger.getName()).getAppenders();
        if (appenders.containsKey(logger.getName())) {
            var appender = appenders.get(logger.getName());
            if (appender instanceof TranslatableAppender ta) {
                return ta.contents;
            }
        }
        return Collections.emptyList();
    }

    public List<TranslatableLogEvent> cloneContents() {
        return new ArrayList<>(getContents());
    }

    /**
     * Writes logs to the buffer.
     * Writes the entries in reverse order to the buffer.
     *
     * @return number of logs written
     */
    public static int encode(List<TranslatableLogEvent> logs, FriendlyByteBuf buf) {
        int maxReadableBytes = 32600;

        FriendlyByteBuf chunk = new FriendlyByteBuf(Unpooled.buffer());

        // Send latest logs first by iterating backwards
        ListIterator<TranslatableLogEvent> iterator = logs.listIterator(logs.size());
        int count = 0;
        while (iterator.hasPrevious()) {
            TranslatableLogEvent entry = iterator.previous();
            FriendlyByteBuf check = new FriendlyByteBuf(Unpooled.buffer());
            entry.encode(check);
            if (check.readableBytes() + chunk.readableBytes() + buf.readableBytes() >= maxReadableBytes) {
                break;
            }
            chunk.writeBytes(check);
            count += 1;
        }

        buf.writeVarInt(count);
        buf.writeBytes(chunk);

        return count;
    }

    public List<TranslatableLogEvent> getLatestLogs() {
        List<TranslatableLogEvent> contents = getContents();
        LinkedList<TranslatableLogEvent> toSend = new LinkedList<>();
        // Add from tail until we reach the last sent marker
        var iter = contents.listIterator(contents.size());
        while (iter.hasPrevious()) {
            var entry = iter.previous();
            if (comesAfter(entry.instant(), lastSentMarker)) {
                toSend.addFirst(entry);
            } else {
                break;
            }
        }
        return toSend;
    }

    /**
     * Encodes the latest log entries to the buffer, truncating the oldest entries as needed to fit within the buffer size.
     * Writes the entries in reverse order to the buffer.
     *
     * @param buf the buffer to encode to
     */
    public void encodeAndTruncate(FriendlyByteBuf buf) {
        List<TranslatableLogEvent> contents = getContents();
        LinkedList<TranslatableLogEvent> toSend = new LinkedList<>();
        // Add from tail until we reach the last sent marker
        var iter = contents.listIterator(contents.size());
        while (iter.hasPrevious()) {
            var entry = iter.previous();
            if (comesAfter(entry.instant(), lastSentMarker)) {
                toSend.addFirst(entry);
            } else {
                break;
            }
        }

        int written = encode(toSend, buf);
        if (written > 0) {
            if (lastSentMarker.equals(new MutableInstant())) {
                // We sent all logs, so we can truncate whatever wasn't sent because it doesn't fit.
                contents.subList(0, contents.size() - written).clear();
            }
            // Update the marker to the latest entry
            lastSentMarker.initFrom(toSend.getLast().instant());
        } else if (!toSend.isEmpty()) {
            SFM.LOGGER.warn(
                    "We wrote zero out of {} logs, but the list was not empty. Was your buffer already full?",
                    toSend.size()
            );
        }
    }

    public void pruneSoWeDontEatAllTheRam() {
        List<TranslatableLogEvent> contents = getContents();
        if (contents.size() > 1000) {
            // Remove the oldest 100 entries
            contents.subList(0, 100).clear();
        }
    }

    public void info(TranslatableContents contents) {
        if (!this.active && !logger.isEnabled(Level.INFO)) return;
        logger.info(contents.getKey(), contents.getArgs());
    }

    public void info(Consumer<Consumer<TranslatableContents>> logger) {
        if (!this.active && !this.logger.isEnabled(Level.INFO)) return;
        logger.accept(this::info);
    }

    public void warn(TranslatableContents contents) {
        if (!this.active && !logger.isEnabled(Level.WARN)) return;
        logger.warn(contents.getKey(), contents.getArgs());
    }

    public void warn(Consumer<Consumer<TranslatableContents>> logger) {
        if (!this.active && !this.logger.isEnabled(Level.WARN)) return;
        logger.accept(this::warn);
    }

    public void error(TranslatableContents contents) {
        if (!this.active && !logger.isEnabled(Level.ERROR)) return;
        logger.error(contents.getKey(), contents.getArgs());
    }

    public void error(Consumer<Consumer<TranslatableContents>> logger) {
        if (!this.active && !this.logger.isEnabled(Level.ERROR)) return;
        logger.accept(this::error);
    }

    public void debug(TranslatableContents contents) {
        if (!this.active && !logger.isEnabled(Level.DEBUG)) return;
        logger.debug(contents.getKey(), contents.getArgs());
    }

    public void debug(Consumer<Consumer<TranslatableContents>> logger) {
        if (!this.active && !this.logger.isEnabled(Level.DEBUG)) return;
        logger.accept(this::debug);
    }

    public void trace(TranslatableContents contents) {
        if (!this.active && !logger.isEnabled(Level.TRACE)) return;
        logger.trace(contents.getKey(), contents.getArgs());
    }

    public void trace(Consumer<Consumer<TranslatableContents>> logger) {
        if (!this.active && !this.logger.isEnabled(Level.TRACE)) return;
        logger.accept(this::trace);
    }

    public void clear() {
        List<TranslatableLogEvent> contents = getContents();
        contents.clear();
        lastSentMarker.initFrom(new MutableInstant());
    }
}
