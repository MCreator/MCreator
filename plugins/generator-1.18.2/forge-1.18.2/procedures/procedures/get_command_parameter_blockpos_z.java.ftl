(new Object() {
            public double getZ() {
                try {
                    return BlockPosArgument.getBlockPos(cmdargs, "${field$param}").getZ();
                } catch (CommandSyntaxException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        }.getZ())