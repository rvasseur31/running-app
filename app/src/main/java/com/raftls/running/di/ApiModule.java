package com.raftls.running.di;

import com.raftls.running.app.Constants;
import com.raftls.running.network.interfaces.ApiInterface;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class ApiModule {

    private static final int REQUEST_TIMEOUT = 60;

    @Provides
    public String provideBaseUrl() {
        return Constants.BASE_URL;
    }

    @Provides
    public Converter.Factory provideConverterFactory() {
        return GsonConverterFactory.create();
    }

    @Provides
    public HttpLoggingInterceptor provideLoggingInterceptor() {
        return new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    @Provides
    public OkHttpClient.Builder providesOkHttpClientBuilder(HttpLoggingInterceptor level) {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient().newBuilder()
                .connectTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIMEOUT, TimeUnit.SECONDS);
        okHttpClientBuilder.addInterceptor(level);
        return okHttpClientBuilder;
    }


    @Provides
    public OkHttpClient provideOkHttp(OkHttpClient.Builder builder) {
        builder.addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json");

            Request request = requestBuilder.build();
            return chain.proceed(request);
        });

        return builder.build();
    }


    @Provides
    @Singleton
    public ApiInterface provideApi(@NotNull Retrofit retrofit) {
        return retrofit.create(ApiInterface.class);
    }

    @Provides
    public Retrofit provideRetrofit(OkHttpClient client, String baseUrl, Converter.Factory converterFactory) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(converterFactory)
                .build();
    }

}
