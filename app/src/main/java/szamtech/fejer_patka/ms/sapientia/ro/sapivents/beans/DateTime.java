package szamtech.fejer_patka.ms.sapientia.ro.sapivents.beans;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Custom implementation of Calendaristic Date and Time
 * By Patka Zsolt-Andras
 * Written on 2018-11-24
 * Uses Hungarian date format YYYY-MM-DDThh:mm
 * Implements Serializable interface to tell the JVM that this class can be serialized
 * The DateTime object is immutable it is set in the constructor and can not be changed (no setters)
 */
public class DateTime implements Serializable{
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minutes;

    /**
     * The whole date is stored in a string
     * Format: YYYY-MM-DDThh:mm
     * Hours are stored in 24 hour format
     */
    private String date;

    public DateTime(int year, int month, int day, int hour, int minutes) throws InvalidDateTimeException{
        validateDate(year,month,day,hour,minutes);
    }

    /**
     * Date validation
     * checks for leap year as well
     * @param year should be in the (2000,2100) interval
     * @param month should be in the [1,12] interval
     * @param day should be in the [1,31] interval
     * @param hour should be in the [0,24) interval
     * @param minutes should be in the [0,60) interval
     * @throws InvalidDateTimeException if the validation fails
     */
    private void validateDate(int year, int month, int day, int hour, int minutes) throws InvalidDateTimeException{
        //Basic validation
        //Year should be in the (2000,2100) interval
        if( year < 2000 || year > 2100 || month < 0 || month > 12 || hour < 0 || hour > 23 || minutes < 0 || minutes > 59){
            throw new InvalidDateTimeException();
        }else{
            this.year = year;
            this.month = month;
            this.hour = hour;
            this.minutes = minutes;
        }
        if(day > 31){
            throw new InvalidDateTimeException();
        }
        //If the month is February, check if 29 is a valid day value (leap year test)
        if(month == 2 && day == 29) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            if(calendar.getActualMaximum(Calendar.DAY_OF_YEAR) > 365){
                this.day = day;
            }else{
                throw new InvalidDateTimeException();
            }
        }
        //The number of days is not bigger than 31, and the month is not February
        //Check if the month is 4 (April) or 6 (June) or 9 (September) or 11 (November)
        //These months can only have a maximum of 30 days
        if(month == 4 || month == 6 || month == 9 || month == 11) {
            if(day == 31){
                throw new InvalidDateTimeException();
            }
            this.day = day;
        }else{
            this.day = day;
        }
        //Build up the string
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(year).append("-");
        appendIfLessThanTen(stringBuilder, month).append("-");
        appendIfLessThanTen(stringBuilder, day).append("T");
        appendIfLessThanTen(stringBuilder, hour).append(":");
        appendIfLessThanTen(stringBuilder, minutes);
        date = stringBuilder.toString();
    }
    /**
     * Parses @param date with the following format to a DateTime object: YYYYsMMsDDshhsmm
     * s -> separator, can be any character
     * @throws Exception if the format is not correct
     */
    public DateTime(String date) throws RuntimeException{
        //parse YYYY
        int year = 0;
        for(int i = 0; i <= 3; ++i) {
            year = year * 10 + Integer.parseInt(date.charAt(i) + "");
        }
        //parse MM
        int month = 0;
        for(int i = 5; i <= 6; ++i) {
            month = month * 10 + Integer.parseInt(date.charAt(i) + "");
        }
        //parse DD
        int day = 0;
        for(int i = 8; i <= 9; ++i) {
            day = day * 10 + Integer.parseInt(date.charAt(i) + "");
        }
        //parse hh
        int hour = 0;
        for(int i = 11; i <= 12; ++i){
            hour = hour * 10 + Integer.parseInt(date.charAt(i) + "");
        }
        //parse minute
        int minute = 0;
        for(int i = 14; i <= 15; ++i){
            minute = minute * 10 + Integer.parseInt(date.charAt(i) + "");
        }
        System.out.println(year + " " + month + " " + day + " " + hour + " " + minute);
        validateDate(year,month,day,hour,minute);
    }

    /**
     * Adds a 0 as left padding to the number if it's less than 10
     */
    private StringBuilder appendIfLessThanTen(StringBuilder stringBuilder, int value){
        if(value < 10){
            return stringBuilder.append("0").append(value);
        }
        return stringBuilder.append(value);
    }

    public String getDate(){
        return date;
    }

    /**
     * Custom Exception class
     */
    public class InvalidDateTimeException extends RuntimeException{
        InvalidDateTimeException(){
            super("Invalid date value");
        }
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        if(hour < 0 || hour > 23){
            throw new InvalidDateTimeException();
        }
        this.hour = hour;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        if(minutes < 0 || minutes > 59){
            throw new InvalidDateTimeException();
        }
        this.minutes = minutes;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(year).append(".");
        appendIfLessThanTen(stringBuilder, month).append(".");
        appendIfLessThanTen(stringBuilder, day).append(" ");
        appendIfLessThanTen(stringBuilder, hour).append(":");
        appendIfLessThanTen(stringBuilder, minutes);
        return stringBuilder.toString();
    }
}