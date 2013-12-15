package com.github.bindernews.fasttitles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ASubcommand {

	String name();
	String description();
	String permission();
	String usage();
	String[] aliases();
	
}
