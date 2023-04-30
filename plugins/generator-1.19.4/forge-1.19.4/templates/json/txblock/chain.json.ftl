<#-- @formatter:off -->
{
	"parent": "block/block",
	"textures": {
		"all": "${modid}:block/${data.texture}",
		"particle": "${modid}:block/${data.particleTexture?has_content?then(data.particleTexture, data.texture)}"
	},
	"elements": [
		{
			"from": [ 6.5, 0, 8 ],
			"to": [ 9.5, 16, 8 ],
			"rotation": { "origin": [ 8, 8, 8 ], "axis": "y", "angle": 45},
			"shade": false,
			"faces": {
				"north": { "uv": [ 3, 0, 0, 16 ], "texture": "#all" },
				"south": { "uv": [ 0, 0, 3, 16 ], "texture": "#all" }
			}
		},
		{
			"from": [ 8, 0, 6.5 ],
			"to": [ 8, 16, 9.5 ],
			"rotation": { "origin": [ 8, 8, 8 ], "axis": "y", "angle": 45},
			"shade": false,
			"faces": {
				"west": { "uv": [ 6, 0, 3, 16 ], "texture": "#all" },
				"east": { "uv": [ 3, 0, 6, 16 ], "texture": "#all" }
			}
		}
	]
}
<#-- @formatter:on -->