package com.example.ahut_util;

import com.example.ahut.GlobalContext;

import android.util.Log;


public class Util {
	public static void log(String i) {
		if(GlobalContext.DEBUG)
			Log.i("AHUTLESSON", i);
	}
}
