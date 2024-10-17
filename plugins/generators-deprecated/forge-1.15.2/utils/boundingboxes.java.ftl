<#macro makeBoundingBox positiveBoxes negativeBoxes noOffset facing>
    return <#if negativeBoxes?size != 0>VoxelShapes.combineAndSimplify(</#if>
    VoxelShapes.or(
    <#list positiveBoxes as box>
        <@makeCuboid box facing/> <#if box?has_next>,</#if>
    </#list>)
    <#if negativeBoxes?size != 0>, VoxelShapes.or(
        <#list negativeBoxes as box>
            <@makeCuboid box facing/> <#if box?has_next>,</#if>
        </#list>), IBooleanFunction.ONLY_FIRST)</#if>
    <#if !noOffset>.withOffset(offset.x, offset.y, offset.z)</#if>;
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
        makeCuboidShape(${16 - box.mx}, ${box.my}, ${16 - box.mz}, ${16 - box.Mx}, ${box.My}, ${16 - box.Mz})
    <#elseif facing == "east">
        makeCuboidShape(${16 - box.mz}, ${box.my}, ${box.mx}, ${16 - box.Mz}, ${box.My}, ${box.Mx})
    <#elseif facing == "west">
        makeCuboidShape(${box.mz}, ${box.my}, ${16 - box.mx}, ${box.Mz}, ${box.My}, ${16 - box.Mx})
    <#elseif facing == "up">
        makeCuboidShape(${box.mx}, ${16 - box.mz}, ${box.my}, ${box.Mx}, ${16 - box.Mz}, ${box.My})
    <#elseif facing == "down" || facing == "z">
        makeCuboidShape(${box.mx}, ${box.mz}, ${16 - box.my}, ${box.Mx}, ${box.Mz}, ${16 - box.My})
    <#elseif facing == "x">
        makeCuboidShape(${box.my}, ${box.mz}, ${box.mx}, ${box.My}, ${box.Mz}, ${box.Mx})
    <#else>
        makeCuboidShape(${box.mx}, ${box.my}, ${box.mz}, ${box.Mx}, ${box.My}, ${box.Mz})
    </#if>
</#macro>