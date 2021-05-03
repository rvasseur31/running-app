package com.raftls.running.history.services;

import android.content.Context;

import com.google.gson.Gson;
import com.raftls.running.app.models.ApiResponse;
import com.raftls.running.app.models.ResponseError;
import com.raftls.running.history.adapters.HistoryItem;
import com.raftls.running.network.ApiClient;
import com.raftls.running.tracking.models.geojson.Run;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import eu.davidea.flexibleadapter.FlexibleAdapter;
import eu.davidea.flexibleadapter.items.IFlexible;
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

    public void getAllRuns(Context context, ApiResponse<FlexibleAdapter<HistoryItem>> callback) {
        ApiClient.getApi().getAllRun().enqueue(new Callback<ArrayList<Run>>() {
            @Override
            public void onResponse(@NotNull Call<ArrayList<Run>> call, @NotNull Response<ArrayList<Run>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<HistoryItem> list = new ArrayList<>();
                    for (Run run: response.body()) {
                        list.add(new HistoryItem(context, run));
                    }
                    FlexibleAdapter<HistoryItem> adapter = new FlexibleAdapter<>(list);
                    callback.success(adapter);
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
}