package com.raftls.running.app.models;

public interface ApiResponse<T> {
    void success(T response);

    void failure(ResponseError response);
}
