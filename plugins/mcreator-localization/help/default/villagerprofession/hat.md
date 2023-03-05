This parameter specifies the kind of hat defined by the selected profession texture, which controls
whether the villager will still wear the hat defined by their type after claiming this profession:
* **None:** In all cases;
* **Partial:** In cases it doesn't cover villager's entire head;
* **Full:** In no cases.

This condition can be represented with the following table (where TH is type-dependent hat and PH is profession hat):

| TH visibility | TH = None | TH = Partial | TH = Full |
|---------------|:---------:|:------------:|:---------:|
| PH = Nons     |  Visible  |   Visible    |  Visible  |
| PH = Partial  |  Visible  |   Visible    |  Hidden   |
| PH = Full     |  Hidden   |    Hidden    |  Hidden   |