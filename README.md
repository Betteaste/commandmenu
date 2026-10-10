# Command Menu (NeoForge 1.21.1)

Appuie sur **G** en jeu pour ouvrir le menu.

- Onglet Items : recherche, quantité, joueur ciblé. Clic = quantité, Maj+clic = stack, clic droit = x1, molette = page.
- Onglet Commandes : gamemode, temps, météo, difficulté, gamerules, effets + champ de commande libre.

Il faut être opérateur (cheats activés) car le menu envoie de vraies commandes (/give, /time...).

## Compiler
Java 21 requis.
    ./gradlew build        (Windows : gradlew.bat build)
Le .jar est dans build/libs/ -> à mettre dans le dossier mods avec NeoForge 21.1.x.
Pour tester directement : ./gradlew runClient
