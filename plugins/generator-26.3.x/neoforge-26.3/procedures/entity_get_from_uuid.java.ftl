<@addTemplate file="utils/entity/entity_from_uuid.java.ftl"/>
(world instanceof ServerLevel _level${cbi} ? getEntityFromUUID(_level${cbi}, ${input$uuid}) : null)