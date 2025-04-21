{
    <#if parent??><#assign guiTexture = parent.guiTexture><#elseif data.guiTexture??><#assign guiTexture = data.guiTexture></#if>
    <#if guiTexture?has_content>
    "loader": "neoforge:separate_transforms",
    "base": { <@modelDefinition/> },
    "perspectives": {
        "gui": {
            "parent": "item/generated",
            "textures": {
                "layer0": "${guiTexture.format("%s:item/%s")}"
            }
        },
        "fixed": {
            "parent": "item/generated",
            "textures": {
                "layer0": "${guiTexture.format("%s:item/%s")}"
            }
        },
		"ground": {
			"parent": "item/generated",
			"textures": {
				"layer0": "${guiTexture.format("%s:item/%s")}"
			}
		}
    }
    <#else>
    <@modelDefinition/>
    </#if>
    <#macro modelDefinition>
    "parent": "item/generated",
    "textures": {
      "layer0": "${data.texture.format("%s:item/%s")}"
    },
    "display": {
        "thirdperson_righthand": {
            "rotation": [-80, 260, -40],
            "translation": [-1, -2, 2.5],
            "scale": [1, 1, 1]
        },
        "thirdperson_lefthand": {
            "rotation": [-80, -280, 40],
            "translation": [-1, -2, 2.5],
            "scale": [1, 1, 1]
        },
        "firstperson_righthand": {
            "rotation": [0, -90, 25],
            "translation": [1.13, 3.2, 1.13],
            "scale": [1, 1, 1]
        },
        "firstperson_lefthand": {
            "rotation": [0, 90, -25],
            "translation": [1.13, 3.2, 1.13],
            "scale": [1, 1, 1]
        }
    }
    </#macro>
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