package fr.ensim.interop.introrest;

import fr.ensim.interop.introrest.model.telegram.ApiResponseUpdateTelegram;
import fr.ensim.interop.introrest.model.telegram.Message;
import fr.ensim.interop.introrest.model.telegram.Update;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class ListenerUpdateTelegram implements CommandLineRunner {

	private static final Logger LOGGER = Logger.getLogger("ListenerUpdateTelegram");

	@Value("${telegram.api.url}")
	private String telegramApiUrl;

	@Value("${telegram.bot.id}")
	private String botToken;

	private long offset = 0;
	private final RestTemplate restTemplate = new RestTemplate();

	@Override
	public void run(String... args) throws Exception {
		LOGGER.log(Level.INFO, "Démarage du listener d'updates Telegram...");

		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				pollUpdates();
			}
		}, 0, 10000);
	}

	private void pollUpdates() {
		String url = telegramApiUrl + botToken + "/getUpdates?offset=" + offset;
		try {
			ApiResponseUpdateTelegram response = restTemplate.getForObject(url, ApiResponseUpdateTelegram.class);
			if (response != null && Boolean.TRUE.equals(response.getOk()) && response.getResult() != null) {
				for (Update update : response.getResult()) {
					handleUpdate(update);
					offset = update.getUpdateId() + 1;
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Erreur polling Telegram: " + e.getMessage());
		}
	}

	private void handleUpdate(Update update) {
		if (!update.hasMessage()) return;
		Message message = update.getMessage();
		if (!message.hasText()) return;

		String text = message.getText().toLowerCase().trim();
		Long chatId = message.getChatId();

		if (text.contains("meteo")) {
			// TODO étape 2 : appel OpenWeatherMap
			sendMessage(chatId, "Fonctionnalité météo bientôt disponible !");
		} else if (text.contains("blague")) {
			// TODO étape 3 : blague aléatoire
			sendMessage(chatId, "Fonctionnalité blague bientôt disponible !");
		}
		else {
			sendMessage(chatId, "PROUT 💩💩💩");
		}
	}

	private void sendMessage(Long chatId, String text) {
		String url = telegramApiUrl + botToken + "/sendMessage";
		Map<String, Object> body = new HashMap<>();
		body.put("chat_id", chatId);
		body.put("text", text);
		try {
			restTemplate.postForObject(url, body, String.class);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Erreur envoi message Telegram: " + e.getMessage());
		}
	}
}
