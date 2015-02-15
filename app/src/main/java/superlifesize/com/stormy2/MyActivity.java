package superlifesize.com.stormy2;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MyActivity extends Activity implements LocationProvider.LocationCallback {

    public static final String TAG = MyActivity.class.getSimpleName();

    private CurrentWeather mCurrentWeather;
    private LocationProvider mLocationProvider;

    private double latitude = 0.0;
    private double longitude = 0.0;

    @InjectView(R.id.temperatureLabel) TextView mTemperatureLabel;
    @InjectView(R.id.tv_timeLabel) TextView mTimeLabel;
    @InjectView(R.id.tv_humidityLabel) TextView mHumidityLabel;
    @InjectView(R.id.tv_humidityValue) TextView mHumidityValue;
    @InjectView(R.id.tv_precipLabel) TextView mPrecipLabel;
    @InjectView(R.id.tv_precipValue) TextView mPrecipValue;
    @InjectView(R.id.tv_summaryLabel) TextView mSummaryLabel;
    @InjectView(R.id.ic_location) ImageView mIconLocation;
    @InjectView(R.id.iv_refresh) ImageView mRefreshImageView;
    @InjectView(R.id.progressBar) ProgressBar mProgressBar;
    @InjectView(R.id.tv_locationLabel) TextView mLocationTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        ButterKnife.inject(this);

        mProgressBar.setVisibility(View.INVISIBLE);

        mLocationProvider = new LocationProvider(this, this);

        mRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getForecast(latitude, longitude);
            }
        });

        Log.d(TAG, "Running on the Main Thread");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "ON RESUME");
        mLocationProvider.Connect();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mLocationProvider.Disconnect();
    }


    private void getForecast(double latitude, double longitude) {
        String apiKey = getString(R.string.forecast_api_key);
        String forecastUrl = "https://api.forecast.io/forecast/" + apiKey + "/" + latitude + "," + longitude;

        if (isNetworkAvailable()) {
            toggleRefresh();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(forecastUrl)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    alertUserAboutError();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mCurrentWeather = getCurrentDetails(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "UPDATE DISPLAY DATA HERE");
                                    updateDisplay();
                                }
                            });
                        } else {
                            alertUserAboutError();
                        }
                    }
                    catch (IOException e) {
                        Log.e(TAG, "Exception Caught: ", e);
                    }
                    catch (JSONException e) {
                        Log.e(TAG, "Exception Caught: ", e);
                    }
                }
            });
        } else {
            Toast.makeText(this, getString(R.string.network_unavailable), Toast.LENGTH_LONG).show();
        }
    }

    private void toggleRefresh() {
        if (mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageView.setVisibility(View.INVISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImageView.setVisibility(View.VISIBLE);
        }
    }

    private void updateDisplay() {
        //Update Current Weather
        mTemperatureLabel.setText(mCurrentWeather.getTemperature() + "");
        mTimeLabel.setText("At " + mCurrentWeather.getFormattedTime() + " it will be");
        mHumidityValue.setText(mCurrentWeather.getHumidity() + "");
        mPrecipValue.setText(mCurrentWeather.getPrecipChance() + "%");
        mSummaryLabel.setText(mCurrentWeather.getSummary());

        Drawable drawable = getResources().getDrawable(mCurrentWeather.getIconId());
        mIconLocation.setImageDrawable(drawable);

        //Update Hourly Weather


        //Update Daily Weather

    }

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString(WeatherConstants.KEY_ATTR_TIMEZONE);
        Log.i(TAG, "From JSON: " + timezone);

        //Weather Objects
        JSONObject currently = forecast.getJSONObject(WeatherConstants.KEY_CURRENT_WEATHER);

        CurrentWeather currentWeather = new CurrentWeather();

        //Current Weather
        currentWeather.setHumidity(currently.getDouble(WeatherConstants.KEY_ATTR_HUMIDITY));
        currentWeather.setTime(currently.getLong(WeatherConstants.KEY_ATTR_TIME));
        currentWeather.setIcon(currently.getString(WeatherConstants.KEY_ATTR_ICON));
        currentWeather.setPrecipChance(currently.getDouble(WeatherConstants.KEY_ATTR_PRECIP_PROB));
        currentWeather.setSummary(currently.getString(WeatherConstants.KEY_ATTR_SUMMARY));
        currentWeather.setTemperature(currently.getDouble(WeatherConstants.KEY_ATTR_TEMPERATURE));
        currentWeather.setTimeZone(timezone);
        currentWeather.setFeelsLike(currently.getDouble(WeatherConstants.KEY_ATTR_FEELS_LIKE));


        //Hourly Weather
        //data structure >>>> hourly.data[x].time
        JSONObject hourly = forecast.getJSONObject(WeatherConstants.KEY_HOURLY_WEATHER);

        JSONArray jsonHourlyData = hourly.getJSONArray(WeatherConstants.KEY_DATA);
        Log.d(TAG, "Hourly JSON Data: " + jsonHourlyData);
        for (int i=0; i < jsonHourlyData.length(); i++) {
            JSONObject hourlyData = jsonHourlyData.getJSONObject(i);

//            currentWeather.setTemperature(hourlyData.getDouble(WeatherConstants.KEY_ATTR_TEMPERATURE));
//            Double hourlyTemp = hourlyData.getDouble(WeatherConstants.KEY_ATTR_TEMPERATURE);
//            Log.d(TAG, "" + hourlyTemp);

//            String houlyIcon = hourlyData.getString(WeatherConstants.KEY_ATTR_ICON);
//            Log.d(TAG, "icon: " + houlyIcon);
        }

        
        //Daily Weather
        JSONObject daily = forecast.getJSONObject(WeatherConstants.KEY_DAILY_WEATHER);

        JSONArray jsonDailyData = daily.getJSONArray(WeatherConstants.KEY_DATA);
        for (int i=0; i < jsonDailyData.length(); i++) {
            JSONObject dailyData = jsonDailyData.getJSONObject(i);


        }

//        currentWeather.setTempMax(daily.getDouble(WeatherConstants.KEY_ATTR_TEMP_MAX));
//        currentWeather.setTempMin(daily.getDouble(WeatherConstants.KEY_ATTR_TEMP_MIN));
//        currentWeather.setIcon(daily.getString(WeatherConstants.KEY_ATTR_ICON));


        Log.d(TAG, currentWeather.getFormattedTime());

        return currentWeather;
    }

    private void getGeolocation() {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addressList = null;
        Address address = null;
        try {
            addressList = geocoder.getFromLocation(latitude, longitude, 1);
            address = addressList.get(0);
            if (address != null && addressList.size() > 0) {
                mLocationTitle.setText(address.getLocality() + ", " + address.getCountryCode());
                Log.d(TAG, address.getLocality() + ", " + address.getCountryCode());
            }
        } catch (IOException e) {
            Log.e(TAG, "Unable to connect to Geocoder", e);
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }


    @Override
    public void handleNewLocation(Location location) {
        Log.d(TAG, "HANDLE NEW LOCATION: " + location.toString());

        //Get current location coordinates

//        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            Log.i(TAG, "Latitude from GeoLocation: " + latitude);
            Log.i(TAG, "Longitude from GeoLocation: " + longitude);

            getForecast(latitude, longitude);

            getGeolocation();

//        }
    }

}









