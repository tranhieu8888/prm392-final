package com.teamapp.core.network.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * (De)serialize Date theo ISO-8601 UTC.
 * Ví dụ: 2025-11-06T12:34:56Z
 * Đồng thời cố gắng parse vài biến thể phổ biến.
 */
public class UtcDateAdapter extends TypeAdapter<Date> {

    private static final String[] PARSE_PATTERNS = new String[] {
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSSX",
            "yyyy-MM-dd'T'HH:mm:ssX",
            "yyyy-MM-dd HH:mm:ss" // fallback (không timezone)
    };

    private static final ThreadLocal<SimpleDateFormat> ISO_FORMAT = ThreadLocal.withInitial(() -> {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        f.setTimeZone(TimeZone.getTimeZone("UTC"));
        return f;
    });

    @Override
    public void write(JsonWriter out, Date value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.value(ISO_FORMAT.get().format(value));
    }

    @Override
    public Date read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        String s = in.nextString();
        if (s == null || s.isEmpty()) return null;

        for (String p : PARSE_PATTERNS) {
            try {
                SimpleDateFormat f = new SimpleDateFormat(p, Locale.US);
                if (p.endsWith("'Z'") || p.endsWith("X")) {
                    f.setTimeZone(TimeZone.getTimeZone("UTC"));
                }
                return f.parse(s);
            } catch (ParseException ignored) {}
        }
        // last resort: trả về current nếu parse fail để tránh crash (hoặc throw)
        try {
            return new Date(Long.parseLong(s));
        } catch (Exception e) {
            return null;
        }
    }
}
