if(${input$entity} instanceof Player _player) {
    _player.getAbilities().mayfly = ${input$condition};
    _player.onUpdateAbilities();
}