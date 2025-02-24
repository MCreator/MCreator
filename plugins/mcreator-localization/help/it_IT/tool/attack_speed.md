Questo parametro controlla quanto velocemente può essere utilizzato il tuo strumento.

Questo attributo controlla la durata del cooldown, con il tempo preso è T = 1 / velocità di attacco * 20tick.

Il moltiplicatore del danno è quindi 0.2 + ((t + 0.5) / T) ^ 2 * 0.8, limitato al campo 0. – 1, dove t è il numero di tick dall’ultimo attacco o interruttore di item.