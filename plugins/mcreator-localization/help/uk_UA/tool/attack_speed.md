Цей параметр визначає швидкість використання інструмента.

Цей атрибут контролює тривалість часу перезарядки, який дорівнює T = 1 / attackSpeed * 20ticks.

Тоді множник шкоди дорівнює 0.2 + ((t + 0.5) / T) ^ 2 * 0.8, обмежений діапазоном 0.2 - 1, де t — кількість тиків з моменту останньої атаки або перемикання предмета.