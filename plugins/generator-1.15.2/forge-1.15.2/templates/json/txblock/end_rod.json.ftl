<#-- @formatter:off -->
{
    "parent": "block/block",
    "display": {
        "head": {
            "rotation": [ -60, 0, 0 ],
            "translation": [ 0, 5, -9],
            "scale":[ 1, 1, 1]
        },
        "thirdperson_righthand": {
            "rotation": [ 0, 0, 0 ],
            "translation": [ 0, 0, 0],
            "scale": [ 0.375, 0.375, 0.375 ]
        }
    },
    "ambientocclusion": false,
    "textures": {
        "end_rod": "${modid}:blocks${data.textureFront?has_content?then(data.textureFront, data.texture)}",
        <#if data.particleTexture?has_content>"particle": "${modid}:blocks${data.particleTexture}"
        <#else> "particle": "${modid}:blocks${data.textureFront?has_content?then(data.textureFront, data.texture)}"</#if>
    },
    "elements": [
        {
            "from": [ 6, 0, 6 ],
            "to": [ 10, 1, 10 ],
            "faces": {
                "down":  { "uv": [ 6, 6, 2, 2 ], "texture": "#end_rod", "cullface": "down" },
                "up":    { "uv": [ 2, 2, 6, 6 ], "texture": "#end_rod" },
                "north": { "uv": [ 2, 6, 6, 7 ], "texture": "#end_rod" },
                "south": { "uv": [ 2, 6, 6, 7 ], "texture": "#end_rod" },
                "west":  { "uv": [ 2, 6, 6, 7 ], "texture": "#end_rod" },
                "east":  { "uv": [ 2, 6, 6, 7 ], "texture": "#end_rod" }
            }
        },
        {
            "from": [ 7, 1, 7 ],
            "to": [ 9, 16, 9 ],
            "faces": {
                "up":    { "uv": [ 2, 0, 4, 2 ], "texture": "#end_rod", "cullface": "up" },
                "north": { "uv": [ 0, 0, 2, 15 ], "texture": "#end_rod" },
                "south": { "uv": [ 0, 0, 2, 15 ], "texture": "#end_rod" },
                "west":  { "uv": [ 0, 0, 2, 15 ], "texture": "#end_rod" },
                "east":  { "uv": [ 0, 0, 2, 15 ], "texture": "#end_rod" }
            }
        }
    ]
}

<#-- @formatter:on -->