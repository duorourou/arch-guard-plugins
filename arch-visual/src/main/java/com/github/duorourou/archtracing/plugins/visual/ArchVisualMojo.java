package com.github.duorourou.archtracing.plugins.visual;

import com.github.duorourou.archtracing.plugins.visual.writer.Clazz;
import com.github.duorourou.archtracing.plugins.visual.writer.Project;
import com.github.duorourou.archtracing.plugins.visual.writer.Writer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Mojo(name = "arch-visual", defaultPhase = LifecyclePhase.COMPILE,
        requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class ArchVisualMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject mavenProject;

    @SneakyThrows
    @Override
    public void execute() {
        log.debug("project.getCompileClasspathElements() : {}", mavenProject.getCompileClasspathElements());

        List<Class<?>> allClasses = new CurrentModuleClassesLoader(mavenProject.getCompileClasspathElements()).loadAllClasses();

        log.debug("load all classes {} in path {}", allClasses, mavenProject.getCompileClasspathElements());

        Project project = Project.builder()
                .group(mavenProject.getGroupId())
                .artifact(mavenProject.getArtifactId())
                .clazzes(allClasses.stream()
                        .map(Clazz::from)
                        .collect(toList()))
                .build();
        log.debug("project -> {}", project);

        Writer.builder().outputPath(mavenProject.getBuild().getDirectory())
                .build()
                .write(project);
        log.debug("finished loading.");
//        JUnitCore unitCore = new JUnitCore();
//        unitCore.addListener(new TextListener(new RealSystem()));
//        Result result = unitCore.run(ArchitectureValidator.class);
//        log.info("run architecture validator : {}, {}", result.getRunCount(), result.getFailures());

    }

}

