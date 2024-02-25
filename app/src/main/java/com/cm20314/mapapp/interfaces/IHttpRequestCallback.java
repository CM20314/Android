package com.cm20314.mapapp.interfaces;

import com.cm20314.mapapp.services.HttpRequestService;

public interface IHttpRequestCallback<T> {
    void onCompleted(HttpRequestService.HttpRequestResponse<T> response);
    void onException();
}
