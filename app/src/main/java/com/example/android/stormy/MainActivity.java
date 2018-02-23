package com.example.android.stormy;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

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
    private CurrentWeather currentWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String apiKey = "210439731034a0102c3f6e4a1f7973f9";
        double latitude = 37.8267;
        double longitude = -122.4233;
        String forecastURL = "https://api.darksky.net/forecast/" + apiKey + "/" + latitude + "," + longitude;

        if (isNetworkAvailable()) {

            final OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(forecastURL).build();
            Call call = client.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    try {

                        String jsonData = response.body().string();
                        Log.v(LOG_TAG, jsonData);
                        if (response.isSuccessful()) {

                            currentWeather = getCurrentDetails(jsonData);
                        } else {
                            alertUserAboutError(getString(R.string.error_title), getString(R.string.error_message));
                        }
                    }
                    catch (IOException e) {
                        Log.e(LOG_TAG, "IOException caught: ", e);
                    }
                    catch (JSONException e) {
                        Log.e(LOG_TAG, "IOException caught: ", e);
                    }
                }
            });
        } else {
            alertUserAboutError(getString(R.string.no_network_connection), getString(R.string.no_network_connection_text));
            Toast.makeText(this, R.string.network_unavailable, Toast.LENGTH_LONG).show();
        }

        Log.d(LOG_TAG, "Main UI code is running!");
    }

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException {

        JSONObject forcast = new JSONObject(jsonData);
        String timeZone = forcast.getString("timezone");
        Log.i(LOG_TAG, "From getCurrentDetails(): " + timeZone);
        JSONObject currently = forcast.getJSONObject("currently");

        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setTime(currently.getLong("time"));
        currentWeather.setIcon(currently.getString("icon"));
        currentWeather.setSummary(currently.getString("summary"));
        currentWeather.setPrecipChance(currently.getDouble("precipProbability"));
        currentWeather.setTemperature(currently.getDouble("temperature"));
        currentWeather.setHumidity(currently.getDouble("humidity"));
        currentWeather.setTimeZone(timeZone);

        Log.d(LOG_TAG, "Time: " + currentWeather.getFormattedTime());

        return currentWeather;
    }

    private boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected()) ? true : false;
    }

    private void alertUserAboutError(String alertTitle, String alertBody) {

        Bundle messageArgs = new Bundle();

        // Use the arguments to set up the bundle
        messageArgs.putString(AlertDialogFragment.TITLE_ID, alertTitle);
        messageArgs.putString(AlertDialogFragment.MESSAGE_ID, alertBody);

        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.setArguments(messageArgs);
        dialog.show(getFragmentManager(), "error_dialog");
    }
}
