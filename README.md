# Eau de Paris - Synchro Remocra

* **Client** : Eau de Paris
* **Nom projet Financier** : TMA SIG Open Source - Echanges REMOCRA / WatGIS -Interface REMOCRA
* **Objet** : Synchronisation des caractéristiques techniques et des disponibilités avec Remocra et suivi de la synchronisation



## Démarrage rapide

Les commandes sont exécutées dans le répertoire racine du projet.

### Les dépendances

Elles sont démarrées via des conteneurs :

```sh
# Démarrage d'une instance Remocra
# TODO cf. projet atolcd/sdis-remocra

# Démarrage de dépendances directes
(cd docker && docker-compose up)
```

Voir le fichier [docker/README.md](docker/README.md) pour plus de détails.


### Exécution de l'application

**Maven :**
```sh
mvn clean compile exec:java \
  -Dedp.database.serverName=localhost \
  -Dexec.args="help"
```

**Docker :**

```sh
docker run --network="host" --rm \
  --name edp-dev \
  -u $(id -u):$(id -g) \
  -v "$(pwd)":/app -w /app \
  -v ~/.m2:/var/maven/.m2 -v "${HOME}":/var/maven \
  -e HOME=/var/maven -e MAVEN_CONFIG=/var/maven/.m2 -e MAVEN_OPTS="-Duser.home=/var/maven" \
  \
  maven:3-jdk-11 \
  \
  mvn clean compile exec:java
```

**IDE :**

Classe principale : `fr.eaudeparis.syncremocra.App`

Exemple d'arguments :

```
  -Dedp.database.serverName=localhost
```

Exemple pour Visual Studio Code .vscode/launch.json :
```json
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "Job tests",
            "request": "launch",
            "mainClass": "fr.eaudeparis.syncremocra.App",
            "projectName": "syncremocra-app",
            "vmArgs": "-Dedp.database.serverName=localhost"
            "args": "-c=path/to/dev.conf -l=path/to/log4j2-dev.conf job-test-all"
        }
    ]
}
```



## Formatage de code (API)

Le code java de ce projet se conforme au [Google's code styleguide](https://google.github.io/styleguide/javaguide.html).

Le plugin maven `spotless` traite cet aspect :
```sh
# Vérification du formatage
mvn spotless:check

# Application du formatage (à faire avant tout commit)
mvn spotless:apply
```

### Editeurs pris en charge

Des formateurs spécifiques existent pour [Eclipse](https://github.com/google/styleguide/blob/gh-pages/eclipse-java-google-style.xml) et [IntelliJ](https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml).

### Visual Studio

On automatise l'utilisation de [google-java-format](https://github.com/google/google-java-format) à la sauvegarde des fichiers.

Installation du plugin [emeraldwalk/vscode-runonsave](https://marketplace.visualstudio.com/items?itemName=emeraldwalk.RunOnSave) :
```sh
code --install-extension emeraldwalk.RunOnSave
```

Puis on configure la commande dans le fichier .vscode/settings.json :
```json
{
    "java.configuration.updateBuildConfiguration": "automatic",
    "emeraldwalk.runonsave": {
    "commands": [
      {
        "match": "\\.java$",
        "cmd": "app/java-format.sh ${file}"
      },
    ],
  }
}
```



## Opérations sur la base de données

La génération du modèle Jooq et l'application des migrations sont expliquées dans le fichier [db/README.md](db/README.md).



## Documentation d'exploitation

Les manuels d'installation, de mise à jour et d'exploitation sont maintenus avec le code source dans le projet `doc`.



## Pentaho Data Integration

Cf. [pdi/README.md](pdi/README.md).



## Intégration continue

Les pipelines sont décrits dans le fichier [jenkins/README.md](jenkins/README.md).



## Validation dans une box Vagrant

Pour tester l'application dans une box Vagrant, se référer au fichier [vagrant/README.md](vagrant/README.md).



## Distribution

La distribution est expliquée dans le fichier [dist/README.md](dist/README.md). En particulier, cette documentation décrit le **process de mise à jour d'un serveur**.
