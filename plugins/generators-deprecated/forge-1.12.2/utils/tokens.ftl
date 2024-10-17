<#function translateTokens source>
    <#local varTokens = source.toString().split("(?=<(VAR|ENBT|BNBT)|(?<=>))")>
    <#assign sourceNew = "">
    <#list varTokens as token>
        <#if token.toString()?starts_with("<VAR:")>
            <#assign sourceNew += "<"+translateGlobalVarName(token.replace("<VAR:", "").replace(">", "").toString())+">">
        <#elseif token.toString()?starts_with("<ENBT:number:")>
            <#assign sourceNew += "<(entity.getEntityData().getDouble(\"" + (token.replace("<ENBT:number:", "").replace(">", "").toString()) + "\"))>">
        <#elseif token.toString()?starts_with("<ENBT:integer:")>
            <#assign sourceNew += "<((int)entity.getEntityData().getDouble(\"" + (token.replace("<ENBT:integer:", "").replace(">", "").toString()) + "\"))>">
        <#elseif token.toString()?starts_with("<ENBT:logic:")>
            <#assign sourceNew += "<(entity.getEntityData().getBoolean(\"" + (token.replace("<ENBT:logic:", "").replace(">", "").toString()) + "\"))>">
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
        <#else>
            <#assign sourceNew += token>
        </#if>
    </#list>
    <#return sourceNew?replace(":text>", ".getText()+\"")
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
    </#if>
</#function>