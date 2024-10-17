[{
  "modid": "${settings.getModID()}",
  "name": "${settings.getModName()}",
  "description": "${settings.getDescription()!""}",
  "version": "${settings.getVersion()}",
"credits": "${settings.getCredits()}",
<#if settings.getModPicture()??>
    "logoFile": "assets/${modid}/textures/${settings.getModPicture()}.png",
<#else>
  "logoFile": "",
</#if>
  "mcversion": "1.12.2",
  "url": "${settings.getWebsiteURL()}",
  "updateUrl": "${settings.getUpdateURL()!""}",
<#if settings.getAuthor()?has_content>
  "authorList": [ "${settings.getAuthor()!""}" ],
</#if>
  "dependencies": [
<#if !settings.isDisableForgeVersionCheck()>
    "forge@[${generator.getGeneratorBuildFileVersion()}]"<#if settings.getDependencies()?has_content>,</#if>
</#if>
<#list settings.getDependencies() as e>
      "${e}"<#if e?has_next>,</#if>
</#list>
  ],
  "dependants": [
<#list settings.getDependants() as e>
      "${e}"<#if e?has_next>,</#if>
</#list>
  ],
  "requiredMods": [
<#list settings.getRequiredMods() as e>
      "${e}"<#if e?has_next>,</#if>
</#list>
  ],
  "useDependencyInformation": "true"
}]