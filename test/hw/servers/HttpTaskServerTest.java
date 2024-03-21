package hw.servers;

import com.google.gson.reflect.TypeToken;
import main.java.hw.managers.Managers;
import main.java.hw.managers.taskmanagers.TaskManager;
import main.java.hw.model.Task;
import main.java.hw.servers.HttpTaskServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import com.google.gson.Gson;
public class HttpTaskServerTest {
    protected static TaskManager taskManager = Managers.getDefault();
    Gson gson;
    @BeforeEach
    public void createHttpServerAndClient() throws IOException {
        HttpTaskServer server = new HttpTaskServer();
        server.start();
//        HttpClient client = HttpClient.newHttpClient();
//        URI url = URI.create("http://localhost:8080/tasks");

    }

//    @Test

}
