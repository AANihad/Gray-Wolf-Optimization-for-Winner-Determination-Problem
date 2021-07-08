[Présentation sur Canva](https://www.canva.com/design/DAEjlBe23r8/4SSa-JRcMMho_3lwCS-GVw/edit?layoutQuery=Pr%C3%A9sentations#)
# Introduction

Le problème de la détermination du gagnant dans les enchères combinatoires, ou bien Winner determination problem (WDP) consiste à trouver un ensemble d'objets qui satisfaisent plusieures auctions à objets multiples sans conflict. C'est un problème Np-Complet qui ne peut être résout par des algorithmes classiques qu'en un long temps et de grandes ressources mémoire, pour cela il est préférable d'utiliser des métaheuristiques qui sont plus performantes dans ce domaine.

Dans ce mini-projet le WDP en utilisant la métaheuristique **Gray Wolf Optimizer** ou GWO développée par Seyedali Mirjalili.

# Définition du problème de détermination du gagnant dans les enchères combinatoires

Les enchères combinatoires consistent en un ensemble d'offres sur multiples objets à la fois, chaque offre dispose d'un prix ou valeur, et le but c'est de déterminer les offres à satisfaire, en faisant attention aux conflicts ainsi qu'en maximisant notre gain, soit, la somme des valeurs des offres.
###### En résumé :
- Nous disposons de N items et de M offres (auctions / Bids)
- Chaque offre est composée de i Items ayant la valeur V.
- Le but est de maximiser la somme des V sous la contrainte que chaque item ce trouve dans une seule offre au plus.

# Gray Wolf Optimization

L'optimisation du loup gris est une métaheuristique s'inspirant du comportement des meutes loups gris lors d'une chasse.

## Hiérarchie d'une meute de loups

Une meute de loups se compose généralement de 5 à 12 individus, elle fonctionne toujours en suivant une hiérarchie.
Les loups alphas sont responsables des décisions à prendre concernant tout le mode de vie de la meute.
Les loups beta assistent l'alpha et sont les candidats à devenir des alphas, ils suivent les ordres de l'alpha mais commandent les autres loups.
Les loups delta sont des vieux loups, des chasseurs, sentinnels.
Finalement les loups omega sont au plus bas de la hiérarchie, les derniers à manger et à recevoir des ordres des autres loups.

## Stratégie d'une meute de loups

Lorseque une meute de loups décide de chasser une proie chaque individu la garde en vue, et ajuste sa propre position par rapport à la position de l'alpha et des loups beta et delta.
Lorseque tous les loups encèclent la proie, c'est la fin de la traque et le début de l'attaque.

# Modélisation Mathématique de WDP et GWO

- **L'espace de recherche** c'est l'ensemble de toutes les enchères
- **Une solution** est un ensemble d'auctions et aussi la proie recherchée par les loups.
- La meute de loups représente un **ensemble de solutions**, les trois meilleures solutions sont respectivement l'alpha, le beta et delta, le reste des loups seront des oméga.
- L'ensemble des enchères (WDP) est représenté par une liste de Bids (enchères).
- Chaque enchère détient une liste de type Short qui représente les Items (objets).
- La fonction de fitnesse est la maximisation de la somme des gains des enchères du problème.
- Dans le monde réel les loups se repèrent à leurs sens cependant il n'est pas possible de voir la solution (proie) dans un monde mathématique, nous assumons alors que l'alpha, le bêta et le delta ont la meilleure connaissance de la position de la proie.

$$V=\{B_1, B_2, \dots, B_n\}$$
$$F(V) = \sum_{i=1}^n Prix(B_i) = \sum_{i=1}^n P_i$$


## Mise à jour de la position.

$$D = |C.X_p(t) -X(t)|$$
$$X(t+1) = X_p(t) - A.D$$
![[Pasted image 20210708063703.png]]
A et C sont des vecteurs de coefficients, $X_p$ est la position de la proie et $X$ indique la position du loup actuel.
![[Pasted image 20210708063717.png]]

$$A = 2.a.r_1-a$$ (valeurs entre $-a$ et $a$)
Ce paramètre oblige le loup de se rapprocher ou de s'eloigner de la solution.
$$C=2.r_2$$(valeurs entre $0$ et $2$)
C>1 plus d'exploration, C<1 plus d'exploitation.
d'ou $a$ est initialement égal à 2 et décrémenté au long des itérations, $r1$ et $r2$ sont des nombres au hasard, ils permettent au loup de se repositionner.

$$X_1 = X_{\alpha} - A_1.D_{\alpha}$$
$$X_2 = X_{\beta} - A_2.D_{\beta}$$
$$X_3 = X_{\delta} - A_3.D_{\delta}$$

$$X(t+1) = \frac{X_1+X_2+X_3}{3}$$
![[Pasted image 20210708063735.png]]
Biensur ces equations nous permettent de se positionner dans un espace à trois dimensions, dans le monde réel ces repositionnemens hasardeux sont dus au terrain et aux obstacles.
Par contre dans un WDP on ne peut pas faire ces multiplications sur un ensemble d'auctions, nous utilisons la position afin de modifier une partie de la solution trouvée.
La modification se fait par la supression d'une partie des auctions de la solution et leur remplacement pas d'autres au hasad qui respectent les conflicts entre objets

Il se peux qu'elle soit meilleure ou moins bien que la précédente.
Si la solution d'un individu ne s'est pas améliorée après une itération, une nouvelle solution est générée.

# Solution au hasard

Comme on ne peut pas générer une solution random de la façon habituelle tout en respectant les conflicts entre les objets **The Random Key Encoding** est habituellement utilisé, dans ce mini-projet une variante a été utilisée.
Le Random Key Encoding consiste en un ensemble $r$ de $n$ réels qui représentent l'ordre dans lequel les offres vont êtres considérées.

Par exemple pour une ensemble d'offres $B_1, B_2, B_3$ et un $r =\{0.6, 0.85, 0.23\}$. On va considérer les Bids selon l'ordre suivant $\{B_2, B_1, B_3\}$.
$B_2$ va être rajouté automatiquement à la solution ensuite $B_1$ si et seulement si il ne cause pas de conflict pas avec $B2$ et ainsi de suite.

# Pseudo Code

![[Pasted image 20210708092400.png]]
![[Pasted image 20210708092419.png]]

# Outil développé

# Résultats d'execution sur les instances

# Développement futur

### Graphe de conflict

Un graphe de conflict est un graphe qui représente les conflits entre les enchères d'une instance, du à un manque de temps, une représentation graphique n'a pas pu être implémentée. Un graphe de conflicts est un graphe qui a comme noeuds représentées par des enchère et chaque arc entre deux enchères représente au moins un item en commun entre les deux enchères.

### Problème de définition du gagnant dans les enchères combinatoires.

En sachant que les enchères remprésentent une partie importante du commerce qu'il soit electronique ou non, il est important d'optimiser le temps de résolution du problème des enchères combinatoires.
Un problème Np-complet tel que les enchères combinatoires pourrait prendre beaucoup de temps à des algorithmes classiques comme la recherche Taboue ou même les algorithmes exactes développées spécéfiquement pour résoudre ce problème.

### Algorithme du Gray Wolf Optimizer.

Ce travail a permis de se familiariser avec cette nouvelle méta-heuritique, l'implémentation coté code peux définitivement être améliorée afin d'optimiser le temps d'execution et la performance, Ce qui sera fait inchallah si le temps le permet.

### Problèmes rencontrés.

- Le problème de mutation, crossover, changement de la solution.
- Avoir des solutions composée d'objets et non pas de variables simples auxquelles on peut affecter un 0 ou un 1 a été interessant à voir.
- Les contraintes lors de la création d'une solution ont posé un challenge qui a été interessant à relever.

### Ce qu'on peut améliorer?

- Ajouter un graphe de conflicts
- Voir comment d'autres métaheuristiques performent face au problème
- Optimiser le code pour une meilleure execution
- Utiliser un système multi agent ou chaque individu est un agent.
- Utiliser le parallélisme ou les threads afin de mieux exploiter les ressources de la machine.

---

