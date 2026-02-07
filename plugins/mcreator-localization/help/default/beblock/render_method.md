Render methods control how a block is drawn in the world.
They affect transparency, visibility from behind, and how blocks behave at long distances.

---

**alpha_test**  
Transparency: yes  
Translucency: no  
Backface culling: no  
Distant culling: yes  
Examples: Ladder, Monster Spawner, Vines

**alpha_test_single_sided**  
Transparency: yes  
Translucency: no  
Backface culling: yes  
Distant culling: yes  
Examples: Doors, Saplings, Trapdoors

**blend**  
Transparency: yes  
Translucency: yes  
Backface culling: yes  
Distant culling: no  
Examples: Glass, Beacon, Honey Block

**double_sided**  
Transparency: no  
Translucency: no  
Backface culling: no  
Distant culling: no  
Examples: Powder Snow

**opaque (default)**  
Transparency: no  
Translucency: no  
Backface culling: yes  
Distant culling: no  
Examples: Dirt, Stone, Concrete

Transparency = fully invisible pixels (cutouts).  
Translucency = semi-transparent pixels.  
Backface culling = faces are invisible when viewed from behind.  
Distant culling = faces disappear after half render distance.

---

**Distance-based render methods**

**alpha_test_to_opaque**  
Near: alpha_test  
Far: opaque  
Examples: Leaves

**alpha_test_single_sided_to_opaque**  
Near: alpha_test_single_sided  
Far: opaque  
Examples: Kelp, Sugar Cane

**blend_to_opaque**  
Near: blend  
Far: opaque  
Examples: N/A

Near = used when close to the block.  
Far = used when far away for performance.