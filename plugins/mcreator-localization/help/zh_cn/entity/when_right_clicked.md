This trigger triggers the procedure when a player right-clicks on this entity.

The procedure should return an action result of type SUCCESS/CONSUME if there was an interaction with the entity, FAIL 
if the interaction failed, and PASS if there was no interaction. 
If the procedure doesn't return any value, the action result type will be determined by whether the entity is rideable, 
tameable, or opens a GUI.