package fr.ensim.interop.introrest.service;

import fr.ensim.interop.introrest.model.Joke;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ChatbotService {
    private static final Logger LOGGER = Logger.getLogger("ChatbotService");

    @Value("${open.weather.api.url}")
    private String weatherApiUrl;

    @Value("${open.weather.forecast.url}")
    private String forecastApiUrl;

    @Value("${open.weather.api.token}")
    private String weatherApiToken;

    @Value("${server.port:9090}")
    private int serverPort;

    @Value("${api.access.token}")
    private String apiAccessToken;

    @Autowired
    private JokeService jokeService;

    private final RestTemplate restTemplate = new RestTemplate();

    public String executeCommand(String messageText) {
        if (messageText == null) {
            return helpMessage();
        }

        String text = messageText.toLowerCase().trim();
        String originalText = messageText.trim();

        if (text.startsWith("previsions")) {
            String city = text.substring("previsions".length()).trim();
            return getForecastText(city.isEmpty() ? "Paris" : city);
        }
        if (text.contains("meteo")) {
            String city = text.replace("meteo", "").trim();
            return getWeatherText(city.isEmpty() ? "Paris" : city);
        }
        if (text.startsWith("ajouter blague ")) {
            return addJokeFromCommand(originalText.substring("ajouter blague ".length()).trim());
        }
        if (text.startsWith("modifier blague ")) {
            return updateJokeFromCommand(originalText.substring("modifier blague ".length()).trim());
        }
        if (text.startsWith("supprimer blague ")) {
            return deleteJokeFromCommand(text.substring("supprimer blague ".length()).trim());
        }
        if (text.startsWith("joke")) {
            return getCarambarJokeText();
        }
        if (text.startsWith("blague")) {
            String remainder = text.substring("blague".length()).trim();
            if (remainder.isEmpty()) {
                return getRandomJokeText();
            }
            if (remainder.startsWith("nulle") || remainder.startsWith("pourrie")) {
                return getWorstJokeText();
            }
            if (remainder.startsWith("top") || remainder.startsWith("super") || remainder.startsWith("bonne")) {
                return getBestJokeText();
            }
            try {
                return getJokeByIdText(Long.parseLong(remainder));
            } catch (NumberFormatException e) {
                return getRandomJokeText();
            }
        }

        return helpMessage();
    }

    public String getWeatherText(String city) {
        try {
            String url = weatherApiUrl + "?q=" + city + "&appid=" + weatherApiToken + "&units=metric&lang=fr";
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null) {
                return "Meteo indisponible pour " + city;
            }
            return formatCurrentWeather(city, response);
        } catch (HttpClientErrorException e) {
            return e.getStatusCode().value() == 404
                    ? "Ville introuvable : " + city
                    : "Erreur meteo (" + e.getStatusCode().value() + ")";
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur meteo: {0}", e.getMessage());
            return "Meteo indisponible pour " + city;
        }
    }

    public String getForecastText(String city) {
        try {
            String url = forecastApiUrl + "?q=" + city + "&appid=" + weatherApiToken + "&units=metric&lang=fr&cnt=40";
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null) {
                return "Previsions indisponibles pour " + city;
            }
            return formatForecast(city, response);
        } catch (HttpClientErrorException e) {
            return e.getStatusCode().value() == 404
                    ? "Ville introuvable : " + city
                    : "Erreur previsions (" + e.getStatusCode().value() + ")";
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur previsions: {0}", e.getMessage());
            return "Previsions indisponibles pour " + city;
        }
    }

    public String getRandomJokeText() {
        return jokeService.getRandom().map(this::formatJoke).orElse("Pas de blague disponible !");
    }

    /** Blague Carambar via notre propre REST API. */
    public String getCarambarJokeText() {
        try {
            String url = "http://localhost:" + serverPort + "/jokes/carambar";
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-API-Token", apiAccessToken);
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers), Map.class).getBody();
            if (response == null || response.get("text") == null) {
                return "Pas de blague Carambar disponible !";
            }
            return String.valueOf(response.get("text"));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur API Carambar: {0}", e.getMessage());
            return "API Carambar injoignable pour le moment.";
        }
    }

    public String getBestJokeText() {
        return jokeService.getBest().map(this::formatJoke).orElse("Pas de blague disponible !");
    }

    public String getWorstJokeText() {
        return jokeService.getWorst().map(this::formatJoke).orElse("Pas de blague disponible !");
    }

    public String getJokeByIdText(Long id) {
        return jokeService.findById(id)
                .map(this::formatJoke)
                .orElse("Blague #" + id + " introuvable.");
    }

    private String formatJoke(Joke joke) {
        return "#" + joke.getId() + " - " + joke.getTitle() + " (" + joke.getRating() + "/10)\n"
                + joke.getText();
    }

    private String addJokeFromCommand(String content) {
        String[] parts = content.split("\\s*\\|\\s*", 3);
        if (parts.length >= 2 && !parts[0].isEmpty() && !parts[1].isEmpty()) {
            Integer rating = parts.length == 3 ? parseRating(parts[2]) : null;
            Joke joke = jokeService.add(parts[0].trim(), parts[1].trim(), rating);
            return "Blague #" + joke.getId() + " ajoutee (note " + joke.getRating() + "/10) !";
        }
        return "Format : ajouter blague Titre | Texte | Note(0-10)";
    }

    private String updateJokeFromCommand(String content) {
        int spaceIdx = content.indexOf(' ');
        if (spaceIdx < 0) {
            return "Format : modifier blague <id> Titre | Texte | Note(0-10)";
        }
        try {
            Long id = Long.parseLong(content.substring(0, spaceIdx).trim());
            String rest = content.substring(spaceIdx).trim();
            String[] parts = rest.split("\\s*\\|\\s*", 3);
            if (parts.length >= 2) {
                Integer rating = parts.length == 3 ? parseRating(parts[2]) : null;
                return jokeService.update(id, parts[0].trim(), parts[1].trim(), rating).isPresent()
                        ? "Blague #" + id + " modifiee !"
                        : "Blague #" + id + " introuvable.";
            }
        } catch (NumberFormatException e) {
            return "Format : modifier blague <id> Titre | Texte | Note(0-10)";
        }
        return "Format : modifier blague <id> Titre | Texte | Note(0-10)";
    }

    private Integer parseRating(String value) {
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String deleteJokeFromCommand(String idStr) {
        try {
            Long id = Long.parseLong(idStr);
            return jokeService.delete(id)
                    ? "Blague #" + id + " supprimee !"
                    : "Blague #" + id + " introuvable.";
        } catch (NumberFormatException e) {
            return "Format : supprimer blague <id>";
        }
    }

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
        return "Meteo a " + city + " : " + description + ", " + temp + " degC";
    }

    @SuppressWarnings("unchecked")
    private String formatForecast(String city, Map<String, Object> response) {
        List<Map<String, Object>> list = (List<Map<String, Object>>) response.get("list");
        if (list == null || list.isEmpty()) {
            return "Pas de previsions pour " + city;
        }

        Map<String, Map<String, Object>> byDay = new LinkedHashMap<>();
        for (Map<String, Object> entry : list) {
            String dtTxt = (String) entry.get("dt_txt");
            if (dtTxt == null || dtTxt.length() < 13) {
                continue;
            }
            String day = dtTxt.substring(0, 10);
            String hour = dtTxt.substring(11, 13);
            if (!byDay.containsKey(day) || "12".equals(hour)) {
                byDay.put(day, entry);
            }
        }

        DateTimeFormatter inFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter outFmt = DateTimeFormatter.ofPattern("EEE dd/MM", Locale.FRENCH);
        StringBuilder sb = new StringBuilder("Previsions pour " + city + " :\n");
        int count = 0;
        for (Map.Entry<String, Map<String, Object>> e : byDay.entrySet()) {
            if (count++ >= 5) {
                break;
            }
            Map<String, Object> entry = e.getValue();
            Map<String, Object> main = (Map<String, Object>) entry.get("main");
            List<Map<String, Object>> wList = (List<Map<String, Object>>) entry.get("weather");
            String desc = wList != null && !wList.isEmpty() ? String.valueOf(wList.get(0).get("description")) : "";
            Object tMin = main != null ? main.get("temp_min") : "?";
            Object tMax = main != null ? main.get("temp_max") : "?";
            LocalDate date = LocalDate.parse(e.getKey(), inFmt);
            sb.append(date.format(outFmt)).append(" : ").append(desc)
                    .append(", ").append(tMin).append(" degC - ").append(tMax).append(" degC\n");
        }
        return sb.toString().trim();
    }

    private String helpMessage() {
        return "Commandes disponibles :\n"
                + "- meteo <ville>\n"
                + "- previsions <ville>\n"
                + "- blague\n"
                + "- blague top (la mieux notee)\n"
                + "- blague nulle (la moins bien notee)\n"
                + "- blague <id>\n"
                + "- joke (blague de l'API locale Carambar)\n"
                + "- ajouter blague Titre | Texte | Note(0-10)\n"
                + "- modifier blague <id> Titre | Texte | Note(0-10)\n"
                + "- supprimer blague <id>";
    }
}
