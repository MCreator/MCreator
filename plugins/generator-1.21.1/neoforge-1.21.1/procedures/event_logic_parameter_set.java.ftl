if (event instanceof ${eventClass} _event){
	<#if field$parameter == "ITEM_PICKUP_RESULT">
		_event.${method}(TriState.${input$value?upper_case});
	<#else>
	 	_event.${method}(${input$value});
	</#if>
}