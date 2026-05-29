# Graph Report - .  (2026-05-28)

## Corpus Check
- 28 files · ~7,500 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 195 nodes · 241 edges · 25 communities (15 shown, 10 thin omitted)
- Extraction: 90% EXTRACTED · 10% INFERRED · 0% AMBIGUOUS · INFERRED: 24 edges (avg confidence: 0.83)
- Token cost: 24,932 input · 0 output

## Community Hubs (Navigation)
- [[_COMMUNITY_Community 0|Community 0]]
- [[_COMMUNITY_Community 1|Community 1]]
- [[_COMMUNITY_Community 2|Community 2]]
- [[_COMMUNITY_Community 3|Community 3]]
- [[_COMMUNITY_Community 4|Community 4]]
- [[_COMMUNITY_Community 5|Community 5]]
- [[_COMMUNITY_Community 6|Community 6]]
- [[_COMMUNITY_Community 7|Community 7]]
- [[_COMMUNITY_Community 8|Community 8]]
- [[_COMMUNITY_Community 9|Community 9]]
- [[_COMMUNITY_Community 10|Community 10]]
- [[_COMMUNITY_Community 11|Community 11]]
- [[_COMMUNITY_Community 12|Community 12]]
- [[_COMMUNITY_Community 13|Community 13]]
- [[_COMMUNITY_Community 14|Community 14]]
- [[_COMMUNITY_Community 15|Community 15]]
- [[_COMMUNITY_Community 16|Community 16]]
- [[_COMMUNITY_Community 17|Community 17]]
- [[_COMMUNITY_Community 18|Community 18]]
- [[_COMMUNITY_Community 20|Community 20]]
- [[_COMMUNITY_Community 21|Community 21]]
- [[_COMMUNITY_Community 22|Community 22]]
- [[_COMMUNITY_Community 23|Community 23]]

## God Nodes (most connected - your core abstractions)
1. `Message` - 21 edges
2. `BotApiObject` - 20 edges
3. `JsonIgnore` - 17 edges
4. `Message (Telegram model)` - 16 edges
5. `Update` - 9 edges
6. `ApiResponseTelegram` - 8 edges
7. `ApiResponseUpdateTelegram` - 8 edges
8. `ListenerUpdateTelegram` - 6 edges
9. `Chat` - 6 edges
10. `BotApiObject` - 6 edges

## Surprising Connections (you probably didn't know these)
- `Message (Telegram model)` --references--> `API requirement: message (sendMessage)`  [EXTRACTED]
  src/main/java/fr/ensim/interop/introrest/model/telegram/Message.java → mini-projet-eval-interop.pdf
- `Message (Telegram model)` --references--> `Provided Telegram object classes (Message, ApiResponseTelegram, ApiResponseUpdateTelegram)`  [EXTRACTED]
  src/main/java/fr/ensim/interop/introrest/model/telegram/Message.java → mini-projet-eval-interop.pdf
- `Message (Telegram model)` --references--> `Bonus: reply_to_message_id quoting`  [EXTRACTED]
  src/main/java/fr/ensim/interop/introrest/model/telegram/Message.java → mini-projet-eval-interop.pdf
- `Message (Telegram model)` --references--> `Pre-built Telegram API object model`  [EXTRACTED]
  src/main/java/fr/ensim/interop/introrest/model/telegram/Message.java → CLAUDE.md
- `Update (Telegram model)` --conceptually_related_to--> `Polling with getUpdates and offset`  [INFERRED]
  src/main/java/fr/ensim/interop/introrest/model/telegram/Update.java → mini-projet-eval-interop.pdf

## Hyperedges (group relationships)
- **Telegram BotApiObject Model Family** — botapiobject_interface, audio_class, chat_class, chatinvitelink_class, chatjoinrequest_class, chatlocation_class, chatmemberupdated_class [EXTRACTED 1.00]
- **Telegram Update Polling Flow** — listenerupdatetelegram_run, listenerupdatetelegram_pollupdates, listenerupdatetelegram_handleupdate, listenerupdatetelegram_sendmessage [EXTRACTED 1.00]
- **Message media payload composition** — message_Message, document_Document, video_Video, photosize_PhotoSize, contact_Contact, location_Location [EXTRACTED 1.00]
- **Telegram getUpdates polling data flow** — pdf_Requirement_Polling, update_Update, message_Message [INFERRED 0.85]

