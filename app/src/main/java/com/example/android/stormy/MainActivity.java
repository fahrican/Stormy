package com.example.android.stormy;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
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

    @BindView(R.id.timeLabel) TextView mTimeLabel;
    @BindView(R.id.temperatureLabel) TextView mTemperatureLabel;
    @BindView(R.id.humidityValue) TextView mHumidityValue;
    @BindView(R.id.precipValue) TextView mPrecipValue;
    @BindView(R.id.summaryLabel) TextView mSummaryLabel;
    @BindView(R.id.iconImageView) ImageView mIconImageView;
    @BindView(R.id.refreshImageView) ImageView refreshImageView;
    @BindView(R.id.myProgressBar) ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        progressBar.setVisibility(View.INVISIBLE);

        final double latitude = 37.8267;
        final double longitude = -122.4233;

        refreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getForeCast(latitude, longitude);
            }
        });

        getForeCast(latitude, longitude);

        Log.d(LOG_TAG, "Main UI code is running!");
    }

    private void getForeCast(double latitude, double longitude) {

        String apiKey = "210439731034a0102c3f6e4a1f7973f9";
        String forecastURL = "https://api.darksky.net/forecast/" + apiKey + "/" + latitude + "," + longitude;

        if (isNetworkAvailable()) {

            toggleRefresh();

            final OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(forecastURL).build();
            Call call = client.newCall(request);

            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    alertUserAboutError("Refresh Failed", "Something went wrong by requesting new data");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });

                    try {

                        String jsonData = response.body().string();
                        Log.v(LOG_TAG, jsonData);
                        if (response.isSuccessful()) {

                            currentWeather = getCurrentDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });
                        } else {
                            alertUserAboutError(getString(R.string.error_title), getString(R.string.error_message));
                        }
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "IOException caught: ", e);
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "IOException caught: ", e);
                    }
                }
            });
        } else {
            alertUserAboutError(getString(R.string.no_network_connection), getString(R.string.no_network_connection_text));
            Toast.makeText(this, R.string.network_unavailable, Toast.LENGTH_LONG).show();
        }
    }

    private void toggleRefresh() {

        if (progressBar.getVisibility() == View.INVISIBLE){

            refreshImageView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }
        else {
            refreshImageView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void updateDisplay() {

        mTimeLabel.setText("At " + currentWeather.getFormattedTime() + " it will be");
        mTemperatureLabel.setText(currentWeather.getTemperature() + "");
        mHumidityValue.setText(currentWeather.getHumidity() + "");
        mPrecipValue.setText(currentWeather.getPrecipChance() + "%");
        mSummaryLabel.setText(currentWeather.getSummary());
        mIconImageView.setImageDrawable(getResources().getDrawable(currentWeather.getIconId()));
    }

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException {

        JSONObject forcast = new JSONObject(jsonData);
        JSONObject currently = forcast.getJSONObject("currently");

        CurrentWeather currentWeather = new CurrentWeather();
        currentWeather.setTime(currently.getLong("time"));
        currentWeather.setIcon(currently.getString("icon"));
        currentWeather.setSummary(currently.getString("summary"));
        currentWeather.setPrecipChance(currently.getDouble("precipProbability"));
        currentWeather.setTemperature(currently.getDouble("temperature"));
        currentWeather.setHumidity(currently.getDouble("humidity"));
        currentWeather.setTimeZone(forcast.getString("timezone"));

        Log.i(LOG_TAG, "Timezone: " + currentWeather.getTimeZone());
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
