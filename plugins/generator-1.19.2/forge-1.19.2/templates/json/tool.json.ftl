{
    "parent": "item/handheld",
    "textures": {
        <#if var_item??>
            "layer0": "${modid}:items/${data.getItemTextureFor(var_item)}"
        <#else>
            "layer0": "${modid}:items/${data.texture}"
        </#if>
    }
    <#if data.getModElement?? && data.getModElement().getTypeString() == "item" && data.getModels()?has_content>,
    "overrides": [
        <#list data.getModels() as model>
        {
            "predicate": {
                <#list model.stateMap.keySet() as property>
                    <#assign value = model.stateMap.get(property)>
                    "${generator.map(property.getName(), "itemproperties")}": ${value?is_boolean?then(value?then("1", "0"), value)}<#sep>,
                </#list>
            },
            "model": "${modid}:item/${registryname}_${model?index}"
        }<#sep>,
        </#list>
    ]
    </#if>
}