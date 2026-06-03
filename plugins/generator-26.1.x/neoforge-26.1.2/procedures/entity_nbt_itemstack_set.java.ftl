<#include "mcitems.ftl">
Entity _entity${cbi} = ${input$entity};
_entity${cbi}.getPersistentData().put(${input$tagName}, (CompoundTag) ItemStack.OPTIONAL_CODEC.encode(${mappedMCItemToItemStackCode(input$tagValue, 1)}, _entity${cbi}.registryAccess().createSerializationContext(NbtOps.INSTANCE), new CompoundTag()).result().orElseGet(CompoundTag::new));