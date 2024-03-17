/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.mcreator.workspace.settings;

import com.google.common.base.CaseFormat;
import net.mcreator.plugin.modapis.ModAPIImplementation;
import net.mcreator.plugin.modapis.ModAPIManager;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.Workspace;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused") public class WorkspaceSettings {

	private final String modid;

	private String modName;
	private String version;
	private String description;
	private String author;
	private String websiteURL;
	private String license;

	private boolean serverSideOnly = false;
	private String updateURL;

	private String modPicture = null;

	public Set<String> requiredMods = new HashSet<>();
	public Set<String> dependencies = new HashSet<>();
	public Set<String> dependants = new HashSet<>();

	private Set<String> mcreatorDependencies = new HashSet<>();

	private String currentGenerator;

	protected String credits;
	protected String modElementsPackage;

	private transient Workspace workspace; // we should never serialize this!!

	private static final Pattern cleanVersionPattern = Pattern.compile("[^0-9.]+");

	public WorkspaceSettings(WorkspaceSettings other) {
		this.modid = other.modid;
		this.modName = other.modName;
		this.version = other.version;
		this.description = other.description;
		this.author = other.author;
		this.license = other.license;
		this.websiteURL = other.websiteURL;
		this.serverSideOnly = other.serverSideOnly;
		this.updateURL = other.updateURL;
		this.modPicture = other.modPicture;
		this.requiredMods = other.requiredMods;
		this.dependencies = other.dependencies;
		this.dependants = other.dependants;
		this.mcreatorDependencies = other.mcreatorDependencies;
		this.currentGenerator = other.currentGenerator;
		this.credits = other.credits;
		this.modElementsPackage = other.modElementsPackage;

		this.workspace = other.workspace;
	}

	public WorkspaceSettings(String modid) {
		this.modid = modid;
	}

	public void setWorkspace(Workspace workspace) {
		this.workspace = workspace;
	}

	public void setModName(String modName) {
		this.modName = modName;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setWebsiteURL(String websiteURL) {
		this.websiteURL = websiteURL;
	}

	public void setServerSideOnly(boolean serverSideOnly) {
		this.serverSideOnly = serverSideOnly;
	}

	public void setUpdateURL(String updateURL) {
		this.updateURL = updateURL;
	}

	public void setModPicture(String modPicture) {
		this.modPicture = modPicture;
	}

	public void setRequiredMods(Set<String> requiredMods) {
		this.requiredMods = requiredMods;
	}

	public void setDependencies(Set<String> dependencies) {
		this.dependencies = dependencies;
	}

	public void setDependants(Set<String> dependants) {
		this.dependants = dependants;
	}

	public void setCredits(String credits) {
		this.credits = credits;
	}

	public void setModElementsPackage(String modElementsPackage) {
		this.modElementsPackage = modElementsPackage;
	}

	public void setMCreatorDependencies(Set<String> mcreatorDependencies) {
		this.mcreatorDependencies = mcreatorDependencies;
	}

	public String getLicense() {
		if (license == null || license.isEmpty())
			return "Not specified";

		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public Set<String> getRequiredMods() {
		List<String> apisSupportedNames = ModAPIManager.getModAPIsForGenerator(
						workspace.getGenerator().getGeneratorName()).stream().filter(ModAPIImplementation::requiredWhenEnabled)
				.map(e -> e.parent().id()).toList();

		return Stream.concat(requiredMods.stream(), mcreatorDependencies.stream().filter(apisSupportedNames::contains))
				.map(String::trim).collect(Collectors.toSet());
	}

	public Set<String> getDependencies() {
		List<String> apisSupportedNames = ModAPIManager.getModAPIsForGenerator(
						workspace.getGenerator().getGeneratorName()).stream().filter(e -> !e.requiredWhenEnabled())
				.map(e -> e.parent().id()).toList();

		return Stream.concat(dependencies.stream(), mcreatorDependencies.stream().filter(apisSupportedNames::contains))
				.map(String::trim).collect(Collectors.toSet());
	}

	public Set<String> getDependants() {
		return dependants.stream().map(String::trim).collect(Collectors.toSet());
	}

	public Set<String> getMCreatorDependencies() {
		List<String> apisSupportedNames = ModAPIManager.getModAPIsForGenerator(
				workspace.getGenerator().getGeneratorName()).stream().map(e -> e.parent().id()).toList();
		return mcreatorDependencies.stream().filter(apisSupportedNames::contains).collect(Collectors.toSet());
	}

	public Set<String> getMCreatorDependenciesRaw() {
		return mcreatorDependencies;
	}

	public String getModID() {
		return modid;
	}

	public String getModName() {
		return modName;
	}

	public String getVersion() {
		return version;
	}

	public String getCleanVersion() {
		String cleanVersion = cleanVersionPattern.matcher(version).replaceAll("");
		if (!cleanVersion.isEmpty())
			return cleanVersion;
		return "0.0.0.0";
	}

	public String getDescription() {
		return description;
	}

	public String getAuthor() {
		return author;
	}

	public boolean isServerSideOnly() {
		return serverSideOnly;
	}

	public String getUpdateURL() {
		return updateURL;
	}

	public String getModPicture() {
		return modPicture;
	}

	public void setCurrentGenerator(String currentGeneratorName) {
		this.currentGenerator = currentGeneratorName;
	}

	public String getCurrentGenerator() {
		if (!currentGenerator.contains("-")) // pre 2020.3 compatibility
			return "forge-" + currentGenerator;
		return currentGenerator;
	}

	public String getModElementsPackage() {
		if (modElementsPackage == null)
			return "net.mcreator." + modid;
		return modElementsPackage;
	}

	public String getJavaModName() {
		try {
			String camelCaseModID = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, modid);
			return StringUtils.uppercaseFirstLetter(camelCaseModID) + "Mod"; // just to be sure
		} catch (Exception ignored) {
		}
		return StringUtils.uppercaseFirstLetter(modid) + "Mod";
	}

	public String getCredits() {
		if (credits == null || credits.isBlank())
			return "Created using mod maker MCreator - https://mcreator.net/about";
		return credits;
	}

	public String getWebsiteURL() {
		if (websiteURL == null || websiteURL.isBlank() || !websiteURL.contains("http") || !websiteURL.contains("://")
				|| !websiteURL.contains("."))
			return MCreatorApplication.SERVER_DOMAIN;
		return websiteURL;
	}

	public int[] get3DigitVersion() {
		int[] ver3 = { 0, 0, 0 };
		try {
			String[] parts = version.split("\\.");
			for (int i = 0; i < Math.min(parts.length, ver3.length); i++) {
				String digit = parts[i].replaceAll("[^\\d]", "");
				ver3[i] = Integer.parseInt(digit);
			}
		} catch (Exception ignored) {
		}
		return ver3;
	}

}
