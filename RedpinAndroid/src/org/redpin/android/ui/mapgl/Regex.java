package org.redpin.android.ui.mapgl;

import java.util.regex.* ;

/**
 * Make a control on datahours put in arguments
 * @author Davy Ushaka Ishimwe
 *
 */
public class Regex {
	static Pattern regex_de_comparaison = Pattern.compile ("^[0-9]{2}:[0-9]{2}$",Pattern.UNIX_LINES); 
	private static Matcher matcher;

	public static int testregex(String compare) {		
		matcher = regex_de_comparaison.matcher(compare);
		if (matcher.find()) {
			return 1;
		}else{
			System.out.print("der");
			return 0;
		}
	}
}

