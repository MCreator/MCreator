Este parâmetro controla a rapidez com que sua ferramenta pode ser usada.

Este atributo controla a duração do tempo de recarga, sendo o tempo gasto T = 1 / VelocidadeDeAtaque * 20 ticks.

O multiplicador de dano é então 0,2 + ((t + 0,5) / T) ^ 2 * 0,8, restrito ao intervalo de 0,2 a 1, onde t é o número de tiques desde o último ataque ou troca de item.