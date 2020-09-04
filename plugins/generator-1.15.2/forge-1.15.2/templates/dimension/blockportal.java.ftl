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
				.hardnessAndResistance(-1.0F).sound(SoundType.GLASS).lightValue(${(data.portalLuminance * 15)?round}).noDrops());
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
		CustomPortalBlock.Size portalsize = this.isValid(world, pos);
		if (portalsize != null)
			portalsize.placePortalBlocks();
	}

	${mcc.getMethod("net.minecraft.block.NetherPortalBlock", "isPortal", "IWorld", "BlockPos")
		 .replace("NetherPortalBlock.", "CustomPortalBlock.")
		 .replace("isPortal", "isValid")}

	${mcc.getMethod("net.minecraft.block.NetherPortalBlock", "createPatternHelper", "IWorld", "BlockPos")
	               .replace("NetherPortalBlock.", "CustomPortalBlock.")}

	@Override ${mcc.getMethod("net.minecraft.block.NetherPortalBlock", "updatePostPlacement", "BlockState", "Direction", "BlockState", "IWorld", "BlockPos", "BlockPos")
				   .replace("NetherPortalBlock.", "CustomPortalBlock.")}

	@OnlyIn(Dist.CLIENT) @Override public void animateTick(BlockState state, World world, BlockPos pos, Random random) {
		for (int i = 0; i < 4; i++) {
			double px = pos.getX() + random.nextFloat();
			double py = pos.getY() + random.nextFloat();
			double pz = pos.getZ() + random.nextFloat();
			double vx = (random.nextFloat() - 0.5) / 2f;
			double vy = (random.nextFloat() - 0.5) / 2f;
			double vz = (random.nextFloat() - 0.5) / 2f;
			int j = random.nextInt(4) - 1;
			if (world.getBlockState(pos.west()).getBlock() != this && world.getBlockState(pos.east()).getBlock() != this) {
				px = pos.getX() + 0.5 + 0.25 * j;
				vx = random.nextFloat() * 2 * j;
			} else {
				pz = pos.getZ() + 0.5 + 0.25 * j;
				vz = random.nextFloat() * 2 * j;
			}
			world.addParticle(ParticleTypes.${data.portalParticles}, px, py, pz, vx, vy, vz);
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
			if (entity.timeUntilPortal > 0) {
				entity.timeUntilPortal = entity.getPortalCooldown();
			} else if (entity.dimension != type) {
				entity.timeUntilPortal = entity.getPortalCooldown();
				teleportToDimension(entity, pos, type);
			} else {
				entity.timeUntilPortal = entity.getPortalCooldown();
				teleportToDimension(entity, pos, DimensionType.OVERWORLD);
			}
		}
	}

	private void teleportToDimension(Entity entity, BlockPos pos, DimensionType destinationType) {
		entity.changeDimension(destinationType, getTeleporterForDimension(entity, pos, entity.getServer().getWorld(destinationType)));
	}

	private TeleporterDimensionMod getTeleporterForDimension(Entity entity, BlockPos pos, ServerWorld nextWorld) {
		BlockPattern.PatternHelper bph = ${name}Dimension.CustomPortalBlock.createPatternHelper(entity.world, pos);
		double d0 = bph.getForwards().getAxis() == Direction.Axis.X ? (double) bph.getFrontTopLeft().getZ() : (double) bph.getFrontTopLeft().getX();
		double d1 = bph.getForwards().getAxis() == Direction.Axis.X ? entity.getPosZ() : entity.getPosX();
		d1 = Math.abs(MathHelper.pct(d1 - (double) (bph.getForwards().rotateY().getAxisDirection() == Direction.AxisDirection.NEGATIVE ? 1 : 0), d0, d0 - (double) bph.getWidth()));
		double d2 = MathHelper.pct(entity.getPosY() - 1, (double) bph.getFrontTopLeft().getY(), (double) (bph.getFrontTopLeft().getY() - bph.getHeight()));
		return new TeleporterDimensionMod(nextWorld, new Vec3d(d1, d2, 0), bph.getForwards());
	}

	public static class Size ${mcc.getInnerClassBody("net.minecraft.block.NetherPortalBlock", "Size")
					.replace("Blocks.OBSIDIAN", mappedBlockToBlockStateCode(data.portalFrame) + ".getBlock()")
					.replace("Blocks.NETHER_PORTAL", "portal")
					.replace("this.world.getBlockState(blockpos.down()).isPortalFrame(this.world, blockpos.down())",
						"(this.world.getBlockState(blockpos.down()).getBlock() == " + mappedBlockToBlockStateCode(data.portalFrame) + ".getBlock())")
					.replace("this.world.getBlockState(framePos).isPortalFrame(this.world, framePos)",
						"(this.world.getBlockState(framePos).getBlock() == " + mappedBlockToBlockStateCode(data.portalFrame) + ".getBlock())")}

}