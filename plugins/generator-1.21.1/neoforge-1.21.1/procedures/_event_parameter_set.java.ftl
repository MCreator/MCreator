<#assign floatParameters = ["INCOMING_DAMAGE_AMOUNT"]>
if (event instanceof ${eventClass} _event){
	<#if floatParameters?seq_contains(fieldParameterName)>
		_event.${method}(${opt.toFloat(inputValue)});
	<#else>
		_event.${method}(${inputValue});
	</#if>
}