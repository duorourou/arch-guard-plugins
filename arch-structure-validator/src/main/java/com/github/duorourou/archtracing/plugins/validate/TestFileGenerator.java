package com.github.duorourou.archtracing.plugins.validate;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Objects;

import static com.github.duorourou.archtracing.plugins.validate.Configuration.ROOT_PACKAGE_PLACEHOLDER;

@Slf4j
public class TestFileGenerator {

    private final Configuration configuration;
    private final String rootPackageDirectory;
    private final String toGeneratedTestSourceDirectory;
    private final File templateResourceDirRoot;

    private TestFileGenerator(Configuration configuration,
                              String rootPackageDirectory,
                              String toGeneratedTestSourceDirectory,
                              File templateResourceDirRoot) {
        this.configuration = configuration;
        this.rootPackageDirectory = rootPackageDirectory;
        this.toGeneratedTestSourceDirectory = toGeneratedTestSourceDirectory;
        this.templateResourceDirRoot = templateResourceDirRoot;
        log.info("rootPackageDirectory {}", rootPackageDirectory);
        log.info("generatedTestFileDirectory {}", toGeneratedTestSourceDirectory);
        log.info("templateResourceDirRoot {}", templateResourceDirRoot);
        log.info("configuration {}", configuration);
    }

    static TestFileGenerator of(String generatedTestFileDirectory,
                                Configuration configuration) {
        String rootPackageAbsolutePath = configuration.getRootPackageName().
                replaceAll("\\.", File.separator);
        return new TestFileGenerator(configuration,
                rootPackageAbsolutePath,
                generatedTestFileDirectory,
                templateResourceDir());
    }


    public void generate() {
        if (templateResourceDirRoot == null) {
            return;
        }

        File testRoot = new File(toGeneratedTestSourceDirectory);
        if (!testRoot.exists() && testRoot.mkdirs()) {
            log.error("could not create generate test source directory.");
            return;
        }

        Collection<File> templates = FileUtils.listFiles(templateResourceDirRoot,
                new String[]{"java"},
                Boolean.TRUE);
        templates.forEach(
                t -> {
                    File file = getGenerateFile(t);
                    try {
                        String content = getGenerateContent(t);
                        file.getParentFile().mkdirs();
                        FileUtils.write(file, content);
                    } catch (Exception e) {
                        log.error("Error occurred when generate a test file {} with error message : {}", file, e.getMessage());
                        log.debug("Error details : ", e);
                    }

                }
        );
    }

    private File getGenerateFile(File originTemplate) {
        log.info("origin file info : name -> {}, path -> {}, absolutePath -> {}", originTemplate.getName(),
                originTemplate.getPath(),
                originTemplate.getAbsolutePath());
        String destinationDirector = toGeneratedTestSourceDirectory + File.separator + rootPackageDirectory +
                File.separator +
                originTemplate.getPath() + File.separator + originTemplate.getName();
        return new File(destinationDirector);
    }

    private String getGenerateContent(File originTemplate) {

        try {
            String template = FileUtils.readFileToString(originTemplate, "UTF-8");

            return template.replaceAll(ROOT_PACKAGE_PLACEHOLDER,
                    configuration.getRootPackageName());

        } catch (IOException e) {
            log.error("Could not read template {}, generating skipped.", originTemplate.getName());
            log.debug("Error occurred when read template {}", originTemplate.getName(), e);
        }
        return null;
    }

    private static File templateResourceDir() {
        try {
            return Paths.get(Objects.requireNonNull(ArchValidateMojo.class.getClassLoader()
                    .getResource("OnionArchitectureValidator.java"))
                    .toURI())
                    .toFile();
        } catch (URISyntaxException e) {
            log.error("Could not find the templates directory. error: {}", e.getMessage());
            log.debug("Error details : ", e);
        }
        return null;
    }
}
