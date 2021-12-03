{
    "parent": "item/handheld",
    "textures": {
        "layer0": "${modid}:items/${data.texture}"
    }
    <#if data.modelsMap?has_content>,
    "overrides": [
    <#list data.modelsMap as model>
        {
            "predicate": {
            <#list model.getKey().entrySet() as state>
                "${state.getKey()}": ${state.getValue()}<#sep>,
            </#list>
            },
            "model": "${modid}:item/${model.getValue().modelName}_${model?index}"
        }<#sep>,
    </#list>
    ]
    </#if>
}