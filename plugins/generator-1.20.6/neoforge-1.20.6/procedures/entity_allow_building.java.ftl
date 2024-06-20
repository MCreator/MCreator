if(${input$entity} instanceof Player _player) {
    _player.getAbilities().mayBuild = ${input$condition};
    _player.onUpdateAbilities();
}