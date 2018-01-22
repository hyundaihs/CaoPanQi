package com.dashuai.android.treasuremap.util;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.dashuai.android.treasuremap.R;

import org.json.JSONObject;

public class VolleyErrorHelper {
    public static final String CONNECT_FAILED = "网络链接失败，请检查网络链接！";
    public static final String CONNECT_WEB_FAILED = "服务器响应为空，请稍后再试！";
    public static final String CONNECT_WEB_TIME_OUT = "链接超时，请稍后再试！";
    public static final String CONNECT_UN_KNOW = "未知错误，请稍后再试！";

    public static String getMessage(Object error, Context context) {
        if (error instanceof TimeoutError) {
            return CONNECT_WEB_TIME_OUT;
        } else if (isServerProblem(error)) {
            return handleServerError(error, context);
        } else if (isNetworkProblem(error)) {
            return CONNECT_FAILED;
        } else
            return CONNECT_UN_KNOW;
    }

    private static boolean isNetworkProblem(Object error) {
        return (error instanceof NoConnectionError) || (error instanceof NetworkError);
    }

    private static boolean isServerProblem(Object error) {
        return (error instanceof ServerError)
                || (error instanceof AuthFailureError);
    }

    private static String handleServerError(Object err, Context context) {
        VolleyError error = (VolleyError) err;

        NetworkResponse response = error.networkResponse;

        if (response != null) {
            switch (response.statusCode) {
                case 404:
                case 422:
                case 401:
                    try {
                        // server might return error like this { "error":
                        // "Some error occured" }
                        // Use "Gson" to parse the result
                        if (null != response.data && !"".equals(response.data)) {
                            JSONObject object = new JSONObject(new String(
                                    response.data));
                            if (object != null && !object.isNull("error")) {
                                return object.getString("error");
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return error.getMessage();

                default:
                    return CONNECT_WEB_TIME_OUT + " " + response.statusCode;
            }
        }
        return CONNECT_UN_KNOW;
    }
}
