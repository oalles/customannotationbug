package com.customannotationbug.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface EventRoot {

	@AliasFor(annotation = Document.class, attribute = "collection")
	String collection() default "";

	@AliasFor(annotation = Document.class, attribute = "language")
	String language() default "";

}
