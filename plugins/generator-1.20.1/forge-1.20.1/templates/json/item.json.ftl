{
    "parent": "item/generated",
    "textures": {
        <#if var_item??>
            "layer0": "${modid}:item/${data.getItemTextureFor(var_item)}"
        <#else>
            "layer0": "${modid}:item/${data.texture}"
        </#if>
    }
    <#if data.getModels?? && data.getModels()?has_content>,
    "overrides": [
        <#list data.getModels() as model>
        {
            "predicate": {
                <#list model.stateMap.keySet() as property>
                    <#assign value = model.stateMap.get(property)>
                    "${generator.map(property.getPrefixedName(registryname + "_"), "itemproperties")}": ${value?is_boolean?then(value?then("1", "0"), value)}<#sep>,
                </#list>
            },
            "model": "${modid}:item/${registryname}_${model?index}"
        }<#sep>,
        </#list>
    ]
    </#if>
}