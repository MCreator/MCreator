{Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(${input$item});
if (enchants.containsKey(${generator.map(field$enhancement, "enchantments")})) {
	enchants.remove(${generator.map(field$enhancement, "enchantments")});
	EnchantmentHelper.setEnchantments(enchants, ${input$item});
}}