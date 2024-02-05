import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

// BackEnd of the application
// Retrieve datas from API
// It will fetch the latest weather
public class WeatherApp {
    // Fetch weather data for given location
    public static JSONObject getWeatherData(String locationName) {
        // Get location coordinates using geolocation API
        JSONArray locationData = getLocationData(locationName);

        return null;
    }

    // Retrieves geolocation coordinates for give location name
    public static JSONArray getLocationData(String locationName) {
        // Replace any whitespace in location name  to + to adhere to API's request format
        locationName = locationName.replaceAll(" ", "+");

        // Build API URL with location parameter
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" + locationName +
                "&count=10&language=en&format=json";

        try {
            // API call
            HttpURLConnection conn = fetchApiResponse(urlString);

            if (conn.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to API");
                return null;
            } else {
                // Store the API results
                // Manipulate String
                StringBuilder resultJson = new StringBuilder();

                // Scanner read the JSON data that is returned from our API call
                Scanner scanner = new Scanner(conn.getInputStream());

                // Read and store datas into the stringBuilder
                while (scanner.hasNext()) {
                    resultJson.append(scanner.nextLine());
                }

                scanner.close();
                conn.disconnect();

                // Parse the JSON String into a JSON Object
                JSONParser parser = new JSONParser();

                // Convertis l'objet resultJson en une chaîne de caractères
                // Puis l'analyse pour la transformer en JSONObject pour le manipuler plus facilement
                JSONObject resultsJsonObject = (JSONObject) parser.parse(String.valueOf(resultJson));

                // Get the list of location data the API generated from the location name
                JSONArray locationData = (JSONArray) resultsJsonObject.get("results");
                return locationData;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            // Attempt to create connection
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.connect();
            return conn;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
