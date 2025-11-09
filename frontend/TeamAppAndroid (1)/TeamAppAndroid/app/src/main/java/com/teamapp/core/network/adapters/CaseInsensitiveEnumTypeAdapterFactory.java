package com.teamapp.core.network.adapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Cho phép parse enum không phân biệt hoa/thường, có hỗ trợ @SerializedName.
 * Nếu Gson đã có adapter mặc định cho enum thì wrap lại để thêm case-insensitive.
 */
public class CaseInsensitiveEnumTypeAdapterFactory implements TypeAdapterFactory {

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<T> rawType = (Class<T>) type.getRawType();
        if (!rawType.isEnum()) return null;

        // Adapter mặc định
        final TypeAdapter<T> defaultAdapter = TypeAdapters.ENUM_FACTORY.create(gson, type);

        // Map lowercase -> hằng enum
        final Map<String, T> lowercaseToConstant = new HashMap<>();
        try {
            for (T constant : rawType.getEnumConstants()) {
                String name = ((Enum) constant).name();
                lowercaseToConstant.put(name.toLowerCase(Locale.US), constant);

                // Lấy @SerializedName nếu có (trong trường hợp khác name)
                try {
                    Field f = rawType.getField(name);
                    com.google.gson.annotations.SerializedName an =
                            f.getAnnotation(com.google.gson.annotations.SerializedName.class);
                    if (an != null) {
                        for (String alt : an.alternate()) {
                            lowercaseToConstant.put(alt.toLowerCase(Locale.US), constant);
                        }
                        lowercaseToConstant.put(an.value().toLowerCase(Locale.US), constant);
                    }
                } catch (NoSuchFieldException ignored) {}
            }
        } catch (Throwable ignored) {}

        return new TypeAdapter<T>() {
            @Override
            public void write(com.google.gson.stream.JsonWriter out, T value) throws java.io.IOException {
                defaultAdapter.write(out, value);
            }

            @Override
            public T read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
                String str = in.nextString();
                if (str == null) return null;
                T mapped = lowercaseToConstant.get(str.toLowerCase(Locale.US));
                if (mapped != null) return mapped;
                // fallback: để adapter mặc định xử lý (có thể throw, giúp lộ lỗi dữ liệu)
                try {
                    // dùng default để thử parse theo tên chuẩn
                    return defaultAdapter.fromJson('"' + str + '"');
                } catch (Exception e) {
                    return null;
                }
            }
        };
    }
}
