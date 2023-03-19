<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2023, Pylo, opensource contributors
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
<#include "../boundingboxes.java.ftl">
<#include "../mcitems.ftl">
<#include "../procedures.java.ftl">
<#include "../triggers.java.ftl">

package ${package}.block;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;

<#compress>
public class ${name}WallBlock extends WallSignBlock implements EntityBlock {

    <#macro blockProperties>
        <#if generator.map(data.colorOnMap, "mapcolors") != "DEFAULT">
            BlockBehaviour.Properties.of(Material.${data.material}, MaterialColor.${generator.map(data.colorOnMap, "mapcolors")})
        <#else>
            BlockBehaviour.Properties.of(Material.${data.material})
        </#if>
        <#if data.isCustomSoundType>
            .sound(new ForgeSoundType(1.0f, 1.0f, () -> new SoundEvent(new ResourceLocation("${data.breakSound}")),
            () -> new SoundEvent(new ResourceLocation("${data.stepSound}")),
            () -> new SoundEvent(new ResourceLocation("${data.placeSound}")),
            () -> new SoundEvent(new ResourceLocation("${data.hitSound}")),
            () -> new SoundEvent(new ResourceLocation("${data.fallSound}"))))
        <#else>
            .sound(SoundType.${data.soundOnStep})
        </#if>
        <#if data.unbreakable>
            .strength(-1, 3600000)
        <#elseif (data.hardness == 0) && (data.resistance == 0)>
            .instabreak()
        <#elseif data.hardness == data.resistance>
            .strength(${data.hardness}f)
        <#else>
            .strength(${data.hardness}f, ${data.resistance}f)
            </#if>
            <#if data.luminance != 0>
                .lightLevel(s -> ${data.luminance})
            </#if>
            <#if data.requiresCorrectTool>
                .requiresCorrectToolForDrops()
            </#if>
            .noCollission()
            <#if data.slipperiness != 0.6>
                .friction(${data.slipperiness}f)
            </#if>
            <#if data.speedFactor != 1.0>
                .speedFactor(${data.speedFactor}f)
            </#if>
            <#if data.jumpFactor != 1.0>
                .jumpFactor(${data.jumpFactor}f)
            </#if>
            .noOcclusion()
            <#if data.tickRandomly>
                .randomTicks()
            </#if>
            <#if data.emissiveRendering>
                .hasPostProcess((bs, br, bp) -> true).emissiveRendering((bs, br, bp) -> true)
            </#if>
            .isRedstoneConductor((bs, br, bp) -> false)
            .dynamicShape()
            <#if !data.useLootTableForDrops && (data.dropAmount == 0)>
                .noLootTable()
            </#if>
            <#if data.offsetType != "NONE">
                .offsetType(Block.OffsetType.${data.offsetType})
            </#if>
        </#macro>

    public ${name}WallBlock() {
        super(<@blockProperties/>, WoodType.OAK);
    }

    <#if data.specialInfo?has_content>
    @Override public void appendHoverText(ItemStack itemstack, BlockGetter world, List<Component> list, TooltipFlag flag) {
        super.appendHoverText(itemstack, world, list, flag);
        <#list data.specialInfo as entry>
        list.add(Component.literal("${JavaConventions.escapeStringForJava(entry)}"));
        </#list>
    }
    </#if>

    <#if data.beaconColorModifier?has_content>
    @Override public float[] getBeaconColorMultiplier(BlockState state, LevelReader world, BlockPos pos, BlockPos beaconPos) {
        return new float[] { ${data.beaconColorModifier.getRed()/255}f, ${data.beaconColorModifier.getGreen()/255}f, ${data.beaconColorModifier.getBlue()/255}f };
    }
    </#if>

