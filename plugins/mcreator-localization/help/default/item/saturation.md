Saturation is the first statistic to decrease when a player performs energy-intensive actions, 
and it must be completely depleted before the visible hunger meter begins decreasing.

This option is a multiplier used by the game to determine the resulting saturation provided by a food item. It uses this formula:

```
Resulting saturation = Player's previous saturation
+ (2 * Nutritional value * Saturation multiplier)
```
There is also a clamp in the game preventing the resulting saturation from being greater than the player's hunger point level.

Similar to hunger, 1 point of saturation is equivalent to 1/2 of an icon in the hunger bar and 20 points are equivalent to the full hunger bar. However, saturation is invisible and does not display a bar to the player.