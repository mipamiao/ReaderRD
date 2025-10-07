package com.mipa.common.annotation;


import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface PageCacheRoot {
	String fieldName() default "";
	int ttl() default 300;
}
