package ca.teamdman.sfm.common.logging;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.item.DiskItem;
import ca.teamdman.sfm.common.util.SFMUtils;
import io.netty.buffer.Unpooled;
import mekanism.common.util.NBTUtils;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO: make the log level configurable in manager GUI
public class TranslatableLogger {
    private static final LoggerContext CONTEXT = new LoggerContext(SFM.MOD_ID);
    private final Logger logger;
    private boolean active = false;

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
     * Encodes the latest log entries to the buffer, truncating the oldest entries as needed to fit within the buffer size.
     * Writes the entries in reverse order to the buffer.
     *
     * @param buf the buffer to encode to
     */
    public void encode(FriendlyByteBuf buf) {
        int maxReadableBytes = 32600;
        int gap = maxReadableBytes - 4000; // I pulled this number out of my ass

        // We will write the latest log entries first until we get close to the limit
        // Then we will truncate the oldest entries to free memory before returning

        // To know how many entries to write at the start, we will write to a separate buffer
        FriendlyByteBuf firstPass = new FriendlyByteBuf(Unpooled.buffer());
        List<TranslatableLogEvent> contents = getContents();

        // Iterate contents in reverse
        ListIterator<TranslatableLogEvent> iterator = contents.listIterator(contents.size());
        int count = 0;
        while (iterator.hasPrevious()) {
            TranslatableLogEvent entry = iterator.previous();
            entry.encode(firstPass);
            count += 1;
            if (firstPass.readableBytes() + gap >= maxReadableBytes) {
                break;
            }
        }

        // Write the number of entries written to firstPass
        buf.writeVarInt(count);

        // Write the contents of firstPass to buf
        buf.writeBytes(firstPass);
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
}
