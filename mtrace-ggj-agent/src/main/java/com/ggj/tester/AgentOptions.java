package com.ggj.tester;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

import static java.lang.String.format;

public final class AgentOptions {

    public static final String TRACECLASS = "traceClass";
    public static final String TRACEMETHOD = "traceMethod";
    public static final String TRACEFILEPATH = "traceFilePath";

    public static final String INCLUDES = "includes";
    public static final String EXCLUDES = "excludes";
    public static final String EXCLCLASSLOADER = "exclclassloader";
    public static final String INCLBOOTSTRAPCLASSES = "inclbootstrapclasses";
    public static final String INCLNOLOCATIONCLASSES = "inclnolocationclasses";
    public static final String OUTPUT = "output";
    private static final Pattern OPTION_SPLIT = Pattern.compile(",(?=[a-zA-Z0-9_\\-]+=)");

    public static enum OutputMode {
        file,
        tcpserver,
        tcpclient,
        none
    }

    public static final String PORT = "port";
    public static final int DEFAULT_PORT = 6300;

    private static final Collection<String> VALID_OPTIONS = Arrays.asList(
            TRACECLASS, TRACEMETHOD, TRACEFILEPATH, INCLUDES, EXCLUDES, EXCLCLASSLOADER,
            INCLBOOTSTRAPCLASSES, INCLNOLOCATIONCLASSES,
            OUTPUT, PORT);

    private final Map<String, String> options;

    public AgentOptions() {
        this.options = new HashMap();
    }

    public AgentOptions(final String optionstr) {
        this();
        if (optionstr != null && optionstr.length() > 0) {
            for (final String entry : OPTION_SPLIT.split(optionstr)) {
                final int pos = entry.indexOf('=');
                if (pos == -1) {
                    throw new IllegalArgumentException(format(
                            "Invalid agent option syntax \"%s\".", optionstr));
                }
                final String key = entry.substring(0, pos);
                if (!VALID_OPTIONS.contains(key)) {
                    throw new IllegalArgumentException(
                            format("Unknown agent option \"%s\".", key));
                }

                final String value = entry.substring(pos + 1);
                setOption(key, value);
            }
            validateAll();
        }
    }

    public AgentOptions(final Properties properties) {
        this();
        for (final String key : VALID_OPTIONS) {
            final String value = properties.getProperty(key);
            if (value != null) {
                setOption(key, value);
            }
        }
    }

    private void validateAll() {
        validatePort(getPort());
        getOutput();
    }

    private void validatePort(final int port) {
        if (port < 0) {
            throw new IllegalArgumentException("port must be positive");
        }
    }

    public String getIncludes() {
        return getOption(INCLUDES, "*");
    }

    public void setIncludes(final String includes) {
        setOption(INCLUDES, includes);
    }

    public String getExcludes() {
        return getOption(EXCLUDES, "");
    }

    public void setExcludes(final String excludes) {
        setOption(EXCLUDES, excludes);
    }

    public String getExclClassloader() {
        return getOption(EXCLCLASSLOADER, "sun.reflect.DelegatingClassLoader");
    }

    public void setExclClassloader(final String expression) {
        setOption(EXCLCLASSLOADER, expression);
    }

    public boolean getInclBootstrapClasses() {
        return getOption(INCLBOOTSTRAPCLASSES, false);
    }

    public void setInclBootstrapClasses(final boolean include) {
        setOption(INCLBOOTSTRAPCLASSES, include);
    }

    public boolean getInclNoLocationClasses() {
        return getOption(INCLNOLOCATIONCLASSES, false);
    }

    public void setInclNoLocationClasses(final boolean include) {
        setOption(INCLNOLOCATIONCLASSES, include);
    }

    public int getPort() {
        return getOption(PORT, DEFAULT_PORT);
    }

    public void setPort(final int port) {
        validatePort(port);
        setOption(PORT, port);
    }

    public String getTraceClass() {
        return getOption(TRACECLASS, "true");
    }

    public void setTraceClass(final String traceClass) {
        setOption(TRACECLASS, traceClass);
    }

    public String getTraceMethod() {
        return getOption(TRACEMETHOD, "true");
    }

    public void setTraceMethod(final String traceMethod) {
        setOption(TRACEMETHOD, traceMethod);
    }

    public String getTraceFilePath() {
        return getOption(TRACEFILEPATH, "");
    }

    public void setTraceFilePath(final String traceFilePath) {
        setOption(TRACEFILEPATH, traceFilePath);
    }

    public OutputMode getOutput() {
        final String value = options.get(OUTPUT);
        return value == null ? OutputMode.file : OutputMode.valueOf(value);
    }

    public void setOutput(final String output) {
        setOutput(OutputMode.valueOf(output));
    }

    public void setOutput(final OutputMode output) {
        setOption(OUTPUT, output.name());
    }

    private void setOption(final String key, final int value) {
        setOption(key, Integer.toString(value));
    }

    private void setOption(final String key, final boolean value) {
        setOption(key, Boolean.toString(value));
    }

    private void setOption(final String key, final String value) {
        options.put(key, value);
    }

    private String getOption(final String key, final String defaultValue) {
        final String value = options.get(key);
        return value == null ? defaultValue : value;
    }

    private boolean getOption(final String key, final boolean defaultValue) {
        final String value = options.get(key);
        return value == null ? defaultValue : Boolean.parseBoolean(value);
    }

    private int getOption(final String key, final int defaultValue) {
        final String value = options.get(key);
        return value == null ? defaultValue : Integer.parseInt(value);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final String key : VALID_OPTIONS) {
            final String value = options.get(key);
            if (value != null) {
                if (sb.length() > 0) {
                    sb.append(',');
                }
                sb.append(key).append('=').append(value);
            }
        }
        return sb.toString();
    }

}
