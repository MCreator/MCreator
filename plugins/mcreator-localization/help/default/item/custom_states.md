Here you can list additional properties of this item and specify how its texture changes
depending on given combination of properties' values that form a state.

Along with custom ones, you can also use built-in item properties:

* `damaged`: Returns 1.0 if the item is damaged and 0.0 otherwise;
* `damage`: Returns the total damage caused to the item (between 0.0 and 1.0);
* `lefthanded`: Returns 1.0 if the item is held in left hand of an entity and 0.0 otherwise;
* `cooldown`: Returns the remaining item cooldown time (between 0.0 and 1.0);
* `trim_type` (1.19.4+): Returns fractional "ID" of armor trim type applied to the item (between 0.0 and 1.0).

NOTE: Duplicate states are not allowed. If two or more states only differ in value of single property, then removing
that property will automatically remove duplicates of first of these states.