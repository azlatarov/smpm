package custom.tools;

import sun.text.normalizer.NormalizerBase;


public class jvuDiacriticsRemover {
	
	/**
	 * Removes all diacritics (i.e. diacritical marks) from text.
	 * @info::: http://en.wikipedia.org/wiki/Diacritic
	 * @author Atanas P. Zlatarov
	 */
	public static String removeAll(String in) {
		return NormalizerBase.decompose("ËÈ‡Ásqjgm", false, 0).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}
	
}
