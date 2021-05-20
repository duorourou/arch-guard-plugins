package com.github.duorourou.archtracing.plugins.validate;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

@Slf4j
@Mojo(name = "validate", defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES,
        requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class ArchValidateMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject mavenProject;

    @Parameter(defaultValue = "${project.build.directory}/generated-sources/arch-validator/",
            name = "generateTestSourceDir",
            required = true)
    private String generateTestSourceDir;

    @Parameter(name = "rootPackage",
            required = true)
    private String rootPackage;


    @SneakyThrows
    @Override
    public void execute() {

        TestFileGenerator.of(generateTestSourceDir,
                new Configuration(rootPackage))
                .generate();


    }
}

