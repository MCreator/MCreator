<#include "procedures.java.ftl">
<#-- @formatter:off -->
<#macro conditionCode conditionfield="" includeBractets=true>
    <#if conditionfield?has_content>
        <#assign conditions = generator.procedureNamesToObjects(conditionfield)>
        <#if hasProcedure(conditions[0]) || hasProcedure(conditions[1])>
			<#if includeBractets>{</#if>
                <#if hasProcedure(conditions[0])>
                @Override public boolean canUse() {
                	double x = ${name}Entity.this.getX();
			        double y = ${name}Entity.this.getY();
			        double z = ${name}Entity.this.getZ();
			        Entity entity = ${name}Entity.this;
					Level world = ${name}Entity.this.level;
                	return super.canUse() && <@procedureOBJToConditionCode conditions[0]/>;
                }
                </#if>
                <#if hasProcedure(conditions[1])>
                @Override public boolean canContinueToUse() {
                	double x = ${name}Entity.this.getX();
			        double y = ${name}Entity.this.getY();
			        double z = ${name}Entity.this.getZ();
			        Entity entity = ${name}Entity.this;
			        Level world = ${name}Entity.this.level;
                	return super.canContinueToUse() && <@procedureOBJToConditionCode conditions[0]/>;
                }
                </#if>
			<#if includeBractets>}</#if>
        </#if>
    </#if>
</#macro>
<#-- @formatter:on -->