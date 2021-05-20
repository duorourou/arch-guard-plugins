package com.github.duorourou.archtracing.plugins.visual.writer;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class Project {

    private String group;
    private String artifact;
    private List<Clazz> clazzes;

}
