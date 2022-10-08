<#-- @formatter:off -->
class WaitHandler${customBlockIndex} {
	private int ticks = 0;
	private float waitTicks;
	private LevelAccessor world;

	public void start(LevelAccessor world, int waitTicks) {
		this.waitTicks = waitTicks;
		this.world = world;

		MinecraftForge.EVENT_BUS.register(WaitHandler${customBlockIndex}.this);
	}

	@SubscribeEvent
	public void tick(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			WaitHandler${customBlockIndex}.this.ticks += 1;
			if (WaitHandler${customBlockIndex}.this.ticks >= WaitHandler${customBlockIndex}.this.waitTicks)
				run();
		}
	}

	private void run() {
		MinecraftForge.EVENT_BUS.unregister(WaitHandler${customBlockIndex}.this);

        ${statement$do}
	}
}

new WaitHandler${customBlockIndex}().start(world, ${opt.toInt(input$ticks)});
<#-- @formatter:on -->