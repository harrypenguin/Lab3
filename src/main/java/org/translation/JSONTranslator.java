package org.translation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * An implementation of the Translator interface which reads in the translation
 * data from a JSON file. The data is read in once each time an instance of this class is constructed.
 */
public class JSONTranslator implements Translator {

    private Map<String, Map<String, String>> data;
    // Mapping the 3-letter country code to a Map of the language code
    // and its corresponding translated name.
    private List<String> countries;
    // A list of 3-letter country codes

    /**
     * Constructs a JSONTranslator using data from the sample.json resources file.
     */
    public JSONTranslator() {
        this("sample.json");
    }

    /**
     * Constructs a JSONTranslator populated using data from the specified resources file.
     * @param filename the name of the file in resources to load the data from
     * @throws RuntimeException if the resource file can't be loaded properly
     */
    public JSONTranslator(String filename) {
        data = new HashMap<>();
        countries = new ArrayList<>();

        try {

            String jsonString = Files.readString(Paths.get(getClass().getClassLoader().getResource(filename).toURI()));
            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject country = jsonArray.getJSONObject(i);
                String countryName = country.getString("alpha3");

                countries.add(countryName);
                Map<String, String> countryNamesTranslated = new HashMap<>();
                for (String name : country.keySet()) {
                    if (!("id".equals(name) || "alpha2".equals(name) || "alpha3".equals(name))) {
                        countryNamesTranslated.put(name, country.getString(name));
                    }
                }
                data.put(countryName, countryNamesTranslated);
            }

        }
        catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<String> getCountryLanguages(String country) {
        String lcountry = country.toLowerCase();
        return new ArrayList<>(data.get(lcountry).keySet());
    }

    @Override
    public List<String> getCountries() {
        return countries;
    }

    @Override
    public String translate(String country, String language) {
        return data.get(country).get(language);
    }
}
