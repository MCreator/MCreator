<#-- @formatter:off -->
package ${package};

public class ${JavaModName}Variables {

	<#list variables as var>
	    <#if var.getScope().name() == "GLOBAL_SESSION">
	        <#if var.getType().name() == "NUMBER">
	        public static double ${var.getName()} = ${var.getValue()};
	        <#elseif var.getType().name() == "LOGIC">
	        public static boolean ${var.getName()} = ${var.getValue()};
	        <#elseif var.getType().name() == "STRING">
	        public static String ${var.getName()} ="${JavaConventions.escapeStringForJava(var.getValue())}";
	        </#if>
	    </#if>
	</#list>

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
                </#if>
            </#if>
        </#list>

		public MapVariables() {
			super(DATA_NAME);
		}

		public MapVariables(String s) {
			super(s);
		}

		@Override public void readFromNBT(NBTTagCompound nbt) {
			<#list variables as var>
                <#if var.getScope().name() == "GLOBAL_MAP">
                    <#if var.getType().name() == "NUMBER">
                        ${var.getName()} =nbt.getDouble("${var.getName()}" );
                    <#elseif var.getType().name() == "LOGIC">
                        ${var.getName()} =nbt.getBoolean("${var.getName()}" );
                    <#elseif var.getType().name() == "STRING">
                        ${var.getName()} =nbt.getString("${var.getName()}" );
                    </#if>
                </#if>
            </#list>
		}

		@Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
			<#list variables as var>
                <#if var.getScope().name() == "GLOBAL_MAP">
                    <#if var.getType().name() == "NUMBER">
        				nbt.setDouble("${var.getName()}" , ${var.getName()});
                    <#elseif var.getType().name() == "LOGIC">
						nbt.setBoolean("${var.getName()}" , ${var.getName()});
                    <#elseif var.getType().name() == "STRING">
						nbt.setString("${var.getName()}" , ${var.getName()});
                    </#if>
                </#if>
            </#list>
			return nbt;
		}

		public void syncData(World world) {
			this.markDirty();

			if (world.isRemote) {
				${JavaModName}.PACKET_HANDLER.sendToServer(new WorldSavedDataSyncMessage(0, this));
			} else {
				${JavaModName}.PACKET_HANDLER.sendToAll(new WorldSavedDataSyncMessage(0, this));
			}
		}

		public static MapVariables get(World world) {
			MapVariables instance = (MapVariables) world.getMapStorage().getOrLoadData(MapVariables.class, DATA_NAME);
			if (instance == null) {
				instance = new MapVariables();
				world.getMapStorage().setData(DATA_NAME, instance);
			}
			return instance;
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
                </#if>
            </#if>
        </#list>

		public WorldVariables() {
			super(DATA_NAME);
		}

		public WorldVariables(String s) {
			super(s);
		}

		@Override public void readFromNBT(NBTTagCompound nbt) {
			<#list variables as var>
                <#if var.getScope().name() == "GLOBAL_WORLD">
                    <#if var.getType().name() == "NUMBER">
                        ${var.getName()} =nbt.getDouble("${var.getName()}" );
                    <#elseif var.getType().name() == "LOGIC">
                        ${var.getName()} =nbt.getBoolean("${var.getName()}" );
                    <#elseif var.getType().name() == "STRING">
                        ${var.getName()} =nbt.getString("${var.getName()}" );
                    </#if>
                </#if>
            </#list>
		}

		@Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
			<#list variables as var>
                <#if var.getScope().name() == "GLOBAL_WORLD">
                    <#if var.getType().name() == "NUMBER">
        				nbt.setDouble("${var.getName()}" , ${var.getName()});
                    <#elseif var.getType().name() == "LOGIC">
						nbt.setBoolean("${var.getName()}" , ${var.getName()});
                    <#elseif var.getType().name() == "STRING">
						nbt.setString("${var.getName()}" , ${var.getName()});
                    </#if>
                </#if>
            </#list>
			return nbt;
		}

		public void syncData(World world) {
			this.markDirty();

			if (world.isRemote) {
				${JavaModName}.PACKET_HANDLER.sendToServer(new WorldSavedDataSyncMessage(1, this));
			} else {
				${JavaModName}.PACKET_HANDLER.sendToDimension(new WorldSavedDataSyncMessage(1, this), world.provider.getDimension());
			}
		}

		public static WorldVariables get(World world) {
			WorldVariables instance = (WorldVariables) world.getPerWorldStorage()
					.getOrLoadData(WorldVariables.class, DATA_NAME);
			if (instance == null) {
				instance = new WorldVariables();
				world.getPerWorldStorage().setData(DATA_NAME, instance);
			}
			return instance;
		}

	}

	public static class WorldSavedDataSyncMessageHandler
			implements IMessageHandler<WorldSavedDataSyncMessage, IMessage> {

		@Override public IMessage onMessage(WorldSavedDataSyncMessage message, MessageContext context) {
			if (context.side == Side.SERVER)
				context.getServerHandler().player.getServerWorld().addScheduledTask(()
						-> syncData(message, context, context.getServerHandler().player.world));
			else
				Minecraft.getMinecraft().addScheduledTask(()
						-> syncData(message, context, Minecraft.getMinecraft().player.world));

			return null;
		}

		private void syncData(WorldSavedDataSyncMessage message, MessageContext context, World world) {
			if (context.side == Side.SERVER) {
				message.data.markDirty();
				if (message.type == 0)
					${JavaModName}.PACKET_HANDLER.sendToAll(message);
				else
					${JavaModName}.PACKET_HANDLER.sendToDimension(message, world.provider.getDimension());
			}

			if (message.type == 0) {
				world.getMapStorage().setData(MapVariables.DATA_NAME, message.data);
			} else {
				world.getPerWorldStorage().setData(WorldVariables.DATA_NAME, message.data);
			}
		}
	}

	public static class WorldSavedDataSyncMessage implements IMessage {

		public int type;
		public WorldSavedData data;

		public WorldSavedDataSyncMessage() {
		}

		public WorldSavedDataSyncMessage(int type, WorldSavedData data) {
			this.type = type;
			this.data = data;
		}

		@Override public void toBytes(io.netty.buffer.ByteBuf buf) {
			buf.writeInt(this.type);
			ByteBufUtils.writeTag(buf, this.data.writeToNBT(new NBTTagCompound()));
		}

		@Override public void fromBytes(io.netty.buffer.ByteBuf buf) {
			this.type = buf.readInt();
			if (this.type == 0)
				this.data = new MapVariables();
			else
				this.data = new WorldVariables();
			this.data.readFromNBT(ByteBufUtils.readTag(buf));
		}

	}

}
<#-- @formatter:on -->