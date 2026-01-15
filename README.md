# Remember_velocity

Plugin Velocity (3.4.0) pour envoyer un joueur soit au spawn, soit sur son dernier serveur connu.

## Fonctionnalités
- Envoi au serveur `spawn` pour les nouveaux joueurs.
- Reconnexion automatique au dernier serveur connu si déjà enregistré.
- Stockage YAML ou MariaDB.
- Fichier de configuration complet + fichier de langue.
- Mode debug très verbeux.

## Build
```bash
mvn -q -DskipTests package
```

Le jar se trouve dans `target/remember-velocity-1.0.0.jar`.

## Configuration
Le plugin crée `config.yml` et `lang/messages.yml` dans son dossier de données.

- `spawn-server`: nom du serveur par défaut.
- `storage.type`: `yaml` ou `mariadb`.
- `debug`: active les logs verbeux.

## Compatibilité
Java 21 / 22.
