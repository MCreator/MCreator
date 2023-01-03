{
    "parent": "item/generated",
    "textures": {
        <#if var_item??>
            "layer0": "${modid}:items/${data.getItemTextureFor(var_item)}"
        <#else>
            "layer0": "${modid}:items/${data.texture}"
        </#if>
    }
    <#if data.modelsMap?has_content>,
    "overrides": [
    <#list data.modelsMap.entrySet() as model>
        {
            "predicate": {
            <#list model.getKey().split(",") as state>
                "${state.split("=")[0]}": ${(state.split("=")[1]?number?float * 1000)?int / 1000}<#sep>,
            </#list>
            },
            "model": "${modid}:item/${registryname}_${model?index}"
        }<#sep>,
    </#list>
    ]
    </#if>
}