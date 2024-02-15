<#include "mcelements.ftl">
if(${input$entity} instanceof Player _player) {
	BlockPos _bp = ${toBlockPos(input$x,input$y,input$z)};
	_player.level().getBlockState(_bp).use(_player.level(), _player, InteractionHand.MAIN_HAND,
BlockHitResult.miss(new Vec3(_bp.getX(), _bp.getY(), _bp.getZ()), Direction.UP, _bp));
}