package rhedox.gesahuvertretungsplan.util;

/**
 * Created by Robin on 23.07.2015.
 */
public final class TextUtils {
	private TextUtils() {}

    public static boolean isEmpty(String string) {
        //return android.text.TextUtils.isEmpty(string) || android.text.TextUtils.getTrimmedLength(string) == 0;

        return string == null || string.trim().length() == 0;
    }

}
