Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(${input$item});
if (enchantments.containsKey(${generator.map(field$enhancement, "enchantments")})) {
	enchantments.remove(${generator.map(field$enhancement, "enchantments")});
	EnchantmentHelper.setEnchantments(enchantments, ${input$item});
}