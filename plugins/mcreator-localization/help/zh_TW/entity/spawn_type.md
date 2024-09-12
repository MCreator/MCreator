該引數控制生物將會屬於什麼生成類型。

* 被標記為 Monster 的生物將只會在暗處或在晚上生成。
* 被標記為 Creature 的生物將會生成在陽光直射下且只在標籤 `minecraft:animals_spawnable_on` 内的方塊。 不要對怪物使用這種類型，否則他們將不會生成。
* 标记为「Ambient」的怪物将在任何条件下生成，除非方塊类型阻止它。 但这一类型应该用于渲染環境氛圍的生物，如蝙蝠
* 「WaterCreature」將會沒有限制的在水中生成

生成類型系統在 [此處](https://mcreator.net/wiki/mob-spawning-parameters) 有著深入的介紹。
