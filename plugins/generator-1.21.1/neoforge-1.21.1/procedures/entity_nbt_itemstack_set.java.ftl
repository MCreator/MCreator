<#include "mcitems.ftl">
${input$entity}.getPersistentData().put(${input$tagName}, ${mappedMCItemToItemStackCode(input$tagValue, 1)}.saveOptional(${input$entity}.level().registryAccess()));
