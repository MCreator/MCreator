(new Object(){ int check(LivingEntity _entity) {
    if(_entity instanceof LivingEntity) {
        Collection<EffectInstance> effects = _entity.getActivePotionEffects();
        for(EffectInstance effect : effects){
            if(effect.getPotion() == ${generator.map(field$potion, "potions")})
                return effect.getDuration();
        }
    }
    return 0;
}}.check((LivingEntity)${input$entity}))