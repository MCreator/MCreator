<#function mappedSingleTexture texture type modid>
    <#if texture?starts_with("minecraft:")>
        <#return texture>
    <#else>
        <#return modid + ":" + type + "/" + texture>
    </#if>
</#function>

<#function mappedDoubleTexture texture defaultTexture type modid>
    <#if texture?starts_with("minecraft:")>
        <#return texture>
    <#else>
        <#return modid + ":" + type + "/" + defaultTexture>
    </#if>
</#function>