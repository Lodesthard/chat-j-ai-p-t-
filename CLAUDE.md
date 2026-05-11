# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```powershell
mvn clean compile          # compile
mvn spring-boot:run        # run app (port 9090)
mvn clean install          # build full jar
```

Maven is installed at `C:\Users\bidbi\Documents\apache-maven-3.9.15-bin\apache-maven-3.9.15`. Java 17 (Eclipse Adoptium). No test suite configured.

## Architecture

Spring Boot 2.4.5 chatbot that bridges Telegram Bot API and OpenWeatherMap.

**Two independent execution paths:**

1. **REST API** (`/controller`) — manually triggered via Postman/curl. Endpoints to implement: `GET /weather`, `GET /joke`, `POST /message`.
2. **Telegram polling** (`ListenerUpdateTelegram`) — runs on startup via `CommandLineRunner`, polls `getUpdates` every 3s with `Timer`, reacts to keywords "meteo" and "blague" by calling `sendMessage`.

**Key classes:**
- `ListenerUpdateTelegram` — polling loop, offset tracking to avoid reprocessing events
- `MessageRestController` — REST endpoints (mostly empty, to be implemented)
- `ClientRestTest.main()` — scratch pad for manual API call tests
- `model/telegram/` — pre-built Telegram API object model (Message, Update, Chat, ApiResponseUpdateTelegram, etc.), all Lombok-annotated

**Config** (`application.properties`):
- `telegram.api.url=https://api.telegram.org/bot` — must include trailing `bot`
- `telegram.bot.id` — full bot token
- `open.weather.api.url` / `open.weather.api.token` — OpenWeatherMap

## Lombok note

VS Code Java Language Server shows false errors on Lombok-generated getters. `mvn compile` is authoritative. Install extension `gabrielbb.vscode-lombok` to suppress IDE false positives.

## Telegram URL pattern

All Telegram calls: `telegramApiUrl + botToken + "/endpoint"` → `https://api.telegram.org/bot<TOKEN>/getUpdates`
