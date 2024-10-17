{
    List<? extends PlayerEntity> _players = world.getPlayers();
    for(Entity entityiterator : _players) {
        ${statement$foreach}
    }
}