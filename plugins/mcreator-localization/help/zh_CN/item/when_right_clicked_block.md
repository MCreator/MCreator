该流程是在玩家右键点击一个方块时执行的。

如果物品与方块交互，流程应返回类型为 SUCCESS/CONSUME 的操作结果，如果交互失败则返回 FAIL，如果没有交互则返回 PASS。

如果流程没有返回任何值，结果操作类型将是 PASS。

如果您想要 ${l10n.t（"elementgui.common.event_right_clicked_air"）} 流程只在实体在空中用该物品右击时被调用，该流程应该总是返回 SUCCESS/CONSUME。