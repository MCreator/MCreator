{
    "parent": "item/handheld",
    "textures": {
        <#if var_item??>
            "layer0": "${data.getItemTextureFor(var_item).format("%s:item/%s")}"
        <#else>
            "layer0": "${data.texture.format("%s:item/%s")}"
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