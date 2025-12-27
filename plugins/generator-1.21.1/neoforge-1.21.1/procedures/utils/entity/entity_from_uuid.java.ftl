private static Entity getEntityFromUUID(ServerLevel level, String uuid) {
    try {
        return level.getEntity(UUID.fromString(uuid));
    } catch (IllegalArgumentException e) {
        return null;
    }
}