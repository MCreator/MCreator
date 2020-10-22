((Entity _ent = ${input$entity}) instanceof PlayerEntity ? _ent.getBedLocation(
<#if field$dimension=="Surface">
	DimensionType.OVERWORLD;
<#elseif field$dimension=="Nether">
	DimensionType.THE_NETHER;
<#elseif field$dimension=="End">
	DimensionType.THE_END;
<#else>
	${(field$dimension.toString().replace("CUSTOM:", ""))}Dimension.type;
</#if>).getY() : 0)