# Eau de Paris - Synchro Remocra - Intégration continue

L'intégration continue est assurée par des pipelines jenkins :

📚 **Relecture de code**
* *[eau-de-paris/sync-remocra [review]](https://jenkins.priv.atolcd.com/blue/organizations/jenkins/eaudeparis--syncremocra--review/activity)*
* `jenkins/Jenkinsfile-review`
* Déclenché automatiquement (tout commit de relecture)

🏭 **Fusion**
* *[eau-de-paris/sync-remocra [merge]](https://jenkins.priv.atolcd.com/blue/organizations/jenkins/eaudeparis--syncremocra--merge/activity)*
* `jenkins/Jenkinsfile-merge`
* Déclenché automatiquement (tout commit validé)
* commit ➞ artefacts archivés dans jenkins

👍 **Release**
* *[eau-de-paris/sync-remocra [release]](https://jenkins.priv.atolcd.com/blue/organizations/jenkins/eaudeparis--syncremocra--release/activity)*
* `jenkins/Jenkinsfile-release`
* Déclenché manuellement
* commit ➞ images docker dans [nexus client](https://nexus3-ovh.priv.atolcd.com/#browse/search=keyword%3Dedp%2Fsyncremocra)
