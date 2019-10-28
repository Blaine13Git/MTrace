/*******************************************************************************
 * Copyright (c) 2009, 2019 Mountainminds GmbH & Co. KG and Contributors
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *
 *******************************************************************************/
package com.ggj.tester;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

import static java.lang.String.format;

public final class AgentOptions {

    public static final String TRACECLASS = "traceClass";
    public static final String TRACEMETHOD = "traceMethod";

    public static final String DESTFILE = "destfile";
    public static final String DEFAULT_DESTFILE = "jacoco.exec";
    public static final String APPEND = "append";
    public static final String INCLUDES = "includes";
    public static final String EXCLUDES = "excludes";
    public static final String EXCLCLASSLOADER = "exclclassloader";
    public static final String INCLBOOTSTRAPCLASSES = "inclbootstrapclasses";
    public static final String INCLNOLOCATIONCLASSES = "inclnolocationclasses";
    public static final String SESSIONID = "sessionid";
    public static final String DUMPONEXIT = "dumponexit";
    public static final String OUTPUT = "output";
    private static final Pattern OPTION_SPLIT = Pattern.compile(",(?=[a-zA-Z0-9_\\-]+=)");
    public static enum OutputMode {
        file,
        tcpserver,
        tcpclient,
        none
    }
    public static final String ADDRESS = "address";
    public static final String DEFAULT_ADDRESS = null;
    public static final String PORT = "port";
    public static final int DEFAULT_PORT = 6300;
    public static final String CLASSDUMPDIR = "classdumpdir";
    public static final String JMX = "jmx";

    private static final Collection<String> VALID_OPTIONS = Arrays.asList(
            TRACECLASS, TRACEMETHOD, DESTFILE, APPEND, INCLUDES, EXCLUDES, EXCLCLASSLOADER,
            INCLBOOTSTRAPCLASSES, INCLNOLOCATIONCLASSES, SESSIONID, DUMPONEXIT,
            OUTPUT, ADDRESS, PORT, CLASSDUMPDIR, JMX);

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

