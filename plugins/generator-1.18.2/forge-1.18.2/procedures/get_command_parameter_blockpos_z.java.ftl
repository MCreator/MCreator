(new Object() {
            public double getZ() {
                try {
                    return BlockPosArgument.getLoadedBlockPos(cmdargs, "${field$param}").getZ();
                } catch (CommandSyntaxException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        }.getZ())