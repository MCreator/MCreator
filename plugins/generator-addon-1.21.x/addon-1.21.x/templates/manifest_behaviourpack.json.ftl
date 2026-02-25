<#-- @formatter:off -->
<#assign v = settings.get3DigitVersion()>
{
    "format_version": 2,
    "header": {
      "description": "${JavaConventions.escapeStringForJava(settings.getDescription())!""}",
      "name": "${settings.getModName()}",
      "uuid": "${w.getUUID()}",
      "version": [${v[0]}, ${v[1]}, ${v[2]}],
      "min_engine_version": [1, 21, 90]
    },
    "modules": [
      {
        "description": "${JavaConventions.escapeStringForJava(settings.getDescription())!""}",
        "type": "data",
        "uuid": "${w.getUUID("module_data")}",
        "version": [${v[0]}, ${v[1]}, ${v[2]}]
      }
      <#if types["bescripts"]??>,
      {
        "uuid": "${w.getUUID("scripts")}",
        "version": [${v[0]}, ${v[1]}, ${v[2]}],
        "type": "script",
        "language": "javascript",
        "entry": "scripts/${modid}_scripts.js"
      }
      </#if>
    ],
    "dependencies": [
      {
        "uuid": "${w.getUUID("resourcepack")}",
        "version": [${v[0]}, ${v[1]}, ${v[2]}]
      }
      <#if types["bescripts"]??>,
      {
        "module_name": "@minecraft/server",
        "version": "2.2.0"
      }
      </#if>
    ],
    "metadata": {
      "authors": [ "${settings.getAuthor()!""}" ],
      "url": "${settings.getWebsiteURL()}"
    }
}
<#-- @formatter:on -->