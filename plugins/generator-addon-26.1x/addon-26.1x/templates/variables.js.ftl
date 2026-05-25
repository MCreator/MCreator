import { world, system } from "@minecraft/server";

function initVariable(obj, key, defaultValue) {
    const v = obj.getDynamicProperty(key);
    if (v === undefined)
        obj.setDynamicProperty(key, defaultValue);
    return obj.getDynamicProperty(key);
}

<#if w.hasVariablesOfScope("PLAYER_PERSISTENT")>
function initPlayer(player) {
	<#list variables as var>
		<#if var.getScope().name() == "PLAYER_PERSISTENT">
			<@var.getType().getScopeDefinition(generator.getWorkspace(), "PLAYER_PERSISTENT")['init']?interpret/>
		</#if>
	</#list>
}
</#if>

system.run(() => {
	<#list variables as var>
		<#if var.getScope().name() == "GLOBAL_MAP">
			<@var.getType().getScopeDefinition(generator.getWorkspace(), "GLOBAL_MAP")['init']?interpret/>
		</#if>
	</#list>

	<#if w.hasVariablesOfScope("PLAYER_PERSISTENT")>
    world.getPlayers().forEach(initPlayer);
    world.afterEvents.playerJoin.subscribe(({ player }) => initPlayer(player));
	</#if>
});