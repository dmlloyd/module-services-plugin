package io.github.dmlloyd.maven;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import io.github.dmlloyd.classfile.Attributes;
import io.github.dmlloyd.classfile.ClassFile;
import io.github.dmlloyd.classfile.ClassModel;
import io.github.dmlloyd.classfile.attribute.ModuleAttribute;
import io.github.dmlloyd.classfile.attribute.ModuleProvideInfo;
import io.github.dmlloyd.classfile.constantpool.ClassEntry;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * The module services mojo.
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public final class ModuleServicesMojo extends AbstractMojo {
    /**
     * The input directory where the module info class should be found.
     */
    @Parameter(defaultValue = "${project.build.outputDirectory}", required = true, property = "module.services.classes")
    private File classesDirectory;

    /**
     * The output directory where the services file should be placed.
     */
    @Parameter(defaultValue = "${project.build.outputDirectory}", required = true, property = "module.services.output")
    private File outputDirectory;

    /**
     * Set to true to skip execution of this plugin.
     */
    @Parameter(defaultValue = "false", property = "module.services.skip")
    private boolean skip;

    /**
     * Run the Mojo.
     *
     * @throws MojoExecutionException if the mojo didn't run correctly
     */
    public void execute() throws MojoExecutionException {
        if (skip) {
            getLog().info("Execution of module-services-plugin:generate has been skipped by configuration");
            return;
        }
        Path resolved = classesDirectory.toPath().resolve("module-info.class");
        byte[] moduleInfo;
        try {
            moduleInfo = Files.readAllBytes(resolved);
        } catch (IOException e) {
            throw new MojoExecutionException("Cannot read `module-info.class` file from " + classesDirectory, e);
        }
        Path services = outputDirectory.toPath().resolve("META-INF/services");
        try {
            Files.createDirectories(services);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to create " + services, e);
        }
        ClassModel cm = ClassFile.of().parse(moduleInfo);
        ModuleAttribute ma = cm.findAttribute(Attributes.module()).orElseThrow(() -> new MojoExecutionException("Module file does not contain a module attribute"));
        for (ModuleProvideInfo provided : ma.provides()) {
            String serviceName = provided.provides().asInternalName().replace('/', '.');
            List<ClassEntry> with = provided.providesWith();
            if (! with.isEmpty()) {
                Path outputFile = services.resolve(serviceName);
                getLog().debug("Writing " + with.size() + " services to " + outputFile);
                try (BufferedWriter bw = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8)) {
                    bw.write("# NOTE: this file was generated from `module-info.class` by module-services-plugin\n");
                    for (ClassEntry classEntry : with) {
                        bw.write(classEntry.asInternalName().replace('/', '.'));
                        bw.write('\n');
                    }
                } catch (IOException e) {
                    throw new MojoExecutionException("Failed to write " + outputFile, e);
                }
            }
        }
    }
}
