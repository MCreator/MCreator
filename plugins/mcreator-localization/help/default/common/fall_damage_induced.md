The number determines the amount of fall-damage dealt to the entity that falls onto the block.
The block must have collisions enabled and be solid for fall damage to be dealt.

* Default value of 1 keeps the normal fall-damage
* Value of 0 completely disables fall-damage

The calculation is done using: `dealt damage = value of this field * default fall damage`.