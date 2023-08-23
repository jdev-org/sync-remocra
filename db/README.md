# Eau de Paris - Synchro Remocra - Base de données

Les commandes sont exécutées dans le répertoire `db`.

# Migrations

Les scripts sont dans le répertoire `src/main/resources/db/migration`.

Les migrations sont réalisées à partir des commande suivantes :

```sh
# Validation
mvn clean flyway:validate

# Migration
mvn clean flyway:migrate
```

# Génération du modèle JOOQ

Lorsque le schéma de la base de données a changé, il est nécessaire de regénérer les classes du modèle Jooq :

```sh
# Génération des classes de src/main/jooq/
mvn clean jooq-codegen:generate
```