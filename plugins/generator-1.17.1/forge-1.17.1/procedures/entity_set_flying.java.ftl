if(${input$entity} instanceof Player _player) {
    _player.getAbilities().flying = ${input$condition};
    _player.onUpdateAbilities();
}