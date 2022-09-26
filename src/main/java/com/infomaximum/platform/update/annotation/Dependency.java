package com.infomaximum.platform.update.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({})
@Retention(RUNTIME)
public @interface Dependency {

    String componentUUID();
    String version();
    boolean optional() default false;
}
