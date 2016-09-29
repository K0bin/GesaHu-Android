package rhedox.gesahuvertretungsplan.util;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by robin on 29.09.2016.
 */

public final class Md5Util {
	private Md5Util() {

	}

	private static MessageDigest digest;

	public static String Md5(String text) {
		if(digest == null) {
			try {
				digest = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				return "";
			}
		}

		try {
			byte[] md5 = digest.digest(text.getBytes("UTF-8"));
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < md5.length; i++) {
				sb.append(Integer.toHexString((md5[i] & 0xFF) | 0x100).substring(1, 3));
			}
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}
}
