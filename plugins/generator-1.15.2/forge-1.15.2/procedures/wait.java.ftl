new Object() {

            private int ticks;
            private float time;

            public Object init() {
                this.time = ${input$SEC} * 20F;
                MinecraftForge.EVENT_BUS.register(this);
                return this;
            }

            @SubscribeEvent
            public void worldTick(TickEvent.WorldTickEvent event) {
                if (!event.world.isRemote) {
                    this.ticks += 1;
                    if (this.ticks >= this.time) {
                        run();
                    }
                }
            }

            private void run() {
                ${statement$DO}
                MinecraftForge.EVENT_BUS.unregister(this);
            }
}.init();