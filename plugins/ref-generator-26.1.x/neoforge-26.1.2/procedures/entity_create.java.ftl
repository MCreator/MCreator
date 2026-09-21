<#assign entity = generator.map(field$entity, "entities", 1)!"null">
(<#if entity != "null">world instanceof Level _level ? ${entity}.create(_level, EntitySpawnReason.EVENT) : </#if>null)