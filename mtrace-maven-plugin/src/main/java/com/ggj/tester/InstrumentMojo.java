package com.ggj.tester;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "mtInstrument")
public class InstrumentMojo extends AbstractMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("《《《《《《《《《《《《《-启动调用链追踪-》》》》》》》》》》》》》");
    }
}
