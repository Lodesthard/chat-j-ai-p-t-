package fr.ensim.interop.introrest.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.ensim.interop.introrest.exception.TelegramApiException;
import fr.ensim.interop.introrest.model.telegram.ApiResponseTelegram;
import fr.ensim.interop.introrest.model.telegram.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class TelegramService {

    @Value("${telegram.api.url}")
    private String telegramApiUrl;

    @Value("${telegram.bot.id}")
    private String botToken;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    public TelegramService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Message sendMessage(Long chatId, String text, Integer replyToMessageId) {
        String url = telegramApiUrl + botToken + "/sendMessage";
        Map<String, Object> body = new HashMap<>();
        body.put("chat_id", chatId);
        body.put("text", text);
        if (replyToMessageId != null) {
            body.put("reply_to_message_id", replyToMessageId);
        }

        try {
            String json = restTemplate.postForObject(url, body, String.class);
            ApiResponseTelegram<Message> response = objectMapper.readValue(
                    json, new TypeReference<ApiResponseTelegram<Message>>() {});
            if (response == null || !Boolean.TRUE.equals(response.getOk())) {
                String description = response != null ? response.getErrorDescription() : "Reponse Telegram vide";
                throw new TelegramApiException(HttpStatus.BAD_GATEWAY, description);
            }
            return response.getResult();
        } catch (HttpClientErrorException e) {
            HttpStatus status = e.getStatusCode() == HttpStatus.BAD_REQUEST
                    ? HttpStatus.BAD_REQUEST
                    : HttpStatus.BAD_GATEWAY;
            throw new TelegramApiException(status, "Erreur Telegram : " + e.getResponseBodyAsString());
        } catch (IOException e) {
            throw new TelegramApiException(HttpStatus.BAD_GATEWAY, "Reponse Telegram illisible : " + e.getMessage());
        }
    }
}
