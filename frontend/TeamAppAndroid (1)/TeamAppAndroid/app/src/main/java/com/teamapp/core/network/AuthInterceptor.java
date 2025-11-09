package com.teamapp.core.network;

import androidx.annotation.NonNull;
import android.util.Log;
import com.teamapp.core.prefs.SessionStore;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/** Gắn JWT (nếu có) vào mọi request. */
public class AuthInterceptor implements Interceptor {

    private final SessionStore session;

    public AuthInterceptor(SessionStore session) {
        this.session = session;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request req = chain.request();
        String token = session != null ? session.getToken() : null;

        if (token != null && !token.isEmpty()) {
            req = req.newBuilder()
                    .addHeader("Authorization", "Bearer " + token)
                    .build();
        } else {
            Log.d("AuthInterceptor", "No token -> " + req.method() + " " + req.url());
        }
        return chain.proceed(req);
    }
}
