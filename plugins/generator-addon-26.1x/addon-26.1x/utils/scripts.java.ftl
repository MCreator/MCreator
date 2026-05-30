<#-- @formatter:off -->
<#macro optionalDependencies requiredDependencies dependencies={}>
<@javacompress>
    <#assign deps_filtered = [] />
    <#list requiredDependencies as dependency>
        <#list dependencies as name, value>
            <#if dependency.getName() == name>
                <#assign deps_filtered += ["const " + name + " = " + value + ";"] />
            </#if>
        </#list>
    </#list>

    <#list deps_filtered as value>${value}<#sep>${"\n"}</#list>
</@javacompress>
</#macro>
<#-- @formatter:on -->