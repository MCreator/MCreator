<#include "procedures.java.ftl">
<#-- @formatter:off -->
<#macro conditionCode conditionfield="" includeBractets=true>
    <#if conditionfield?has_content>
        <#assign conditions = generator.procedureNamesToObjects(conditionfield)>
        <#if hasProcedure(conditions[0]) || hasProcedure(conditions[1])>
			<#if includeBractets>{</#if>
                <#if hasProcedure(conditions[0])>
                @Override public boolean shouldExecute() {
                	double x = CustomEntity.this.getPosX();
			        double y = CustomEntity.this.getPosY();
			        double z = CustomEntity.this.getPosZ();
			        Entity entity = CustomEntity.this;
                	return super.shouldExecute() && <@procedureOBJToConditionCode conditions[0]/>;
                }
                </#if>
                <#if hasProcedure(conditions[1])>
                @Override public boolean shouldContinueExecuting() {
                	double x = CustomEntity.this.getPosX();
			        double y = CustomEntity.this.getPosY();
			        double z = CustomEntity.this.getPosZ();
			        Entity entity = CustomEntity.this;
                	return super.shouldContinueExecuting() && <@procedureOBJToConditionCode conditions[0]/>;
                }
                </#if>
			<#if includeBractets>}</#if>
        </#if>
    </#if>
</#macro>
<#-- @formatter:on -->