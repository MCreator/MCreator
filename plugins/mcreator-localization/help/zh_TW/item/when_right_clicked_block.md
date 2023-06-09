這個函式是在玩家右鍵點選一個方塊時執行的。

如果物品與方塊互動，過程應返回型別為SUCCESS/CONSUME的操作結果，如果互動失敗則返回FAIL，如果沒有互動則返回PASS。

如果過程沒有返回任何值，結果操作型別將是PASS。

如果你想要“${l10n.t("elementgui.common.event_right_clicked_air")}”過程只在實體在空中用這個專案右擊時被呼叫，這個過程應該總是返回SUCCESS/CONSUME。