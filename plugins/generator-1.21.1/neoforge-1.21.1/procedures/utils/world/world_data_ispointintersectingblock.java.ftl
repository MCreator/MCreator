private static boolean isPointIntersectingBlock(LevelAccessor world, Vec3 point, double x, double y, double z) {
    BlockPos pos = BlockPos.containing(x, y, z);
    for (AABB localBox : world.getBlockState(pos).getCollisionShape(world, pos).toAabbs()) {
        AABB worldBox = localBox.move(pos.getX(), pos.getY(), pos.getZ());
        if (worldBox.contains(point.x(), point.y(), point.z())) {
            return true;
        }
    }
    return false;
}