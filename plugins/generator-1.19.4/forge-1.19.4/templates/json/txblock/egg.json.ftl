<#-- @formatter:off -->
{
	"parent": "block/block",
	"ambientocclusion": false,
	"textures": {
		"all": "${modid}:block/${data.texture}",
		"particle": "${modid}:block/${data.particleTexture?has_content?then(data.particleTexture, data.texture)}"
	},
	"elements": [
		{
			"from": [ 6, 15, 6 ],
			"to": [ 10, 16, 10 ],
			"faces": {
				"down":  { "uv": [ 6,  6, 10, 10 ], "texture": "#all" },
				"up":    { "uv": [ 6,  6, 10, 10 ], "texture": "#all" },
				"north": { "uv": [ 6, 15, 10, 16 ], "texture": "#all" },
				"south": { "uv": [ 6, 15, 10, 16 ], "texture": "#all" },
				"west":  { "uv": [ 6, 15, 10, 16 ], "texture": "#all" },
				"east":  { "uv": [ 6, 15, 10, 16 ], "texture": "#all" }
			}
		},
		{
			"from": [ 5, 14, 5 ],
			"to": [ 11, 15, 11 ],
			"faces": {
				"down":  { "uv": [ 5,  5, 11, 11 ], "texture": "#all" },
				"up":    { "uv": [ 5,  5, 11, 11 ], "texture": "#all" },
				"north": { "uv": [ 5, 14, 11, 15 ], "texture": "#all" },
				"south": { "uv": [ 5, 14, 11, 15 ], "texture": "#all" },
				"west":  { "uv": [ 5, 14, 11, 15 ], "texture": "#all" },
				"east":  { "uv": [ 5, 14, 11, 15 ], "texture": "#all" }
			}
		},
		{
			"from": [ 5, 13, 5 ],
			"to": [ 11, 14, 11 ],
			"faces": {
				"down":  { "uv": [ 4,  4, 12, 12 ], "texture": "#all" },
				"up":    { "uv": [ 4,  4, 12, 12 ], "texture": "#all" },
				"north": { "uv": [ 4, 13, 12, 14 ], "texture": "#all" },
				"south": { "uv": [ 4, 13, 12, 14 ], "texture": "#all" },
				"west":  { "uv": [ 4, 13, 12, 14 ], "texture": "#all" },
				"east":  { "uv": [ 4, 13, 12, 14 ], "texture": "#all" }
			}
		},
		{
			"from": [ 3, 11, 3 ],
			"to": [ 13, 13, 13 ],
			"faces": {
				"down":  { "uv": [ 3,  3, 13, 13 ], "texture": "#all" },
				"up":    { "uv": [ 3,  3, 13, 13 ], "texture": "#all" },
				"north": { "uv": [ 3, 11, 13, 13 ], "texture": "#all" },
				"south": { "uv": [ 3, 11, 13, 13 ], "texture": "#all" },
				"west":  { "uv": [ 3, 11, 13, 13 ], "texture": "#all" },
				"east":  { "uv": [ 3, 11, 13, 13 ], "texture": "#all" }
			}
		},
		{
			"from": [ 2, 8, 2 ],
			"to": [ 14, 11, 14 ],
			"faces": {
				"down":  { "uv": [ 2, 2, 14, 14 ], "texture": "#all" },
				"up":    { "uv": [ 2, 2, 14, 14 ], "texture": "#all" },
				"north": { "uv": [ 2, 8, 14, 11 ], "texture": "#all" },
				"south": { "uv": [ 2, 8, 14, 11 ], "texture": "#all" },
				"west":  { "uv": [ 2, 8, 14, 11 ], "texture": "#all" },
				"east":  { "uv": [ 2, 8, 14, 11 ], "texture": "#all" }
			}
		},
		{
			"from": [ 1, 3, 1 ],
			"to": [ 15, 8, 15 ],
			"faces": {
				"down":  { "uv": [ 1, 1, 15, 15 ], "texture": "#all" },
				"up":    { "uv": [ 1, 1, 15, 15 ], "texture": "#all" },
				"north": { "uv": [ 1, 3, 15,  8 ], "texture": "#all" },
				"south": { "uv": [ 1, 3, 15,  8 ], "texture": "#all" },
				"west":  { "uv": [ 1, 3, 15,  8 ], "texture": "#all" },
				"east":  { "uv": [ 1, 3, 15,  8 ], "texture": "#all" }
			}
		},
		{
			"from": [ 2, 1, 2 ],
			"to": [ 14, 3, 14 ],
			"faces": {
				"down":  { "uv": [ 2, 2, 14, 14 ], "texture": "#all" },
				"up":    { "uv": [ 2, 2, 14, 14 ], "texture": "#all" },
				"north": { "uv": [ 2, 1, 14,  3 ], "texture": "#all" },
				"south": { "uv": [ 2, 1, 14,  3 ], "texture": "#all" },
				"west":  { "uv": [ 2, 1, 14,  3 ], "texture": "#all" },
				"east":  { "uv": [ 2, 1, 14,  3 ], "texture": "#all" }
			}
		},
		{
			"from": [ 3, 0, 3 ],
			"to": [ 13, 1, 13 ],
			"faces": {
				"down":  { "uv": [ 3, 3, 13, 13 ], "texture": "#all" },
				"up":    { "uv": [ 3, 3, 13, 13 ], "texture": "#all" },
				"north": { "uv": [ 3, 0, 13,  1 ], "texture": "#all" },
				"south": { "uv": [ 3, 0, 13,  1 ], "texture": "#all" },
				"west":  { "uv": [ 3, 0, 13,  1 ], "texture": "#all" },
				"east":  { "uv": [ 3, 0, 13,  1 ], "texture": "#all" }
			}
		}
	]
}
<#-- @formatter:on -->