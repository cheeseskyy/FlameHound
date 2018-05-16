package com.example.hvale.loginapp;

import com.android.volley.AuthFailureError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Network;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Response.ErrorListener;
import com.android.volley.toolbox.HttpHeaderParser;
import java.util.Map;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;


public class RegisterRequest extends JsonObjectRequest{

    public RegisterRequest(int Method, String url, JSONObject jsonObject, Response.Listener<JSONObject>
                           listener, Response.ErrorListener errorListener){
        super(Method, url, jsonObject, listener,errorListener);
    }

    public RegisterRequest(String url, JSONObject jsonObject, Response.Listener<JSONObject>
            listener, Response.ErrorListener errorListener){
        super(url, jsonObject, listener,errorListener);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            JSONObject jsonResponse = new JSONObject(json);
            int mStatusCode = response.statusCode;
            jsonResponse.put("statusCode", mStatusCode);
            return Response.success(jsonResponse,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }
}

