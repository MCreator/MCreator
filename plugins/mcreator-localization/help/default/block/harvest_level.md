This is the tier of tool required to break this block.

* -1 is hand
* 0 is wood
* 1 is stone
* 2 is iron
* 3 is diamond.

Only the tier of tool you specify will be able to break your block. 
You can define larger tiers than diamond too by setting the tier to 4 or larger.

Condition for the block to drop the items when broken is:

`IF BLOCK HARVEST LEVEL <= TOOL HARVEST LEVEL`
