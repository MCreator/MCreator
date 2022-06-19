<#function mappedSingleTexture texture type modid>
    <#if texture?starts_with("minecraft:")>
        <#return texture>
    <#else>
        <#return modid + ":" + type + "/" + texture>
    </#if>
</#function>

<#function mappedElseTexture texture otherTexture type modid>
    <#if texture?has_content>
        <#return mappedSingleTexture(texture, type, modid)>
    <#else>
        <#return mappedSingleTexture(otherTexture, type, modid)>
    </#if>
</#function>

<#function mappedDoubleElseTexture texture otherTexture lastTexture type modid>
    <#if texture?has_content>
        <#return mappedSingleTexture(texture, type, modid)>
    <#else>
        <#return mappedElseTexture(otherTexture, lastTexture, type, modid)>
    </#if>
</#function>

<#function mappedElseFileTexture texture otherTexture type otherType modid>
    <#if texture?has_content>
        <#return mappedSingleTexture(texture, type, modid)>
    <#else>
        <#return mappedSingleTexture(otherTexture, otherType, modid)>
    </#if>
</#function>

<#function mappedDoubleTexture texture defaultTexture type modid>
    <#if texture?starts_with("minecraft:")>
        <#return texture>
    <#else>
        <#return modid + ":" + type + "/" + defaultTexture>
    </#if>
</#function>