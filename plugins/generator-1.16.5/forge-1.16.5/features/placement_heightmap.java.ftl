.withPlacement(
<#if field$heightmap == "MOTION_BLOCKING">
Placement.HEIGHTMAP.configure(NoPlacementConfig.INSTANCE)
<#elseif field$heightmap == "WORLD_SURFACE_WG">
Placement.HEIGHTMAP_WORLD_SURFACE.configure(NoPlacementConfig.INSTANCE)
<#else>
Placement.TOP_SOLID_HEIGHTMAP.configure(NoPlacementConfig.INSTANCE)
</#if>)