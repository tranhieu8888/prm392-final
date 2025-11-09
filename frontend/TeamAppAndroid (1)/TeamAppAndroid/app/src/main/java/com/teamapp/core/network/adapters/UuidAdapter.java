package com.teamapp.core.network.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.UUID;

/** Adapter cho UUID (chuỗi chuẩn). */
public class UuidAdapter extends TypeAdapter<UUID> {
    @Override
    public void write(JsonWriter out, UUID value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.value(value.toString());
    }

    @Override
    public UUID read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        String s = in.nextString();
        if (s == null || s.isEmpty()) return null;
        try {
            return UUID.fromString(s);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
