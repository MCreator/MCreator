(new Object() {
            public Entity getEntity() {
                try {
                    return EntityArgument.getEntity(cmdargs, "${field$param}");
                } catch (CommandSyntaxException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }.getEntity())