package com.dashuai.android.treasuremap.util;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.dashuai.android.treasuremap.Constant;
import com.dashuai.android.treasuremap.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class RequestUtil implements ImageCache {

    public static RequestQueue mQueue;
    private Reply reply;
    private ImageLoader imageLoader;
    public static String PHPSESSID = null;
    private Context context;

    private LruCache<String, Bitmap> mCache;

    public RequestUtil(Context context) {
        initQueue(context);
        imageLoader = new ImageLoader(mQueue, this);
        int maxSize = 10 * 1024 * 1024;
        mCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };
    }

    public RequestUtil(Context context, Reply reply) {
        initQueue(context);
        setReply(reply);
        imageLoader = new ImageLoader(mQueue, this);
        int maxSize = 10 * 1024 * 1024;
        mCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };
    }

    public void initQueue(Context context) {
        // if (mQueue == null) {
        mQueue = Volley.newRequestQueue(context.getApplicationContext());
        this.context = context;
        // File cacheDir = new File(context.getCacheDir(), "volley");
        // DiskBasedCache cache = new DiskBasedCache(cacheDir);
        // mQueue.start();
        //
        // // clear all volley caches.
        // mQueue.add(new ClearCacheRequest(cache, null));
        // }
    }

    private RequestQueue getmQueue() {
        return mQueue;
    }

    public Reply getReply() {
        return reply;
    }

    public void setReply(Reply reply) {
        this.reply = reply;
    }

    public void postRequest(String url, final int what) {
        postRequest(url, null, what, true);
    }

    public void postRequest(String url, final int what, boolean isCache) {
        postRequest(url, null, what, isCache);
    }

    public void postRequest(String url, final Map<String, Object> map,
                            final int what) {
        postRequest(url, map, what, true);
    }

    public void postRequest(String url, final Map<String, Object> map,
                            final int what, boolean isCache) {
        JSONObject jsonObject = null;
        if (null == map) {
            jsonObject = new JSONObject();
        } else {
            jsonObject = new JSONObject(map);
        }
        JsonRequest<JSONObject> jsonRequest = new JsonObjectRequest(
                Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        onSuccess(response, what);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onError(error, what);
            }
        }) {
            // @Override
            // protected Response<JSONObject> parseNetworkResponse(
            // NetworkResponse response) {
            // return getCookie(response);
            // }

            @Override
            public Map<String, String> getHeaders() {
                return setHeaders();
            }
        };
        jsonRequest.setShouldCache(isCache);
        getmQueue().add(jsonRequest);
    }

    public void onSuccess(JSONObject response, int what) {
        if (null == response || "".equals(response) || "null".equals(response)) {
            if (null != reply) {
                reply.onErrorResponse(VolleyErrorHelper.CONNECT_WEB_FAILED, what);
            }
            return;
        } else {
            if (JsonUtil.isSuccess(response)) {
                if (null != reply) {
                    reply.onSuccess(response, what);
                }
            } else {
                if (null != reply) {
                    reply.onFailed(JsonUtil.getErrorInfo(response), what);
                }
            }
        }
    }

    public void onError(VolleyError error, int what) {
        if (null != reply) {
            reply.onErrorResponse(VolleyErrorHelper.getMessage(error, context), what);
        }
    }

    //
    // public Response<JSONObject> getCookie(NetworkResponse response) {
    // try {
    // Map<String, String> responseHeaders = response.headers;
    // String string = responseHeaders.get("Set-Cookie");
    // if (string != null) {
    // if (string.contains("PHPSESSID")) {
    // PHPSESSID = string.split(";")[0];
    // }
    // }
    // String jsonString = new String(response.data,
    // HttpHeaderParser.parseCharset(response.headers));
    // JSONObject jsonObject = new JSONObject(jsonString);
    // return Response.success(jsonObject,
    // HttpHeaderParser.parseCacheHeaders(response));
    // } catch (UnsupportedEncodingException e) {
    // return Response.error(new ParseError(e));
    // } catch (JSONException je) {
    // return Response.error(new ParseError(je));
    // }
    // }

    public Map<String, String> setHeaders() {
        HashMap<String, String> headers = new HashMap<String, String>();
        if (null != PHPSESSID && !"".equals(PHPSESSID)) {
            headers.put("Cookie", PHPSESSID);
        }
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json; charset=UTF-8");
        return headers;
    }

    public void getImage(String url, Response.Listener<Bitmap> listener,
                         Response.ErrorListener errorListener) {
        ImageRequest imageRequest = new ImageRequest(url, listener, 0, 0,
                Config.RGB_565, errorListener);
        getmQueue().add(imageRequest);
    }

    public void getImage(ImageView imageView, String url) {
        ImageListener listener = ImageLoader.getImageListener(imageView,
                R.drawable.loading, R.drawable.fail);
        imageLoader.get(url, listener);
    }

    public void getHeadImage(ImageView imageView, String url) {
        getImage(imageView, url, 300, 300);
    }

    public void getBigHeadImage(ImageView imageView, String url) {
        getImage(imageView, url, 800, 800);
    }

    public void getMiniHeadImage(ImageView imageView, String url) {
        getImage(imageView, url, 100, 100);
    }

    public void getItemImage(ImageView imageView, String url) {
        getImage(imageView, url, 400, 400);
    }

    public void getBigItemImage(ImageView imageView, String url) {
        getImage(imageView, url, 800, 800);
    }

    public void getImage(ImageView imageView, String url, int width, int height) {
        ImageListener listener = ImageLoader.getImageListener(imageView,
                R.drawable.loading, R.drawable.fail);
        imageLoader.get(url, listener, width, height);
    }

    public void getImage(String url, ImageListener listener) {
        imageLoader.get(url, listener);
    }

    @Override
    public Bitmap getBitmap(String url) {
        return mCache.get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        mCache.put(url, bitmap);
    }

    /**
     * 将输入流转成字符串
     *
     * @param is
     * @return
     */
    public static String convertStreamToString(InputStream is) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "UTF-8"), 8 * 1024);

            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "n");
            }
        } catch (IOException e) {
            sb.delete(0, sb.length());
        } finally {
            try {
                is.close();
            } catch (IOException e) {

            }
        }

        return sb.toString();
    }

    public interface Reply {

        public abstract void onSuccess(JSONObject response, int what);

        public abstract void onErrorResponse(String error, int what);

        public abstract void onFailed(String error, int what);

    }

}