    <#if data.connectedSides>
    @Override public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        return adjacentBlockState.getBlock() == this ? true : super.skipRendering(state, adjacentBlockState, side);
    }
    </#if>

    @Override public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return ${data.lightOpacity};
    }

    <#if hasProcedure(data.placingCondition)>
    @Override public boolean canSurvive(BlockState blockstate, LevelReader worldIn, BlockPos pos) {
        if (worldIn instanceof LevelAccessor world) {
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            return <@procedureOBJToConditionCode data.placingCondition/>;
        }
        return super.canSurvive(blockstate, worldIn, pos);
    }
    </#if>

    <#if hasProcedure(data.placingCondition)>
    @Override public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor world, BlockPos currentPos, BlockPos facingPos) {
        return <#if hasProcedure(data.placingCondition)>
            !state.canSurvive(world, currentPos) ? Blocks.AIR.defaultBlockState() :
            </#if> super.updateShape(state, facing, facingState, world, currentPos, facingPos);
    }
    </#if>

    <#if data.enchantPowerBonus != 0>
    @Override public float getEnchantPowerBonus(BlockState state, LevelReader world, BlockPos pos) {
        return ${data.enchantPowerBonus}f;
   }
   </#if>

   <#if data.isReplaceable>
   @Override public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
       return context.getItemInHand().getItem() != this.asItem();
   }
   </#if>

    <#if data.canProvidePower && data.emittedRedstonePower??>
    @Override public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override public int getSignal(BlockState blockstate, BlockGetter blockAccess, BlockPos pos, Direction direction) {
        <#if hasProcedure(data.emittedRedstonePower)>
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            Level world = (Level) blockAccess;
            return (int) <@procedureOBJToNumberCode data.emittedRedstonePower/>;
        <#else>
            return ${data.emittedRedstonePower.getFixedValue()};
        </#if>
    }
    </#if>

    <#if data.flammability != 0>
    @Override public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return ${data.flammability};
    }
    </#if>

    <#if data.fireSpreadSpeed != 0>
    @Override public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return ${data.fireSpreadSpeed};
    }
    </#if>

    <#if data.creativePickItem?? && !data.creativePickItem.isEmpty()>
    @Override public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
        return ${mappedMCItemToItemStackCode(data.creativePickItem, 1)};
    }
    </#if>

    <#if generator.map(data.aiPathNodeType, "pathnodetypes") != "DEFAULT">
    @Override public BlockPathTypes getBlockPathType(BlockState state, BlockGetter world, BlockPos pos, Mob entity) {
        return BlockPathTypes.${generator.map(data.aiPathNodeType, "pathnodetypes")};
    }
    </#if>

    <#if data.isLadder>
    @Override public boolean isLadder(BlockState state, LevelReader world, BlockPos pos, LivingEntity entity) {
        return true;
    }
    </#if>

    <#if data.reactionToPushing != "NORMAL">
    @Override public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.${data.reactionToPushing};
    }
    </#if>

    <#if data.canRedstoneConnect>
     @Override
     public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        return true;
     }
    </#if>

    <#if data.requiresCorrectTool>
    @Override public boolean canHarvestBlock(BlockState state, BlockGetter world, BlockPos pos, Player player) {
        if(player.getInventory().getSelected().getItem() instanceof
        <#if data.destroyTool == "pickaxe">PickaxeItem
        <#elseif data.destroyTool == "axe">AxeItem
        <#elseif data.destroyTool == "shovel">ShovelItem
        <#elseif data.destroyTool == "hoe">HoeItem
        <#else>TieredItem</#if> tieredItem)
        return tieredItem.getTier().getLevel() >= ${data.breakHarvestLevel};
        return false;
    }
    </#if>

    <#if !(data.useLootTableForDrops || (data.dropAmount == 0))>
        <#if data.dropAmount != 1 && !(data.customDrop?? && !data.customDrop.isEmpty())>
        @Override public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
            List<ItemStack> dropsOriginal = super.getDrops(state, builder);
            if(!dropsOriginal.isEmpty())
                return dropsOriginal;
            return Collections.singletonList(new ItemStack(this, ${data.dropAmount}));
        }
        <#elseif data.customDrop?? && !data.customDrop.isEmpty()>
        @Override public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
            List<ItemStack> dropsOriginal = super.getDrops(state, builder);
            if(!dropsOriginal.isEmpty())
                return dropsOriginal;
            return Collections.singletonList(${mappedMCItemToItemStackCode(data.customDrop, data.dropAmount)});
        }
        <#else>
        @Override public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
            List<ItemStack> dropsOriginal = super.getDrops(state, builder);
            if(!dropsOriginal.isEmpty())
                return dropsOriginal;
            return Collections.singletonList(new ItemStack(this, 1));
        }
        </#if>
    </#if>

    <@onBlockAdded data.onBlockAdded, hasProcedure(data.onTickUpdate) && data.shouldScheduleTick(), data.tickRate/>

    <@onRedstoneOrNeighborChanged data.onRedstoneOn, data.onRedstoneOff, data.onNeighbourBlockChanges/>

    <#if hasProcedure(data.onTickUpdate)>
    @Override public void tick(BlockState blockstate, ServerLevel world, BlockPos pos, RandomSource random) {
        super.tick(blockstate, world, pos, random);
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        <@procedureOBJToCode data.onTickUpdate/>

        <#if data.shouldScheduleTick()>
        world.scheduleTick(pos, this, ${data.tickRate});
        </#if>
    }
    </#if>

    <#if hasProcedure(data.onRandomUpdateEvent)>
    @OnlyIn(Dist.CLIENT) @Override
    public void animateTick(BlockState blockstate, Level world, BlockPos pos, RandomSource random) {
        super.animateTick(blockstate, world, pos, random);
        Player entity = Minecraft.getInstance().player;
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        <@procedureOBJToCode data.onRandomUpdateEvent/>
    }
    </#if>

    <@onDestroyedByPlayer data.onDestroyedByPlayer/>

    <@onDestroyedByExplosion data.onDestroyedByExplosion/>

    <@onStartToDestroy data.onStartToDestroy/>

    <@onEntityCollides data.onEntityCollides/>

    <@onEntityWalksOn data.onEntityWalksOn/>

    <@onHitByProjectile data.onHitByProjectile/>

    <@onBlockPlacedBy data.onBlockPlayedBy/>

    <#if hasProcedure(data.onRightClicked) || data.shouldOpenGUIOnRightClick()>
    @Override
    public InteractionResult use(BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
        super.use(blockstate, world, pos, entity, hand, hit);
        <#if data.shouldOpenGUIOnRightClick()>
        if(entity instanceof ServerPlayer player) {
            NetworkHooks.openScreen(player, new MenuProvider() {
                @Override public Component getDisplayName() {
                    return Component.literal("${data.name}");
                }
                @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                    return new ${data.guiBoundTo}Menu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(pos));
                }
            }, pos);
        }
        </#if>

        <#if hasProcedure(data.onRightClicked)>
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();
            double hitX = hit.getLocation().x;
            double hitY = hit.getLocation().y;
            double hitZ = hit.getLocation().z;
            Direction direction = hit.getDirection();
            <#if hasReturnValueOf(data.onRightClicked, "actionresulttype")>
            InteractionResult result = <@procedureOBJToInteractionResultCode data.onRightClicked/>;
            <#else>
            <@procedureOBJToCode data.onRightClicked/>
            </#if>
        </#if>

        <#if data.shouldOpenGUIOnRightClick() || !hasReturnValueOf(data.onRightClicked, "actionresulttype")>
        return InteractionResult.SUCCESS;
        <#else>
        return result;
        </#if>
    }
    </#if>

    <#if data.hasInventory>
        @Override public MenuProvider getMenuProvider(BlockState state, Level worldIn, BlockPos pos) {
            BlockEntity tileEntity = worldIn.getBlockEntity(pos);
            return tileEntity instanceof MenuProvider menuProvider ? menuProvider : null;
        }

        @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return new ${name}BlockEntity(pos, state);
        }

        @Override
        public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int eventID, int eventParam) {
            super.triggerEvent(state, world, pos, eventID, eventParam);
            BlockEntity blockEntity = world.getBlockEntity(pos);
            return blockEntity == null ? false : blockEntity.triggerEvent(eventID, eventParam);
        }

        <#if data.inventoryDropWhenDestroyed>
        @Override public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
            if (state.getBlock() != newState.getBlock()) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                if (blockEntity instanceof ${name}BlockEntity be) {
                    Containers.dropContents(world, pos, be);
                    world.updateNeighbourForOutputSignal(pos, this);
                }

                super.onRemove(state, world, pos, newState, isMoving);
            }
        }
        </#if>

        <#if data.inventoryComparatorPower>
        @Override public boolean hasAnalogOutputSignal(BlockState state) {
            return true;
        }

        @Override public int getAnalogOutputSignal(BlockState blockState, Level world, BlockPos pos) {
            BlockEntity tileentity = world.getBlockEntity(pos);
            if (tileentity instanceof ${name}BlockEntity be)
                return AbstractContainerMenu.getRedstoneSignalFromContainer(be);
            else
                return 0;
        }
        </#if>
    </#if>

    <#if data.tintType != "No tint">
        @OnlyIn(Dist.CLIENT) public static void blockColorLoad(RegisterColorHandlersEvent.Block event) {
            event.getBlockColors().register((bs, world, pos, index) -> {
                <#if data.tintType == "Default foliage">
                    return FoliageColor.getDefaultColor();
                <#elseif data.tintType == "Birch foliage">
                    return FoliageColor.getBirchColor();
                <#elseif data.tintType == "Spruce foliage">
                    return FoliageColor.getEvergreenColor();
                <#else>
                    return world != null && pos != null ?
                    <#if data.tintType == "Grass">
                        BiomeColors.getAverageGrassColor(world, pos) : GrassColor.get(0.5D, 1.0D);
                    <#elseif data.tintType == "Foliage">
                        BiomeColors.getAverageFoliageColor(world, pos) : FoliageColor.getDefaultColor();
                    <#elseif data.tintType == "Water">
                        BiomeColors.getAverageWaterColor(world, pos) : -1;
                    <#elseif data.tintType == "Sky">
                        Minecraft.getInstance().level.getBiome(pos).value().getSkyColor() : 8562943;
                    <#elseif data.tintType == "Fog">
                        Minecraft.getInstance().level.getBiome(pos).value().getFogColor() : 12638463;
                    <#else>
                        Minecraft.getInstance().level.getBiome(pos).value().getWaterFogColor() : 329011;
                    </#if>
                </#if>
            }, ${JavaModName}Blocks.${data.getModElement().getRegistryNameUpper()}.get());
        }

        <#if data.isItemTinted>
        @OnlyIn(Dist.CLIENT) public static void itemColorLoad(RegisterColorHandlersEvent.Item event) {
            event.getItemColors().register((stack, index) -> {
                <#if data.tintType == "Grass">
                    return GrassColor.get(0.5D, 1.0D);
                <#elseif data.tintType == "Foliage" || data.tintType == "Default foliage">
                    return FoliageColor.getDefaultColor();
                <#elseif data.tintType == "Birch foliage">
                    return FoliageColor.getBirchColor();
                <#elseif data.tintType == "Spruce foliage">
                    return FoliageColor.getEvergreenColor();
                <#elseif data.tintType == "Water">
                    return 3694022;
                <#elseif data.tintType == "Sky">
                    return 8562943;
                <#elseif data.tintType == "Fog">
                    return 12638463;
                <#else>
                    return 329011;
                </#if>
            }, ${JavaModName}Blocks.${data.getModElement().getRegistryNameUpper()}.get());
        }
        </#if>
    </#if>

}
</#compress>
<#-- @formatter:on -->
