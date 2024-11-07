package com.intissar.olimpiadas.language;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Clase que se encarga de manejar los idiomas
 */
public class LanguageManager {
    private static LanguageManager instance;
    private Locale locale;
    private ResourceBundle bundle;

    /**
     * Constructor de la clase que carga el bundle
     *
     * @param language Código del idioma (por ejemplo, "es", "en", "eu")
     */
    private LanguageManager(String language) {
        this.locale = new Locale(language);
        loadResourceBundle();
    }

    /**
     * Crea una instancia de LanguageManager y la devuelve
     *
     * @param language Código del idioma (por ejemplo, "es", "en", "eu")
     * @return instancia de LanguageManager
     */
    public static LanguageManager getInstance(String language) {
        if (instance == null) {
            instance = new LanguageManager(language);
        }
        return instance;
    }

    /**
     * Función que carga el bundle
     */
    private void loadResourceBundle() {
        bundle = ResourceBundle.getBundle("languages/" + locale.getLanguage());
    }

    /**
     * Setter de locale
     *
     * @param locale nuevo
     */
    public void setLocale(Locale locale) {
        this.locale = locale;
        loadResourceBundle();
    }

    /**
     * Getter de bundle
     *
     * @return bundle
     */
    public ResourceBundle getBundle() {
        return bundle;
    }

    /**
     * Getter de locale
     *
     * @return locale
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Método para obtener un texto del ResourceBundle
     *
     * @param key Clave del texto a obtener
     * @return Texto correspondiente a la clave
     */
    public String getText(String key) {
        return bundle.getString(key);
    }
}