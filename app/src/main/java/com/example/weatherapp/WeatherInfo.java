package com.example.weatherapp;


import androidx.core.text.util.LocalePreferences;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class WeatherInfo implements Serializable {

    private double latitude;
    private double longitude;
    private String resolvedAddress;
    private List<Days> days;
    private List<Alerts> alerts;
    private CurrentConditions currentConditions;

    public WeatherInfo(double latitude, double longitude, String resolvedAddress, List<Days> days, List<Alerts> alerts, CurrentConditions currentConditions) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.resolvedAddress = resolvedAddress;
        this.days = days;
        this.alerts = alerts;
        this.currentConditions = currentConditions;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getResolvedAddress() {
        return resolvedAddress;
    }

    public List<Days> getDays() {
        return days;
    }

    public List<Alerts> getAlerts() {
        return alerts;
    }

    public CurrentConditions getCurrentConditions() {
        return currentConditions;
    }

    public static class Days {
        private long datetimeEpoch;
        private double tempmax;
        private double tempmin;
        private int precipprob;
        private int uvindex;
        private String conditions;
        private String description;
        private String icon;
        private double morning;
        private double afternoon;
        private double evening;
        private double night;



        public Days(long datetimeEpoch,double tempmax,double tempmin,int precipprob,int uvindex,String conditions, String description, String icon){
            this.datetimeEpoch=datetimeEpoch;
            this.tempmax=tempmax;
            this.tempmin=tempmin;
            this.precipprob=precipprob;
            this.uvindex=uvindex;
            this.conditions=conditions;
            this.description=description;
            this.icon=icon;
        }
        public Days(long datetimeEpoch,double tempmax,double tempmin,int precipprob,int uvindex,String conditions, String description, String icon,double morning,double afternoon, double evening, double night){
            this.datetimeEpoch=datetimeEpoch;
            this.tempmax=tempmax;
            this.tempmin=tempmin;
            this.precipprob=precipprob;
            this.uvindex=uvindex;
            this.conditions=conditions;
            this.description=description;
            this.icon=icon;
            this.morning= morning;
            this.afternoon = afternoon;
            this.evening=evening;
            this.night = night;

        }

        public long getDatetimeEpoch(){
            return datetimeEpoch;
        }
        public double getTempmax(){
            return tempmax;
        }
        public double getTempmin(){
            return tempmin;
        }
        public int getPrecipprob(){
            return precipprob;
        }
        public int getUvindex(){
            return uvindex;
        }
        public String getConditions(){
            return conditions;
        }
        public String getDescription(){
            return description;
        }
        public String getIcon(){
            return icon;
        }
        public double getMorning(){
            return morning;
        }
        public  double getAfternoon(){
            return afternoon;
        }
        public double getEvening(){
            return evening;
        }
        public double getNight() {
            return night;
        }
    }
    public static class Hours implements Serializable{
        private static final long serialVersionUID = 1L;
        private String datetime;
        private long datetimeEpoch;
        private double temp;
        private double feelslike;
        private double humidity;
        private double windgust;
        private double windspeed;
        private double winddir;
        private double visibility;
        private double cloudcover;
        private int uvindex;
        private String conditions;
        private String icon;


        public Hours(String datetime,double temp){
            this.datetime=datetime;
            this.temp=temp;

        }


        public Hours(String datetime, long datetimeEpoch, double temp, double feelslike, double humidity,
                     double windgust, double windspeed, double winddir, double visibility, double cloudcover,
                     int uvindex, String conditions, String icon) {
            this.datetime = datetime;
            this.datetimeEpoch = datetimeEpoch;
            this.temp = temp;
            this.feelslike = feelslike;
            this.humidity = humidity;
            this.windgust = windgust;
            this.windspeed = windspeed;
            this.winddir = winddir;
            this.visibility = visibility;
            this.cloudcover = cloudcover;
            this.uvindex = uvindex;
            this.conditions = conditions;
            this.icon = icon;
        }

        public String getDatetime() {
            return datetime;
        }

        public long getDatetimeEpoch() {
            return datetimeEpoch;
        }

        public double getTemp() {
            return temp;
        }

        public double getFeelslike() {
            return feelslike;
        }

        public double getHumidity() {
            return humidity;
        }

        public double getWindgust() {
            return windgust;
        }

        public double getWindspeed() {
            return windspeed;
        }

        public double getWinddir() {
            return winddir;
        }

        public double getVisibility() {
            return visibility;
        }

        public double getCloudcover() {
            return cloudcover;
        }

        public int getUvindex() {
            return uvindex;
        }

        public String getConditions() {
            return conditions;
        }

        public String getIcon() {
            return icon;
        }

    }

    public static class Alerts{
        private String event;
        private String headline;
        private String id;
        private String description;

        public Alerts(String event, String headline,String id,String description){
            this.event=event;
            this.headline=headline;
            this.id=id;
            this.description=description;
        }
        public String getEvent(){
            return event;
        }
        public String getHeadline(){
            return headline;
        }
        public String getId(){
            return id;
        }
        public String getDescription(){
            return description;
        }

    }

    public static class CurrentConditions{
        private long datetimeEpoch;
        private double temp;
        private double feelslike;
        private double humidity;
        private double windgust;
        private double windspeed;
        private double winddir;
        private double visibility;
        private double cloudcover;
        private double uvindex;
        private String conditions;
        private String icon;
        private long sunriseEpoch;
        private long sunsetEpoch;

        public CurrentConditions(long datetimeEpoch, double temp, double feelslike, double humidity, double windgust,
                              double windspeed, double winddir, double visibility, double cloudcover, double uvindex,
                              String conditions, String icon, long sunriseEpoch, long sunsetEpoch) {
            this.datetimeEpoch = datetimeEpoch;
            this.temp = temp;
            this.feelslike = feelslike;
            this.humidity = humidity;
            this.windgust = windgust;
            this.windspeed = windspeed;
            this.winddir = winddir;
            this.visibility = visibility;
            this.cloudcover = cloudcover;
            this.uvindex = uvindex;
            this.conditions = conditions;
            this.icon = icon;
            this.sunriseEpoch = sunriseEpoch;
            this.sunsetEpoch = sunsetEpoch;
        }

        // Getters for all fields
        public long getDatetimeEpoch() {
            return datetimeEpoch;
        }

        public double getTemp() {
            return temp;
        }

        public double getFeelslike() {
            return feelslike;
        }

        public double getHumidity() {
            return humidity;
        }

        public double getWindgust() {
            return windgust;
        }

        public double getWindspeed() {
            return windspeed;
        }

        public double getWinddir() {
            return winddir;
        }

        public double getVisibility() {
            return visibility;
        }

        public double getCloudcover() {
            return cloudcover;
        }

        public double getUvindex() {
            return uvindex;
        }

        public String getConditions() {
            return conditions;
        }

        public String getIcon() {
            return icon;
        }

        public long getSunriseEpoch() {
            return sunriseEpoch;
        }

        public long getSunsetEpoch() {
            return sunsetEpoch;
        }
    }



}



