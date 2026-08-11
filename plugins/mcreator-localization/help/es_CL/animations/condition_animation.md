Cuando el procedimiento regresa true, la reproducción de la animación comenzara, a menos que la animación ya se esté reproduciendo.

Cuando el procedimiento regrese false, la reproducción de la animación se detendrá.

Si el interruptor ocurre muy rápido, puede que no haya suficiente tiempo para que la animación comience a reproducirse o la reproducción se reinicie.

Si la animación no está configurada para estar en bucle, se detendrá cuando llegue al final de la animación y la condición necesita ser colocada en true, después false y de vuelta a true para reiniciar la animación.

Para animaciones de caminata, este parámetro controla cuando la animación está siendo reproducida.