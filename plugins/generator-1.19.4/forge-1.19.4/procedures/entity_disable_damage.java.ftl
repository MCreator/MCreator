if(${input$entity} instanceof Player _player) {
    _player.getAbilities().invulnerable = ${input$condition};
    _player.onUpdateAbilities();
}