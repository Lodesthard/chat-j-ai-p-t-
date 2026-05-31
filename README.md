# Chatbot Interoperabilite

Application Spring Boot exposant les fonctionnalites d'un chatbot Telegram via :

- une API REST securisee par token ;
- un listener Telegram en polling avec `getUpdates`.

La documentation complete du projet est dans [DOCUMENTATION.txt](DOCUMENTATION.txt).

## Fonctionnalites

API REST (decrite par l'OAS) :

- `GET /weather` — meteo du jour d'une ville ;
- `GET /forecast` — previsions a 5 jours ;
- `GET /joke` — blague au hasard (`?quality=best` / `?quality=worst`) ;
- `GET /jokes`, `GET /jokes/{id}`, `POST /jokes`, `PUT /jokes/{id}`, `DELETE /jokes/{id}` — CRUD blagues ;
- `POST /message` — envoi d'un message Telegram (`reply_to_message_id` supporte) ;
- `GET /chats` — derniers chats connus via `getUpdates`.

Une blague a un `id`, un `title`, un `text` et une `rating` (note sur 10).

Chatbot Telegram (polling `getUpdates`) : `meteo <ville>`, `previsions <ville>`,
`blague`, `blague top`, `blague nulle`, `blague <id>`, `joke` (blague tiree de
l'API `carambar-jokes` hebergee), ainsi que l'ajout, la modification et la
suppression de blagues.

Extras : gestion centralisee des erreurs, securite par token, generation des
DTO depuis l'OAS.

## Lancement

```powershell
mvn clean compile
mvn spring-boot:run
```

Port par defaut :

```text
http://localhost:9090
```

Si le port est deja utilise :

```powershell
mvn spring-boot:run '-Dspring-boot.run.arguments=--server.port=9091'
```

## Securite API

Les endpoints REST attendent un token :

```text
X-API-Token: interop-secret
```

ou :

```text
Authorization: Bearer interop-secret
```

Pour tester directement dans un navigateur :

```text
http://localhost:9090/joke?access_token=interop-secret
```

## OpenAPI

Le contrat OAS est ici :

```text
src/main/resources/openapi/chatbot-api.yaml
```

Les DTO Java sont generes automatiquement par Maven dans :

```text
target/generated-sources/openapi
```
