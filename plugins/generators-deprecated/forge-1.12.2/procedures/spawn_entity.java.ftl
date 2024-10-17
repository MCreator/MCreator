if(!world.isRemote){
	Entity entityToSpawn=new ${generator.map(field$entity, "entities")}(world);
	if(entityToSpawn!=null){
		entityToSpawn.setLocationAndAngles(${input$x}, ${input$y}, ${input$z},world.rand.nextFloat()*360F,0.0F);
		world.spawnEntity(entityToSpawn);
	}
}