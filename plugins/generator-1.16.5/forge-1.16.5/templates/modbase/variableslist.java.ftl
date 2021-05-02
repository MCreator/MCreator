<#-- @formatter:off -->
package ${package};

import ${package}.${JavaModName};

public class ${JavaModName}Variables {

	public ${JavaModName}Variables(${JavaModName}Elements elements) {
		<#if w.hasVariablesOfScope("GLOBAL_WORLD") || w.hasVariablesOfScope("GLOBAL_MAP")>
		elements.addNetworkMessage(WorldSavedDataSyncMessage.class, WorldSavedDataSyncMessage::buffer, WorldSavedDataSyncMessage::new, WorldSavedDataSyncMessage::handler);
		</#if>

		<#if w.hasVariablesOfScope("PLAYER_LIFETIME") || w.hasVariablesOfScope("PLAYER_PERSISTENT")>
		elements.addNetworkMessage(PlayerVariablesSyncMessage.class, PlayerVariablesSyncMessage::buffer, PlayerVariablesSyncMessage::new, PlayerVariablesSyncMessage::handler);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
		</#if>
	}

	<#if w.hasVariablesOfScope("PLAYER_LIFETIME") || w.hasVariablesOfScope("PLAYER_PERSISTENT")>
	private void init(FMLCommonSetupEvent event) {
		CapabilityManager.INSTANCE.register(PlayerVariables.class, new PlayerVariablesStorage(), PlayerVariables::new);
	}
	</#if>

	<#if w.hasVariablesOfScope("GLOBAL_SESSION")>
		<#list variables as var>
			<#if var.getScope().name() == "GLOBAL_SESSION">
				<#if var.getType().name() == "NUMBER">
	        public static double ${var.getName()} = ${var.getValue()};
				<#elseif var.getType().name() == "LOGIC">
	        public static boolean ${var.getName()} = ${var.getValue()};
				<#elseif var.getType().name() == "STRING">
	        public static String ${var.getName()} ="${JavaConventions.escapeStringForJava(var.getValue())}";
				<#elseif var.getType().name() == "ITEMSTACK">
	        public static ItemStack ${var.getName()} = ItemStack.EMPTY;
				</#if>
			</#if>
		</#list>
	</#if>

