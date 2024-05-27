package ca.teamdman.sfm.common.logging;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.item.DiskItem;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO: make the log level configurable in manager GUI
public class TranslatableLogger {
    private static final LoggerContext CONTEXT = new LoggerContext(SFM.MOD_ID);
    private final Logger logger;
    private final LoggerConfig config;
    private final TranslatableAppender appender;
    private boolean active = false;

    public TranslatableLogger(String name) {
        // Create logger
        this.logger = CONTEXT.getLogger(name);

        // Create config - where the user-adjustable log level lives
        this.config = new LoggerConfig(name, Level.OFF, false);

        // Register the config to the logger
        Configuration configuration = CONTEXT.getConfiguration();
        configuration.removeLogger(name);
        configuration.addLogger(name, config);

        // Create appender
        this.appender = TranslatableAppender.createAppender(name);

        // Unregister any previous appenders that may have been created for this location
        config.removeAppender(appender.getName());

        // Attach the appender to the logger
        // TRACE to capture everything, this isn't the level changed by user
        config.addAppender(appender, Level.TRACE, null);

        // Start the appender
        appender.start();
    }

    public Level getLogLevel() {
        return config.getLevel();
    }

    public void setLogLevel(Level level) {
        config.setLevel(level);
        LoggerConfig found = CONTEXT.getConfiguration().getLoggerConfig(logger.getName());

        SFM.LOGGER.debug("Updating log level to {}, local={} found={}", level, System.identityHashCode(config), System.identityHashCode(found));
        found.setLevel(level);
        // I DONT KNOW WHY THIS IS DIFFERENT!!!!!

        this.active = level != Level.OFF;
        CONTEXT.updateLoggers();
    }


    public Stream<TranslatableContents> getLogs() {
        return appender.contents.stream();
    }

    public void dump(Supplier<Optional<ItemStack>> diskSupplier) {
        if (!this.active) return;
        Optional<ItemStack> disk = diskSupplier.get();
        disk.ifPresent(diskStack -> {
            String logs = getLogs().map(x -> I18n.get(x.getKey(), x.getArgs())).collect(Collectors.joining("\n"));
            DiskItem.setLogs(diskStack, logs);
            SFM.LOGGER.debug("Dumped logs length {} from {} to disk", logs.length(), System.identityHashCode(appender));
        });
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
