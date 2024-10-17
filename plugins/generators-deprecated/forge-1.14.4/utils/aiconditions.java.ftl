<#include "procedures.java.ftl">
<#-- @formatter:off -->
<#macro conditionCode conditionfield="" includeBractets=true>
    <#if conditionfield?has_content>
        <#assign conditions = generator.procedureNamesToObjects(conditionfield)>
        <#if hasCondition(conditions[0]) || hasCondition(conditions[1])>
            <#if includeBractets>{</#if>
                <#if hasCondition(conditions[0])>
                @Override public boolean shouldExecute() {
                	double x = CustomEntity.this.posX;
			        double y = CustomEntity.this.posY;
			        double z = CustomEntity.this.posZ;
			        Entity entity = CustomEntity.this;
                	return super.shouldExecute() && <@procedureOBJToConditionCode conditions[0]/>;
                }
                </#if>
                <#if hasCondition(conditions[1])>
                @Override public boolean shouldContinueExecuting() {
                	double x = CustomEntity.this.posX;
			        double y = CustomEntity.this.posY;
			        double z = CustomEntity.this.posZ;
			        Entity entity = CustomEntity.this;
                	return super.shouldContinueExecuting() && <@procedureOBJToConditionCode conditions[0]/>;
                }
                </#if>
			<#if includeBractets>}</#if>
        </#if>
    </#if>
</#macro>
<#-- @formatter:on -->