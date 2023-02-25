This parameter specifies the kind of hat defined by the selected profession texture, which controls
whether the villager will still wear the hat defined by their type after claiming this profession:
* **NONE:** In all cases;
* **PARTIAL:** In cases it doesn't cover villager's entire head;
* **FULL:** In no cases.

This condition can be represented with the following table (where TH is type-dependent hat and PH is profession hat):

| TH visibility | TH = NONE | TH = PARTIAL | TH = FULL |
|---------------|:---------:|:------------:|:---------:|
| PH = NONE     |  Visible  |   Visible    |  Visible  |
| PH = PARTIAL  |  Visible  |   Visible    |  Hidden   |
| PH = FULL     |  Hidden   |    Hidden    |  Hidden   |