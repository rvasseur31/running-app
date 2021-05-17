package com.raftls.running.history.services;

import android.content.Context;

import com.google.gson.Gson;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.raftls.running.app.models.ApiResponse;
import com.raftls.running.app.models.ResponseError;
import com.raftls.running.history.adapters.HistoryItem;
import com.raftls.running.network.ApiClient;
import com.raftls.running.tracking.models.geojson.Run;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryService {
    private static HistoryService instance;

    public static HistoryService getInstance() {
        if (instance == null) {
            instance = new HistoryService();
        }
        return instance;
    }

    private HistoryService() {
    }

    public void getAllRuns(Context context, ApiResponse<ItemAdapter<HistoryItem>> callback) {
        ApiClient.getApi().getAllRun().enqueue(new Callback<ArrayList<Run>>() {
            @Override
            public void onResponse(@NotNull Call<ArrayList<Run>> call, @NotNull Response<ArrayList<Run>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isEmpty()) {
                        callback.failure(null);
                    }
                    ItemAdapter<HistoryItem> list = new ItemAdapter<>();
                    for (Run run : response.body()) {
                        if (run != null) {
                            HistoryItem item = new HistoryItem(context, run);
                            list.add(item);
                        }

                    }
                    callback.success(list);
                } else {
                    if (response.errorBody() != null) {
                        ResponseError responseError = new Gson().fromJson(response.errorBody().charStream(), ResponseError.class);
                        callback.failure(responseError);
                    } else {
                        callback.failure(null);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<ArrayList<Run>> call, @NotNull Throwable t) {

            }
        });
    }

    public void removeRun(String id, ApiResponse<Void> callback) {
        ApiClient.getApi().deleteRun(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NotNull Call<Void> call, @NotNull Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.success(null);
                } else {
                    if (response.errorBody() != null) {
                        ResponseError responseError = new Gson().fromJson(response.errorBody().charStream(), ResponseError.class);
                        callback.failure(responseError);
                    } else {
                        callback.failure(null);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<Void> call, @NotNull Throwable t) {

            }
        });
    }

    public void removeMultipleRuns(ArrayList<String> ids, ApiResponse<Void> callback) {
        ApiClient.getApi().deleteMultipleRuns(ids).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NotNull Call<Void> call, @NotNull Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.success(null);
                } else {
                    if (response.errorBody() != null) {
                        ResponseError responseError = new Gson().fromJson(response.errorBody().charStream(), ResponseError.class);
                        callback.failure(responseError);
                    } else {
                        callback.failure(null);
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<Void> call, @NotNull Throwable t) {

            }
        });
    }
}