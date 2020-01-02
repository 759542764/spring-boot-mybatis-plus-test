package com.github.mybatisPlus.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import com.github.mybatisPlus.config.DynamicDataSourcePlusAutoConfiguration;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({DynamicDataSourcePlusAutoConfiguration.class})
public @interface EnableDynamicRouteDataSource {

}
