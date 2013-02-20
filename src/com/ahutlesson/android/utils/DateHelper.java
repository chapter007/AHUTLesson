package com.ahutlesson.android.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateHelper {
	
	public static boolean atSameDay(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date1);
		cal2.setTime(date2);
		return (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
		                  cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
	}
	
	
	public static boolean atSameYear(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date1);
		cal2.setTime(date2);
		return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
	}

	public static String toSmartDateString(Date date) {
		Date now = new Date();
		if(atSameDay(date, now)) {
	        SimpleDateFormat formatToday = new SimpleDateFormat("HH:mm"); 
	        return formatToday.format(date);
		}else if(atSameYear(date, now)) {
	        SimpleDateFormat formatThisYear = new SimpleDateFormat("M��d��"); 
			return formatThisYear.format(date);
		}else{
	        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy��M��d��"); 
			return formatDate.format(date);
		}
	}

	public static String toSmartTimeString(Date date) {
		Date now = new Date();
		if(atSameDay(date, now)) {
	        SimpleDateFormat formatToday = new SimpleDateFormat("HH:mm"); 
	        return formatToday.format(date);
		}else if(atSameYear(date, now)) {
	        SimpleDateFormat formatThisYear = new SimpleDateFormat("M��d�� HH:mm"); 
			return formatThisYear.format(date);
		}else{
	        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy��M��d�� HH:mm"); 
			return formatDate.format(date);
		}
	}
	
	public static String toDateString(Date date) {
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
		return formatDate.format(date);
	}

}
