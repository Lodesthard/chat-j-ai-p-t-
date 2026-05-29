package fr.ensim.interop.introrest.controller;

import fr.ensim.interop.introrest.model.Joke;
import fr.ensim.interop.introrest.model.telegram.ApiResponseUpdateTelegram;
import fr.ensim.interop.introrest.model.telegram.Update;
import fr.ensim.interop.introrest.service.JokeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
public class MessageRestController {

    @Autowired
    private JokeService jokeService;

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

    private final RestTemplate restTemplate = new RestTemplate();

    // --- Météo courante ---

    @GetMapping("/weather")
    public String weather(@RequestParam(defaultValue = "Paris") String city) {
        try {
            String url = weatherApiUrl + "?q=" + city + "&appid=" + weatherApiToken + "&units=metric&lang=fr";
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            return formatCurrentWeather(city, response);
        } catch (HttpClientErrorException e) {
            return e.getStatusCode().value() == 404
                    ? "Ville introuvable : " + city
                    : "Erreur météo (" + e.getStatusCode().value() + ")";
        } catch (Exception e) {
            return "Météo indisponible : " + e.getMessage();
        }
    }

    // --- Prévisions 5 jours ---

    @GetMapping("/forecast")
    public String forecast(@RequestParam(defaultValue = "Paris") String city) {
        try {
            String url = forecastApiUrl + "?q=" + city + "&appid=" + weatherApiToken + "&units=metric&lang=fr&cnt=40";
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            return formatForecast(city, response);
        } catch (HttpClientErrorException e) {
            return e.getStatusCode().value() == 404
                    ? "Ville introuvable : " + city
                    : "Erreur prévisions (" + e.getStatusCode().value() + ")";
        } catch (Exception e) {
            return "Prévisions indisponibles : " + e.getMessage();
        }
    }

    // --- Blague aléatoire ---

    @GetMapping("/joke")
    public String joke() {
        return jokeService.getRandom()
                .map(j -> j.getQuestion() + "\n" + j.getAnswer())
                .orElse("Pas de blague disponible !");
    }

    // --- CRUD Blagues ---

    @GetMapping("/jokes")
    public Collection<Joke> jokes(@RequestParam(required = false) String q) {
        if (q != null && !q.isEmpty()) return jokeService.findByQuestion(q);
        return jokeService.getAll();
    }

    @GetMapping("/jokes/{id}")
    public ResponseEntity<Joke> jokeById(@PathVariable Long id) {
        return jokeService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/jokes")
    public Joke addJoke(@RequestBody Joke joke) {
        return jokeService.add(joke.getQuestion(), joke.getAnswer());
    }

    @PutMapping("/jokes/{id}")
    public ResponseEntity<Joke> updateJoke(@PathVariable Long id, @RequestBody Joke joke) {
        return jokeService.update(id, joke.getQuestion(), joke.getAnswer())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/jokes/{id}")
    public ResponseEntity<Void> deleteJoke(@PathVariable Long id) {
        return jokeService.delete(id)
                ? ResponseEntity.noContent().<Void>build()
                : ResponseEntity.notFound().build();
    }

    // --- Telegram ---

    @PostMapping("/message")
    public ResponseEntity<String> message(@RequestBody Map<String, Object> payload) {
        try {
            String url = telegramApiUrl + botToken + "/sendMessage";
            Map<String, Object> body = new HashMap<>();
            body.put("chat_id", payload.get("chat_id"));
            body.put("text", payload.get("text"));
            String result = restTemplate.postForObject(url, body, String.class);
            return ResponseEntity.ok(result);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body("Erreur Telegram : " + e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur interne : " + e.getMessage());
        }
    }

    @GetMapping("/chats")
    public List<Map<String, Object>> chats() {
        String url = telegramApiUrl + botToken + "/getUpdates?limit=20";
        ApiResponseUpdateTelegram response = restTemplate.getForObject(url, ApiResponseUpdateTelegram.class);
        if (response == null || response.getResult() == null) return new ArrayList<>();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Update update : response.getResult()) {
            if (update.hasMessage() && update.getMessage().hasText()) {
                Map<String, Object> info = new HashMap<>();
                info.put("chat_id", update.getMessage().getChatId());
                info.put("text", update.getMessage().getText());
                result.add(info);
            }
        }
        return result;
    }

    // --- Formatters partagés ---

    @SuppressWarnings("unchecked")
    private String formatCurrentWeather(String city, Map<String, Object> response) {
        if (response == null) return "Météo indisponible pour " + city;
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
        if (response == null) return "Prévisions indisponibles pour " + city;
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

}
