The procedure is executed when the player right-clicks on a block with this item in his hand.

The procedure should return an action result of type SUCCESS/CONSUME if the item interacted with the block, 
FAIL if the interaction failed, and PASS if there was no interaction. 
If the procedure doesn't return any value, the result action type will be PASS.