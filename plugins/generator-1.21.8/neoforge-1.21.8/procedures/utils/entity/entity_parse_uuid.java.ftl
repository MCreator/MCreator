private static UUID parseUUID(String uuid) {
    try {
        return UUID.fromString(uuid);
    } catch (IllegalArgumentException e) {
        return UUID.randomUUID();
    }
}