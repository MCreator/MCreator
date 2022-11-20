This parameter controls how fast your tool can be used. 

This attribute controls the length of the cooldown time, 
with the time taken being T = 1 / attackSpeed * 20ticks. 

The damage multiplier is then 0.2 + ((t + 0.5) / T) ^ 2 * 0.8, 
restricted to the range 0.2 – 1, where t is the number 
of ticks since the last attack or item switch.