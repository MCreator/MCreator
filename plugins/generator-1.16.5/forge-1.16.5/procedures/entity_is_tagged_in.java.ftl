<#include "mcelements.ftl">
(EntityTypeTags.getCollection().getTagByID(${toResourceLocation(input$tag)}).contains(${input$entity}.getType()))