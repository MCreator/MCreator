<#if trigger == "bonemeal_used">
	<#if result == "DEFAULT">if (event instanceof BonemealEvent _event) _event.setCanceled(false);
	<#elseif result == "ALLOW">if (event instanceof BonemealEvent _event) _event.setSuccessful(true);
	<#elseif result == "DENY">if (event instanceof BonemealEvent _event) _event.setSuccessful(false);
	</#if>
<#elseif trigger == "player_critical_hit">
	<#if result == "DEFAULT">if (event instanceof CriticalHitEvent _event) _event.setCriticalHit(event.isVanillaCritical());
	<#elseif result == "ALLOW">if (event instanceof CriticalHitEvent _event) _event.setCriticalHit(true);
	<#elseif result == "DENY">if (event instanceof CriticalHitEvent _event) _event.setCriticalHit(false);
	</#if>
<#elseif trigger == "entity_grief">
	<#if result == "DEFAULT">if (event instanceof EntityMobGriefingEvent _event) _event.setCanGrief(event.isMobGriefingEnabled());
	<#elseif result == "ALLOW">if (event instanceof EntityMobGriefingEvent _event) _event.setCanGrief(true);
	<#elseif result == "DENY">if (event instanceof EntityMobGriefingEvent _event) _event.setCanGrief(false);
	</#if>
<#elseif trigger == "crop_attempts_growth">
	<#if result == "DEFAULT">if (event instanceof CropGrowEvent.Pre _event) _event.setResult(CropGrowEvent.Pre.Result.DEFAULT);
	<#elseif result == "ALLOW">if (event instanceof CropGrowEvent.Pre _event) _event.setResult(CropGrowEvent.Pre.Result.GROW);
	<#elseif result == "DENY">if (event instanceof CropGrowEvent.Pre _event) _event.setResult(CropGrowEvent.Pre.Result.DO_NOT_GROW);
	</#if>
<#elseif trigger == "entity_item_pickup">
	<#if result == "DEFAULT">if (event instanceof ItemEntityPickupEvent.Pre _event) _event.setCanPickup(TriState.DEFAULT);
	<#elseif result == "ALLOW">if (event instanceof ItemEntityPickupEvent.Pre _event) _event.setCanPickup(TriState.TRUE);
	<#elseif result == "DENY">if (event instanceof ItemEntityPickupEvent.Pre _event) _event.setCanPickup(TriState.FALSE);
	</#if>
</#if>