結構在區塊中生成。這兩個引數控制區塊內結構生成:

* **最小距離** - 區塊內兩結構的最小距離。需要比兩個結構之間的平均距離小。
* **平均距離** - 這個集合中的兩個結構的平均距離。

這是把`最小距離設為 2`，`平均距離設為 5 `的示例，這將會只能在 5x5 區塊內生成結構，而且結構只能在` x `內生成。

```
.............
..XXX..XXX..X
..XXX..XXX..X
..XXX..XXX..X
.............
.............
..XXX..XXX..X
..XXX..XXX..X
..XXX..XXX..X
```