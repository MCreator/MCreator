<@addTemplate file="utils/entity/entity_create.java.ftl"/>
<#assign entity = generator.map(field$entity, "entities", 1)!"null">
((world instanceof Level _level) ? createStaticEntity(<#if entity != "null">${entity}<#else>null</#if>, _level) : null)