<#assign hasShooter = (input$shooter != "null")>
<#assign hasAcceleration = (input$ax != "/*@int*/0") || (input$ay != "/*@int*/0") || (input$az != "/*@int*/0")>
new Object() {
	public Projectile getPotion(Level level<#if hasShooter>, Entity shooter</#if><#if hasAcceleration>, double ax, double ay, double az</#if>) {
		ThrownPotion entityToSpawn = new ThrownPotion(EntityType.POTION, level);
		entityToSpawn.setItem(PotionContents.createItemStack(Items.${field$potionType}, ${generator.map(field$potion, "potions")}));
		<#if hasShooter>entityToSpawn.setOwner(shooter);</#if>
		<#if hasAcceleration>
		entityToSpawn.setDeltaMovement(new Vec3(ax, ay, az));
		entityToSpawn.hasImpulse = true;
		</#if>
		return entityToSpawn;
	}
}.getPotion(projectileLevel<#if hasShooter>, ${input$shooter}</#if><#if hasAcceleration>, ${input$ax}, ${input$ay}, ${input$az}</#if>)