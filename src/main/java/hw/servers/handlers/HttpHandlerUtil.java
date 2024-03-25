package main.java.hw.servers.handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HttpHandlerUtil {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public static void writeResponse(HttpExchange exchange,
                                     String responseString,
                                     int responseCode) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(DEFAULT_CHARSET));
        }
        exchange.close();
    }

    public static int extractTaskIdFromQuery(String query) {
        String[] queryParams = query.split("&");
        for (String param : queryParams) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2 && keyValue[0].equals("id")) {
                try {
                    return Integer.parseInt(keyValue[1]);
                } catch (NumberFormatException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return -1;
    }
}
