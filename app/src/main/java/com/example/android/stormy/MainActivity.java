package com.example.android.stormy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String apiKey = "210439731034a0102c3f6e4a1f7973f9";
        double latitude = 37.8267;
        double longitude = -122.4233;
        String forecastURL = "https://api.darksky.net/forecast/" + apiKey + "/" + latitude + "," + longitude;

        final OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(forecastURL).build();
        Call call = client.newCall(request);

        call.enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {

                try {

                    Log.v(LOG_TAG, response.body().string());
                    if (response.isSuccessful()) {
                    }
                    else {
                        alertUserAboutError();
                    }
                } catch (IOException e) {
                    Log.e(LOG_TAG, "IOException caught: ", e);
                }
            }
        });


        Log.d(LOG_TAG, "Main UI code is running!");
    }

    private void alertUserAboutError() {
    }
}
