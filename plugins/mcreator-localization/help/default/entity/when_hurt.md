This trigger triggers the procedure when the entity is hurt.

`sourceentity` dependency in this case is the entity causing the damage to this entity and can be null
if the damage is caused by a non-entity source.

If the procedure returns a `false` logic value, then the entity won't receive any damage.
