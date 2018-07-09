package com.example.hvale.loginapp;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

class LogOutSingleton {

    private static final String URL_SERVER = "https://my-first-project-196314.appspot.com/rest";

    private static final String TOKEN_ID = "tokenId";
    private static final String USERNAME = "username";

    private static LogOutSingleton mInstance;
    private static Context mCtx;
    private static JSONObject sessionInfo;
    private String loginToken;
    private String username;
    private boolean isLogIn;


    private LogOutSingleton(Context context) {
        mCtx = context;
        loginToken = "0";
        username = "0";
        sessionInfo = null;
        isLogIn = false;
    }

    public static synchronized LogOutSingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new LogOutSingleton(context);
        }
        return mInstance;
    }

    public void setLoginToken(JSONObject sessionInfo) throws JSONException{
        this.sessionInfo = sessionInfo;
        this.loginToken = sessionInfo.get(TOKEN_ID).toString();
        this.username = sessionInfo.get(USERNAME).toString();
    }

    public String getUsername(){
        return username;
    }

    public String getLoginToken(){
        return loginToken;
    }
    
    public void logOut(){
        loginToken = "0";
        username = "0";
        sessionInfo = null;
    }

    public JSONObject getSessionId(){
        return sessionInfo;
    }

    public boolean isLoggedIn(){

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, URL_SERVER + "/utils/validLogin", sessionInfo, new Response.Listener<JSONObject>() {
            public void onResponse(JSONObject response) {
                isLogIn = true;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                isLogIn = false;
            }
        });
        return isLogIn;
    }
}
