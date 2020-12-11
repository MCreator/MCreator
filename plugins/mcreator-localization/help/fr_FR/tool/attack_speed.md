Ce paramètre contrôle la vitesse d'utilisation de l'outil.

Cet attribut contrôle la durée du cooldown d'utilisation avec le temps T = 1 / attackSpeed * 20 ticks.

La multiplication de dégâts est alors 0,2 + ((t + 0,5) / T) ^ 2 * 0,8, restreint à la portée 0,2 - 1, où t est le nombre de ticks depuis la dernière attaque ou le changement d'item dans la main.