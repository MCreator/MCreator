<#macro makeBoundingBox positiveBoxes negativeBoxes noOffset facing>
    return <#if negativeBoxes?size != 0>Shapes.join(</#if>
    Shapes.or(
    <#list positiveBoxes as box>
        <@makeCuboid box facing/> <#if box?has_next>,</#if>
    </#list>)
    <#if negativeBoxes?size != 0>, Shapes.or(
        <#list negativeBoxes as box>
            <@makeCuboid box facing/> <#if box?has_next>,</#if>
        </#list>), BooleanOp.ONLY_FIRST)</#if>
    <#if !noOffset>.move(offset.x, offset.y, offset.z)</#if>;
</#macro>

<#macro boundingBoxWithRotation positiveBoxes negativeBoxes noOffset rotationMode>
    <#if rotationMode == 0>
        <@makeBoundingBox positiveBoxes negativeBoxes noOffset "north"/>
    <#elseif rotationMode != 5>
        switch ((Direction) state.get(FACING)) {
            case SOUTH:
            default:
                <@makeBoundingBox positiveBoxes negativeBoxes noOffset "south"/>
            case NORTH:
                <@makeBoundingBox positiveBoxes negativeBoxes noOffset "north"/>
            case EAST:
                <@makeBoundingBox positiveBoxes negativeBoxes noOffset "east"/>
            case WEST:
                <@makeBoundingBox positiveBoxes negativeBoxes noOffset "west"/>
            <#if rotationMode == 2 || rotationMode == 4>
                case UP:
                    <@makeBoundingBox positiveBoxes negativeBoxes noOffset "up"/>
                case DOWN:
                    <@makeBoundingBox positiveBoxes negativeBoxes noOffset "down"/>
            </#if>
        }
    <#else>
        switch ((Direction.Axis) state.get(AXIS)) {
            case X:
                <@makeBoundingBox positiveBoxes negativeBoxes noOffset "x"/>
            case Y:
            default:
                <@makeBoundingBox positiveBoxes negativeBoxes noOffset "y"/>
            case Z:
                <@makeBoundingBox positiveBoxes negativeBoxes noOffset "z"/>
        }
    </#if>
</#macro>

<#macro makeCuboid box facing>
    <#if facing == "south">
        box(${16 - box.mx}, ${box.my}, ${16 - box.mz}, ${16 - box.Mx}, ${box.My}, ${16 - box.Mz})
    <#elseif facing == "east">
        box(${16 - box.mz}, ${box.my}, ${box.mx}, ${16 - box.Mz}, ${box.My}, ${box.Mx})
    <#elseif facing == "west">
        box(${box.mz}, ${box.my}, ${16 - box.mx}, ${box.Mz}, ${box.My}, ${16 - box.Mx})
    <#elseif facing == "up">
        box(${box.mx}, ${16 - box.mz}, ${box.my}, ${box.Mx}, ${16 - box.Mz}, ${box.My})
    <#elseif facing == "down" || facing == "z">
        box(${box.mx}, ${box.mz}, ${16 - box.my}, ${box.Mx}, ${box.Mz}, ${16 - box.My})
    <#elseif facing == "x">
        box(${box.my}, ${box.mz}, ${box.mx}, ${box.My}, ${box.Mz}, ${box.Mx})
    <#else>
        box(${box.mx}, ${box.my}, ${box.mz}, ${box.Mx}, ${box.My}, ${box.Mz})
    </#if>
</#macro>