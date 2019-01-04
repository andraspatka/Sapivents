package szamtech.fejer_patka.ms.sapientia.ro.sapivents.beans;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Custom implementation of Calendaristic Date and Time
 * By Patka Zsolt-Andras
 * Updated on: 2018-11-30
 * Added year, month, day, hour, minutes fields as transient (transient fields are ignored during
 * serialization).
 * Written on 2018-11-24
 * Uses Hungarian date format YYYY-MM-DDThh:mm
 * Implements Serializable interface to tell the JVM that this class can be serialized
 */
public class DateTime implements Serializable{
    transient private int year;
    transient private int month;
    transient private int day;
    transient private int hour;
    transient private int minutes;

    private static final long serialVersionUID = 1738653275L;

    /**
     * The whole date is stored in a string
     * Format: YYYY-MM-DDThh:mm
     * Hours are stored in 24 hour format
     */
    private String date;

    /**
     * Creates a date from year, month, day, hour, minutes integer values
     * @param year given year
     * @param month given month
     * @param day given day
     * @param hour given hour
     * @param minutes given minutes
     * @throws InvalidDateTimeException if the given input is incorrect
     */
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
     * @throws InvalidDateTimeException if the date is invalid
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
        addPaddingIfLessThanTen(stringBuilder, month).append("-");
        addPaddingIfLessThanTen(stringBuilder, day).append("T");
        addPaddingIfLessThanTen(stringBuilder, hour).append(":");
        addPaddingIfLessThanTen(stringBuilder, minutes);
        date = stringBuilder.toString();
    }
    /**
     * Parses @param date with the following format to a DateTime object: YYYYsMMsDDshhsmm
     * s -> separator, can be any character
     * @throws InvalidDateTimeException if the format is not correct
     */
    public DateTime(String date) throws InvalidDateTimeException{
        parseDate(date);
        validateDate(year,month,day,hour,minutes);
    }

    public DateTime(){

    }

    /**
     * Extracts year, month, day, hour and minutes from a DateTime String
     * In the given format the indexes are, as follows:
     *  [0,3] - YYYY
     *  [5,6] - MM
     *  [8,9] - DD
     *  [11,12] - hh
     *  [14,15] - mm
     * @param date given date in YYYYsMMsDDshhsmm format
     * s -> separator, can be any character
     */
    private void parseDate(String date){
        //parse YYYY
        int year = 0;
        for(int i = 0; i <= 3; ++i) {
            year = year * 10 + Integer.parseInt(date.charAt(i) + "");
        }
        this.year = year;
        //parse MM
        int month = 0;
        for(int i = 5; i <= 6; ++i) {
            month = month * 10 + Integer.parseInt(date.charAt(i) + "");
        }
        this.month = month;
        //parse DD
        int day = 0;
        for(int i = 8; i <= 9; ++i) {
            day = day * 10 + Integer.parseInt(date.charAt(i) + "");
        }
        this.day = day;
        //parse hh
        int hour = 0;
        for(int i = 11; i <= 12; ++i){
            hour = hour * 10 + Integer.parseInt(date.charAt(i) + "");
        }
        this.hour = hour;
        //parse mm
        int minutes = 0;
        for(int i = 14; i <= 15; ++i){
            minutes = minutes * 10 + Integer.parseInt(date.charAt(i) + "");
        }
        this.minutes = minutes;
    }
    /**
     * Adds a 0 as left padding to the number if it's less than 10
     * @param stringBuilder StringBuilder instance, temporarily holds the date string
     * @param value value to be padded
     * @return StringBuilder instances, so the method can be chained
     */
    private StringBuilder addPaddingIfLessThanTen(StringBuilder stringBuilder, int value){
        if(value < 10){
            return stringBuilder.append("0").append(value);
        }
        return stringBuilder.append(value);
    }

    public String getDate(){
        return this.year + "." + this.month + "." + this.day;
    }

    public String getTime(){
        return this.hour + ":" + this.minutes;
    }

    /**
     * Sets the hour
     * Performs validation
     * @param hour to be set
     * @throws InvalidDateTimeException, if the given @param hour isn't in the [0,23] interval
     */
    public void setHour(int hour) throws InvalidDateTimeException{
        if(hour < 0 || hour > 23){
            throw new InvalidDateTimeException();
        }
        //Modify the date string, to reflect the change in hour
        //Convert to charArray
        char[] dateCharArray = this.date.toCharArray();
        //Set the characters at the required indexes
        dateCharArray[11] = (char) ((hour / 10) + '0');
        dateCharArray[12] = (char) ((hour % 10) + '0');
        //Convert back to String
        this.date = String.valueOf(dateCharArray);
        this.hour = hour;
    }

    /**
     * Sets the minutes
     * Performs validation
     * @param minutes to be set
     * @throws InvalidDateTimeException, if the given @param minutes isn't in the [0,59] interval
     */
    public void setMinutes(int minutes) throws InvalidDateTimeException{
        if(minutes < 0 || minutes > 59){
            throw new InvalidDateTimeException();
        }
        //Modify the date string, to reflect the change in minutes
        //Convert to charArray
        char[] dateCharArray = this.date.toCharArray();
        //Set the characters at the required indexes
        dateCharArray[14] = (char) ((minutes / 10) + '0');
        dateCharArray[15] = (char) ((minutes % 10) + '0');
        //Convert back to String
        this.date = String.valueOf(dateCharArray);
        this.minutes = minutes;
    }

    /**
     * @return DateTime in 'YYYY-MM-DD hh:mm' format
     */
    @Override
    public String toString() {
        parseDate(date);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(year).append(".");
        addPaddingIfLessThanTen(stringBuilder, month).append(".");
        addPaddingIfLessThanTen(stringBuilder, day).append(" ");
        addPaddingIfLessThanTen(stringBuilder, hour).append(":");
        addPaddingIfLessThanTen(stringBuilder, minutes);
        return stringBuilder.toString();
    }


    /**
     * Custom Exception class
     */
    public class InvalidDateTimeException extends RuntimeException{
        InvalidDateTimeException(){
            super("Invalid date value");
        }
    }
}