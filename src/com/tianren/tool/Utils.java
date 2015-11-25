package com.tianren.tool;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @Author SunnyCoffee
 * @Date 2014-1-28
 * @version 1.0
 * @Desc 宸ュ叿绫�
 */

public class Utils {

	public static String getCurrentTime(String format) {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		String currentTime = sdf.format(date);
		return currentTime;
	}

	public static String getCurrentTime() {
		return getCurrentTime("yyyy-MM-dd  HH:mm:ss");
	}
	
	public static String timeAccount(long timepoint) {
		long lefttime = System.currentTimeMillis() % (24 * 60 * 60 * 1000);
		lefttime = lefttime / 1000 / 60 / 60;
		long betweenTime = System.currentTimeMillis() - timepoint;
		int minute = (int) betweenTime / 1000 / 60;
		String t = null;
			SimpleDateFormat sf4 = new SimpleDateFormat("yyyy年MM日dd月");
			String newsDate2 = sf4.format(new Date(timepoint));
			t = newsDate2;
			return t;
	}
}
