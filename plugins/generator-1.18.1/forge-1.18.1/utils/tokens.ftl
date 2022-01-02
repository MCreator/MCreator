<#function translateTokens source>
    <#local varTokens = source.toString().split("(?=<(VAR|ENBT|BNBT|energy|fluidlevel)|(?<=>))")>
    <#assign sourceNew = "">
    <#list varTokens as token>
        <#if token.toString()?starts_with("<VAR:integer:")>
            <#assign sourceNew += "<(int) "+translateGlobalVarName(token.replace("<VAR:integer:", "").replace(">", "").toString())+">">
        <#elseif token.toString()?starts_with("<VAR:")>
            <#assign sourceNew += "<"+translateGlobalVarName(token.replace("<VAR:", "").replace(">", "").toString())+">">
        <#elseif token.toString()?starts_with("<energy")>
            <#assign sourceNew += "<(new Object(){
                                            public int getEnergyStored(BlockPos pos) {
                                            	AtomicInteger _retval = new AtomicInteger(0);
                                            	BlockEntity _ent = world.getBlockEntity(pos);
                                            	if (_ent != null)
                                            		_ent.getCapability(CapabilityEnergy.ENERGY, null).ifPresent(capability -\\\\> _retval.set(capability.getEnergyStored()));
                                            	return _retval.get();
                                            }
                                        }.getEnergyStored(new BlockPos((int) x, (int) y, (int) z)))>">
        <#elseif token.toString()?starts_with("<fluidlevel")>
            <#assign sourceNew += "<(new Object() {
                                            public int getFluidTankLevel(BlockPos pos, int tank) {
                                            	AtomicInteger _retval = new AtomicInteger(0);
                                            	BlockEntity _ent = world.getBlockEntity(pos);
                                            	if (_ent != null)
                                            		_ent.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null).ifPresent(capability -\\\\>
                                            			_retval.set(capability.getFluidInTank(tank).getAmount()));
                                            	return _retval.get();
                                            }
                                        }.getFluidTankLevel(new BlockPos((int) x, (int) y, (int) z), 1))>">
        <#elseif token.toString()?starts_with("<ENBT:number:")>
            <#assign sourceNew += "<(entity.getPersistentData().getDouble(\"" + (token.replace("<ENBT:number:", "").replace(">", "").toString()) + "\"))>">
        <#elseif token.toString()?starts_with("<ENBT:integer:")>
            <#assign sourceNew += "<((int)entity.getPersistentData().getDouble(\"" + (token.replace("<ENBT:integer:", "").replace(">", "").toString()) + "\"))>">
        <#elseif token.toString()?starts_with("<ENBT:logic:")>
            <#assign sourceNew += "<(entity.getPersistentData().getBoolean(\"" + (token.replace("<ENBT:logic:", "").replace(">", "").toString()) + "\"))>">
        <#elseif token.toString()?starts_with("<ENBT:text:")>
            <#assign sourceNew += "<(entity.getPersistentData().getString(\"" + (token.replace("<ENBT:text:", "").replace(">", "").toString()) + "\"))>">
        <#elseif token.toString()?starts_with("<BNBT:number:")>
            <#assign sourceNew += "<(new Object(){
                                        public double getValue(BlockPos pos, String tag){
                                        	BlockEntity BlockEntity=world.getBlockEntity(pos);
                                            if(BlockEntity!=null) return BlockEntity.getTileData().getDouble(tag);
                                            return 0;
                                        }
                                        }.getValue(new BlockPos((int) x, (int) y, (int) z), \"" + (token.replace("<BNBT:number:", "").replace(">", "").toString()) + "\"))>">
        <#elseif token.toString()?starts_with("<BNBT:integer:")>
            <#assign sourceNew += "<((int) new Object(){
                                        public double getValue(BlockPos pos, String tag){
                                            BlockEntity BlockEntity=world.getBlockEntity(pos);
                                            if(BlockEntity!=null) return BlockEntity.getTileData().getDouble(tag);
                                            return 0;
                                        }
                                        }.getValue(new BlockPos((int) x, (int) y, (int) z), \"" + (token.replace("<BNBT:integer:", "").replace(">", "").toString()) + "\"))>">
        <#elseif token.toString()?starts_with("<BNBT:logic:")>
            <#assign sourceNew += "<(new Object(){
                                        public boolean getValue(BlockPos pos, String tag){
                                        	BlockEntity BlockEntity=world.getBlockEntity(pos);
                                            if(BlockEntity!=null) return BlockEntity.getTileData().getBoolean(tag);
                                            return false;
                                        }
                                        }.getValue(new BlockPos((int) x, (int) y, (int) z), \"" + (token.replace("<BNBT:logic:", "").replace(">", "").toString()) + "\"))>">
        <#elseif token.toString()?starts_with("<BNBT:text:")>
            <#assign sourceNew += "<(new Object(){
                                        public String getValue(BlockPos pos, String tag){
                                        	BlockEntity BlockEntity=world.getBlockEntity(pos);
                                            if(BlockEntity!=null) return BlockEntity.getTileData().getString(tag);
                                            return \"\";
                                        }
                                        }.getValue(new BlockPos((int) x, (int) y, (int) z), \"" + (token.replace("<BNBT:text:", "").replace(">", "").toString()) + "\"))>">
        <#else>
            <#assign sourceNew += token>
        </#if>
    </#list>
    <#return sourceNew?replace(":text>", ".getValue()+\"")
                ?replace("(?<!\\\\)<", "\"+", "r")?replace("(?<!\\\\)>", "+\"", "r")
                ?replace("\\\\<", "<")?replace("\\\\>", ">")
    >
</#function>

<#function translateGlobalVarName varName>
    <#local scope = generator.getVariableElementByName(varName).getScope().name()>
    <#if scope == "GLOBAL_SESSION">
        <#return "(${JavaModName}Variables.${varName})">
    <#elseif scope == "GLOBAL_WORLD">
        <#return "(${JavaModName}Variables.WorldVariables.get(world).${varName})">
    <#elseif scope == "GLOBAL_MAP">
        <#return "(${JavaModName}Variables.MapVariables.get(world).${varName})">
    <#elseif scope == "PLAYER_LIFETIME" || scope == "PLAYER_PERSISTENT">
        <#return "((entity.getCapability(${JavaModName}Variables.PLAYER_VARIABLES_CAPABILITY, null).orElse(new ${JavaModName}Variables.PlayerVariables())).${varName})">
    </#if>
</#function>