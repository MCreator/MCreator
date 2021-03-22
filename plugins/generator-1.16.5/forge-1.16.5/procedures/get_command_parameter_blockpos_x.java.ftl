(new Object() {
            public double getX() {
                try {
                    return BlockPosArgument.getBlockPos(cmdargs, "${field$param}").getX();
                } catch (CommandSyntaxException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        }.getX())