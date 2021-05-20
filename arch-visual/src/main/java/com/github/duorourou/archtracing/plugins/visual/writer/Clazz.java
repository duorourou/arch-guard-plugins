package com.github.duorourou.archtracing.plugins.visual.writer;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@Slf4j
public class Clazz {
    private String name;
    private Annotation[] annotations;
    private String packageName;
    private Method[] methods;
    private SuperClass superClass;
    private List<Interface> interfaces;
    private Field[] fields;

    public static Clazz from(Class<?> c) {
        try {
            return Clazz.builder()
                    .name(c.getName())
                    .annotations(c.getAnnotations())
                    .packageName(c.getPackage().getName())
                    .methods(c.getDeclaredMethods())
                    .fields(c.getDeclaredFields())
                    .superClass(SuperClass.from(c.getSuperclass()))
                    .interfaces(Interface.from(c.getInterfaces()))
                    .build();
        } catch (Exception e) {
            log.error("to clazz failed -> {}", e.getMessage(), e);
        }
        return null;
    }

    @Getter
    @Value
    public static class SuperClass {
        String name;
        String packageName;

        private SuperClass(String name, String packageName) {
            this.name = name;
            this.packageName = packageName;
        }

        public static SuperClass from(Class<?> superClass) {
            if (superClass == null) {
                return null;
            }
            return new SuperClass(superClass.getName(), superClass.getPackage().getName());
        }
    }

    @Getter
    @Value
    public static class Interface {
        String name;
        String packageName;

        private Interface(String name, String packageName) {
            this.name = name;
            this.packageName = packageName;
        }

        public static List<Interface> from(Class<?>[] interfaceClass) {
            if (interfaceClass == null) {
                return null;
            }
            return Arrays.stream(interfaceClass)
                    .map(i -> new Interface(i.getName(), i.getPackage().getName()))
                    .collect(Collectors.toList());
        }
    }
}
