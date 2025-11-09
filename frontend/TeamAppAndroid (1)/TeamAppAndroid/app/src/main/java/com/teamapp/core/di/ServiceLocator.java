package com.teamapp.core.di;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.teamapp.App;
import com.teamapp.core.db.AppDb;
import com.teamapp.core.network.ApiClient;
import com.teamapp.core.network.AuthInterceptor;
import com.teamapp.core.network.adapters.CaseInsensitiveEnumTypeAdapterFactory;
import com.teamapp.core.network.adapters.UtcDateAdapter;
import com.teamapp.core.network.adapters.UuidAdapter;
import com.teamapp.core.prefs.SessionStore;
import com.teamapp.data.repo.*;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ServiceLocator {

    private static volatile Retrofit retrofit;
    private static volatile OkHttpClient okClient;
    private static volatile AppDb db;
    private static volatile SessionStore session;
    private static volatile Gson gson;

    private ServiceLocator() {}

    private static Gson gson() {
        if (gson == null) {
            synchronized (ServiceLocator.class) {
                if (gson == null) {
                    gson = new GsonBuilder()
                            .registerTypeAdapter(Date.class, new UtcDateAdapter())
                            .registerTypeAdapterFactory(new CaseInsensitiveEnumTypeAdapterFactory())
                            .registerTypeAdapter(UUID.class, new UuidAdapter())
                            .serializeNulls()
                            .create();
                }
            }
        }
        return gson;
    }

    public static SessionStore session() {
        if (session == null) {
            synchronized (ServiceLocator.class) {
                if (session == null) {
                    session = new SessionStore(App.get());
                }
            }
        }
        return session;
    }

    private static OkHttpClient http() {
        if (okClient == null) {
            synchronized (ServiceLocator.class) {
                if (okClient == null) {
                    SessionStore ss = session();

                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                    // logging.redactHeader("Authorization"); // DEBUG: tạm thời không redact để xem token

                    okClient = new OkHttpClient.Builder()
                            .addInterceptor(new AuthInterceptor(ss))
                            .addNetworkInterceptor(chain -> {
                                Request r = chain.request();
                                Log.d("AuthTrace", "Authorization = " + r.header("Authorization")
                                        + " | " + r.method() + " " + r.url());
                                return chain.proceed(r);
                            })
                            .addInterceptor(logging)
                            .connectTimeout(20, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(30, TimeUnit.SECONDS)
                            .retryOnConnectionFailure(true)
                            .build();
                }
            }
        }
        return okClient;
    }

    public static Retrofit retrofit() {
        if (retrofit == null) {
            synchronized (ServiceLocator.class) {
                if (retrofit == null) {
                    retrofit = new Retrofit.Builder()
                            .baseUrl(ApiClient.getBaseUrl()) // nhớ kết thúc bằng '/'
                            .client(http())
                            .addConverterFactory(GsonConverterFactory.create(gson()))
                            .build();
                }
            }
        }
        return retrofit;
    }

    public static AppDb db() {
        if (db == null) {
            synchronized (ServiceLocator.class) {
                if (db == null) {
                    Context ctx = App.get();
                    db = AppDb.create(ctx);
                }
            }
        }
        return db;
    }

    // ==== Repositories ====
    public static ProjectRepository projects() { return new ProjectRepository(retrofit(), db()); }
    public static NotificationRepository notifications() { return new NotificationRepository(retrofit(), db()); }
    public static ProfileRepository profiles() { return new ProfileRepository(retrofit(), session()); }
    public static AuthRepository auth() { return new AuthRepository(retrofit(), session()); }
    public static TaskRepository tasks() { return new TaskRepository(retrofit(), db()); }
    public static CalendarRepository calendar() { return new CalendarRepository(retrofit(), db()); }
    public static CommentRepository comments() { return new CommentRepository(retrofit(), db()); }
    public static ConversationRepository conversations() { return new ConversationRepository(retrofit(), db()); }
    public static DeviceRepository devices() { return new DeviceRepository(retrofit()); }
    public static JoinRequestRepository joinRequests() { return new JoinRequestRepository(retrofit()); }
    public static ProfileRepository profile() { return new ProfileRepository(retrofit(), session()); }
    public static SearchRepository search() { return new SearchRepository(retrofit()); }
}
