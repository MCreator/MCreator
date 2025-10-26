Here you can list additional properties of this item and specify how its texture/model changes
depending on given combination of properties' values that form a state.

An item property can take any number (integer or fractional) as its value, so to avoid a need to follow any granularity
and to allow providing close values, a state matches if actual property values extracted from the item are _the same as
or greater than_ expected (specified here) values. 

If there are multiple states with matching values, the last of these matching states will be used.
If no states match, the item will use its default visual look.

Along with custom ones, you can also use some built-in item properties defined for all items on behalf of Minecraft.

NOTE: Duplicate states are not allowed. If two or more states only differ in value of single property, then removing
that property will automatically remove duplicates of first of these states.