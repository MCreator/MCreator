if(world instanceof WorldServer)
        ((WorldServer)world).spawnParticle(EnumParticleTypes.${generator.map(field$particle, "particles")}, ${input$x}, ${input$y}, ${input$z},
                (int)${input$count}, ${input$dx}, ${input$dy}, ${input$dz}, ${input$speed},new int[0]);