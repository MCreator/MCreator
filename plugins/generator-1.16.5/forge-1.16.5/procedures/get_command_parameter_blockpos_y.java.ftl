(new Object() {
            public double getY() {
                try {
                    return BlockPosArgument.getBlockPos(cmdargs, "${field$param}").getY();
                } catch (CommandSyntaxException e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        }.getY())