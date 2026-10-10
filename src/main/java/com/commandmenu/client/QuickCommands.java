package com.commandmenu.client;

import java.util.List;

/** Catalogue des commandes rapides. Modifie cette liste pour ajouter les tiennes ! */
public final class QuickCommands {
    public record Entry(String label, String command) {}

    public record Category(String name, List<Entry> entries) {}

    public static final List<Category> CATEGORIES = List.of(
            new Category("Joueur", List.of(
                    new Entry("Survie", "gamemode survival"),
                    new Entry("Créatif", "gamemode creative"),
                    new Entry("Aventure", "gamemode adventure"),
                    new Entry("Spectateur", "gamemode spectator"),
                    new Entry("Soin complet", "effect give @s minecraft:instant_health 1 10 true"),
                    new Entry("Rassasier", "effect give @s minecraft:saturation 1 10 true"),
                    new Entry("Régénération", "effect give @s minecraft:regeneration 60 4 true"),
                    new Entry("Vision nocturne", "effect give @s minecraft:night_vision 1000000 0 true"),
                    new Entry("Vitesse II", "effect give @s minecraft:speed 1000000 1 true"),
                    new Entry("Annuler effets", "effect clear @s"),
                    new Entry("+10 niveaux", "xp add @s 10 levels"),
                    new Entry("Vider inventaire", "clear @s"))),
            new Category("Temps", List.of(
                    new Entry("Jour", "time set day"),
                    new Entry("Midi", "time set noon"),
                    new Entry("Coucher soleil", "time set 12000"),
                    new Entry("Nuit", "time set night"),
                    new Entry("Minuit", "time set midnight"),
                    new Entry("Figer le temps", "gamerule doDaylightCycle false"),
                    new Entry("Reprendre le temps", "gamerule doDaylightCycle true"))),
            new Category("Météo", List.of(
                    new Entry("Soleil", "weather clear"),
                    new Entry("Pluie", "weather rain"),
                    new Entry("Orage", "weather thunder"),
                    new Entry("Pas de météo", "gamerule doWeatherCycle false"),
                    new Entry("Météo normale", "gamerule doWeatherCycle true"))),
            new Category("Monde", List.of(
                    new Entry("Paisible", "difficulty peaceful"),
                    new Entry("Facile", "difficulty easy"),
                    new Entry("Normal", "difficulty normal"),
                    new Entry("Difficile", "difficulty hard"),
                    new Entry("Garder inventaire", "gamerule keepInventory true"),
                    new Entry("Perdre inventaire", "gamerule keepInventory false"),
                    new Entry("Pas de mobs hostiles", "gamerule doMobSpawning false"),
                    new Entry("Mobs normaux", "gamerule doMobSpawning true"),
                    new Entry("Tuer les items", "kill @e[type=minecraft:item]"),
                    new Entry("Tuer mobs proches", "kill @e[type=!minecraft:player,type=!minecraft:item,type=!minecraft:villager,type=!minecraft:armor_stand,tag=!keep,distance=..64]"),
                    new Entry("Point de spawn ici", "setworldspawn ~ ~ ~"),
                    new Entry("Sauvegarder", "save-all"))));

    private QuickCommands() {}
}
