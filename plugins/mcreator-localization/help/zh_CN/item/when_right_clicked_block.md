这个流程是在玩家右键点击一个方块时执行的。

如果物品与方块交互，流程应返回类型为SUCCESS/CONSUME的操作结果，如果交互失败则返回FAIL，如果没有交互则返回PASS。

如果流程没有返回任何值，结果操作类型将是PASS。

如果你想要“${l10n.t("elementgui.common.event_right_clicked_air")}”流程只在实体在空中用这个物品右击时被调用，这个流程应该总是返回SUCCESS/CONSUME。
