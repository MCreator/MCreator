<#-- @formatter:off -->
class ${parent.getName()}Wait${customBlockIndex} {
	private int ticks = 0;
	private float waitTicks;
	private LevelAccessor world;

	public void start(LevelAccessor world, int waitTicks) {
		this.waitTicks = waitTicks;
		this.world = world;

		MinecraftForge.EVENT_BUS.register(${parent.getName()}Wait${customBlockIndex}.this);
	}

	@SubscribeEvent
	public void tick(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			${parent.getName()}Wait${customBlockIndex}.this.ticks += 1;
			if (${parent.getName()}Wait${customBlockIndex}.this.ticks >= ${parent.getName()}Wait${customBlockIndex}.this.waitTicks)
				run();
		}
	}

	private void run() {
		MinecraftForge.EVENT_BUS.unregister(${parent.getName()}Wait${customBlockIndex}.this);

        ${statement$do}
	}
}

new ${parent.getName()}Wait${customBlockIndex}().start(world, ${opt.toInt(input$ticks)});
<#-- @formatter:on -->