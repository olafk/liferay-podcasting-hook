package de.olafkock.liferay.blogs;

/**
 * This class contains code that is copied to the overloaded jsp contained in this
 * plugin for the sole purpose of being available for the accompanying unit test.
 * 
 * It feels more comfortable developing this with a test in place, but the classloading
 * issues of a jsp hook (ending up in Liferay's classloader) and plain java code
 * (ending up in the plugin) lead to this style, manually copying this code over to the
 * jsp after the tests turned green.
 * 
 * It's a stupid hack, but also a nice workaround around classloading issues that would
 * overly complicate this plugin. LPS-33455 will eliminate the necessity of this code
 * in version 6.2
 * 
 * @author Olaf Kock
 *
 */

public class PseudoCodeForTest {
	
	private static final String token = "<div class=\"aui-field aui-field-wrapper\">";
	
	public static String cleanupCustomFields(String markup) {
		String[] tokens = markup.trim().split(token);
		String result = "";
		System.out.println(tokens.length);
		// element 0 is an outer <div> element, ignore here
		// last element contains closing </div> for outer element: Eliminate
		int last = tokens.length-1;
		tokens[last] = tokens[last].substring(0, tokens[last].length()-"</div>".length());

		for(int i = 1; i < tokens.length; i++) {
			if(tokens[i].contains("enclosure-type") || tokens[i].contains("enclosure-length") || tokens[i].contains("enclosure-url") || tokens[i].contains("itunes-duration") ) {
				continue;
			}
			result += token + tokens[i];
		}
		if(result.length() > 0) {
			result = "<div class=\"taglib-custom-attributes-list\">" + result + "</div>";
		}
		return result;
	}

}
