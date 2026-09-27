{
  "type": "environment_scan",
  "direction_of_search": "${generator.map(field$direction, "directions", 1)}",
  "target_condition": ${input$condition},
  "allowed_search_condition": ${input$searchCondition},
  "max_steps": ${field$maxSteps}
},