package net.mcshockwave.MCS.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MiscUtils {
	
	public static final String DEFAULT_FORMAT = "MMM d, yyyy hh:mm:ss a zz";
	
	public static String getTimeInFormat(long time, String format) {
		DateFormat form = new SimpleDateFormat(format);
		return form.format(new Date(time));
	}
	
	public static String getTime(long time) {
		return getTimeInFormat(time, DEFAULT_FORMAT);
	}
	
	public static String getTimeInFormat(String format) {
		return getTimeInFormat(System.currentTimeMillis(), format);
	}
	
	public static String getTime() {
		return getTime(System.currentTimeMillis());
	}

}
