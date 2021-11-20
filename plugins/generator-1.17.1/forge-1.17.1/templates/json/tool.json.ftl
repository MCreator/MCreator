{
    "parent": "item/handheld",
    "textures": {
        "layer0": "${modid}:items/${data.texture}"
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