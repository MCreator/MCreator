if (${input$entity} instanceof LivingEntity livingEntity${cbi})
	livingEntity${cbi}.getAttribute(${generator.map(field$attribute, "attributes")}).setBaseValue(${input$value});