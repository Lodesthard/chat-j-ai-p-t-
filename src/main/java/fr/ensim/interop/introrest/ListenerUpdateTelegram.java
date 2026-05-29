package fr.ensim.interop.introrest;

import fr.ensim.interop.introrest.model.Joke;
import fr.ensim.interop.introrest.model.telegram.ApiResponseUpdateTelegram;
import fr.ensim.interop.introrest.model.telegram.Message;
import fr.ensim.interop.introrest.model.telegram.Update;
import fr.ensim.interop.introrest.service.JokeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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

	@Value("${open.weather.api.url}")
	private String weatherApiUrl;

	@Value("${open.weather.forecast.url}")
	private String forecastApiUrl;

	@Value("${open.weather.api.token}")
	private String weatherApiToken;

	@Autowired
	private JokeService jokeService;

	private long offset = 0;
	private final RestTemplate restTemplate = new RestTemplate();

	@Override
	public void run(String... args) throws Exception {
		LOGGER.log(Level.INFO, "Démarage du listener d''updates Telegram...");
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
			LOGGER.log(Level.SEVERE, "Erreur polling Telegram: {0}", e.getMessage());
		}
	}

	private void handleUpdate(Update update) {
		if (!update.hasMessage()) return;
		Message message = update.getMessage();
		if (!message.hasText()) return;

		String text = message.getText().toLowerCase().trim();
		String originalText = message.getText().trim();
		Long chatId = message.getChatId();

		if (text.startsWith("previsions")) {
			String city = text.substring("previsions".length()).trim();
			if (city.isEmpty()) city = "Paris";
			sendMessage(chatId, callForecast(city));
		} else if (text.contains("meteo")) {
			String city = text.replace("meteo", "").trim();
			if (city.isEmpty()) city = "Paris";
			sendMessage(chatId, callWeather(city));
		} else if (text.startsWith("ajouter blague ")) {
			handleAddJoke(chatId, originalText.substring("ajouter blague ".length()).trim());
		} else if (text.startsWith("modifier blague ")) {
			handleUpdateJoke(chatId, originalText.substring("modifier blague ".length()).trim());
		} else if (text.startsWith("supprimer blague ")) {
			handleDeleteJoke(chatId, text.substring("supprimer blague ".length()).trim());
		} else if (text.startsWith("blague")) {
			String remainder = text.substring("blague".length()).trim();
			if (!remainder.isEmpty()) {
				try {
					handleGetJokeById(chatId, Long.parseLong(remainder));
				} catch (NumberFormatException e) {
					sendRandomJoke(chatId);
				}
			} else {
				sendRandomJoke(chatId);
			}
		} else {
			sendMessage(chatId, "Commandes disponibles :\n"
					+ "• meteo <ville>\n"
					+ "• previsions <ville>\n"
					+ "• blague\n"
					+ "• blague <id>\n"
					+ "• ajouter blague Question? | Réponse\n"
					+ "• modifier blague <id> Question? | Réponse\n"
					+ "• supprimer blague <id>");
		}
	}

	// --- Blagues ---

	private void handleAddJoke(Long chatId, String content) {
		String[] parts = content.split("\\s*\\|\\s*", 2);
		if (parts.length == 2 && !parts[0].isEmpty() && !parts[1].isEmpty()) {
			Joke joke = jokeService.add(parts[0].trim(), parts[1].trim());
			sendMessage(chatId, "Blague #" + joke.getId() + " ajoutée !");
		} else {
			sendMessage(chatId, "Format : ajouter blague Question? | Réponse");
		}
	}

	private void handleUpdateJoke(Long chatId, String content) {
		int spaceIdx = content.indexOf(' ');
		if (spaceIdx < 0) {
			sendMessage(chatId, "Format : modifier blague <id> Question? | Réponse");
			return;
		}
		try {
			Long id = Long.parseLong(content.substring(0, spaceIdx).trim());
			String rest = content.substring(spaceIdx).trim();
			String[] parts = rest.split("\\s*\\|\\s*", 2);
			if (parts.length == 2) {
				jokeService.update(id, parts[0].trim(), parts[1].trim())
						.ifPresentOrElse(
								j -> sendMessage(chatId, "Blague #" + id + " modifiée !"),
								() -> sendMessage(chatId, "Blague #" + id + " introuvable.")
						);
			} else {
				sendMessage(chatId, "Format : modifier blague <id> Question? | Réponse");
			}
		} catch (NumberFormatException e) {
			sendMessage(chatId, "Format : modifier blague <id> Question? | Réponse");
		}
	}

	private void handleDeleteJoke(Long chatId, String idStr) {
		try {
			Long id = Long.parseLong(idStr);
			sendMessage(chatId, jokeService.delete(id)
					? "Blague #" + id + " supprimée !"
					: "Blague #" + id + " introuvable.");
		} catch (NumberFormatException e) {
			sendMessage(chatId, "Format : supprimer blague <id>");
		}
	}

	private void handleGetJokeById(Long chatId, Long id) {
		sendMessage(chatId, jokeService.findById(id)
				.map(j -> "#" + j.getId() + " — " + j.getQuestion() + "\n" + j.getAnswer())
				.orElse("Blague #" + id + " introuvable."));
	}

	private void sendRandomJoke(Long chatId) {
		sendMessage(chatId, jokeService.getRandom()
				.map(j -> j.getQuestion() + "\n" + j.getAnswer())
				.orElse("Pas de blague disponible !"));
	}

	// --- Météo ---

	private String callWeather(String city) {
		try {
			String url = weatherApiUrl + "?q=" + city + "&appid=" + weatherApiToken + "&units=metric&lang=fr";
			@SuppressWarnings("unchecked")
			Map<String, Object> response = restTemplate.getForObject(url, Map.class);
			if (response == null) return "Météo indisponible pour " + city;
			return formatCurrentWeather(city, response);
		} catch (HttpClientErrorException e) {
			return e.getStatusCode().value() == 404
					? "Ville introuvable : " + city
					: "Erreur météo (" + e.getStatusCode().value() + ")";
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Erreur météo: {0}", e.getMessage());
			return "Météo indisponible pour " + city;
		}
	}

	private String callForecast(String city) {
		try {
			String url = forecastApiUrl + "?q=" + city + "&appid=" + weatherApiToken + "&units=metric&lang=fr&cnt=40";
			@SuppressWarnings("unchecked")
			Map<String, Object> response = restTemplate.getForObject(url, Map.class);
			if (response == null) return "Prévisions indisponibles pour " + city;
			return formatForecast(city, response);
		} catch (HttpClientErrorException e) {
			return e.getStatusCode().value() == 404
					? "Ville introuvable : " + city
					: "Erreur prévisions (" + e.getStatusCode().value() + ")";
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Erreur prévisions: {0}", e.getMessage());
			return "Prévisions indisponibles pour " + city;
		}
	}

	// --- Formatters ---

	@SuppressWarnings("unchecked")
	private String formatCurrentWeather(String city, Map<String, Object> response) {
		Map<String, Object> main = (Map<String, Object>) response.get("main");
		List<?> weatherList = (List<?>) response.get("weather");
		String description = "";
		if (weatherList != null && !weatherList.isEmpty()) {
			Map<String, Object> w = (Map<String, Object>) weatherList.get(0);
			description = String.valueOf(w.get("description"));
		}
		Object temp = main != null ? main.get("temp") : "?";
		return "Météo à " + city + " : " + description + ", " + temp + "°C";
	}

	@SuppressWarnings("unchecked")
	private String formatForecast(String city, Map<String, Object> response) {
		List<Map<String, Object>> list = (List<Map<String, Object>>) response.get("list");
		if (list == null || list.isEmpty()) return "Pas de prévisions pour " + city;

		Map<String, Map<String, Object>> byDay = new LinkedHashMap<>();
		for (Map<String, Object> entry : list) {
			String dtTxt = (String) entry.get("dt_txt");
			if (dtTxt == null || dtTxt.length() < 13) continue;
			String day = dtTxt.substring(0, 10);
			String hour = dtTxt.substring(11, 13);
			if (!byDay.containsKey(day) || "12".equals(hour)) byDay.put(day, entry);
		}

		DateTimeFormatter inFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter outFmt = DateTimeFormatter.ofPattern("EEE dd/MM", Locale.FRENCH);
		StringBuilder sb = new StringBuilder("Prévisions pour " + city + " :\n");
		int count = 0;
		for (Map.Entry<String, Map<String, Object>> e : byDay.entrySet()) {
			if (count++ >= 5) break;
			Map<String, Object> entry = e.getValue();
			Map<String, Object> main = (Map<String, Object>) entry.get("main");
			List<Map<String, Object>> wList = (List<Map<String, Object>>) entry.get("weather");
			String desc = wList != null && !wList.isEmpty() ? String.valueOf(wList.get(0).get("description")) : "";
			Object tMin = main != null ? main.get("temp_min") : "?";
			Object tMax = main != null ? main.get("temp_max") : "?";
			LocalDate date = LocalDate.parse(e.getKey(), inFmt);
			sb.append(date.format(outFmt)).append(" : ").append(desc)
			  .append(", ").append(tMin).append("°C — ").append(tMax).append("°C\n");
		}
		return sb.toString().trim();
	}

	// --- Envoi ---

	private void sendMessage(Long chatId, String text) {
		String url = telegramApiUrl + botToken + "/sendMessage";
		Map<String, Object> body = new HashMap<>();
		body.put("chat_id", chatId);
		body.put("text", text);
		try {
			restTemplate.postForObject(url, body, String.class);
		} catch (HttpClientErrorException e) {
			LOGGER.log(Level.WARNING, "Telegram chat_id invalide ({0}): {1}",
					new Object[]{chatId, e.getResponseBodyAsString()});
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Erreur envoi message Telegram: {0}", e.getMessage());
		}
	}
}
