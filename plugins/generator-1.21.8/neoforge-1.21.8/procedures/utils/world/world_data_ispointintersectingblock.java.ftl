private static boolean isPointIntersectingBlock(LevelAccessor world, Vec3 point) {
    BlockPos pos = BlockPos.containing(point);
    VoxelShape shape = world.getBlockState(pos).getCollisionShape(world, pos);
    if (shape.isEmpty())
        return false;
    Vec3 local = point.subtract(pos.getX(), pos.getY(), pos.getZ());
    return shape.toAabbs().stream().anyMatch(box -> box.contains(local.x, local.y, local.z));
}