package rhedox.gesahuvertretungsplan.util;

/**
 * Created by Robin on 23.07.2015.
 */
public class TextUtils {

    public static boolean isEmpty(String string) {
        return android.text.TextUtils.isEmpty(string) || android.text.TextUtils.getTrimmedLength(string) == 0;
    }

}
