package com.ipredictfantasy.asynctasks;


import android.os.AsyncTask;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class EmailLoginTask extends AsyncTask<String, Void, String> {
    private OkHttpClient client = new OkHttpClient();
    private String rm_url;
    private String data;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public EmailLoginTask(String url, String data) {
        this.rm_url = url;
        this.data = data;
    }

    @Override
    protected String doInBackground(String... params) {
        RequestBody body = RequestBody.create(JSON, data);
        Request request = new Request.Builder()
                .url(rm_url)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful())
                return String.valueOf(response.code());
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