    public String getDestfile() {
        return getOption(DESTFILE, DEFAULT_DESTFILE);
    }
    public void setDestfile(final String destfile) {
        setOption(DESTFILE, destfile);
    }
    public boolean getAppend() {
        return getOption(APPEND, true);
    }
    public void setAppend(final boolean append) {
        setOption(APPEND, append);
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

    /**
     * Returns whether classes without source location should be instrumented.
     *
     * @return <code>true</code> if classes without source location should be
     * instrumented
     */
    public boolean getInclNoLocationClasses() {
        return getOption(INCLNOLOCATIONCLASSES, false);
    }

    /**
     * Sets whether classes without source location should be instrumented.
     *
     * @param include <code>true</code> if classes without source location should be
     *                instrumented
     */
    public void setInclNoLocationClasses(final boolean include) {
        setOption(INCLNOLOCATIONCLASSES, include);
    }

    /**
     * Returns the session identifier.
     *
     * @return session identifier
     */
    public String getSessionId() {
        return getOption(SESSIONID, null);
    }

    /**
     * Sets the session identifier.
     *
     * @param id session identifier
     */
    public void setSessionId(final String id) {
        setOption(SESSIONID, id);
    }

    /**
     * Returns whether coverage data should be dumped on exit.
     *
     * @return <code>true</code> if coverage data will be written on VM exit
     */
    public boolean getDumpOnExit() {
        return getOption(DUMPONEXIT, true);
    }

    /**
     * Sets whether coverage data should be dumped on exit.
     *
     * @param dumpOnExit <code>true</code> if coverage data should be written on VM
     *                   exit
     */
    public void setDumpOnExit(final boolean dumpOnExit) {
        setOption(DUMPONEXIT, dumpOnExit);
    }

    /**
     * Returns the port on which to listen to when the output is
     * <code>tcpserver</code> or the port to connect to when output is
     * <code>tcpclient</code>.
     *
     * @return port to listen on or connect to
     */
    public int getPort() {
        return getOption(PORT, DEFAULT_PORT);
    }

    /**
     * Sets the port on which to listen to when output is <code>tcpserver</code>
     * or the port to connect to when output is <code>tcpclient</code>
     *
     * @param port port to listen on or connect to
     */
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

    public void setTracemethod(final String traceMethod) {
        setOption(TRACEMETHOD, traceMethod);
    }

    /**
     * Gets the hostname or IP address to listen to when output is
     * <code>tcpserver</code> or connect to when output is
     * <code>tcpclient</code>
     *
     * @return Hostname or IP address
     */
    public String getAddress() {
        return getOption(ADDRESS, DEFAULT_ADDRESS);
    }

    /**
     * Sets the hostname or IP address to listen to when output is
     * <code>tcpserver</code> or connect to when output is
     * <code>tcpclient</code>
     *
     * @param address Hostname or IP address
     */
    public void setAddress(final String address) {
        setOption(ADDRESS, address);
    }

    /**
     * Returns the output mode
     *
     * @return current output mode
     */
    public OutputMode getOutput() {
        final String value = options.get(OUTPUT);
        return value == null ? OutputMode.file : OutputMode.valueOf(value);
    }


    /**
     * Sets the output mode
     *
     * @param output Output mode
     */
    public void setOutput(final String output) {
        setOutput(OutputMode.valueOf(output));
    }

    /**
     * Sets the output mode
     *
     * @param output Output mode
     */
    public void setOutput(final OutputMode output) {
        setOption(OUTPUT, output.name());
    }

    /**
     * Returns the location of the directory where class files should be dumped
     * to.
     *
     * @return dump location or <code>null</code> (no dumps)
     */
    public String getClassDumpDir() {
        return getOption(CLASSDUMPDIR, null);
    }

    /**
     * Sets the directory where class files should be dumped to.
     *
     * @param location dump location or <code>null</code> (no dumps)
     */
    public void setClassDumpDir(final String location) {
        setOption(CLASSDUMPDIR, location);
    }

    /**
     * Returns whether the agent exposes functionality via JMX.
     *
     * @return <code>true</code>, when JMX is enabled
     */
    public boolean getJmx() {
        return getOption(JMX, false);
    }

    /**
     * Sets whether the agent should expose functionality via JMX.
     *
     * @param jmx <code>true</code> if JMX should be enabled
     */
    public void setJmx(final boolean jmx) {
        setOption(JMX, jmx);
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

    /**
     * Generate required JVM argument based on current configuration and
     * supplied agent jar location.
     *
     * @param agentJarFile location of the JaCoCo Agent Jar
     * @return Argument to pass to create new VM with coverage enabled
     */
    public String getVMArgument(final File agentJarFile) {
        return format("-javaagent:%s=%s", agentJarFile, this);
    }

    /**
     * Generate required quoted JVM argument based on current configuration and
     * supplied agent jar location.
     *
     * @param agentJarFile location of the JaCoCo Agent Jar
     * @return Quoted argument to pass to create new VM with coverage enabled
     */
    public String getQuotedVMArgument(final File agentJarFile) {
        return CommandLineSupport.quote(getVMArgument(agentJarFile));
    }

    /**
     * Generate required quotes JVM argument based on current configuration and
     * prepends it to the given argument command line. If a agent with the same
     * JAR file is already specified this parameter is removed from the existing
     * command line.
     *
     * @param arguments    existing command line arguments or <code>null</code>
     * @param agentJarFile location of the JaCoCo Agent Jar
     * @return VM command line arguments prepended with configured JaCoCo agent
     */
    public String prependVMArguments(final String arguments,
                                     final File agentJarFile) {
        final List<String> args = CommandLineSupport.split(arguments);
        final String plainAgent = format("-javaagent:%s", agentJarFile);
        for (final Iterator<String> i = args.iterator(); i.hasNext(); ) {
            if (i.next().startsWith(plainAgent)) {
                i.remove();
            }
        }
        args.add(0, getVMArgument(agentJarFile));
        return CommandLineSupport.quote(args);
    }

    /**
     * Creates a string representation that can be passed to the agent via the
     * command line. Might be the empty string, if no options are set.
     */
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
