package com.re.session09.configuration;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.JsonEncoder;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.util.FileSize;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;

public class LogbackConfiguration extends ContextAwareBase implements Configurator {

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Override
    public ExecutionStatus configure(LoggerContext loggerContext) {
        // Assign system context for current configuration class
        setContext(loggerContext);

        // Find root logger
        Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);

        if ("prod".equalsIgnoreCase(activeProfile)) {
            configureProductionLogging(loggerContext, rootLogger);
        } else {
            configureDevelopmentLogging(loggerContext, rootLogger);
        }

        return ExecutionStatus.DO_NOT_INVOKE_NEXT_IF_ANY;
    }

    private void configureDevelopmentLogging(LoggerContext loggerContext, Logger rootLogger) {
        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
        consoleAppender.setContext(loggerContext);
        consoleAppender.setName("CONSOLE");

        PatternLayoutEncoder patternLayoutEncoder = new PatternLayoutEncoder();
        patternLayoutEncoder.setContext(loggerContext);
        patternLayoutEncoder.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %5level %logger{36} - %msg%n");
        patternLayoutEncoder.setCharset(StandardCharsets.UTF_8);
        patternLayoutEncoder.start();

        consoleAppender.setEncoder(patternLayoutEncoder);
        consoleAppender.start();

        rootLogger.setLevel(Level.INFO);
        rootLogger.addAppender(consoleAppender);

        Logger appLogger = loggerContext.getLogger("com.re.session09");
        appLogger.setLevel(Level.DEBUG);
    }

    private void configureProductionLogging(LoggerContext loggerContext, Logger rootLogger) {
        rootLogger.setLevel(Level.INFO);

        ConsoleAppender<ILoggingEvent> jsonConsoleAppender = new ConsoleAppender<>();
        jsonConsoleAppender.setContext(loggerContext);
        jsonConsoleAppender.setName("JSON_CONSOLE");

        JsonEncoder jsonEncoder = new JsonEncoder();
        jsonEncoder.setContext(loggerContext);
        jsonEncoder.start();

        jsonConsoleAppender.setEncoder(jsonEncoder);
        jsonConsoleAppender.start();
        rootLogger.addAppender(jsonConsoleAppender);

        RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender<>();
        fileAppender.setContext(loggerContext);
        fileAppender.setName("FILE");
        fileAppender.setFile("logs/app-error.log");

        ThresholdFilter filter = new ThresholdFilter();
        filter.setLevel("WARN");
        filter.start();
        fileAppender.addFilter(filter);

        SizeAndTimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new SizeAndTimeBasedRollingPolicy<>();
        rollingPolicy.setContext(loggerContext);
        rollingPolicy.setParent(fileAppender);
        rollingPolicy.setFileNamePattern("logs/archived/app-error-%d{yyyy-MM-dd}.%i.log");
        rollingPolicy.setMaxFileSize(FileSize.valueOf("10MB"));
        rollingPolicy.setMaxHistory(30);
        rollingPolicy.start();

        fileAppender.setRollingPolicy(rollingPolicy);

        JsonEncoder fileJsonEncoder = new JsonEncoder();
        fileJsonEncoder.setContext(loggerContext);
        fileJsonEncoder.start();
        fileAppender.setEncoder(fileJsonEncoder);

        fileAppender.start();
        rootLogger.addAppender(fileAppender);
    }
}
