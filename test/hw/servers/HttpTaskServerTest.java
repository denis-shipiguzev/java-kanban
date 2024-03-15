package hw.servers;

import main.java.hw.servers.HttpTaskServer;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;

public class HttpTaskServerTest {
    @BeforeEach
    public void createHttpServerAndClient() throws IOException {
        HttpTaskServer server = new HttpTaskServer();
        server.start();
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
    }

}
