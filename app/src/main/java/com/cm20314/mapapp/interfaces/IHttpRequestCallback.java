package com.cm20314.mapapp.interfaces;

import com.cm20314.mapapp.services.HttpRequestService;

/**
 * HTTP request callback to receive response or exception
 * @param <T> Response object type
 */
public interface IHttpRequestCallback<T> {
    void onCompleted(HttpRequestService.HttpRequestResponse<T> response);
    void onException();
}
