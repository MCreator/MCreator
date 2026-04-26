选择要与该方块一起使用的模型。模型只定义视觉外观，而不定义方块的边框。

* <strongx-id="1">普通</strong> - 每边都有纹理的普通方块
* <strongx-id="1">单一纹理</strong> - 方块每一面的纹理都相同
* <strongx-id="1">交叉</strong> -植物使用的模型
* <strongx-id="1">作物（Crop）</strong> - 作物和植物使用的模型
* <strongx-id="1">草方块（Grass Block）</strong> - 草方块使用的模型（顶部和侧面纹理将被着色）
* Custom - 你可以自定义JSON、JAVA和OBJ模型

在创建自定义模型时，建议使用 JSON，因为 JSON 支持原版模型类型。

选择Java模型会强制启用该方块的方块实体。 Java模型会消耗更多的性能。 出于性能考虑请不要将Java模型用在普遍存在的方块。