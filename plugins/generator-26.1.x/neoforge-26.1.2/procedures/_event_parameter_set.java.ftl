<#assign floatParameters = ["INCOMING_DAMAGE_AMOUNT", "FALL_DISTANCE", "FALL_DAMAGE_MULTIPLIER", "CRITICAL_DAMAGE_MULTIPLIER"]>
<#assign intParameters = ["INVULNERABILITY_TICKS", "DROPPED_EXPERIENCE"]>
if (event instanceof ${eventClass} _event){
	<#if floatParameters?seq_contains(fieldParameterName)>
		_event.${method}(${opt.toFloat(inputValue)});
	<#elseif intParameters?seq_contains(fieldParameterName)>
		_event.${method}(${opt.toInt(inputValue)});
	<#else>
		_event.${method}(${inputValue});
	</#if>
}