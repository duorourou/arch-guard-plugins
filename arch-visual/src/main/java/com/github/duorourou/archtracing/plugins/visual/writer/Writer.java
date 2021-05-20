package com.github.duorourou.archtracing.plugins.visual.writer;

import com.alibaba.fastjson.JSON;
import lombok.Builder;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

@Builder
public class Writer {
    private static final String dataPath = "/arch-data/";
    private static final String dataFile = "project.json";
    private final String outputPath;

    public void write(Project project) throws IOException {
        String json = JSON.toJSONString(project);
        FileUtils.write(new File(outputPath + dataPath + dataFile), json);
    }
}
