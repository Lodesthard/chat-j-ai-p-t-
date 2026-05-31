package fr.ensim.interop.introrest.controller;

import fr.ensim.interop.introrest.exception.ApiException;
import fr.ensim.interop.introrest.model.Joke;
import fr.ensim.interop.introrest.model.telegram.ApiResponseUpdateTelegram;
import fr.ensim.interop.introrest.model.telegram.Message;
import fr.ensim.interop.introrest.model.telegram.Update;
import fr.ensim.interop.introrest.oas.model.ApiJoke;
import fr.ensim.interop.introrest.oas.model.ChatEvent;
import fr.ensim.interop.introrest.oas.model.JokeRequest;
import fr.ensim.interop.introrest.oas.model.SendMessageRequest;
import fr.ensim.interop.introrest.oas.model.SentMessageResponse;
import fr.ensim.interop.introrest.oas.model.TextResponse;
import fr.ensim.interop.introrest.service.ChatbotService;
import fr.ensim.interop.introrest.service.JokeService;
import fr.ensim.interop.introrest.service.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Validated
@RestController
public class MessageRestController {

    @Autowired
    private ChatbotService chatbotService;

    @Autowired
    private JokeService jokeService;

    @Autowired
    private TelegramService telegramService;

    @Value("${telegram.api.url}")
    private String telegramApiUrl;

    @Value("${telegram.bot.id}")
    private String botToken;

    @Value("${carambar.api.url}")
    private String carambarApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/weather")
    public TextResponse weather(@RequestParam(defaultValue = "Paris") String city) {
        return text(chatbotService.getWeatherText(city));
    }

    @GetMapping("/forecast")
    public TextResponse forecast(@RequestParam(defaultValue = "Paris") String city) {
        return text(chatbotService.getForecastText(city));
    }

    @GetMapping("/joke")
    public TextResponse joke(@RequestParam(required = false) String quality) {
        if ("best".equalsIgnoreCase(quality)) {
            return text(chatbotService.getBestJokeText());
        }
        if ("worst".equalsIgnoreCase(quality)) {
            return text(chatbotService.getWorstJokeText());
        }
        return text(chatbotService.getRandomJokeText());
    }

    @GetMapping("/jokes/carambar")
    public TextResponse jokeCarambar() {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(carambarApiUrl + "/random", Map.class);
            if (response == null || response.get("content") == null) {
                return text("Pas de blague Carambar disponible !");
            }
            return text(String.valueOf(response.get("content")));
        } catch (Exception e) {
            return text("API Carambar injoignable pour le moment.");
        }
    }

    @GetMapping("/jokes")
    public List<ApiJoke> jokes(@RequestParam(required = false) String q) {
        Collection<Joke> jokes = q != null && !q.isEmpty() ? jokeService.findByTitle(q) : jokeService.getAll();
        return jokes.stream().map(this::toApiJoke).collect(Collectors.toList());
    }

    @GetMapping("/jokes/{id}")
    public ApiJoke jokeById(@PathVariable Long id) {
        return jokeService.findById(id)
                .map(this::toApiJoke)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Blague #" + id + " introuvable"));
    }

    @PostMapping("/jokes")
    public ResponseEntity<ApiJoke> addJoke(@Valid @RequestBody JokeRequest request) {
        Joke joke = jokeService.add(request.getTitle(), request.getText(), request.getRating());
        return ResponseEntity.status(HttpStatus.CREATED).body(toApiJoke(joke));
    }

    @PutMapping("/jokes/{id}")
    public ApiJoke updateJoke(@PathVariable Long id, @Valid @RequestBody JokeRequest request) {
        return jokeService.update(id, request.getTitle(), request.getText(), request.getRating())
                .map(this::toApiJoke)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Blague #" + id + " introuvable"));
    }

    @DeleteMapping("/jokes/{id}")
    public ResponseEntity<Void> deleteJoke(@PathVariable Long id) {
        if (!jokeService.delete(id)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Blague #" + id + " introuvable");
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/message")
    public SentMessageResponse message(@Valid @RequestBody SendMessageRequest request) {
        Message sent = telegramService.sendMessage(
                request.getChatId(),
                request.getText(),
                request.getReplyToMessageId());
        SentMessageResponse response = new SentMessageResponse().ok(true).text(request.getText());
        if (sent != null) {
            response.messageId(sent.getMessageId()).chatId(sent.getChatId());
        } else {
            response.chatId(request.getChatId());
        }
        return response;
    }

    @GetMapping("/chats")
    public List<ChatEvent> chats() {
        String url = telegramApiUrl + botToken + "/getUpdates?limit=20";
        ApiResponseUpdateTelegram response = restTemplate.getForObject(url, ApiResponseUpdateTelegram.class);
        if (response == null || response.getResult() == null) {
            return new ArrayList<>();
        }

        List<ChatEvent> result = new ArrayList<>();
        for (Update update : response.getResult()) {
            if (update.hasMessage() && update.getMessage().hasText()) {
                Message message = update.getMessage();
                result.add(new ChatEvent()
                        .updateId(update.getUpdateId())
                        .chatId(message.getChatId())
                        .messageId(message.getMessageId())
                        .text(message.getText()));
            }
        }
        return result;
    }

    private TextResponse text(String value) {
        return new TextResponse().text(value);
    }

    private ApiJoke toApiJoke(Joke joke) {
        return new ApiJoke()
                .id(joke.getId())
                .title(joke.getTitle())
                .text(joke.getText())
                .rating(joke.getRating());
    }
}
