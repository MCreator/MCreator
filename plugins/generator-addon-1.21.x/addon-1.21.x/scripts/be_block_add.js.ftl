<#include "mcitems.ftl">
dimension.getBlock({ ${input$x}, ${input$y}, ${input$z} })?.setPermutation(${mappedBlockToBlockPermutation(input$block)});