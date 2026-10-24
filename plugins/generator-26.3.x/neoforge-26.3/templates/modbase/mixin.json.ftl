{
  "required": true,
  "package": "${package}.mixin",
  "compatibilityLevel": "JAVA_25",
  "mixins": [
	<#list generator.getFragments("mixin") as mixin>"${mixin}"<#sep>,</#list>
  ],
  "client": [
  ],
  "injectors": {
    "defaultRequire": 1
  },
  "minVersion": "0.8.4"
}