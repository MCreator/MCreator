<#include "mcelements.ftl">
(world instanceof ServerLevel _level${customBlockIndex} && _level${customBlockIndex}.isRaided(${toBlockPos(input$x,input$y,input$z)}))