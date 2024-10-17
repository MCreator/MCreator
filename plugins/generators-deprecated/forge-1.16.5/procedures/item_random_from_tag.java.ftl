<#include "mcelements.ftl">
(new Object() {
	public Item getRandomItem(ResourceLocation name) {
		ITag<Item> _tag = ItemTags.getCollection().getTagByID(name);
		return _tag.getAllElements().isEmpty() ? Items.AIR : _tag.getRandomElement(new Random());
}}.getRandomItem(${toResourceLocation(input$tag)}))