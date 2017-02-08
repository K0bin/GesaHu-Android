package rhedox.gesahuvertretungsplan.util;

/**
 * Created by robin on 07.10.2016.
 */

public final class TextUtils {
	private TextUtils() {

	}

	public static boolean isEmpty(String text){
		return text == null || text.trim().length() == 0;
	}
}
