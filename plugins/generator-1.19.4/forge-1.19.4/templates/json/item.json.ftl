{
    "parent": "item/generated",
    "textures": {
        <#if var_item??>
            "layer0": "${modid}:item/${data.getItemTextureFor(var_item)}"
        <#else>
            "layer0": "${modid}:item/${data.texture}"
        </#if>
    }
    <#if data.getModElement?? && data.getModElement().getTypeString() == "item" && data.getModels()?has_content>,
    "overrides": [
        <#list data.getModels() as model>
        {
            "predicate": {
                <#list model.stateMap.keySet() as property>
                    "${generator.map(property.getName(), "itemproperties")}": ${model.stateMap.get(property)}<#sep>,
                </#list>
            },
            "model": "${modid}:item/${registryname}_${model?index}"
        }<#sep>,
        </#list>
    ]
    </#if>
}