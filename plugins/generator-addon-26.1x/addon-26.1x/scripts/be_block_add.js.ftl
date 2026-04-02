<#include "mcitems.ftl">
dimension.getBlock({ x: ${input$x}, y: ${input$y}, z: ${input$z} })?.setPermutation(${mappedBlockToBlockPermutation(input$block)});