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
<#include "procedures.java.ftl">

package ${package}.block;

import net.minecraft.block.material.Material;

@${JavaModName}Elements.ModElement.Tag public class ${name}Block extends ${JavaModName}Elements.ModElement{

	@ObjectHolder("${modid}:${registryname}")
	public static final FlowingFluidBlock block = null;

	@ObjectHolder("${modid}:${registryname}_bucket")
	public static final Item bucket = null;

	public static FlowingFluid flowing = null;
	public static FlowingFluid still = null;

	private ForgeFlowingFluid.Properties fluidproperties = null;

	public ${name}Block (${JavaModName}Elements instance) {
		super(instance, ${data.getModElement().getSortID()});

		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}

	@SubscribeEvent public void registerFluids(RegistryEvent.Register<Fluid> event) {
		event.getRegistry().register(still);
		event.getRegistry().register(flowing);
	}

	@Override @OnlyIn(Dist.CLIENT) public void clientLoad(FMLClientSetupEvent event) {
		RenderTypeLookup.setRenderLayer(still, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(flowing, RenderType.getTranslucent());
	}

	@Override public void initElements() {
		fluidproperties = new ForgeFlowingFluid.Properties(() -> still, () -> flowing, FluidAttributes
				.builder(new ResourceLocation("${modid}:blocks/${data.textureStill}"), new ResourceLocation("${modid}:blocks/${data.textureFlowing}"))
					.luminosity(${data.luminosity})
					.density(${data.density})
					.viscosity(${data.viscosity})
					.temperature(${data.temperature})
					<#if data.isGas>.gaseous()</#if>
					.rarity(Rarity.${data.rarity})
					<#if data.emptySound?has_content && data.emptySound.getMappedValue()?has_content>
					.sound(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("${data.emptySound}")))
					</#if>)
					.explosionResistance(${data.resistance}f)
					<#if data.canMultiply>.canMultiply()</#if>
					.tickRate(${data.flowRate})
					.levelDecreasePerBlock(${data.levelDecrease})
					.slopeFindDistance(${data.slopeFindDistance})
					<#if data.generateBucket>.bucket(() -> bucket)</#if>
					.block(() -> block);

		<#if data.spawnParticles>
		still = (FlowingFluid) new CustomFlowingFluid.Source(fluidproperties).setRegistryName("${registryname}");
		flowing = (FlowingFluid) new CustomFlowingFluid.Flowing(fluidproperties).setRegistryName("${registryname}_flowing");
		<#else>
		still = (FlowingFluid) new ForgeFlowingFluid.Source(fluidproperties).setRegistryName("${registryname}");
		flowing = (FlowingFluid) new ForgeFlowingFluid.Flowing(fluidproperties).setRegistryName("${registryname}_flowing");
		</#if>

		elements.blocks.add(() -> new FlowingFluidBlock(still,
			<#if generator.map(data.colorOnMap, "mapcolors") != "DEFAULT">
			Block.Properties.create(Material.${data.type}, MaterialColor.${generator.map(data.colorOnMap, "mapcolors")})
			<#else>
			Block.Properties.create(Material.${data.type})
			</#if>
			.hardnessAndResistance(${data.resistance}f)
			.lightValue(${data.luminance})
			){
			<#if data.flammability != 0>
			@Override public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
				return ${data.flammability};
			}
			</#if>

			<#if data.fireSpreadSpeed != 0>
			@Override public int getFireSpreadSpeed(BlockState state, IBlockReader world, BlockPos pos, Direction face) {
				return ${data.fireSpreadSpeed};
			}
			</#if>

			<#if data.emissiveRendering>
			@OnlyIn(Dist.CLIENT) @Override public boolean isEmissiveRendering(BlockState blockState) {
				return true;
			}
			</#if>

			<#if data.lightOpacity == 0>
			@Override
			public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
				return true;
			}
			<#elseif data.lightOpacity != 1>
			@Override
			public int getOpacity(BlockState state, IBlockReader worldIn, BlockPos pos) {
				return ${data.lightOpacity};
			}
			</#if>

			<#if hasProcedure(data.onBlockAdded) || hasProcedure(data.onTickUpdate)>
			@Override public void onBlockAdded(BlockState blockstate, World world, BlockPos pos, BlockState oldState, boolean moving) {
				super.onBlockAdded(blockstate, world, pos, oldState, moving);
				int x = pos.getX();
				int y = pos.getY();
				int z = pos.getZ();
				<#if hasProcedure(data.onTickUpdate)>
				world.getPendingBlockTicks().scheduleTick(new BlockPos(x, y, z), this, ${data.tickRate});
				</#if>
				<@procedureOBJToCode data.onBlockAdded/>
			}
            </#if>

			<#if hasProcedure(data.onNeighbourChanges)>
			public void neighborChanged(BlockState blockstate, World world, BlockPos pos, Block neighborBlock, BlockPos fromPos, boolean moving) {
				super.neighborChanged(blockstate, world, pos, neighborBlock, fromPos, moving);
				int x = pos.getX();
				int y = pos.getY();
				int z = pos.getZ();
				<@procedureOBJToCode data.onNeighbourChanges/>
			}
			</#if>

			<#if hasProcedure(data.onTickUpdate)>
			@Override public void tick(BlockState blockstate, ServerWorld world, BlockPos pos, Random random) {
				super.tick(blockstate, world, pos, random);
				int x = pos.getX();
				int y = pos.getY();
				int z = pos.getZ();
				<@procedureOBJToCode data.onTickUpdate/>
				world.getPendingBlockTicks().scheduleTick(new BlockPos(x, y, z), this, ${data.tickRate});
			}
			</#if>

			<#if hasProcedure(data.onEntityCollides)>
			@Override public void onEntityCollision(BlockState blockstate, World world, BlockPos pos, Entity entity) {
				super.onEntityCollision(blockstate, world, pos, entity);
				int x = pos.getX();
				int y = pos.getY();
				int z = pos.getZ();
    			<@procedureOBJToCode data.onEntityCollides/>
			}
			</#if>

			<#if hasProcedure(data.onRandomUpdateEvent)>
			@OnlyIn(Dist.CLIENT) @Override
			public void animateTick(BlockState blockstate, World world, BlockPos pos, Random random) {
				super.animateTick(blockstate, world, pos, random);
				PlayerEntity entity = Minecraft.getInstance().player;
				int x = pos.getX();
				int y = pos.getY();
				int z = pos.getZ();
				<@procedureOBJToCode data.onRandomUpdateEvent/>
			}
			</#if>

			<#if hasProcedure(data.onDestroyedByExplosion)>
			@Override public void onExplosionDestroy(World world, BlockPos pos, Explosion e) {
				super.onExplosionDestroy(world, pos, e);
				int x = pos.getX();
				int y = pos.getY();
				int z = pos.getZ();
				<@procedureOBJToCode data.onDestroyedByExplosion/>
			}
			</#if>
		}.setRegistryName("${registryname}"));

		<#if data.generateBucket>
		elements.items.add(() -> new BucketItem(still, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1)
			<#if data.creativeTab??>.group(${data.creativeTab})<#else>.group(ItemGroup.MISC)</#if>.rarity(Rarity.${data.rarity}))
			<#if data.specialInfo?has_content>{
			@Override public void addInformation(ItemStack itemstack, World world, List<ITextComponent> list, ITooltipFlag flag) {
				super.addInformation(itemstack, world, list, flag);
				<#list data.specialInfo as entry>
				list.add(new StringTextComponent("${JavaConventions.escapeStringForJava(entry)}"));
				</#list>
			}
			}</#if>
			.setRegistryName("${registryname}_bucket"));
		</#if>
	}

	<#if data.spawnParticles>
	public static abstract class CustomFlowingFluid extends ForgeFlowingFluid {
		public CustomFlowingFluid(Properties properties) {
			super(properties);
		}

		@OnlyIn(Dist.CLIENT)
		@Override
		public IParticleData getDripParticleData() {
			return ${data.dripParticle};
		}

		public static class Source extends CustomFlowingFluid {
			public Source(Properties properties) {
				super(properties);
			}

			public int getLevel(IFluidState state) {
				return 8;
			}

			public boolean isSource(IFluidState state) {
				return true;
			}
		}

		public static class Flowing extends CustomFlowingFluid {
			public Flowing(Properties properties) {
				super(properties);
			}

			protected void fillStateContainer(StateContainer.Builder<Fluid, IFluidState> builder) {
				super.fillStateContainer(builder);
				builder.add(LEVEL_1_8);
			}

			public int getLevel(IFluidState state) {
				return state.get(LEVEL_1_8);
			}

			public boolean isSource(IFluidState state) {
				return false;
			}
		}
	}
	</#if>

	<#if (data.spawnWorldTypes?size > 0)>
	@Override public void init(FMLCommonSetupEvent event) {
		for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
			<#if data.restrictionBiomes?has_content>
				boolean biomeCriteria = false;
				<#list data.restrictionBiomes as restrictionBiome>
					<#if restrictionBiome.canProperlyMap()>
					if (ForgeRegistries.BIOMES.getKey(biome).equals(new ResourceLocation("${restrictionBiome}")))
						biomeCriteria = true;
					</#if>
				</#list>
				if (!biomeCriteria)
					continue;
			</#if>

			biome.addFeature(GenerationStage.Decoration.LOCAL_MODIFICATIONS, new LakesFeature(BlockStateFeatureConfig::deserialize) {
					@Override public boolean place(IWorld world, ChunkGenerator generator, Random rand, BlockPos pos, BlockStateFeatureConfig config) {
					DimensionType dimensionType = world.getDimension().getType();
					boolean dimensionCriteria = false;

    				<#list data.spawnWorldTypes as worldType>
						<#if worldType=="Surface">
							if(dimensionType == DimensionType.OVERWORLD)
								dimensionCriteria = true;
						<#elseif worldType=="Nether">
							if(dimensionType == DimensionType.THE_NETHER)
								dimensionCriteria = true;
						<#elseif worldType=="End">
							if(dimensionType == DimensionType.THE_END)
								dimensionCriteria = true;
						<#else>
							if(dimensionType == ${(worldType.toString().replace("CUSTOM:", ""))}Dimension.type)
								dimensionCriteria = true;
						</#if>
					</#list>

					if(!dimensionCriteria)
						return false;

					<#if hasProcedure(data.generateCondition)>
					int x = pos.getX();
					int y = pos.getY();
					int z = pos.getZ();
					if (!<@procedureOBJToConditionCode data.generateCondition/>)
						return false;
					</#if>

					return super.place(world, generator, rand, pos, config);
				}}.withConfiguration(new BlockStateFeatureConfig(block.getDefaultState()))
					.withPlacement(Placement.WATER_LAKE.configure(new ChanceConfig(${data.frequencyOnChunks}))));
		}
	}
	</#if>

}
<#-- @formatter:on -->