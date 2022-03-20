<#macro makeBoundingBox positiveBoxes negativeBoxes noOffset facing pitchType="none">
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
    <#if (rotationMode == 1 || rotationMode == 3) && enablePitch>
        switch ((AttachFace) state.get(FACE)) {
            case FLOOR:
            default:
                <@makeBoundingBox positiveBoxes negativeBoxes noOffset facing "floor"/>
            case WALL:
                <@makeBoundingBox positiveBoxes negativeBoxes noOffset facing "wall"/>
            case CEILING:
                <@makeBoundingBox positiveBoxes negativeBoxes noOffset facing "ceiling"/>
        }
    <#else>
        <@makeBoundingBox positiveBoxes negativeBoxes noOffset facing/>
    </#if>
</#macro>

<#macro boundingBoxWithRotation positiveBoxes negativeBoxes noOffset rotationMode enablePitch=false>
    <#if rotationMode == 0>
        <@makeBoundingBox positiveBoxes negativeBoxes noOffset "north"/>
    <#elseif rotationMode != 5>
        switch ((Direction) state.get(FACING)) {
            case SOUTH:
            default:
                <@checkPitchSupport positiveBoxes negativeBoxes noOffset "south" enablePitch/>
            case NORTH:
                <@checkPitchSupport positiveBoxes negativeBoxes noOffset "north" enablePitch/>
            case EAST:
                <@checkPitchSupport positiveBoxes negativeBoxes noOffset "east" enablePitch/>
            case WEST:
                <@checkPitchSupport positiveBoxes negativeBoxes noOffset "west" enablePitch/>
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
        <#elseif pitchType == "wall">
            makeCuboidShape(${16 - box.mx}, ${16 - box.mz}, ${16 - box.My}, ${16 - box.Mx}, ${16 - box.Mz}, ${16 - box.my})
        <#elseif pitchType == "ceiling">
            makeCuboidShape(${16 - box.mx}, ${16 - box.My}, ${box.Mz}, ${16 - box.Mx}, ${16 - box.my}, ${box.mz})
        <#else>
            makeCuboidShape(${16 - box.mx}, ${box.my}, ${16 - box.mz}, ${16 - box.Mx}, ${box.My}, ${16 - box.Mz})
        </#if>
    <#elseif facing == "east">
        <#if pitchType == "floor">
            makeCuboidShape(${16 - box.mz}, ${box.my}, ${box.mx}, ${16 - box.Mz}, ${box.My}, ${box.Mx})
        <#elseif pitchType == "wall">
            makeCuboidShape(${16 - box.My}, ${16 - box.mz}, ${box.mx}, ${16 - box.my}, ${16 - box.Mz}, ${box.Mx})
        <#elseif pitchType == "ceiling">
            makeCuboidShape(${box.Mz}, ${16 - box.My}, ${box.mx}, ${box.mz}, ${16 - box.my}, ${box.Mx})
        <#else>
            makeCuboidShape(${16 - box.mz}, ${box.my}, ${box.mx}, ${16 - box.Mz}, ${box.My}, ${box.Mx})
        </#if>
    <#elseif facing == "west">
        <#if pitchType == "floor">
            makeCuboidShape(${box.mz}, ${box.my}, ${16 - box.mx}, ${box.Mz}, ${box.My}, ${16 - box.Mx})
        <#elseif pitchType == "wall">
            makeCuboidShape(${box.my}, ${16 - box.Mz}, ${16 - box.mx}, ${box.My}, ${16 - box.mz}, ${16 - box.Mx})
        <#elseif pitchType == "ceiling">
            makeCuboidShape(${16 - box.Mz}, ${16 - box.My}, ${16 - box.mx}, ${16 - box.mz}, ${16 - box.my}, ${16 - box.Mx})
        <#else>
            makeCuboidShape(${box.mz}, ${box.my}, ${16 - box.mx}, ${box.Mz}, ${box.My}, ${16 - box.Mx})
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
        <#elseif pitchType == "wall">
            makeCuboidShape(${box.mx}, ${16 - box.Mz}, ${box.my}, ${box.Mx}, ${16 - box.mz}, ${box.My})
        <#elseif pitchType == "ceiling">
            makeCuboidShape(${box.mx}, ${16 - box.My}, ${16 - box.Mz}, ${box.Mx}, ${16 - box.my}, ${16 - box.mz})
        <#else>
            makeCuboidShape(${box.mx}, ${box.my}, ${box.mz}, ${box.Mx}, ${box.My}, ${box.Mz})
        </#if>
    </#if>
</#macro>