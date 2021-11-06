<#macro makeBoundingBox positiveBoxes negativeBoxes noOffset facing>
    return <#if negativeBoxes?size != 0>Shapes.join(</#if>
    <@mergeBoxes positiveBoxes, facing/>
    <#if negativeBoxes?size != 0>
    , <@mergeBoxes negativeBoxes, facing/>, BooleanOp.ONLY_FIRST)</#if>
    <#if !noOffset>.move(offset.x, offset.y, offset.z)</#if>;
</#macro>

<#macro boundingBoxWithRotation positiveBoxes negativeBoxes noOffset rotationMode>
    <#if rotationMode == 0>
        <@makeBoundingBox positiveBoxes negativeBoxes noOffset "north"/>
    <#elseif rotationMode != 5>
        switch ((Direction) state.getValue(FACING)) {
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

<#macro makeCuboid box facing>
    <#if facing == "south">
        box(${min(16 - box.mx, 16 - box.Mx)}, ${min(box.my, box.My)}, ${min(16 - box.mz, 16 - box.Mz)},
            ${max(16 - box.mx, 16 - box.Mx)}, ${max(box.my, box.My)}, ${max(16 - box.mz, 16 - box.Mz)})
    <#elseif facing == "east">
        box(${min(16 - box.mz, 16 - box.Mz)}, ${min(box.my, box.My)}, ${min(box.mx, box.Mx)},
            ${max(16 - box.mz, 16 - box.Mz)}, ${max(box.my, box.My)}, ${max(box.mx, box.Mx)})
    <#elseif facing == "west">
        box(${min(box.mz, box.Mz)}, ${min(box.my, box.My)}, ${min(16 - box.mx, 16 - box.Mx)},
            ${max(box.mz, box.Mz)}, ${max(box.my, box.My)}, ${max(16 - box.mx, 16 - box.Mx)})
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
        box(${min(box.mx, box.Mx)}, ${min(box.my, box.My)}, ${min(box.mz, box.Mz)},
            ${max(box.mx, box.Mx)}, ${max(box.my, box.My)}, ${max(box.mz, box.Mz)})
    </#if>
</#macro>

<#function min(a, b)>
    <#return (a < b)?then(a, b)>
</#function>

<#function max(a, b)>
    <#return (a > b)?then(a, b)>
</#function>

<#macro mergeBoxes boxes facing>
<#if boxes?size == 1>
    <@makeCuboid boxes.get(0) facing/>
<#else>
    Shapes.or(<#list boxes as box>
        <@makeCuboid box facing/><#sep>,</#list>)
</#if>
</#macro>