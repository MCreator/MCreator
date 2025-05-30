<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2024, Pylo, opensource contributors
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
<#include "../mcitems.ftl">
<#include "../triggers.java.ftl">

package ${package}.item;

<#compress>
public class ${name}Item extends <#if data.hasBannerPatterns()>BannerPattern</#if>Item {
	<#if data.hasBannerPatterns()>
	public static final TagKey<BannerPattern> PROVIDED_PATTERNS = TagKey.create(Registries.BANNER_PATTERN, ResourceLocation.fromNamespaceAndPath(${JavaModName}.MODID, "pattern_item/${registryname}"));
	</#if>

	public ${name}Item(Item.Properties properties) {
		super(<#if data.hasBannerPatterns()>PROVIDED_PATTERNS, </#if>properties
				<#if data.rarity != "COMMON">
				.rarity(Rarity.${data.rarity})
				</#if>
				<#if data.hasInventory()>
				.stacksTo(1)
				<#elseif data.damageCount != 0>
				.durability(${data.damageCount})
				<#elseif data.stackSize != 64>
				.stacksTo(${data.stackSize})
				</#if>
				<#if data.immuneToFire>
				.fireResistant()
				</#if>
				<#if data.isFood>
				.food((new FoodProperties.Builder())
					.nutrition(${data.nutritionalValue})
					.saturationModifier(${data.saturation}f)
					<#if data.isAlwaysEdible>.alwaysEdible()</#if>
					.build()
					<#if data.hasCustomFoodConsumable()>,
						<#if data.animation == "eat">
						Consumables.defaultFood()
						<#elseif data.animation == "drink">
						Consumables.defaultDrink()
						<#else>
						Consumables.defaultFood().animation(ItemUseAnimation.${data.animation?upper_case})
						</#if>
						<#if data.useDuration != 32>
						.consumeSeconds(${[data.useDuration, 0]?max / 20}F)
						</#if>
						.build()
					<#elseif data.animation == "drink">,
						Consumables.DEFAULT_DRINK
					</#if>
				)
				</#if>
				<#if data.hasEatResultItem()>
				.usingConvertsTo(${mappedMCItemToItem(data.eatResultItem)})
				</#if>
				<#if data.enableMeleeDamage>
				.attributes(ItemAttributeModifiers.builder()
					.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, ${data.damageVsEntity - 1},
							AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
					.add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.4,
							AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
					.build())
				</#if>
				<#if data.isMusicDisc>
				.jukeboxPlayable(ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath(${JavaModName}.MODID, "${registryname}")))
				</#if>
				<#if data.enchantability != 0>
				.enchantable(${data.enchantability})
				</#if>
				<#if data.stayInGridWhenCrafting && (!data.recipeRemainder?? || data.recipeRemainder.isEmpty()) && data.damageCount != 0>
				.setNoCombineRepair()
				</#if>
		);
	}

	<#if !data.isFood && data.animation != "none"> <#-- If item is food, this is handled by the consumable component -->
	@Override public ItemUseAnimation getUseAnimation(ItemStack itemstack) {
		return ItemUseAnimation.${data.animation?upper_case};
	}
	</#if>

	<#if data.stayInGridWhenCrafting>
		<#if data.recipeRemainder?? && !data.recipeRemainder.isEmpty()>
			@Override public ItemStack getCraftingRemainder(ItemStack itemstack) {
				return ${mappedMCItemToItemStackCode(data.recipeRemainder, 1)};
			}
		<#elseif data.damageOnCrafting && data.damageCount != 0>
			@Override public ItemStack getCraftingRemainder(ItemStack itemstack) {
				ItemStack retval = new ItemStack(this);
				retval.setDamageValue(itemstack.getDamageValue() + 1);
				if(retval.getDamageValue() >= retval.getMaxDamage()) {
					return ItemStack.EMPTY;
				}
				return retval;
			}
		<#else>
			@Override public ItemStack getCraftingRemainder(ItemStack itemstack) {
				return new ItemStack(this);
			}
		</#if>
	</#if>

	<#if !data.isFood && data.useDuration != 0> <#-- If item is food, this is handled by the consumable component -->
	@Override public int getUseDuration(ItemStack itemstack, LivingEntity livingEntity) {
		return ${data.useDuration};
	}
	</#if>

	<#if data.toolType != 1>
	@Override public float getDestroySpeed(ItemStack itemstack, BlockState state) {
		return ${data.toolType}f;
	}
	</#if>

	<@hasGlow data.glowCondition/>

	<#if data.destroyAnyBlock>
	@Override public boolean isCorrectToolForDrops(ItemStack itemstack, BlockState state) {
		return true;
	}
	</#if>

	<@addSpecialInformation data.specialInformation, "item." + modid + "." + registryname/>

	<#assign shouldExplicitlyCallStartUsing = !data.isFood && (data.useDuration > 0)> <#-- ranged items handled in if below so no need to check for that here too -->
	<#if hasProcedure(data.onRightClickedInAir) || data.hasInventory() || data.enableRanged || shouldExplicitlyCallStartUsing>
	@Override public InteractionResult use(Level world, Player entity, InteractionHand hand) {
		<#if data.enableRanged>
		InteractionResult ar = InteractionResult.FAIL;
		<#else>
		InteractionResult ar = super.use(world, entity, hand);
		</#if>

		<#if data.enableRanged>
			<#if hasProcedure(data.rangedUseCondition)>
			if (<@procedureCode data.rangedUseCondition, {
				"x": "entity.getX()",
				"y": "entity.getY()",
				"z": "entity.getZ()",
				"world": "world",
				"entity": "entity",
				"itemstack": "entity.getItemInHand(hand)"
			}, false/>)
			</#if>
			if (entity.getAbilities().instabuild || findAmmo(entity) != ItemStack.EMPTY) {
				ar = InteractionResult.SUCCESS;
				entity.startUsingItem(hand);
			}
		<#elseif shouldExplicitlyCallStartUsing>
			entity.startUsingItem(hand);
		</#if>

		<#if data.hasInventory()>
		if (entity instanceof ServerPlayer serverPlayer) {
			serverPlayer.openMenu(new MenuProvider() {
				@Override public Component getDisplayName() {
					return Component.literal("${data.name}");
				}

				@Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
					FriendlyByteBuf packetBuffer = new FriendlyByteBuf(Unpooled.buffer());
					packetBuffer.writeBlockPos(entity.blockPosition());
					packetBuffer.writeByte(hand == InteractionHand.MAIN_HAND ? 0 : 1);
					return new ${data.guiBoundTo}Menu(id, inventory, packetBuffer);
				}
			}, buf -> {
				buf.writeBlockPos(entity.blockPosition());
				buf.writeByte(hand == InteractionHand.MAIN_HAND ? 0 : 1);
			});
		}
		</#if>

		<#if hasProcedure(data.onRightClickedInAir)>
			<@procedureCode data.onRightClickedInAir, {
				"x": "entity.getX()",
				"y": "entity.getY()",
				"z": "entity.getZ()",
				"world": "world",
				"entity": "entity",
				"itemstack": "entity.getItemInHand(hand)"
			}/>
		</#if>
		return ar;
	}
	</#if>

	<#if hasProcedure(data.onFinishUsingItem)>
		@Override public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
			ItemStack retval = super.finishUsingItem(itemstack, world, entity);
			<@procedureCode data.onFinishUsingItem, {
				"x": "entity.getX()",
				"y": "entity.getY()",
				"z": "entity.getZ()",
				"world": "world",
				"entity": "entity",
				"itemstack": "itemstack"
			}/>
			return retval;
		}
	</#if>

	<@onItemUsedOnBlock data.onRightClickedOnBlock/>

	<@onEntityHitWith data.onEntityHitWith, (data.damageCount != 0 && data.enableMeleeDamage), 1/>

	<@onEntitySwing data.onEntitySwing/>

	<@onCrafted data.onCrafted/>

	<@onItemTick data.onItemInUseTick, data.onItemInInventoryTick/>

	<@onDroppedByPlayer data.onDroppedByPlayer/>

	<#if hasProcedure(data.onStoppedUsing) || (data.enableRanged && !data.shootConstantly)>
		@Override public boolean releaseUsing(ItemStack itemstack, Level world, LivingEntity entity, int time) {
			<#if hasProcedure(data.onStoppedUsing)>
				<@procedureCode data.onStoppedUsing, {
					"x": "entity.getX()",
					"y": "entity.getY()",
					"z": "entity.getZ()",
					"world": "world",
					"entity": "entity",
					"itemstack": "itemstack",
					"time": "time"
				}/>
			</#if>
			<#if data.enableRanged && !data.shootConstantly>
				if (!world.isClientSide() && entity instanceof ServerPlayer player) {
					<#if data.rangedItemChargesPower>
						float pullingPower = BowItem.getPowerForTime(this.getUseDuration(itemstack, player) - time);
						if (pullingPower < 0.1)
							return false;
					</#if>
					<@arrowShootCode/>
				}
			</#if>
			return super.releaseUsing(itemstack, world, entity, time);
		}
	</#if>

	<#if data.enableRanged && data.shootConstantly>
		@Override public void onUseTick(Level world, LivingEntity entity, ItemStack itemstack, int count) {
			if (!world.isClientSide() && entity instanceof ServerPlayer player) {
				<@arrowShootCode/>
				entity.releaseUsingItem();
			}
		}
	</#if>

	<#if data.enableRanged>
	private ItemStack findAmmo(Player player) {
		<#if data.projectileDisableAmmoCheck>
		return new ItemStack(${generator.map(data.projectile.getUnmappedValue(), "projectiles", 2)});
		<#else>
		ItemStack stack = ProjectileWeaponItem.getHeldProjectile(player, e -> e.getItem() == ${generator.map(data.projectile.getUnmappedValue(), "projectiles", 2)});
		if(stack == ItemStack.EMPTY) {
			for (int i = 0; i < player.getInventory().items.size(); i++) {
				ItemStack teststack = player.getInventory().items.get(i);
				if(teststack != null && teststack.getItem() == ${generator.map(data.projectile.getUnmappedValue(), "projectiles", 2)}) {
					stack = teststack;
					break;
				}
			}
		}
		return stack;
		</#if>
	}
	</#if>

	<#list data.customProperties.entrySet() as property>
		<#assign propClassName = StringUtils.snakeToCamel(property.getKey())>
		public record ${propClassName}Property() implements RangeSelectItemModelProperty {
			public static final MapCodec<${propClassName}Property> MAP_CODEC = MapCodec.unit(new ${propClassName}Property());

			@Override
			public float get(ItemStack itemStackToRender, @Nullable ClientLevel clientWorld, @Nullable LivingEntity entity, int seed) {
				<#if hasProcedure(property.getValue())>
				return (float) <@procedureCode property.getValue(), {
					"x": "entity != null ? entity.getX() : 0",
					"y": "entity != null ? entity.getY() : 0",
					"z": "entity != null ? entity.getZ() : 0",
					"world": "entity != null ? entity.level() : clientWorld",
					"entity": "entity",
					"itemstack": "itemStackToRender"
				}, false/>;
				<#else>
				return 0;
				</#if>
			}

			@Override
			public MapCodec<${propClassName}Property> type() {
				return MAP_CODEC;
			}
		}
	</#list>

}

