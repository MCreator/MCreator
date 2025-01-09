modLoader="javafml"
loaderVersion="[4,)"
license="${JavaConventions.escapeStringForJava(settings.getLicense())}"

[[mods]]
modId="${settings.getModID()}"
version="${settings.getCleanVersion()}"
displayName="${JavaConventions.escapeStringForJava(settings.getModName())}"
<#if settings.getUpdateURL()?has_content>
updateJSONURL="${JavaConventions.escapeStringForJava(settings.getUpdateURL())}"
</#if>
<#if settings.getWebsiteURL()?has_content>
displayURL="${JavaConventions.escapeStringForJava(settings.getWebsiteURL())}"
</#if>
<#if settings.getModPicture()?has_content>
logoFile="logo.png"
</#if>
<#if settings.getCredits()?has_content>
credits="${JavaConventions.escapeStringForJava(settings.getCredits())}"
</#if>
<#if settings.getAuthor()?has_content>
authors="${JavaConventions.escapeStringForJava(settings.getAuthor())}"
</#if>
<#if settings.getDescription()?has_content>
description="${JavaConventions.escapeStringForJava(settings.getDescription())}"
</#if>
<#if settings.isServerSideOnly()>
displayTest="IGNORE_SERVER_VERSION"
</#if>
<#if w.getGElementsOfType('livingentity')?filter(e -> e.mobBehaviourType == "Raider")?size != 0>
enumExtensions="META-INF/enumextensions.json"
</#if>

# Start of user code block mod configuration
# End of user code block mod configuration

[[dependencies.${settings.getModID()}]]
    modId="neoforge"
    type="required"
    versionRange="[${generator.getGeneratorBuildFileVersion()},)"
    ordering="AFTER"
    side="BOTH"

[[dependencies.${settings.getModID()}]]
    modId="minecraft"
    type="required"
    versionRange="[${generator.getGeneratorMinecraftVersion()}]"
    ordering="AFTER"
    side="BOTH"

<#list settings.getRequiredMods() as e>
[[dependencies.${settings.getModID()}]]
    modId="${e}"
    type="required"
    versionRange="[0,)"
    ordering="NONE"
    side="BOTH"
</#list>

<#list settings.getDependencies() as e>
[[dependencies.${settings.getModID()}]]
    modId="${e}"
    type="optional"
    versionRange="[0,)"
    ordering="NONE"
    side="BOTH"
</#list>

<#list settings.getDependants() as e>
[[dependencies.${settings.getModID()}]]
    modId="${e}"
    type="optional"
    versionRange="[0,)"
    ordering="NONE"
    side="BOTH"
</#list>

# Start of user code block dependencies configuration
# End of user code block dependencies configuration