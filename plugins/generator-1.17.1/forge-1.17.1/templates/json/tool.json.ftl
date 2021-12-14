{
    "parent": "item/handheld",
    "textures": {
        "layer0": "${modid}:items/${data.texture}"
    }
    <#if data.modelsMap?has_content>,
    "overrides": [
    <#list data.modelsMap.entrySet() as model>
        {
            "predicate": {
            <#list model.getKey().entrySet() as state>
                "${state.getKey()}": ${(state.getValue() * 1000)?int / 1000}<#sep>,
            </#list>
            },
            "model": "${modid}:item/${registryname}_${model?index}"
        }<#sep>,
    </#list>
    ]
    </#if>
}