# TP_Taquin

Ce projet permet grâce à un système multi-agents de résoudre un jeu du Taquin.
Il est possible de modifier les paramètres grâce à la fenêtre des paramètres, pour choisir la taille de la grille ou encore les stratégies qui sont implémentées :
- ligne par ligne : de haut en bas
- coutours d'abords : essaie de remplir tous les contours d'abord, puis le centre
- en spirale : ligne du haut, puis ligne de gauche, puis ligne du bas, puis ligne de droite...
- pas de contrainte

Les meilleures stratégies sont sans aucun doute ligne par ligne et en spirale, notamment quand le nombre d'agents augmente.
Ces deux stratégies permettent de résoudre un Taquin de 5 par 5 avec 20 agents assez fréquemment, et arrivent parfois (1 fois sur 2 environ) à résoudre avec 22 voire 23 agents.
