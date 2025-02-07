<#-- @formatter:off -->
<#assign v = settings.get3DigitVersion()>
{
    "format_version": 2,
    "header": {
      "description": "${JavaConventions.escapeStringForJava(settings.getDescription())!""}",
      "name": "${settings.getModName()}",
      "uuid": "${w.getUUID("resourcepack")}",
      "version": [${v[0]}, ${v[1]}, ${v[2]}],
      "min_engine_version": [1, 21, 50]
    },
    "modules": [
      {
        "description": "${JavaConventions.escapeStringForJava(settings.getDescription())!""}",
        "type": "resources",
        "uuid": "${w.getUUID("module_resources")}",
        "version": [${v[0]}, ${v[1]}, ${v[2]}]
      }
    ],
    "metadata": {
        "authors": [ "${settings.getAuthor()!""}" ],
        "url": "${settings.getWebsiteURL()}"
    }
}
<#-- @formatter:on -->