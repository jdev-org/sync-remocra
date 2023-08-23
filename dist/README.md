# Eau de Paris - Synchro Remocra - Distribution

## Process de mise à jour d'un serveur

**1. Tagger la version si nécessaire**. Exemple depuis son PC :

Le tag doit exister sur le dépôt central, commencer par `v` et être annoté :

```sh
# Création et partage du tag
tag() {
  export APP_VERSION=$1
  if [ -z "${APP_VERSION}" ] ; then
    echo "Usage (ex) :" && echo "  tag 1.0.0"
  else
    git tag -a v${APP_VERSION} -m "Version ${APP_VERSION}" && \
    git push origin v${APP_VERSION}
  fi
}

tag 0.1.0
```

**2. Publier les images docker via le job [eau-de-paris/sync-remocra [release]](https://jenkins.priv.atolcd.com/blue/organizations/jenkins/eaudeparis--syncremocra--release/activity)**

qui construit les images `edp/syncremocra` et `edp/syncremocra-pdi` avec une version calculée par rapport au dernier tag (ex : `0.1.0-3-gb9ec7dd`) et `latest` et la pousse dans le registre docker `client-docker-registry.atolcd.com`.

Le paramètre `COMMIT_TO_BUILD` permet de choisir le commit, la branche ou le tag à construire (exemples : `f06318bef86c474e1f3a8ed6ba81bdc99b23c780`, `master`, `v1.0.0`).

**3. Se connecter au serveur et mettre à jour la version à utiliser**

une fois que le job est terminé et un tag générique type `latest` n'est pas utilisé. Exemple :

```sh
# Exemple de version
export EDP_VERSION=1.0

# Nouvelle version dans le fichier de configuration
sed -i "s/EDP_VERSION.*=.*/EDP_VERSION=${EDP_VERSION}/g" /var/syncremocra/common.env
```



## Distribution par fichier tar

Les images `edp/syncremocra` et `edp/syncremocra-pdi` sont concernées.

Packaging :
```sh
./package-tar.sh
```

Chargement des images sur un serveur (exemple version 1.0) :
```sh
docker load < edp-syncremocra-docker-images-1.0.tar
```