<#macro arrowShootCode>
	<#assign projectile = data.projectile.getUnmappedValue()>
	ItemStack stack = findAmmo(player);
	if (player.getAbilities().instabuild || stack != ItemStack.EMPTY) {
		<#assign projectileClass = generator.map(projectile, "projectiles", 0)>
		<#if projectile.startsWith("CUSTOM:")>
			${projectileClass} projectile = ${projectileClass}.shoot(world, entity, world.getRandom()<#if data.rangedItemChargesPower>, pullingPower</#if>);
		<#elseif projectile.endsWith("Arrow")>
			ItemStack arrowPickupStack = stack;
			if (arrowPickupStack.isEmpty()) {
				arrowPickupStack = new ItemStack(${generator.map(projectile, "projectiles", 2)});
				arrowPickupStack.set(DataComponents.INTANGIBLE_PROJECTILE, Unit.INSTANCE);
			}
			${projectileClass} projectile = new ${projectileClass}(world, entity, arrowPickupStack, itemstack);
			projectile.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), 0, <#if data.rangedItemChargesPower>pullingPower * </#if>3.15f, 1.0F);
			world.addFreshEntity(projectile);
			world.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
				BuiltInRegistries.SOUND_EVENT.getValue(ResourceLocation.parse("entity.arrow.shoot")), SoundSource.PLAYERS,
				1, 1f / (world.getRandom().nextFloat() * 0.5f + 1));
		</#if>

		<#if data.damageCount != 0>
		itemstack.hurtAndBreak(1, entity, LivingEntity.getSlotForHand(entity.getUsedItemHand()));
		</#if>

		if (player.getAbilities().instabuild) {
			projectile.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
		} else {
			if (stack.isDamageableItem()) {
				if (world instanceof ServerLevel serverLevel)
					stack.hurtAndBreak(1, serverLevel, player, _stkprov -> {});
			} else {
				stack.shrink(1);
			}
		}

		<#if hasProcedure(data.onRangedItemUsed)>
			<@procedureCode data.onRangedItemUsed, {
				"x": "entity.getX()",
				"y": "entity.getY()",
				"z": "entity.getZ()",
				"world": "world",
				"entity": "entity",
				"itemstack": "itemstack"
			}/>
		</#if>
	}
</#macro>
</#compress>
<#-- @formatter:on -->