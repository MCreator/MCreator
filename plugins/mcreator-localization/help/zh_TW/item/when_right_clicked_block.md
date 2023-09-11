當玩家使用此物品右擊方塊時，該函式將會被執行。

如果物品與方塊互動，函式返回類型應為 SUCCESS/CONSUME 的操作結果，如果互動失敗則返回 FAIL，如果沒有互動則返回 PASS。

如果函式沒有返回任何值，結果操作類型將是 PASS。

如果你想要「${l10n.t("elementgui.common.event_right_clicked_air")}」函式僅在實體在空中對此物品右擊時被呼叫，此函式應總是返回 SUCCESS/CONSUME。