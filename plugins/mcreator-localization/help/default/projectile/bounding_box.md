This parameter controls the size of your projectile's hitbox in block units.
The first option sets the width and depth, whilst the second option sets the height.
 
Projectiles with either width/depth or height of more than 0.5 will not collide with
their shooter to prevent initial collisions due to their size. If shooter is not known, they
will collide with the nearest entity in all cases, meaning shooting them with e.g. command may
cause them to collide with the command sender.

Very big projectile bounding box sizes may also cause the projectile to collide with the blocks
very often and may prevent them from going far due to early collisions.