package fr.ensim.interop.introrest;

import fr.ensim.interop.introrest.exception.TelegramApiException;
import fr.ensim.interop.introrest.model.telegram.ApiResponseUpdateTelegram;
import fr.ensim.interop.introrest.model.telegram.Message;
import fr.ensim.interop.introrest.model.telegram.Update;
import fr.ensim.interop.introrest.service.ChatbotService;
import fr.ensim.interop.introrest.service.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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

    @Autowired
    private ChatbotService chatbotService;

    @Autowired
    private TelegramService telegramService;

    private long offset = 0;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void run(String... args) {
        LOGGER.log(Level.INFO, "Demarrage du listener d'updates Telegram...");
        Timer timer = new Timer("telegram-polling", true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                pollUpdates();
            }
        }, 0, 5000);
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
        if (!update.hasMessage()) {
            return;
        }

        Message message = update.getMessage();
        if (!message.hasText()) {
            return;
        }

        Long chatId = message.getChatId();
        String responseText = chatbotService.executeCommand(message.getText());
        try {
            telegramService.sendMessage(chatId, responseText, message.getMessageId());
        } catch (TelegramApiException e) {
            LOGGER.log(Level.WARNING, "Envoi Telegram impossible pour chat_id {0}: {1}",
                    new Object[]{chatId, e.getMessage()});
        }
    }
}
