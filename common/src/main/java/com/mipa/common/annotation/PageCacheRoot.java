package com.mipa.common.annotation;


import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface PageCacheRoot {
	String fieldName() default "";
	int ttl() default 300;


	String pageNumberParamIndex();
	String pageSizeParamIndex();

	String idName() default "id";
	String extraFieldInfo() default "default";
}
