package com.github.duorourou.archtracing.plugins.visual;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Slf4j
public class CurrentModuleClassesLoader {

    private static final String PACKAGE_SEPARATOR = ".";
    private static final String EMPTY_STRING = "";
    private static final String CLASS_FILE_EXTENSION = ".class";
    private static final String URL_PROTOCOL_FILE_STRING = "file:";
    private static final String URL_PROTOCOL_JAR_STRING = "jar:";
    private static final String URL_PROTOCOL_JAR_END_STRING = "!/";
    private final ClassLoader classLoader;
    private final List<String> compileDirs;

    public CurrentModuleClassesLoader(List<String> compileDirs) {
        this.compileDirs = compileDirs;
        URL[] urls = compileDirs.stream()
                .filter(dir -> !StringUtils.isBlank(dir))
                .map(this::toUrl)
                .filter(Objects::nonNull)
                .toArray(URL[]::new);
        log.debug("Initialize classLoader with URLs : {}", Arrays.stream(urls));
        this.classLoader = new URLClassLoader(urls);
    }

    private URL toUrl(String dir) {
        String fullPath;
        if (dir.endsWith(".jar")) {
            fullPath = URL_PROTOCOL_JAR_STRING + URL_PROTOCOL_FILE_STRING + parseDir(dir) + URL_PROTOCOL_JAR_END_STRING;
        } else {
            fullPath = URL_PROTOCOL_FILE_STRING + parseDir(dir) + File.separator;
        }

        try {
            log.debug("convert the path {} to a url.", fullPath);
            return new URL(fullPath);
        } catch (MalformedURLException e) {
            log.warn("Could not format path {} as url.", fullPath, e);
        }
        return null;
    }

    private String parseDir(String dir) {

        if (!StringUtils.startsWith(dir, File.separator)) {
            return File.separator + dir;
        }
        return dir;
    }

    public List<Class<?>> loadAllClasses() {
        return compileDirs.stream()
                .filter(dir -> !dir.endsWith(".jar"))
                .filter(dir -> new File(dir).exists())
                .flatMap(this::loadClassesOfDir)
                .collect(toList());
    }

    private Stream<? extends Class<?>> loadClassesOfDir(String dir) {
        return FileUtils.listFiles(new File(dir), new String[]{"class"}, Boolean.TRUE)
                .stream()
                .map(file -> toClass(dir, file))
                .filter(Objects::nonNull);
    }

    private Class<?> toClass(String dir, File file) {
        String className = file.getName().replace(CLASS_FILE_EXTENSION, EMPTY_STRING);
        String packageName = getPackage(file.getPath(), dir);
        String fullClassName = String.join(PACKAGE_SEPARATOR, packageName, className);
        log.info("origin file {} to class {}", file.getPath(), fullClassName);
        try {
            return classLoader.loadClass(fullClassName);
        } catch (ClassNotFoundException e) {
            log.error("load the file {} as class {} in package {} failed.", file.getPath(), fullClassName, packageName, e);
        }
        return null;
    }

    private String getPackage(String file, String dir) {
        return new File(file).getParent()
                .replaceFirst(dir, EMPTY_STRING)
                .replaceFirst(File.separator, EMPTY_STRING)
                .replaceAll(File.separator, PACKAGE_SEPARATOR);
    }

}
