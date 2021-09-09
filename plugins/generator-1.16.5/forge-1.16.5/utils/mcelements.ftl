<#function toResourceLocation string>
    <#if string?matches('"[^+]*"')>
        <#return "new ResourceLocation(" + string?lower_case + ")">
    <#else>
        <#return "new ResourceLocation((" + string + ").toLowerCase(java.util.Locale.ENGLISH))">
    </#if>
</#function>