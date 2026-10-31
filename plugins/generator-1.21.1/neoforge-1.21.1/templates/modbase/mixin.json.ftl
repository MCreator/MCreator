{
  "required": true,
  "package": "${package}.mixin",
  "compatibilityLevel": "JAVA_21",
  "mixins": [
	<#list fragments.mixin as mixin>"${mixin}"<#sep>,</#list>
  ],
  "client": [
  ],
  "injectors": {
    "defaultRequire": 1
  },
  "minVersion": "0.8.4"
}