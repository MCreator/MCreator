if(${input$entity} instanceof ServerPlayer _player) {
	Advancement _adv = _player.server.getAdvancements().getAdvancement(new ResourceLocation("${generator.map(field$achievement, "achievements")}"));
    AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
    if (!_ap.isDone()) {
        Iterator _iterator = _ap.getRemainingCriteria().iterator();
        while(_iterator.hasNext())
            _player.getAdvancements().award(_adv, (String)_iterator.next());
    }
}