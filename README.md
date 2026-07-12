#  Hydration Tracker (Suivi Hydratation)

Projet réalisé dans le cadre du programme **LPRGL3A** à PIGIER Côte d'Ivoire.

## Infos sur le  projet 

Une application Android qui aide à suivre sa consommation d'eau au quotidien. L'idée de base : on boit souvent moins d'eau qu'on ne le pense, donc autant avoir une app qui garde le compte à notre place plutôt que de se fier à sa mémoire.

C'était l'occasion pour moi d'appliquer concrètement tout ce qu'on a vu en cours sur le développement Android moderne : Jetpack Compose pour l'interface, Room pour stocker les données en local, Firebase pour la partie cloud, et Retrofit pour parler à une API.

## Fonctionnalités

-  Suivi de la consommation d'eau au fil de la journée
-  Sauvegarde locale des données (Room)
-  Synchronisation avec Firebase
-  Interface construite entièrement en Jetpack Compose


## Stack technique

| Techno | Usage |
|---|---|
| **Kotlin** | Langage principal |
| **Jetpack Compose** | Interface utilisateur |
| **Room** | Base de données locale |
| **Firebase** | Backend / synchronisation cloud |
| **Retrofit + Moshi** | Appels réseau et parsing JSON |
| **KSP** | Génération de code (annotations Room) |

Versions imposées par le professeur (voir `gradle/libs.versions.toml`) :
```
```

## Comment lancer le projet chez toi

1. Clone le dépôt 

2. **Ouvre le projet dans Android Studio.**

3. **Laisse Gradle synchroniser.** Le fichier `debug.keystore`, nécessaire pour signer l'app en mode debug, se **génère automatiquement tout seul** au premier build — pas besoin de commande manuelle. (Si ça t'intéresse de savoir comment, regarde `Documentation.pdf`.)

4. **Lance l'app** (Run) sur un émulateur ou ton téléphone.

C'est tout — pas de configuration compliquée à faire à la main.

## Structure du projet (rapide aperçu)

```
Hydration Tracker/
├── app/
│   ├── src/main/kotlin/     → code source (écrans Compose, ViewModels, etc.)
│   └── build.gradle.kts     → config du module app
├── gradle/
│   └── libs.versions.toml   → catalogue de versions (imposé par le prof)
├── build.gradle.kts         → config racine
├── TROUBLESHOOTING.md       → tout ce qui a merdé et comment c'est réglé
└── README.md                → toi, tu es là
```

## Auteur

Koné Cheick Mohamed Yassine 

---

*Projet réalisé dans un cadre pédagogique. N'hésite pas à ouvrir une issue ou me contacter si tu galères en le clonant.*
