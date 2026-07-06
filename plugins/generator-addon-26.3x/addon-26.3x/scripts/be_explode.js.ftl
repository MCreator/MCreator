dimension.createExplosion({ x: ${input$x}, y: ${input$y}, z: ${input$z} }, ${input$power}, {
    breaksBlocks: ${field$breaks_blocks?lower_case},
    causesFire: ${field$causes_fire?lower_case},
    allowUnderwater: ${field$works_underwater?lower_case}
});