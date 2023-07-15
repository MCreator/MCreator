if (${input$entity} instanceof Entity || ${input$entity} instanceof Player) {
    // Display a message to the entity or player
    ${input$entity}.displayClientMessage(Component.literal(${input$text}), ${input$actbar});
    // Play the specified animation on the entity or player at the specified speed
    function playAnimation(entity, animationId, speed) {
      // Load the animation data from the file with the specified ID
      var animationData = loadAnimationData(animationId);
      // Check if the entity already has an animation controller
      if (!entity.hasComponent("minecraft:entity_animations")) {
        // If not, add one
        entity.addTag("minecraft:entity_animations");
        entity.addComponent("minecraft:entity_animations", {
          animations: {}
        });
      }
      // Set the animation data for the specified animation ID
      var animationComponent = entity.getComponent("minecraft:entity_animations");
      animationComponent.animations[animationId] = animationData;
      // Set the speed of the animation
      animationComponent.current_animation_speed = speed;
      // Play the animation
      entity.playAnimation(animationId);
    }
    playAnimation(${input$entity}, ${input$animation_id}, ${input$speed});
}
