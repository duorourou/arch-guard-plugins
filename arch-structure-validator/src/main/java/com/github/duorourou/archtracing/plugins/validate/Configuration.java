package com.github.duorourou.archtracing.plugins.validate;

public class Configuration {

    public static final String ROOT_PACKAGE_PLACEHOLDER = "@@{{rootPackageName}}@@";

    private final String rootPackageName;

    public Configuration(String rootPackageName) {
        this.rootPackageName = rootPackageName;
    }

    public String getRootPackageName() {
        return rootPackageName;
    }
}
