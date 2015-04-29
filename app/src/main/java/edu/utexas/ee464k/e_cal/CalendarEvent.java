package edu.utexas.ee464k.e_cal;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

/**
 * Created by Leo on 2/16/2015.
 */
public class CalendarEvent implements Parcelable, Comparable<CalendarEvent>{
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

    @Override
    public int compareTo(CalendarEvent other){
        Calendar c1 = this.getStartCalendar();
        Calendar c2 = this.getStartCalendar();
        return c1.compareTo(c2);
    }

    public Calendar getStartCalendar(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR,Integer.parseInt(startYear));
        cal.set(Calendar.MONTH,Integer.parseInt(startMonth) - 1);
        cal.set(Calendar.DAY_OF_MONTH,Integer.parseInt(startDay));
        cal.set(Calendar.HOUR_OF_DAY,Integer.parseInt(startHour_Of_Day));
        cal.set(Calendar.MINUTE,Integer.parseInt(startMinute));
        return cal;
    }

    public Calendar getEndCalendar(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR,Integer.parseInt(endYear));
        cal.set(Calendar.MONTH,Integer.parseInt(endMonth) - 1);
        cal.set(Calendar.DAY_OF_MONTH,Integer.parseInt(endDay));
        cal.set(Calendar.HOUR_OF_DAY,Integer.parseInt(endHour_Of_Day));
        cal.set(Calendar.MINUTE,Integer.parseInt(endMinute));
        return cal;
    }

    public String getStartHour_Of_Day() {
        return startHour_Of_Day;
    }

    public void setStartHour_Of_Day(String startHour_Of_Day) {
        if(Integer.parseInt(startHour_Of_Day) / 10 == 0){
            this.startHour_Of_Day = "0" + startHour_Of_Day;
        }else{
            this.startHour_Of_Day = startHour_Of_Day;
        }
    }

    public void setStartHour_Of_Day(int startHour_of_day){
        this.setStartHour_Of_Day(String.valueOf(startHour_of_day));
    }

    public String getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(String startMinute) {
        if(Integer.parseInt(startMinute) / 10 == 0){
            this.startMinute = "0" + startMinute;
        } else{
            this.startMinute = startMinute;
        }
    }

    public void setStartMinute(int startMinute) {
        this.setStartMinute(String.valueOf(startMinute));
    }

    public String getStartDay() {
        return startDay;
    }

    public void setStartDay(String startDay) {
        if(Integer.parseInt(startDay) / 10 == 0){
            this.startDay = "0" + startDay;
        }else{
            this.startDay = startDay;
        }
    }

    public void setStartDay(int startDay) {
        this.setStartDay(String.valueOf(startDay));
    }

    public String getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(String startMonth) {
        if(Integer.parseInt(startMonth)/10==0){
            this.startMonth = "0" + startMonth;
        }else{
            this.startMonth = startMonth;
        }
    }

    public void setStartMonth(int startMonth) {
        this.setStartMonth(String.valueOf(startMonth));
    }

    public String getStartYear() {
        return startYear;
    }

    public void setStartYear(String startYear) {
        this.startYear = startYear;
    }

    public void setStartYear(int startYear) {
        this.setStartYear(String.valueOf(startYear));
    }

    public String getEndHour_Of_Day() {
        return endHour_Of_Day;
    }

    public void setEndHour_Of_Day(String endHour_Of_Day) {
        if(Integer.parseInt(endHour_Of_Day) / 10 == 0){
            this.endHour_Of_Day = "0" + endHour_Of_Day;
        } else{
            this.endHour_Of_Day = endHour_Of_Day;
        }
    }

    public void setEndHour_Of_Day(int endHour_Of_Day) {
        this.setEndHour_Of_Day(String.valueOf(endHour_Of_Day));
    }

    public String getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(String endMinute) {
        if(Integer.parseInt(endMinute) / 10 == 0){
            this.endMinute = "0" + endMinute;
        } else{
            this.endMinute = endMinute;
        }
    }

    public void setEndMinute(int endMinute) {
        this.setEndMinute(String.valueOf(endMinute));
    }

    public String getEndDay() {
        return endDay;
    }

    public void setEndDay(String endDay) {
        if(Integer.parseInt(endDay) / 10 == 0){
            this.endDay = "0" + endDay;
        } else{
            this.endDay = endDay;
        }
    }

    public void setEndDay(int endDay) {
        this.setEndDay(String.valueOf(endDay));
    }

    public String getEndMonth() {
        return endMonth;
    }

    public void setEndMonth(String endMonth) {
        if(Integer.parseInt(endMonth) / 10 == 0){
            this.endMonth = "0" + endMonth;
        } else{
            this.endMonth = endMonth;
        }
    }

    public void setEndMonth(int endMonth){
        this.setEndMonth(String.valueOf(endMonth));
    }

    public String getEndYear() {
        return endYear;
    }

    public void setEndYear(String endYear) {
        this.endYear = endYear;
    }

    public void setEndYear(int endYear) {
        this.setEndYear(String.valueOf(endYear));
    }

    public CalendarEvent(){
        Name = "";
        Location = "";
        Description = "";
        startHour_Of_Day = "";
        startMinute = "";
        startDay = "";
        startMonth = "";
        startYear = "";
        endHour_Of_Day = "";
        endMinute = "";
        endDay = "";
        endMonth = "";
        endYear = "";

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
