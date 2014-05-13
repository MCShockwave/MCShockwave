package net.mcshockwave.MCS.Utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MiscUtils {

	public static final String	DEFAULT_FORMAT	= "MMM d, yyyy hh:mm:ss a zz";

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

	public static double getRoundedNumber(double num, int places) {
		double pla = Math.pow(10, places);

		num = Math.round(num * pla);
		num /= pla;

		return num;
	}

	public static String[] readTextFile(String link) {
		ArrayList<String> ret = new ArrayList<>();

		try {
			URL url = new URL("[url]" + link + "[/url]");
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				ret.add(inputLine);
			}
			in.close();
		} catch (Exception e) {
		}

		return ret.toArray(new String[0]);
	}
}
