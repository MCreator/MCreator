<#macro makeBoundingBox positiveBoxes negativeBoxes noOffset facing pitchType="wall">
    return <#if negativeBoxes?size != 0>VoxelShapes.combineAndSimplify(</#if>
    VoxelShapes.or(
    <#list positiveBoxes as box>
        <@makeCuboid box facing pitchType/> <#sep>,
    </#list>)
    <#if negativeBoxes?size != 0>, VoxelShapes.or(
        <#list negativeBoxes as box>
            <@makeCuboid box facing pitchType/> <#sep>,
        </#list>), IBooleanFunction.ONLY_FIRST)</#if>
    <#if !noOffset>.withOffset(offset.x, offset.y, offset.z)</#if>;
</#macro>

<#macro checkPitchSupport positiveBoxes negativeBoxes noOffset facing enablePitch>
    <#if enablePitch>
        switch ((AttachFace) state.get(FACE)) {
            case FLOOR:
                <@makeBoundingBox positiveBoxes negativeBoxes noOffset facing "floor"/>
            case CEILING:
                <@makeBoundingBox positiveBoxes negativeBoxes noOffset facing "ceiling"/>
            case WALL:
            default:
                <@makeBoundingBox positiveBoxes negativeBoxes noOffset facing "wall"/>
        }
    <#else>
        <@makeBoundingBox positiveBoxes negativeBoxes noOffset facing/>
    </#if>
</#macro>

<#macro boundingBoxWithRotation positiveBoxes negativeBoxes noOffset rotationMode enablePitch=false>
    <#if rotationMode == 0>
        <@makeBoundingBox positiveBoxes negativeBoxes noOffset "north"/>
    <#elseif rotationMode != 5>
        <#assign pitch = (rotationMode == 1 || rotationMode == 3) && enablePitch>
        switch ((Direction) state.get(FACING)) {
            case SOUTH:
            default:
                <@checkPitchSupport positiveBoxes negativeBoxes noOffset "south" pitch/>
            case NORTH:
                <@checkPitchSupport positiveBoxes negativeBoxes noOffset "north" pitch/>
            case EAST:
                <@checkPitchSupport positiveBoxes negativeBoxes noOffset "east" pitch/>
            case WEST:
                <@checkPitchSupport positiveBoxes negativeBoxes noOffset "west" pitch/>
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

<#macro makeCuboid box facing pitchType>
    <#if facing == "south">
        <#if pitchType == "floor">
            makeCuboidShape(${16 - box.mx}, ${box.my}, ${16 - box.mz}, ${16 - box.Mx}, ${box.My}, ${16 - box.Mz})
        <#elseif pitchType == "ceiling">
            makeCuboidShape(${16 - box.mx}, ${16 - box.My}, ${box.Mz}, ${16 - box.Mx}, ${16 - box.my}, ${box.mz})
        <#elseif pitchType == "wall">
            makeCuboidShape(${16 - box.mx}, ${box.mz}, ${box.My}, ${16 - box.Mx}, ${box.Mz}, ${box.my})
        </#if>
    <#elseif facing == "east">
        <#if pitchType == "floor">
            makeCuboidShape(${16 - box.mz}, ${box.my}, ${box.mx}, ${16 - box.Mz}, ${box.My}, ${box.Mx})
        <#elseif pitchType == "ceiling">
            makeCuboidShape(${box.Mz}, ${16 - box.My}, ${box.mx}, ${box.mz}, ${16 - box.my}, ${box.Mx})
        <#elseif pitchType == "wall">
            makeCuboidShape(${box.My}, ${box.mz}, ${box.mx}, ${box.my}, ${box.Mz}, ${box.Mx})
        </#if>
    <#elseif facing == "west">
        <#if pitchType == "floor">
            makeCuboidShape(${box.mz}, ${box.my}, ${16 - box.mx}, ${box.Mz}, ${box.My}, ${16 - box.Mx})
        <#elseif pitchType == "ceiling">
            makeCuboidShape(${16 - box.Mz}, ${16 - box.My}, ${16 - box.mx}, ${16 - box.mz}, ${16 - box.my}, ${16 - box.Mx})
        <#elseif pitchType == "wall">
            makeCuboidShape(${16 - box.my}, ${box.Mz}, ${16 - box.mx}, ${16 - box.My}, ${box.mz}, ${16 - box.Mx})
        </#if>
    <#elseif facing == "up">
        makeCuboidShape(${box.mx}, ${16 - box.mz}, ${box.my}, ${box.Mx}, ${16 - box.Mz}, ${box.My})
    <#elseif facing == "down" || facing == "z">
        makeCuboidShape(${box.mx}, ${box.mz}, ${16 - box.my}, ${box.Mx}, ${box.Mz}, ${16 - box.My})
    <#elseif facing == "x">
        makeCuboidShape(${box.my}, ${box.mz}, ${box.mx}, ${box.My}, ${box.Mz}, ${box.Mx})
    <#else>
        <#if pitchType == "floor">
            makeCuboidShape(${box.mx}, ${box.my}, ${box.mz}, ${box.Mx}, ${box.My}, ${box.Mz})
        <#elseif pitchType == "ceiling">
            makeCuboidShape(${box.mx}, ${16 - box.My}, ${16 - box.Mz}, ${box.Mx}, ${16 - box.my}, ${16 - box.mz})
        <#elseif pitchType == "wall">
            makeCuboidShape(${box.mx}, ${box.Mz}, ${16 - box.my}, ${box.Mx}, ${box.mz}, ${16 - box.My})
        </#if>
    </#if>
</#macro>