有効にすると、ブロックはコード内でドロップを定義せず（ブロックmod要素で定義されたドロップ）、代わりにブロックのドロップをルートテーブルで定義する必要があります。

レジストリ名`blocks/${registryname}`、名前空間_mod_、タイプ_Block_でルートテーブルmod要素を作成します。

このパラメータがチェックされていない場合、ルートテーブルは依然としてブロックドロップを上書きしますが、ルートテーブルがエントリを返さないときは、ブロックmod要素で定義されたブロックドロップが使用されます。

このパラメータがチェックされている場合、このブロックのドロップは完全にルートテーブルによって制御されます。