(new Object() {
            public double getX() {
                try {
                    return BlockPosArgument.getLoadedBlockPos(cmdargs, "${field$param}").getX();
                } catch (CommandSyntaxException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        }.getX())