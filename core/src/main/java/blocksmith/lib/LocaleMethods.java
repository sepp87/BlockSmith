package blocksmith.lib;

import blocksmith.infra.blockloader.annotations.Block;
import java.util.Locale;

/**
 *
 * @author joost
 */
public class LocaleMethods {

    @Block(
            type = "Locale.fromLanguage",
            category = "Core",
            description = "Obtains a locale from a language code. This method normalizes the language value to lowercase.")
    public static Locale fromLanguage(String language) {
        return Locale.of(language);
    }

    @Block(
            type = "Locale.fromLanguageAndCountry",
            category = "Core",
            description = "Obtains a locale from language and country. This method normalizes the language value to lowercase and the country value to uppercase.")
    public static Locale fromLanguageAndCountry(String language, String country) {
        return Locale.of(language, country);
    }

    @Block(
            type = "Locale.fromLanguageCountryAndVariant",
            category = "Core",
            description = "Obtains a locale from language, country and variant. This method normalizes the language value to lowercase and the country value to uppercase.")
    public static Locale fromLanguageCountryAndVariant(String language, String country, String variant) {
        return Locale.of(language, country, variant);
    }

}
