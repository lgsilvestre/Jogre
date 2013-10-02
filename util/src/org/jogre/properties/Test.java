package org.jogre.properties;

import com.google.api.translate.Language;
import com.google.api.translate.Translate;

public class Test {
	
	public static void main (String[] args) {
		try {
			String translatedText = Translate.translate("Salut le monde", Language.SPANISH, Language.ENGLISH);
			System.out.println(translatedText);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}