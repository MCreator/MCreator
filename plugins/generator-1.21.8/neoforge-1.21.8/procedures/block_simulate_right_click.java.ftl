<#include "mcelements.ftl">
<@head>if(${input$entity} instanceof Player _player) {</@head>
	BlockPos _bp${cbi} = ${toBlockPos(input$x,input$y,input$z)};
	_player.level().getBlockState(_bp${cbi}).useWithoutItem(_player.level(), _player, BlockHitResult.miss(new Vec3(_bp${cbi}.getX(), _bp${cbi}.getY(), _bp${cbi}.getZ()), Direction.UP, _bp${cbi}));
<@tail>}</@tail>