	<#if w.hasVariablesOfScope("GLOBAL_WORLD") || w.hasVariablesOfScope("GLOBAL_MAP")>
	@SubscribeEvent public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		if (!event.getPlayer().world.isRemote()) {
			WorldSavedData mapdata = MapVariables.get(event.getPlayer().world);
			WorldSavedData worlddata = WorldVariables.get(event.getPlayer().world);
			if(mapdata != null)
				${JavaModName}.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()), new WorldSavedDataSyncMessage(0, mapdata));
			if(worlddata != null)
				${JavaModName}.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()), new WorldSavedDataSyncMessage(1, worlddata));
		}
	}

	@SubscribeEvent public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		if (!event.getPlayer().world.isRemote()) {
			WorldSavedData worlddata = WorldVariables.get(event.getPlayer().world);
			if(worlddata != null)
				${JavaModName}.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()), new WorldSavedDataSyncMessage(1, worlddata));
		}
	}

	public static class WorldVariables extends WorldSavedData {

		public static final String DATA_NAME = "${modid}_worldvars";

		<#list variables as var>
            <#if var.getScope().name() == "GLOBAL_WORLD">
                <#if var.getType().name() == "NUMBER">
        			public double ${var.getName()} = ${var.getValue()};
                <#elseif var.getType().name() == "LOGIC">
					public boolean ${var.getName()} = ${var.getValue()};
                <#elseif var.getType().name() == "STRING">
       				 public String ${var.getName()} ="${JavaConventions.escapeStringForJava(var.getValue())}";
				<#elseif var.getType().name() == "ITEMSTACK">
					public ItemStack ${var.getName()} = ItemStack.EMPTY;
                </#if>
            </#if>
        </#list>

		public WorldVariables() {
			super(DATA_NAME);
		}

		public WorldVariables(String s) {
			super(s);
		}

		@Override public void read(CompoundNBT nbt) {
			<#list variables as var>
                <#if var.getScope().name() == "GLOBAL_WORLD">
                    <#if var.getType().name() == "NUMBER">
                        ${var.getName()} =nbt.getDouble("${var.getName()}");
                    <#elseif var.getType().name() == "LOGIC">
                        ${var.getName()} =nbt.getBoolean("${var.getName()}");
                    <#elseif var.getType().name() == "STRING">
                        ${var.getName()} =nbt.getString("${var.getName()}");
					<#elseif var.getType().name() == "ITEMSTACK">
						${var.getName()} = ItemStack.read(nbt.getCompound("${var.getName()}"));
                    </#if>
                </#if>
            </#list>
		}

		@Override public CompoundNBT write(CompoundNBT nbt) {
			<#list variables as var>
                <#if var.getScope().name() == "GLOBAL_WORLD">
                    <#if var.getType().name() == "NUMBER">
        				nbt.putDouble("${var.getName()}" , ${var.getName()});
                    <#elseif var.getType().name() == "LOGIC">
						nbt.putBoolean("${var.getName()}" , ${var.getName()});
                    <#elseif var.getType().name() == "STRING">
						nbt.putString("${var.getName()}" , ${var.getName()});
					<#elseif var.getType().name() == "ITEMSTACK">
						nbt.put("${var.getName()}", ${var.getName()}.write(new CompoundNBT()));
                    </#if>
                </#if>
            </#list>
			return nbt;
		}

		public void syncData(IWorld world) {
			this.markDirty();

			if (world instanceof World && !world.isRemote())
				${JavaModName}.PACKET_HANDLER.send(PacketDistributor.DIMENSION.with(((World) world)::getDimensionKey), new WorldSavedDataSyncMessage(1, this));
		}

		static WorldVariables clientSide = new WorldVariables();

		public static WorldVariables get(IWorld world) {
			if (world instanceof ServerWorld) {
        		return ((ServerWorld) world).getSavedData().getOrCreate(WorldVariables::new, DATA_NAME);
        	} else {
				return clientSide;
        	}
		}

	}

	public static class MapVariables extends WorldSavedData {

		public static final String DATA_NAME = "${modid}_mapvars";

		<#list variables as var>
            <#if var.getScope().name() == "GLOBAL_MAP">
                <#if var.getType().name() == "NUMBER">
        			public double ${var.getName()} = ${var.getValue()};
                <#elseif var.getType().name() == "LOGIC">
					public boolean ${var.getName()} = ${var.getValue()};
                <#elseif var.getType().name() == "STRING">
       				 public String ${var.getName()} ="${JavaConventions.escapeStringForJava(var.getValue())}";
				<#elseif var.getType().name() == "ITEMSTACK">
					public ItemStack ${var.getName()} = ItemStack.EMPTY;
                </#if>
            </#if>
        </#list>

		public MapVariables() {
			super(DATA_NAME);
		}

		public MapVariables(String s) {
			super(s);
		}

		@Override public void read(CompoundNBT nbt) {
			<#list variables as var>
                <#if var.getScope().name() == "GLOBAL_MAP">
                    <#if var.getType().name() == "NUMBER">
                        ${var.getName()} =nbt.getDouble("${var.getName()}");
                    <#elseif var.getType().name() == "LOGIC">
                        ${var.getName()} =nbt.getBoolean("${var.getName()}");
                    <#elseif var.getType().name() == "STRING">
                        ${var.getName()} =nbt.getString("${var.getName()}");
					<#elseif var.getType().name() == "ITEMSTACK">
						${var.getName()} = ItemStack.read(nbt.getCompound("${var.getName()}"));
                    </#if>
                </#if>
            </#list>
		}

		@Override public CompoundNBT write(CompoundNBT nbt) {
			<#list variables as var>
                <#if var.getScope().name() == "GLOBAL_MAP">
                    <#if var.getType().name() == "NUMBER">
        				nbt.putDouble("${var.getName()}" , ${var.getName()});
                    <#elseif var.getType().name() == "LOGIC">
						nbt.putBoolean("${var.getName()}" , ${var.getName()});
                    <#elseif var.getType().name() == "STRING">
						nbt.putString("${var.getName()}" , ${var.getName()});
					<#elseif var.getType().name() == "ITEMSTACK">
						nbt.put("${var.getName()}", ${var.getName()}.write(new CompoundNBT()));
                    </#if>
                </#if>
            </#list>
			return nbt;
		}

		public void syncData(IWorld world) {
			this.markDirty();

			if (world instanceof World && !world.isRemote())
				${JavaModName}.PACKET_HANDLER.send(PacketDistributor.ALL.noArg(), new WorldSavedDataSyncMessage(0, this));
		}

		static MapVariables clientSide = new MapVariables();

		public static MapVariables get(IWorld world) {
			if (world instanceof IServerWorld) {
        		return ((IServerWorld) world).getWorld().getServer().getWorld(World.OVERWORLD).getSavedData().getOrCreate(MapVariables::new, DATA_NAME);
        	} else {
				return clientSide;
        	}
		}

	}

	public static class WorldSavedDataSyncMessage {

		public int type;
		public WorldSavedData data;

		public WorldSavedDataSyncMessage(PacketBuffer buffer) {
			this.type = buffer.readInt();
			this.data = this.type == 0 ? new MapVariables() : new WorldVariables();
			this.data.read(buffer.readCompoundTag());
		}

		public WorldSavedDataSyncMessage(int type, WorldSavedData data) {
			this.type = type;
			this.data = data;
		}

		public static void buffer(WorldSavedDataSyncMessage message, PacketBuffer buffer) {
			buffer.writeInt(message.type);
			buffer.writeCompoundTag(message.data.write(new CompoundNBT()));
		}

		public static void handler(WorldSavedDataSyncMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
			NetworkEvent.Context context = contextSupplier.get();
			context.enqueueWork(() -> {
				if (!context.getDirection().getReceptionSide().isServer()) {
					if (message.type == 0)
						MapVariables.clientSide = (MapVariables) message.data;
					else
						WorldVariables.clientSide = (WorldVariables) message.data;
				}
    		});
    		context.setPacketHandled(true);
		}

	}
	</#if>

	<#if w.hasVariablesOfScope("PLAYER_LIFETIME") || w.hasVariablesOfScope("PLAYER_PERSISTENT")>
	@CapabilityInject(PlayerVariables.class) public static Capability<PlayerVariables> PLAYER_VARIABLES_CAPABILITY = null;

	@SubscribeEvent public void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
    	if (event.getObject() instanceof PlayerEntity && !(event.getObject() instanceof FakePlayer))
			event.addCapability(new ResourceLocation("${modid}", "player_variables"), new PlayerVariablesProvider());
	}

	private static class PlayerVariablesProvider implements ICapabilitySerializable<INBT> {

		private final LazyOptional<PlayerVariables> instance = LazyOptional.of(PLAYER_VARIABLES_CAPABILITY::getDefaultInstance);

		@Override public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
			return cap == PLAYER_VARIABLES_CAPABILITY ? instance.cast() : LazyOptional.empty();
		}

		@Override public INBT serializeNBT() {
			return PLAYER_VARIABLES_CAPABILITY.getStorage().writeNBT(PLAYER_VARIABLES_CAPABILITY, this.instance.orElseThrow(RuntimeException::new), null);
		}

		@Override public void deserializeNBT(INBT nbt) {
			PLAYER_VARIABLES_CAPABILITY.getStorage().readNBT(PLAYER_VARIABLES_CAPABILITY, this.instance.orElseThrow(RuntimeException::new), null, nbt);
		}

	}

	private static class PlayerVariablesStorage implements Capability.IStorage<PlayerVariables> {

		@Override public INBT writeNBT(Capability<PlayerVariables> capability, PlayerVariables instance, Direction side) {
			CompoundNBT nbt = new CompoundNBT();
			<#list variables as var>
				<#if var.getScope().name() == "PLAYER_LIFETIME" || var.getScope().name() == "PLAYER_PERSISTENT">
					<#if var.getType().name() == "NUMBER">
			        	nbt.putDouble("${var.getName()}" , instance.${var.getName()});
					<#elseif var.getType().name() == "LOGIC">
						nbt.putBoolean("${var.getName()}" , instance.${var.getName()});
					<#elseif var.getType().name() == "STRING">
						nbt.putString("${var.getName()}" , instance.${var.getName()});
					<#elseif var.getType().name() == "ITEMSTACK">
						nbt.put("${var.getName()}", instance.${var.getName()}.write(new CompoundNBT()));
					</#if>
				</#if>
			</#list>
			return nbt;
		}

		@Override public void readNBT(Capability<PlayerVariables> capability, PlayerVariables instance, Direction side, INBT inbt) {
			CompoundNBT nbt = (CompoundNBT) inbt;
			<#list variables as var>
				<#if var.getScope().name() == "PLAYER_LIFETIME" || var.getScope().name() == "PLAYER_PERSISTENT">
					<#if var.getType().name() == "NUMBER">
						instance.${var.getName()} =nbt.getDouble("${var.getName()}");
					<#elseif var.getType().name() == "LOGIC">
						instance.${var.getName()} =nbt.getBoolean("${var.getName()}");
					<#elseif var.getType().name() == "STRING">
						instance.${var.getName()} =nbt.getString("${var.getName()}");
					<#elseif var.getType().name() == "ITEMSTACK">
						instance.${var.getName()} = ItemStack.read(nbt.getCompound("${var.getName()}"));
					</#if>
				</#if>
			</#list>
		}

	}

	public static class PlayerVariables {

		<#list variables as var>
			<#if var.getScope().name() == "PLAYER_LIFETIME" || var.getScope().name() == "PLAYER_PERSISTENT">
				<#if var.getType().name() == "NUMBER">
			public double ${var.getName()} = ${var.getValue()};
				<#elseif var.getType().name() == "LOGIC">
			public boolean ${var.getName()} = ${var.getValue()};
				<#elseif var.getType().name() == "STRING">
			 public String ${var.getName()} ="${JavaConventions.escapeStringForJava(var.getValue())}";
				<#elseif var.getType().name() == "ITEMSTACK">
			public ItemStack ${var.getName()} = ItemStack.EMPTY;
				</#if>
			</#if>
		</#list>

		public void syncPlayerVariables(Entity entity) {
			if (entity instanceof ServerPlayerEntity)
				${JavaModName}.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) entity), new PlayerVariablesSyncMessage(this));
		}

	}

	@SubscribeEvent public void onPlayerLoggedInSyncPlayerVariables(PlayerEvent.PlayerLoggedInEvent event) {
		if (!event.getPlayer().world.isRemote())
			((PlayerVariables) event.getPlayer().getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElse(new PlayerVariables())).syncPlayerVariables(event.getPlayer());
	}

	@SubscribeEvent public void onPlayerRespawnedSyncPlayerVariables(PlayerEvent.PlayerRespawnEvent event) {
		if (!event.getPlayer().world.isRemote())
			((PlayerVariables) event.getPlayer().getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElse(new PlayerVariables())).syncPlayerVariables(event.getPlayer());
	}

	@SubscribeEvent public void onPlayerChangedDimensionSyncPlayerVariables(PlayerEvent.PlayerChangedDimensionEvent event) {
		if (!event.getPlayer().world.isRemote())
			((PlayerVariables) event.getPlayer().getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElse(new PlayerVariables())).syncPlayerVariables(event.getPlayer());
	}

	@SubscribeEvent public void clonePlayer(PlayerEvent.Clone event) {
		if(event.isWasDeath()) {
			PlayerVariables original = ((PlayerVariables) event.getOriginal().getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElse(new PlayerVariables()));
			PlayerVariables clone = ((PlayerVariables) event.getEntity().getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElse(new PlayerVariables()));
			<#list variables as var>
				<#if var.getScope().name() == "PLAYER_PERSISTENT">
				clone.${var.getName()} = original.${var.getName()};
				</#if>
			</#list>
		}
	}

	public static class PlayerVariablesSyncMessage {

		public PlayerVariables data;

		public PlayerVariablesSyncMessage(PacketBuffer buffer) {
			this.data = new PlayerVariables();
			new PlayerVariablesStorage().readNBT(null, this.data, null, buffer.readCompoundTag());
		}

		public PlayerVariablesSyncMessage(PlayerVariables data) {
			this.data = data;
		}

		public static void buffer(PlayerVariablesSyncMessage message, PacketBuffer buffer) {
			buffer.writeCompoundTag((CompoundNBT) new PlayerVariablesStorage().writeNBT(null, message.data, null));
		}

		public static void handler(PlayerVariablesSyncMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
			NetworkEvent.Context context = contextSupplier.get();
			context.enqueueWork(() -> {
				if (!context.getDirection().getReceptionSide().isServer()) {
					PlayerVariables variables = ((PlayerVariables) Minecraft.getInstance().player.getCapability(PLAYER_VARIABLES_CAPABILITY, null).orElse(new PlayerVariables()));
					<#list variables as var>
						<#if var.getScope().name() == "PLAYER_LIFETIME" || var.getScope().name() == "PLAYER_PERSISTENT">
						variables.${var.getName()} = message.data.${var.getName()};
						</#if>
					</#list>
				}
			});
			context.setPacketHandled(true);
		}

	}

	</#if>

}
<#-- @formatter:on -->