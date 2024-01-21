modLoader="javafml"
loaderVersion="[2,)"
license="${settings.getLicense()}"

[[mods]]
modId="${settings.getModID()}"
version="${settings.getCleanVersion()}"
displayName="${settings.getModName()}"
<#if settings.getUpdateURL()?has_content>
updateJSONURL="${settings.getUpdateURL()}"
</#if>
<#if settings.getWebsiteURL()?has_content>
displayURL="${settings.getWebsiteURL()}"
</#if>
<#if settings.getModPicture()?has_content>
logoFile="logo.png"
</#if>
<#if settings.getCredits()?has_content>
credits="${settings.getCredits()}"
</#if>
<#if settings.getAuthor()?has_content>
authors="${settings.getAuthor()}"
</#if>
<#if settings.getDescription()?has_content>
description="${settings.getDescription()}"
</#if>
<#if settings.isServerSideOnly()>
displayTest="IGNORE_SERVER_VERSION"
</#if>

[[dependencies.${settings.getModID()}]]
    modId="minecraft"
    type="required"
    versionRange="[${generator.getGeneratorMinecraftVersion()}]"
    ordering="NONE"
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