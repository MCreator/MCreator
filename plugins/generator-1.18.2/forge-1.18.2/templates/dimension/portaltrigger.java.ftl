<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2020 Pylo and contributors
 # 
 # This program is free software: you can redistribute it and/or modify
 # it under the terms of the GNU General Public License as published by
 # the Free Software Foundation, either version 3 of the License, or
 # (at your option) any later version.
 # 
 # This program is distributed in the hope that it will be useful,
 # but WITHOUT ANY WARRANTY; without even the implied warranty of
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 # GNU General Public License for more details.
 # 
 # You should have received a copy of the GNU General Public License
 # along with this program.  If not, see <https://www.gnu.org/licenses/>.
 # 
 # Additional permission for code generator templates (*.ftl files)
 # 
 # As a special exception, you may create a larger work that contains part or 
 # all of the MCreator code generator templates (*.ftl files) and distribute 
 # that work under terms of your choice, so long as that work isn't itself a 
 # template for code generation. Alternatively, if you modify or redistribute 
 # the template itself, you may (at your option) remove this special exception, 
 # which will cause the template and the resulting code generator output files 
 # to be licensed under the GNU General Public License without this special 
 # exception.
-->

<#-- @formatter:off -->
<#include "../procedures.java.ftl">

package ${package}.item;

public class ${name}Item extends Item {

	public ${name}Item() {
		super(new Item.Properties().tab(${data.igniterTab}).durability(64));
	}

	@Override public InteractionResult useOn(UseOnContext context) {
		Player entity = context.getPlayer();
		BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
		ItemStack itemstack = context.getItemInHand();
		Level world = context.getLevel();
		if (!entity.mayUseItemAt(pos, context.getClickedFace(), itemstack)) {
			return InteractionResult.FAIL;
		} else {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			boolean success = false;

			if (world.isEmptyBlock(pos) && <@procedureOBJToConditionCode data.portalMakeCondition/>) {
				${name}PortalBlock.portalSpawn(world, pos);
				itemstack.hurtAndBreak(1, entity, c -> c.broadcastBreakEvent(context.getHand()));
				success = true;
			}

			<#if hasProcedure(data.whenPortaTriggerlUsed)>
				<#if hasReturnValueOf(data.whenPortaTriggerlUsed, "actionresulttype")>
					InteractionResult result = <@procedureOBJToInteractionResultCode data.whenPortaTriggerlUsed/>;
					return success ? InteractionResult.SUCCESS : result;
				<#else>
					<@procedureOBJToCode data.whenPortaTriggerlUsed/>
					return InteractionResult.SUCCESS;
				</#if>
			<#else>
				return success ? InteractionResult.SUCCESS : InteractionResult.FAIL;
			</#if>
		}
	}
}

<#-- @formatter:on -->