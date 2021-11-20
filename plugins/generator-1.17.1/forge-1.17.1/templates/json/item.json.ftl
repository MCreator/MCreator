{
    "parent": "item/generated",
    "textures": {
        <#if var_item?? && var_item=="helmet">
            "layer0": "${modid}:items/${data.textureHelmet}"
        <#elseif var_item?? && var_item=="body">
            "layer0": "${modid}:items/${data.textureBody}"
        <#elseif var_item?? && var_item=="leggings">
            "layer0": "${modid}:items/${data.textureLeggings}"
        <#elseif var_item?? && var_item=="boots">
            "layer0": "${modid}:items/${data.textureBoots}"
        <#else>
            "layer0": "${modid}:items/${data.texture}"
        </#if>
    }
    <#if data.modelsMap?has_content>,
    "overrides": [
    <#list data.modelsMap as state, model>
        {
            "predicate": {
            <#list state as property, value>
                "${property}": ${value}<#sep>,
            </#list>
            },
            "model": "${modid}:item/${model.modelName}_${model?index}"
        }<#sep>,
    </#list>
    ]
    </#if>
}