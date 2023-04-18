{
    "parent": "item/generated",
    "textures": {
        <#if var_item??>
            "layer0": "${modid}:item/${data.getItemTextureFor(var_item)}"
        <#else>
            "layer0": "${modid}:item/${(item??)?then(item, data).texture}"
        </#if>
    }
    <#if data?? && data.getModElement().getTypeString() == "item" && data.filterModels()?has_content>,
    "overrides": [
        <#list data.filterModels().entrySet() as model>
        {
            "predicate": {
            <#list model.getKey().split(",") as state>
                <#assign prop = generator.map(state.split("=")[0], "itemproperties")>
                "${prop}": ${(state.split("=")[1]?number?float * 1000)?int / 1000}<#sep>,
            </#list>
            },
            "model": "${modid}:item/${registryname}_${model?index}"
        }<#sep>,
        </#list>
    ]
    </#if>
}