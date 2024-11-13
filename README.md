# Un moteur d’évaluation de requêtes en étoile

**Auteurs**:  

- Adam EL HORRI - adam.el-horri@etu.umontpellier.fr
- Lucie SORREAU - lucie.sorreau@etu.umontpellier.fr

**Supervision**:

- Federico Ulliana - federico.ulliana@inria.fr
- Guillaume Perution-Kihli - guillaume.perution-kihli@inria.fr

## Description du Projet
Ce projet vise à implémenter un mini moteur de requêtes en étoile basé sur l'approche **Hexastore** pour l'interrogation de données RDF. Nous explorerons et évaluerons les performances de ce moteur en le comparant avec le système **Integraal** qui servira de référence pour valider la correction et la complétude des réponses aux requêtes.

Le projet est divisé en plusieurs étapes de développement et de rendu, chacune visant à enrichir le moteur de stockage et de requêtes.

## Structure du Projet

### Classes Principales
- **RDFAtom.java**: Classe représentant les triplets RDF utilisés dans la base de données.
- **StarQuery.java**: Représentation des requêtes en étoile, permettant de spécifier un ensemble de triplets RDF partageant une variable commune.
- **RDFHexaStore.java**: Implémentation de l'Hexastore pour stocker et interroger les triplets RDF avec six index différents pour optimiser les requêtes.
- **RDFStorage.java**: Interface définissant les méthodes de l'Hexastore, telles que l'ajout et la recherche de triplets RDF.
- **Example.java**: Exemple d'usage pour lire des fichiers de données RDF et des requêtes, permettant de vérifier la structure de base du projet.

## Prérequis

- **Java 21** ou version ultérieure
- **Maven** pour la gestion des dépendances
- **Eclipse IDE** ou **IntelliJ IDEA** pour le développement et l'exécution
- Dépendances Java gérées via **Maven** (voir `pom.xml`)

## Compilation et Exécution

1. **Cloner le dépôt du projet**  
   ```bash
   git clone https://github.com/adamelhorri/HexaStore-Systeme-de-Stockage-et-Requete-RDF-Optimise.git

   ```

2. **Configurer Maven**  
   Si Maven n'est pas reconnu par votre IDE, configurez le projet en Maven comme suit :
   - Dans **Eclipse** : Clic droit sur le projet > `Configure` > `Convert to Maven Project`
   - Dans **IntelliJ IDEA** : Importez directement le projet comme un projet Maven

3. **Compiler le Projet**  
   Exécutez la commande suivante depuis le terminal ou via votre IDE pour compiler le projet :
   ```bash
   mvn clean compile
   ```

4. **Exécuter l'Exemple**  
   Après compilation, exécutez la classe `Example.java` pour vérifier que le projet est correctement configuré et que les dépendances sont chargées.

## Fonctionnalités à Implémenter

1. **Dictionnaire et Index**  
   - [x] Encodage des triplets RDF en utilisant un dictionnaire associant un entier à chaque ressource
   - [x] Création de l'Hexastore avec six index pour optimiser l'interrogation
   - Date de rendu : **15 novembre**

2. **Évaluation des Requêtes en Étoile**  
   - [ ] Lecture des requêtes SPARQL en étoile via le parser fourni
   - [ ] Accès aux données en fonction des index et évaluation des requêtes
   - [ ] Comparaison des résultats avec le système Integraal pour vérifier la correction et la complétude
   - Date de rendu : **29 novembre**

3. **Analyse des Performances**  
   - [ ] Évaluer les performances du moteur de requêtes en comparaison avec Integraal
   - [ ] Rédiger un rapport de 5 pages sur les observations et les résultats
   - Date de rendu : **13 décembre**

## Tâches

### Phases de Développement et Checkboxes

- **Dictionnaire et Indexation**
  - [x] Créer un dictionnaire pour encoder les ressources RDF
  - [x] Implémenter l'insertion dans l'Hexastore avec les six index
  - [x] Écrire des tests unitaires pour valider le dictionnaire et les index
  - [x] Tester l'insertion des triplets RDF et vérifier la couverture du code

- **Évaluation des Requêtes**
  - [x] Créer la méthode `match(StarQuery q)` pour interroger l'Hexastore
  - [ ] Valider la précision des réponses aux requêtes en étoile
  - [ ] Comparer les résultats avec Integraal pour la correction et la complétude

- **Analyse des Performances**
  - [ ] Mesurer les temps de réponse pour différentes requêtes en étoile
  - [ ] Comparer les performances du système avec celles de Integraal
  - [ ] Rédiger le rapport d'analyse des performances

### État Actuel du Projet

| Étape                               | Statut |
| ----------------------------------- | ------ |
| Dictionnaire                        | ✅      |
| Indexation Hexastore                | ✅      |
| Évaluation des Requêtes (StarQuery) | 🔲     |
| Vérification avec Integraal         | 🔲     |
| Analyse des Performances            | 🔲     |

## Dépendances

Ce projet utilise plusieurs bibliothèques, notamment :
- **rdf4j** 3.7.3 : pour le traitement des données RDF et des requêtes SPARQL
- **Integraal** 1.6.0 : utilisé comme système de comparaison pour l'évaluation des performances

Les dépendances sont gérées par **Maven**, et toutes les bibliothèques nécessaires sont spécifiées dans le fichier `pom.xml`.

## Tests Unitaires

Des tests unitaires sont disponibles dans le répertoire `src/test/java`. Ils permettent de valider le comportement des méthodes principales et d'assurer une couverture de code suffisante. Avant de commencer chaque fonctionnalité, il est conseillé d'écrire les tests unitaires correspondant, en couvrant tous les cas d'usage.

### Jeu de Données

Les fichiers de données RDF et de requêtes sont situés dans le dossier `data/`. Ils incluent des jeux de données de test simples (`sample_data.nt` et `sample_query.queryset`) et des ensembles de données plus volumineux (`100K.nt` et `STAR_ALL_workload.queryset`).

