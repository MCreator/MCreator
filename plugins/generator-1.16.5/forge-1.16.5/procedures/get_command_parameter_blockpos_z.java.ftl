(new Object() {
            public double getZ() {
                try {
                    return BlockPosArgument.getBlockPos(arguments, "${field$param}").getZ();
                } catch (CommandSyntaxException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        }.getZ())