<#function toTextComponent textToRender>
    <#if textToRender?starts_with("<t:") && textToRender?ends_with(">")>
        <#assign keyCandidate = textToRender.substring(3, textToRender.length() - 1)>
        <#if keyCandidate.replaceAll("[A-Za-z0-9._]*", "") == "">
            <#return "new TranslationTextComponent(\"" + keyCandidate + "\")">
        </#if>
    </#if>
    <#return "new StringTextComponent(\"" + textToRender + "\")">
</#function>

<#function translateTokens source>
    <#if source?starts_with("<t:") && source?ends_with(">")>
        <#assign keyCandidate = source.substring(3, source.length() - 1)>
        <#if keyCandidate.replaceAll("[A-Za-z0-9._]*", "") == "">
            <#return "new TranslationTextComponent(\"" + keyCandidate + "\")">
        </#if>
    </#if>
    <#local varTokens = source.toString().split("(?=<(VAR|ENBT|BNBT|energy|fluidlevel|t)|(?<=>))")>
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
                                            	TileEntity _ent = world.getTileEntity(pos);
                                            	if (_ent != null)
                                            		_ent.getCapability(CapabilityEnergy.ENERGY, null).ifPresent(capability -\\\\> _retval.set(capability.getEnergyStored()));
                                            	return _retval.get();
                                            }
                                        }.getEnergyStored(new BlockPos((int) x, (int) y, (int) z)))>">
        <#elseif token.toString()?starts_with("<fluidlevel")>
            <#assign sourceNew += "<(new Object() {
                                            public int getFluidTankLevel(BlockPos pos, int tank) {
                                            	AtomicInteger _retval = new AtomicInteger(0);
                                            	TileEntity _ent = world.getTileEntity(pos);
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
                                        	TileEntity tileEntity=world.getTileEntity(pos);
                                            if(tileEntity!=null) return tileEntity.getTileData().getDouble(tag);
                                            return 0;
                                        }
                                        }.getValue(new BlockPos((int) x, (int) y, (int) z), \"" + (token.replace("<BNBT:number:", "").replace(">", "").toString()) + "\"))>">
        <#elseif token.toString()?starts_with("<BNBT:integer:")>
            <#assign sourceNew += "<((int) new Object(){
                                        public double getValue(BlockPos pos, String tag){
                                            TileEntity tileEntity=world.getTileEntity(pos);
                                            if(tileEntity!=null) return tileEntity.getTileData().getDouble(tag);
                                            return 0;
                                        }
                                        }.getValue(new BlockPos((int) x, (int) y, (int) z), \"" + (token.replace("<BNBT:integer:", "").replace(">", "").toString()) + "\"))>">
        <#elseif token.toString()?starts_with("<BNBT:logic:")>
            <#assign sourceNew += "<(new Object(){
                                        public boolean getValue(BlockPos pos, String tag){
                                        	TileEntity tileEntity=world.getTileEntity(pos);
                                            if(tileEntity!=null) return tileEntity.getTileData().getBoolean(tag);
                                            return false;
                                        }
                                        }.getValue(new BlockPos((int) x, (int) y, (int) z), \"" + (token.replace("<BNBT:logic:", "").replace(">", "").toString()) + "\"))>">
        <#elseif token.toString()?starts_with("<BNBT:text:")>
            <#assign sourceNew += "<(new Object(){
                                        public String getValue(BlockPos pos, String tag){
                                        	TileEntity tileEntity=world.getTileEntity(pos);
                                            if(tileEntity!=null) return tileEntity.getTileData().getString(tag);
                                            return \"\";
                                        }
                                        }.getValue(new BlockPos((int) x, (int) y, (int) z), \"" + (token.replace("<BNBT:text:", "").replace(">", "").toString()) + "\"))>">
        <#elseif token.toString()?starts_with("<t:")> <#-- special handle for invalid translation tokens -->
            <#assign sourceNew += "\\\\<" + token.substring(1, token.length() - 1) + "\\\\>">
        <#else>
            <#assign sourceNew += token>
        </#if>
    </#list>
    <#return "\"" + sourceNew?replace(":text>", ".getText()+\"")
                ?replace("(?<!\\\\)<", "\"+", "r")?replace("(?<!\\\\)>", "+\"", "r")
                ?replace("\\\\<", "<")?replace("\\\\>", ">") + "\"">
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