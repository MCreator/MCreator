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

public static class CustomPortalBlock extends NetherPortalBlock {

	public CustomPortalBlock() {
		super(Block.Properties.create(Material.PORTAL).doesNotBlockMovement().tickRandomly()
				.hardnessAndResistance(-1.0F).sound(SoundType.GLASS).setLightLevel(s -> ${(data.portalLuminance * 15)?round}).noDrops());
		setRegistryName("${registryname}_portal");
	}

	@Override public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		<#if hasProcedure(data.onPortalTickUpdate)>
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			<@procedureOBJToCode data.onPortalTickUpdate/>
		</#if>
	}

	public void portalSpawn(World world, BlockPos pos) {
		Optional<CustomPortalSize> optional = CustomPortalSize.func_242964_a(world, pos, Direction.Axis.X);
		if (optional.isPresent()) {
			optional.get().placePortalBlocks();
		}
	}

	@Override ${mcc.getMethod("net.minecraft.block.NetherPortalBlock", "updatePostPlacement", "BlockState", "Direction", "BlockState", "IWorld", "BlockPos", "BlockPos")
				   .replace("new PortalSize(", "new CustomPortalSize(")
				   .replace("NetherPortalBlock.", "CustomPortalBlock.")}

	@OnlyIn(Dist.CLIENT) @Override public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
		for (int i = 0; i < 4; i++) {
			double px = pos.getX() + random.nextFloat();
			double py = pos.getY() + random.nextFloat();
			double pz = pos.getZ() + random.nextFloat();
			double vx = (random.nextFloat() - 0.5) / 2.;
			double vy = (random.nextFloat() - 0.5) / 2.;
			double vz = (random.nextFloat() - 0.5) / 2.;
			int j = random.nextInt(4) - 1;
			if (world.getBlockState(pos.west()).getBlock() != this
					&& world.getBlockState(pos.east()).getBlock() != this) {
				px = pos.getX() + 0.5 + 0.25 * j;
				vx = random.nextFloat() * 2 * j;
			} else {
				pz = pos.getZ() + 0.5 + 0.25 * j;
				vz = random.nextFloat() * 2 * j;
			}
			world.addParticle(${data.portalParticles}, px, py, pz, vx, vy, vz);
		}

		<#if data.portalSound.toString()?has_content>
		if (random.nextInt(110) == 0)
			world.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
					(net.minecraft.util.SoundEvent) ForgeRegistries.SOUND_EVENTS
							.getValue(new ResourceLocation(("${data.portalSound}"))), SoundCategory.BLOCKS, 0.5f,
					random.nextFloat() * 0.4F + 0.8F, false);
        </#if>
	}

	@Override public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
		if (!entity.isPassenger() && !entity.isBeingRidden() && entity.isNonBoss()
				&& !entity.world.isRemote && <@procedureOBJToConditionCode data.portalUseCondition/>) {
			if (entity.func_242280_ah()) {
				entity.func_242279_ag();
			} else if (entity.world.getDimensionKey() != RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation("${modid}:${registryname}"))) {
				entity.func_242279_ag();
				teleportToDimension(entity, pos, RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation("${modid}:${registryname}")));
			} else {
				entity.func_242279_ag();
				teleportToDimension(entity, pos, World.OVERWORLD);
			}
		}
	}

	private void teleportToDimension(Entity entity, BlockPos pos, RegistryKey<World> destinationType) {
		entity.changeDimension(entity.getServer().getWorld(destinationType), new TeleporterDimensionMod(entity.getServer().getWorld(destinationType), pos));
	}

}

public static class CustomPortalSize ${mcc.getClassBody("net.minecraft.block.PortalSize")
	.replace("PortalSize", "CustomPortalSize")
	.replace("blockstate.isIn(Blocks.NETHER_PORTAL)", "blockstate.getBlock() == portal")
	.replace("state.isIn(BlockTags.FIRE) || state.isIn(Blocks.NETHER_PORTAL)", "state.getBlock() == portal")
	.replace("Blocks.NETHER_PORTAL.getDefaultState()", "portal.getDefaultState()")
	.replace("return state.isPortalFrame(blockReader, pos);", "return state.getBlock() ==" + mappedBlockToBlockStateCode(data.portalFrame) + ".getBlock();")}

