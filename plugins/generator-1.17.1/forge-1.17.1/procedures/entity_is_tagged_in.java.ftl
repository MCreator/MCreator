<#include "mcelements.ftl">
(EntityTypeTags.getAllTags().getTagOrEmpty(${toResourceLocation(input$tag)}).contains(${input$entity}.getType()))