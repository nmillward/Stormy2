package superlifesize.com.stormy2;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by nmilward on 2/15/15.
 */
public class DailyWeather {

    private String mIcon;
    private long mDayOfWeek;
    private double mTempMin;
    private double mTempMax;

//    public DailyWeather(String mIcon, long mDayOfWeek, double mTempMax, double mTempMin) {
//        super();
//        this.mIcon = mIcon;
//        this.mDayOfWeek = mDayOfWeek;
//        this.mTempMax = mTempMax;
//        this.mTempMin = mTempMin;
//    }


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


    //Formatted for Day of the Week (i.e. "Monday")
    //EEE = Mon
    //EEEEE = M
    public long getDayOfWeek() {
        return mDayOfWeek;
    }

    public String getFormattedDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
        Date day = new Date(getDayOfWeek());
        String dayOfTheWeek = formatter.format(day);
        return dayOfTheWeek;
    }

    public void setDayOfWeek(long dayOfWeek) {
        mDayOfWeek = dayOfWeek;
    }



    public int getTempMin() {
        return (int) Math.round(mTempMin);
    }

    public void setTempMin(double tempMin) {
        mTempMin = tempMin;
    }

    public int getTempMax() {
        return (int) Math.round(mTempMax);
    }

    public void setTempMax(double tempMax) {
        mTempMax = tempMax;
    }


}
