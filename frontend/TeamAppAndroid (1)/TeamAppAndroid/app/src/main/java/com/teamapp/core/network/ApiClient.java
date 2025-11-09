package com.teamapp.core.network;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.teamapp.core.network.adapters.CaseInsensitiveEnumTypeAdapterFactory;
import com.teamapp.core.network.adapters.UtcDateAdapter;
import com.teamapp.core.network.adapters.UuidAdapter;
import com.teamapp.core.prefs.SessionStore;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit builder (singleton).
 * - Logging chỉ dùng cho debug.
 * - Có thể đổi baseUrl lúc runtime bằng setBaseUrl().
 */
public final class ApiClient {

    private static volatile Retrofit retrofit;
    private static volatile String baseUrl = "http://10.0.2.2:7103/"; // TODO: thay bằng domain của bạn

    private ApiClient() {}

    /** Dùng khi cần đổi baseUrl động (ví dụ chọn environment). */
    public static void setBaseUrl(String url) {
        if (TextUtils.isEmpty(url)) return;
        if (!url.endsWith("/")) url = url + "/";
        baseUrl = url;
        // reset retrofit để build lại với baseUrl mới
        retrofit = null;
    }

    public static String getBaseUrl() {
        return baseUrl;
    }

    public static Retrofit get(SessionStore session) {
        if (retrofit != null) return retrofit;

        synchronized (ApiClient.class) {
            if (retrofit != null) return retrofit;

            // ----- Gson với adapters chuẩn hóa -----
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, new UtcDateAdapter()) // ISO-8601 UTC
                    .registerTypeAdapterFactory(new CaseInsensitiveEnumTypeAdapterFactory())
                    .registerTypeAdapter(UUID.class, new UuidAdapter())
                    .serializeNulls()
                    .create();

            // ----- OkHttp -----
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient ok = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(session))
                    .retryOnConnectionFailure(true)
                    .addInterceptor(logging)

                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(ok)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
