package edu.utexas.ee464k.e_cal;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

/**
 * Created by Leo on 2/16/2015.
 */
public class CalendarEvent implements Parcelable{
    private String Name;
    private String Location;
    private String Description;
    private String startHour_Of_Day;
    private String startMinute;
    private String startDay;
    private String startMonth;
    private String startYear;
    private String endHour_Of_Day;
    private String endMinute;
    private String endDay;
    private String endMonth;
    private String endYear;

    public String getStartHour_Of_Day() {
        return startHour_Of_Day;
    }

    public void setStartHour_Of_Day(String startHour_Of_Day) {
        this.startHour_Of_Day = startHour_Of_Day;
    }

    public String getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(String startMinute) {
        this.startMinute = startMinute;
    }

    public String getStartDay() {
        return startDay;
    }

    public void setStartDay(String startDay) {
        this.startDay = startDay;
    }

    public String getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(String startMonth) {
        this.startMonth = startMonth;
    }

    public String getStartYear() {
        return startYear;
    }

    public void setStartYear(String startYear) {
        this.startYear = startYear;
    }

    public String getEndHour_Of_Day() {
        return endHour_Of_Day;
    }

    public void setEndHour_Of_Day(String endHour_Of_Day) {
        this.endHour_Of_Day = endHour_Of_Day;
    }

    public String getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(String endMinute) {
        this.endMinute = endMinute;
    }

    public String getEndDay() {
        return endDay;
    }

    public void setEndDay(String endDay) {
        this.endDay = endDay;
    }

    public String getEndMonth() {
        return endMonth;
    }

    public void setEndMonth(String endMonth) {
        this.endMonth = endMonth;
    }

    public String getEndYear() {
        return endYear;
    }

    public void setEndYear(String endYear) {
        this.endYear = endYear;
    }

    public CalendarEvent(){

    }

    public String getName(){
        return Name;
    }

    public String getLocation(){
        return Location;
    }

    public String getDescription(){
        return Description;
    }

    public void setName(String Name){
        this.Name = Name;
    }

    public void setLocation(String Location){
        this.Location = Location;
    }

    public void setDescription(String Description){
        this.Description = Description;
    }

    public CalendarEvent(Parcel in){
        String[] data = new String[13];

        in.readStringArray(data);
        this.Name = data[0];
        this.startHour_Of_Day = data[1];
        this.startMinute = data[2];
        this.startDay = data[3];
        this.startMonth = data[4];
        this.startYear = data[5];
        this.endHour_Of_Day = data[6];
        this.endMinute = data[7];
        this.endDay = data[8];
        this.endMonth = data[9];
        this.endYear = data[10];
        this.Description = data[11];
        this.Location = data[12];
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.Name,
                this.startHour_Of_Day,
                this.startMinute,
                this.startDay,
                this.startMonth,
                this.startYear,
                this.endHour_Of_Day,
                this.endMinute,
                this.endDay,
                this.endMonth,
                this.endYear,
                this.Description,
                this.Location});
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public CalendarEvent createFromParcel(Parcel in) {
            return new CalendarEvent(in);
        }

        public CalendarEvent[] newArray(int size) {
            return new CalendarEvent[size];
        }
    };
}
