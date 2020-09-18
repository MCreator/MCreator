{
		Map<Enchantment, Integer> _enchantments=EnchantmentHelper.getEnchantments(${input$item});
		if(_enchantments.containsKey(${generator.map(field$enhancement, "enchantments")})){
		_enchantments.remove(${generator.map(field$enhancement, "enchantments")});
		EnchantmentHelper.setEnchantments(_enchantments, ${input$item});
		}
		}