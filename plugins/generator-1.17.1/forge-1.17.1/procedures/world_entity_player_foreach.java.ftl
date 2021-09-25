{
    List<? extends Player> _players = new ArrayList<>(world.players());
    for(Entity entityiterator : _players) {
        ${statement$foreach}
    }
}