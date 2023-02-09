<#-- Hacky workarounds to allow this predicate in 1.18 -->
<#if input$blockSet?contains("BlockTags")> <#-- The holder set is a tag, we "extract" the tag part from the input -->
    BlockPredicate.matchesTag(${input$blockSet?remove_beginning("Registry.BLOCK.getTag(")?remove_ending(".orElseThrow())")}
<#else> <#-- The holder set is a block list, we "extract" the blocks from the input -->
    BlockPredicate.matchesBlocks(List.of(${input$blockSet?remove_beginning("HolderSet.direct(Block::builtInRegistryHolder")?remove_beginning(",")?remove_ending(")")})
</#if>
<#if (field$x != "0")||(field$y != "0")||(field$z != "0")>, new Vec3i(${field$x}, ${field$y}, ${field$z})</#if>)