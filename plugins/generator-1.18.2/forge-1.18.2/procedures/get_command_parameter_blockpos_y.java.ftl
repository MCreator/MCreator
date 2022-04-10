(new Object() {
            public double getY() {
                try {
                    return BlockPosArgument.getLoadedBlockPos(cmdargs, "${field$param}").getY();
                } catch (CommandSyntaxException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        }.getY())