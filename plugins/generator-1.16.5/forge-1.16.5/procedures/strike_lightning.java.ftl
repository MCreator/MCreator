<#include "mcelements.ftl">
if(world instanceof ServerWorld) {
	LightningBoltEntity _ent = EntityType.LIGHTNING_BOLT.create((World) world);
    _ent.moveForced(Vector3d.copyCenteredHorizontally(${toBlockPos(input$x,input$y,input$z)}));
    _ent.setEffectOnly(${(field$effectOnly!false)?lower_case});
    ((World) world).addEntity(_ent);
}