module MCreator {
	requires java.base;
	requires java.xml;
	requires java.desktop;
	requires org.apache.logging.log4j;
	requires freemarker;
	requires com.google.gson;
	requires gradle.tooling.api;
	requires org.apache.commons.io;
	requires org.eclipse.jgit;
	requires languagesupport;
	requires javafx.graphics;
	requires java.management;
	requires yamlbeans;
	requires java.discord.rpc;
	requires univocity.parsers;
	requires org.apache.commons.text;
	requires org.apache.commons.lang3;
	requires rsyntaxtextarea;
	requires org.commonmark.ext.gfm.tables;
	requires org.commonmark.ext.autolink;
	requires org.commonmark;
	requires roaster.api;
	requires javafx.swing;
	requires javafx.web;
	requires jdk.jsobject;
	requires junidecode;
	requires javassist;
	requires jnbt;
	requires foxtrot.core;
	requires autocomplete;
	requires paulscode.soundsystem;
	requires paulscode.libraryjavasound;
	requires paulscode.codecjorbis;
	requires obj;
	requires balloontip;
	requires jdk.management;
	requires jsr305;
	requires com.google.common;
	requires org.reflections;

	opens net.mcreator.ui.res;
	opens org.fife.rsta.ac.java.img;

	exports net.mcreator.ui.laf;

	exports net.mcreator.generator.setup to MCreator.test;
	exports net.mcreator.gradle to MCreator.test;
	exports net.mcreator.workspace to MCreator.test;
}