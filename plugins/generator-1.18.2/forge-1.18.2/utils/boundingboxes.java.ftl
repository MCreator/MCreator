<#macro makeBoundingBox positiveBoxes negativeBoxes noOffset facing pitchType="floor">
    return <#if negativeBoxes?size != 0>Shapes.join(</#if>
    <@mergeBoxes positiveBoxes, facing, pitchType/>
    <#if negativeBoxes?size != 0>
    , <@mergeBoxes negativeBoxes, facing, pitchType/>, BooleanOp.ONLY_FIRST)</#if>
    <#if !noOffset>.move(offset.x, offset.y, offset.z)</#if>;
</#macro>

<#macro checkPitchSupport positiveBoxes negativeBoxes noOffset facing enablePitch>
    <#if enablePitch>
        switch ((AttachFace) state.getValue(FACE)) {
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
        switch ((Direction) state.getValue(FACING)) {
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
        switch ((Direction.Axis) state.getValue(AXIS)) {
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
            box(${min(16 - box.mx, 16 - box.Mx)}, ${min(box.my, box.My)}, ${min(16 - box.mz, 16 - box.Mz)},
                ${max(16 - box.mx, 16 - box.Mx)}, ${max(box.my, box.My)}, ${max(16 - box.mz, 16 - box.Mz)})
        <#elseif pitchType == "ceiling">
            box(${min(box.mx, box.Mx)}, ${min(16 - box.my, 16 - box.My)}, ${min(16 - box.mz, 16 - box.Mz)},
                ${max(box.mx, box.Mx)}, ${max(16 - box.my, 16 - box.My)}, ${max(16 - box.mz, 16 - box.Mz)})
        <#elseif pitchType == "wall">
            box(${min(16 - box.mx, 16 - box.Mx)}, ${min(box.mz, box.Mz)}, ${min(box.my, box.My)},
                ${max(16 - box.mx, 16 - box.Mx)}, ${max(box.mz, box.Mz)}, ${max(box.my, box.My)})
        </#if>
    <#elseif facing == "east">
        <#if pitchType == "floor">
            box(${min(16 - box.mz, 16 - box.Mz)}, ${min(box.my, box.My)}, ${min(box.mx, box.Mx)},
                ${max(16 - box.mz, 16 - box.Mz)}, ${max(box.my, box.My)}, ${max(box.mx, box.Mx)})
        <#elseif pitchType == "ceiling">
            box(${min(16 - box.mz, 16 - box.Mz)}, ${min(16 - box.my, 16 - box.My)}, ${min(16 - box.mx, 16 - box.Mx)},
                ${max(16 - box.mz, 16 - box.Mz)}, ${max(16 - box.my, 16 - box.My)}, ${max(16 - box.mx, 16 - box.Mx)})
        <#elseif pitchType == "wall">
            box(${min(box.my, box.My)}, ${min(box.mz, box.Mz)}, ${min(box.mx, box.Mx)},
                ${max(box.my, box.My)}, ${max(box.mz, box.Mz)}, ${max(box.mx, box.Mx)})
        </#if>
    <#elseif facing == "west">
        <#if pitchType == "floor">
            box(${min(box.mz, box.Mz)}, ${min(box.my, box.My)}, ${min(16 - box.mx, 16 - box.Mx)},
                ${max(box.mz, box.Mz)}, ${max(box.my, box.My)}, ${max(16 - box.mx, 16 - box.Mx)})
        <#elseif pitchType == "ceiling">
            box(${min(box.mz, box.Mz)}, ${min(16 - box.my, 16 - box.My)}, ${min(box.mx, box.Mx)},
                ${max(box.mz, box.Mz)}, ${max(16 - box.my, 16 - box.My)}, ${max(box.mx, box.Mx)})
        <#elseif pitchType == "wall">
            box(${min(16 - box.my, 16 - box.My)}, ${min(box.mz, box.Mz)}, ${min(16 - box.mx, 16 - box.Mx)},
                ${max(16 - box.my, 16 - box.My)}, ${max(box.mz, box.Mz)}, ${max(16 - box.mx, 16 - box.Mx)})
        </#if>
    <#elseif facing == "up">
        box(${min(box.mx, box.Mx)}, ${min(16 - box.mz, 16 - box.Mz)}, ${min(box.my, box.My)},
            ${max(box.mx, box.Mx)}, ${max(16 - box.mz, 16 - box.Mz)}, ${max(box.my, box.My)})
    <#elseif facing == "down" || facing == "z">
        box(${min(box.mx, box.Mx)}, ${min(box.mz, box.Mz)}, ${min(16 - box.my, 16 - box.My)},
            ${max(box.mx, box.Mx)}, ${max(box.mz, box.Mz)}, ${max(16 - box.my, 16 - box.My)})
    <#elseif facing == "x">
        box(${min(box.my, box.My)}, ${min(box.mz, box.Mz)}, ${min(box.mx, box.Mx)},
            ${max(box.my, box.My)}, ${max(box.mz, box.Mz)}, ${max(box.mx, box.Mx)})
    <#else>
        <#if pitchType == "floor">
            box(${min(box.mx, box.Mx)}, ${min(box.my, box.My)}, ${min(box.mz, box.Mz)},
                ${max(box.mx, box.Mx)}, ${max(box.my, box.My)}, ${max(box.mz, box.Mz)})
        <#elseif pitchType == "ceiling">
            box(${min(16 - box.mx, 16 - box.Mx)}, ${min(16 - box.my, 16 - box.My)}, ${min(box.mz, box.Mz)},
                ${max(16 - box.mx, 16 - box.Mx)}, ${max(16 - box.my, 16 - box.My)}, ${max(box.mz, box.Mz)})
        <#elseif pitchType == "wall">
            box(${min(box.mx, box.Mx)}, ${min(box.mz, box.Mz)}, ${min(16 - box.my, 16 - box.My)},
                ${max(box.mx, box.Mx)}, ${max(box.mz, box.Mz)}, ${max(16 - box.my, 16 - box.My)})
        </#if>
    </#if>
</#macro>

<#function min(a, b)>
    <#return (a < b)?then(a, b)>
</#function>

<#function max(a, b)>
    <#return (a > b)?then(a, b)>
</#function>

<#macro mergeBoxes boxes facing pitchType>
<#if boxes?size == 1>
    <@makeCuboid boxes.get(0) facing pitchType/>
<#else>
    Shapes.or(<#list boxes as box>
        <@makeCuboid box facing pitchType/><#sep>,</#list>)
</#if>
</#macro>