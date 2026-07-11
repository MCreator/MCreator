"${registryname}_${cbi}": {
  "trigger": "minecraft:enchanted_item",
  "conditions": {
	"item": {
		${input$item}
	},
	"levels": {
		"min": ${input$levelsSpent}
	}
  }
},