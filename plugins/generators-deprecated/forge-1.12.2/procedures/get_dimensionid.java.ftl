<#if field$dimension=="Surface">
		(0)
<#elseif field$dimension=="Nether">
		(-1)
<#elseif field$dimension=="End">
		(1)
<#else>
		(World${field$dimension.toString().replace("CUSTOM:", "")}.DIMID)
</#if>