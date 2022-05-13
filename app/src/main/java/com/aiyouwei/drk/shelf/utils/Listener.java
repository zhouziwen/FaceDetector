package com.aiyouwei.drk.shelf.utils;

/**
 * Created By jishichen on 2019-07-27
 */
public interface Listener<T> {

    void onResponse(T response);

    void onErrorResponse(String e);
}
