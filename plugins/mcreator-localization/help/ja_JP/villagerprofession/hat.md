このパラメータは、選択した職業テクスチャによって定義される帽子の種類を指定します。 村人がこの職業に就職した後でもタイプによって定義された帽子をかぶるかどうかを制御します。
* **なし:** あらゆる場合;
* **部分的:** この場合、村人の頭全体を覆わない;
* **フル:** いずれの場合もなし。

この条件は以下の表で表すことができます(THがタイプ依存の帽子であり、PHが職業帽子である場合):

| TH 可視性   | TH = なし | TH = 部分的 | TH = フル |
| -------- |:-------:|:--------:|:-------:|
| PH = なし  |   可視    |    可視    |   可視    |
| PH = 部分的 |   可視    |    可視    |   非表示   |
| PH = フル  |   非表示   |   非表示    |   非表示   |