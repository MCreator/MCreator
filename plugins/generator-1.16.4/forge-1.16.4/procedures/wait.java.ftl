new Object() {

            private int ticks;
            private float time;

            public Object init(float sec) {
                this.time = sec * 20F;
                MinecraftForge.EVENT_BUS.register(this);
                return this;
            }

            @SubscribeEvent
            public void tick(TickEvent.ServerTickEvent event) {
                if (event.phase == TickEvent.Phase.END) {
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
}.init((float) ${input$SEC});