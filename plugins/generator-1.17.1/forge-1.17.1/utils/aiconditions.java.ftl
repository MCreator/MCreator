<#include "procedures.java.ftl">
<#-- @formatter:off -->
<#macro conditionCode conditionfield="" includeBractets=true>
    <#if conditionfield?has_content>
        <#assign conditions = generator.procedureNamesToObjects(conditionfield)>
        <#if hasProcedure(conditions[0]) || hasProcedure(conditions[1])>
			<#if includeBractets>{</#if>
                <#if hasProcedure(conditions[0])>
                @Override public boolean canUse() {
                	double x = CustomEntity.this.getX();
			        double y = CustomEntity.this.getY();
			        double z = CustomEntity.this.getZ();
			        Entity entity = CustomEntity.this;
                	return super.canUse() && <@procedureOBJToConditionCode conditions[0]/>;
                }
                </#if>
                <#if hasProcedure(conditions[1])>
                @Override public boolean canContinueToUse() {
                	double x = CustomEntity.this.getX();
			        double y = CustomEntity.this.getY();
			        double z = CustomEntity.this.getZ();
			        Entity entity = CustomEntity.this;
                	return super.canContinueToUse() && <@procedureOBJToConditionCode conditions[0]/>;
                }
                </#if>
			<#if includeBractets>}</#if>
        </#if>
    </#if>
</#macro>
<#-- @formatter:on -->