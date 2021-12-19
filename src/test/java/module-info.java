module MCreator.test {
	exports net.mcreator.unit.java;
	exports net.mcreator.unit.io;
	exports net.mcreator.unit.util;
	exports net.mcreator.unit.vcs.diff;
	exports net.mcreator.integration;
	exports net.mcreator.integration.generator;
	exports net.mcreator.integration.ui;

	requires MCreator;

	requires org.junit.jupiter.api;
	requires org.apache.logging.log4j;
	requires gradle.tooling.api;
	requires com.google.gson;
	requires java.desktop;
}