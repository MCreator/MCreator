{
  "type": "environment_scan",
  "direction_of_search": "${generator.map(field$direction, "directions", 1)}",
  "target_condition": ${input$condition},
  "max_steps": ${field$maxSteps}
},