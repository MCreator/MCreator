import { world, system } from "@minecraft/server";

world.afterEvents.entityDie.subscribe((event) => {
    const damagesource = event.damageSource;
    const sourceentity = event.damageSource.damagingEntity;
    const entity = event.deadEntity;
    const dimension = entity.dimension;
    const x = entity.location.x;
    const y = entity.location.y;
    const z = entity.location.z;
	${scriptcode}
});