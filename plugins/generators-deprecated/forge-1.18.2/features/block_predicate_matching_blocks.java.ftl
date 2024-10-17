<#if input$blockSet?starts_with("/*@Tag*/")> <#-- The holder set is a tag -->
    BlockPredicate.matchesTag(${input$blockSet?remove_beginning("/*@Tag*/")}
<#else> <#-- The holder set is a list of blocks -->
    BlockPredicate.matchesBlocks(List.of(${input$blockSet})
</#if>
<#if (field$x != "0")||(field$y != "0")||(field$z != "0")>, new Vec3i(${field$x}, ${field$y}, ${field$z})</#if>)