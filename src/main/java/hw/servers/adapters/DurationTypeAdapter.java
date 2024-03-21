package main.java.hw.servers.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationTypeAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(JsonWriter out, Duration value) throws IOException {
        out.value(value.toMinutes());
    }

    @Override
    public Duration read(JsonReader in) throws IOException {
        long minutes = in.nextLong();
        return Duration.ofMinutes(minutes);
    }
}