## Communities (25 total, 10 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.05
Nodes (24): Serializable, Boolean, Integer, Override, ResponseParameters, String, T, ApiResponseTelegram (+16 more)

### Community 1 - "Community 1"
Cohesion: 0.13
Nodes (7): MessageEntity, JsonIgnore, List, Long, String, Message, User

### Community 2 - "Community 2"
Cohesion: 0.14
Nodes (6): CommandLineRunner, ListenerUpdateTelegram, Long, String, Update, Update

### Community 3 - "Community 3"
Cohesion: 0.17
Nodes (17): POST /message endpoint, Pre-built Telegram API object model, Contact, Document, Location, Message (Telegram model), MessageEntity, Provided Telegram object classes (Message, ApiResponseTelegram, ApiResponseUpdateTelegram) (+9 more)

### Community 4 - "Community 4"
Cohesion: 0.15
Nodes (8): Boolean, Integer, List, Override, ResponseParameters, String, Update, ApiResponseUpdateTelegram

### Community 5 - "Community 5"
Cohesion: 0.33
Nodes (6): MessageRestController, GetMapping, Map, Object, PostMapping, String

### Community 6 - "Community 6"
Cohesion: 0.25
Nodes (8): ApiResponseTelegram, ApiResponseUpdateTelegram, ChatBotApplication, ChatBotApplication.main, ListenerUpdateTelegram.handleUpdate, ListenerUpdateTelegram.pollUpdates, ListenerUpdateTelegram.run, ListenerUpdateTelegram.sendMessage

### Community 7 - "Community 7"
Cohesion: 0.50
Nodes (3): Boolean, JsonIgnore, Chat

### Community 8 - "Community 8"
Cohesion: 0.52
Nodes (7): Audio, BotApiObject, Chat, ChatInviteLink, ChatJoinRequest, ChatLocation, ChatMemberUpdated

### Community 9 - "Community 9"
Cohesion: 0.33
Nodes (3): ChatBotApplication, String, Override

### Community 11 - "Community 11"
Cohesion: 0.67
Nodes (3): GET /joke endpoint, icanhazdadjoke API (https://icanhazdadjoke.com/), API requirement: joke operation

### Community 12 - "Community 12"
Cohesion: 0.67
Nodes (3): ListenerUpdateTelegram, MessageRestController, Telegram getUpdates Polling Pattern

### Community 13 - "Community 13"
Cohesion: 1.00
Nodes (3): GET /weather endpoint, OpenWeatherMap API (5 Day / 3 Hour Forecast), API requirement: weather operation

## Knowledge Gaps
- **43 isolated node(s):** `recordToolUse.sh script`, `java.compile.nullAnalysis.mode`, `java.configuration.updateBuildConfiguration`, `String`, `Override` (+38 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **10 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `BotApiObject` connect `Community 0` to `Community 1`, `Community 2`, `Community 7`?**
  _High betweenness centrality (0.348) - this node is a cross-community bridge._
- **Why does `Message` connect `Community 1` to `Community 0`, `Community 3`?**
  _High betweenness centrality (0.262) - this node is a cross-community bridge._
- **Why does `POST /message endpoint` connect `Community 3` to `Community 1`?**
  _High betweenness centrality (0.103) - this node is a cross-community bridge._
- **What connects `recordToolUse.sh script`, `java.compile.nullAnalysis.mode`, `java.configuration.updateBuildConfiguration` to the rest of the system?**
  _45 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 0` be split into smaller, more focused modules?**
  _Cohesion score 0.045328399629972246 - nodes in this community are weakly interconnected._
- **Should `Community 1` be split into smaller, more focused modules?**
  _Cohesion score 0.1339031339031339 - nodes in this community are weakly interconnected._
- **Should `Community 2` be split into smaller, more focused modules?**
  _Cohesion score 0.13725490196078433 - nodes in this community are weakly interconnected._