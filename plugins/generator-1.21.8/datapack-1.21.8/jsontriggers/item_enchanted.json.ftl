"${registryname}_${cbi}": {
  "trigger": "minecraft:enchanted_item",
  "conditions": {
	"item": {
		"items": [
			"${input$item}"
		],
		"predicates": {
			"enchantments": [
				{
					"enchantments": "${generator.map(field$enchantment, "enchantments", 1)}",
					"levels": {
						"min": ${input$minLevel},
						"max": ${input$maxLevel}
					}
				}
			]
		}
	},
	"levels": {
		"min": ${input$levelsSpent}
	}
  }
},