<@addTemplate file="utils/entity/entity_parse_uuid.java.ftl"/>
(world instanceof ServerLevel _level${cbi} ? _level${cbi}.getEntity(parseUUID(${input$uuid})) : null)