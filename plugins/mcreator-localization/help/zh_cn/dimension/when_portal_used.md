The procedure will be executed when the player used the portal trigger on a block.

The procedure should return an action result of type SUCCESS/CONSUME if the trigger interacted with the block, FAIL if the
interaction failed, and PASS if there was no interaction. 
If the trigger successfully created a portal, or if the procedure doesn't return any value, then the action result type 
will be SUCCESS.