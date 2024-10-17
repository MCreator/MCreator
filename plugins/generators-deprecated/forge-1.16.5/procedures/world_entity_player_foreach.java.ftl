{
    List<? extends PlayerEntity> _players = new ArrayList<>(world.getPlayers());
    for(Entity entityiterator : _players) {
        ${statement$foreach}
    }
}