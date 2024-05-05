package io.goji.tg.notifier;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.Map;

public class Agent {
    private final String botToken;
    private final int id;
    private final String token;
    private final HttpClient session;
    private final String callbackUrl;

    public Agent(String token, String callbackUrl) {
        this.botToken = token;
        var parts = token.split(":");
        this.id = Integer.parseInt(parts[0]);
        this.token = parts[1];
        this.session = HttpClient.newHttpClient();
        this.callbackUrl = callbackUrl;
    }

    public HttpResponse<String> setCommands(List<Map<String, Object>> commands) throws Exception {
        var request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.telegram.org/bot" + botToken + "/deleteMyCommands"))
                .GET()
                .build();
        session.send(request, BodyHandlers.ofString());

        if (commands != null) {
            var requestBody = HttpRequest.BodyPublishers.ofString("{\"commands\":" + commands + "}");
            var requestWithCommands = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.telegram.org/bot" + botToken + "/setMyCommands"))
                    .header("Content-Type", "application/json")
                    .POST(requestBody)
                    .build();
            return session.send(requestWithCommands, BodyHandlers.ofString());
        }
        return null;
    }

    public HttpResponse<String> setWebhook(List<String> allowUpdates) throws Exception {
        var request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.telegram.org/bot" + botToken + "/deleteWebhook"))
                .GET()
                .build();
        session.send(request, BodyHandlers.ofString());

        var requestBody = HttpRequest.BodyPublishers.ofString("{\"url\":\"" + callbackUrl + "\", \"allowed_updates\":" + allowUpdates + ", \"secret_token\":\"" + token + "\"}");
        var requestWithWebhook = HttpRequest.newBuilder()
                .uri(URI.create("https://api.telegram.org/bot" + botToken + "/setWebhook"))
                .header("Content-Type", "application/json")
                .POST(requestBody)
                .build();
        return session.send(requestWithWebhook, BodyHandlers.ofString());
    }


    public HttpResponse<String> answerCallbackQuery(String callbackQueryId, String text, boolean showAlert) throws Exception {
        var requestBody = HttpRequest.BodyPublishers.ofString("{\"callback_query_id\":\"" + callbackQueryId + "\", \"show_alert\":" + showAlert + (text != null ? ", \"text\":\"" + text + "\"" : "") + "}");
        var request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.telegram.org/bot" + botToken + "/answerCallbackQuery"))
                .header("Content-Type", "application/json")
                .POST(requestBody)
                .build();
        return session.send(request, BodyHandlers.ofString());
    }

    public HttpResponse<String> sendMessage(String chatId, String message, String parseMode, Object replyMarkup, Map<String, Object> additionalData) throws Exception {
        var data = "{\"chat_id\":\"" + chatId + "\", \"text\":\"" + message + "\"" + (parseMode != null ? ", \"parse_mode\":\"" + parseMode + "\"" : "") + (replyMarkup != null ? ", \"reply_markup\":" + replyMarkup : "") + "}";
        if (additionalData != null) {
            data = additionalData.entrySet().stream()
                    .reduce(data, (acc, entry) -> acc + ", \"" + entry.getKey() + "\":\"" + entry.getValue() + "\"", (acc1, acc2) -> acc1);
        }
        var requestBody = HttpRequest.BodyPublishers.ofString(data);
        var request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.telegram.org/bot" + botToken + "/sendMessage"))
                .header("Content-Type", "application/json")
                .POST(requestBody)
                .build();
        return session.send(request, BodyHandlers.ofString());
    }

    public HttpResponse<String> editMessageReplyMarkup(String chatId, int messageId, Object replyMarkup) throws Exception {
        var requestBody = HttpRequest.BodyPublishers.ofString("{\"chat_id\":\"" + chatId + "\", \"message_id\":" + messageId + (replyMarkup != null ? ", \"reply_markup\":" + replyMarkup : "") + "}");
        var request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.telegram.org/bot" + botToken + "/editMessageReplyMarkup"))
                .header("Content-Type", "application/json")
                .POST(requestBody)
                .build();
        return session.send(request, BodyHandlers.ofString());
    }
}
