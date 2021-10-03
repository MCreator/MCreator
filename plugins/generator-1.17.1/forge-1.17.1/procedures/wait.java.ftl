<#-- @formatter:off -->
new Object() {

    private int ticks = 0;
    private float waitTicks;
    private LevelAccessor world;

    public void start(LevelAccessor world, int waitTicks) {
		this.waitTicks = waitTicks;
		MinecraftForge.EVENT_BUS.register(this);
		this.world = world;
	}

    @SubscribeEvent
    public void tick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            this.ticks += 1;
            if (this.ticks >= this.waitTicks)
                run();
        }
    }

    private void run() {
        ${statement$do}
        MinecraftForge.EVENT_BUS.unregister(this);
    }

}.start(world, ${opt.toInt(input$ticks)});
<#-- @formatter:on -->