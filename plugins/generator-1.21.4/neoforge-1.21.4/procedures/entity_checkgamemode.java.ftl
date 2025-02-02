<@addTemplate file="utils/entity/entity_checkgamemode.java.ftl"/>
(getEntityGameType(${input$entity}) == GameType.${generator.map(field$gamemode, "gamemodes")})