package com.example.cm20314.interfaces;

import com.example.cm20314.services.HttpRequestService;

public interface IHttpRequestCallback<T> {
    void onCompleted(HttpRequestService.HttpRequestResponse<T> response);
    void onException();
}
