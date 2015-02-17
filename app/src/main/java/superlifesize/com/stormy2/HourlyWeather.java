package superlifesize.com.stormy2;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by nmilward on 2/8/15.
 */
public class HourlyWeather {

    private String mIcon;
    private double mTemperature;
    private long mHourOfDay;

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public int getIconId() {
        //clear-day, clear-night, rain, snow, sleet, wind, fog, cloudy, partly-cloudy-day, or partly-cloudy-night
        int iconId = R.drawable.clear_day;

        if(mIcon.equals("clear-day")) {
            iconId = R.drawable.clear_day;
        }
        else if(mIcon.equals("clear-night")) {
            iconId = R.drawable.clear_night;
        }
        else if (mIcon.equals("rain")) {
            iconId = R.drawable.rain;
        }
        else if (mIcon.equals("snow")) {
            iconId = R.drawable.snow;
        }
        else if (mIcon.equals("sleet")) {
            iconId = R.drawable.sleet;
        }
        else if (mIcon.equals("wind")) {
            iconId = R.drawable.wind;
        }
        else if (mIcon.equals("fog")) {
            iconId = R.drawable.fog;
        }
        else if (mIcon.equals("cloudy")) {
            iconId = R.drawable.cloudy;
        }
        else if (mIcon.equals("partly-cloudy-day")) {
            iconId = R.drawable.partly_cloudy;
        }
        else if (mIcon.equals("partly-cloudy-night")) {
            iconId = R.drawable.cloudy_night;
        }

        return iconId;
    }

    public int getTemperature() {
        return (int) Math.round(mTemperature);
    }

    public void setTemperature(double temperature) {
        mTemperature = temperature;
    }

    public long getTime() {
        return mHourOfDay;
    }

    public String getFormattedTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("ha");
        Date dateTime = new Date(getTime() * 1000);
        String timeString = formatter.format(dateTime);
        return timeString;
    }

    public void setTime(long hourOfDay) {
        mHourOfDay = hourOfDay;
    }

}
