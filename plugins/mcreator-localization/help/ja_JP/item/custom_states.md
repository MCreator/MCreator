ここでは、このアイテムの追加のプロパティをリストし、状態を形成するプロパティの値の組み合わせに応じて、そのテクスチャ/モデルがどのように変化するかを指定できます。 アイテムのプロパティは、任意の数値（整数または分数）を値として取ることができるため、どんな細かさにも従う必要を避け、近い値を提供できるように、アイテムから抽出された実際のプロパティ値が_同じまたはそれ以上である場合、状態が一致します_ここで指定された（期待される）値。一致する値を持つ状態が複数ある場合、これらの一致する状態の最後が使用されます。状態が一致しない場合、アイテムはデフォルトの視覚的外観を使用します。

カスタムプロパティとともに、Minecraftに代わってすべてのアイテムに定義されたいくつかの組み込みアイテムプロパティも使用できます。

注意：重複した状態は許可されていません。2つ以上の状態が単一のプロパティの値のみで異なる場合、そのプロパティを削除すると、これらの状態の最初の重複が自動的に削除されます。