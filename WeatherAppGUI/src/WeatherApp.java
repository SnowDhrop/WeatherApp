import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

// BackEnd of the application
// Retrieve datas from API
// It will fetch the latest weather
public class WeatherApp {
    // Fetch weather data for given location
    public static JSONObject getWeatherData(String locationName) {
        // Get location coordinates using geolocation API
        JSONArray locationData = getLocationData(locationName);

        // Extract latitude and longitude datas
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        // Build API request URL with location coordinates
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude=" + longitude +
                "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=Europe%2FBerlin";

        try {
            // Call API and get response
            HttpURLConnection conn = fetchApiResponse(urlString);

            if (conn.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to API");
                return null;
            } else {
                // Store resulting json data
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());

                while (scanner.hasNext()) {
                    resultJson.append(scanner.nextLine());
                }

                scanner.close();
                conn.disconnect();

                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObject = (JSONObject) parser.parse(String.valueOf(resultJson));

                JSONObject hourly = (JSONObject) resultsJsonObject.get("hourly");

                // We want to get the current hour data
                // So we need to get the index of our current hour
                JSONArray time = (JSONArray) hourly.get("time");
                int index = findIndexOfCurrentTime(time);

                // Get temperature
                JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
                float temperature = (float) temperatureData.get(index);

                // Get weather code
                JSONArray weatherCodeData = (JSONArray) hourly.get("weathercode");
                String weatherCondition = convertWeatherCode((long) weatherCodeData.get(index));

                // Get humidity
                JSONArray relativeHumidity = (JSONArray) hourly.get("relativehumidity_2m");
                long humidity = (long) relativeHumidity.get(index);

                // Get windspeed
                JSONArray windSpeed = (JSONArray) hourly.get("windspeed_10m");
                float windspeed = (float) windSpeed.get(index);

                // Build the weather json data object that we are going to access in our frontend
                JSONObject weatherData = new JSONObject();
                weatherData.put("temperature", temperature);
                weatherData.put("weather_condition", weatherCondition);
                weatherData.put("relativeHumidity", humidity);
                weatherData.put("windspeed", windspeed);

                return weatherData;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private static int findIndexOfCurrentTime(JSONArray timeList) {
        String currentTime = getCurrentTime();

        // Iterate though the time list and see which one matches our current time
        for (int i = 0; i < timeList.size(); i++) {
            String time = (String) timeList.get(i);

            if (time.equalsIgnoreCase(currentTime)) {
                // return the index
                return i;
            }
        }

        return 0;
    }

    private static String getCurrentTime() {
        // Get current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Format date needs to be 2023-09-02T00:00 (this is how it is read in the API)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        // Format and print the current date and time
        String formattedDateTime = currentDateTime.format(formatter);

        return formattedDateTime;
    }

    // Convert the weather code to something more readable
    private static String convertWeatherCode(long weatherCode) {
        String weatherCondition = "";

        if (weatherCode == 0L) {
            weatherCondition = "Clear";
        } else if (weatherCode <= 3L) {
            weatherCondition = "Cloudy";
        } else if (weatherCode <= 67L) {
            weatherCondition = "Rain";
        } else if (weatherCode <= 77L) {
            weatherCondition = "Snow";
        } else {
            weatherCondition = "Rain";
        }

        return weatherCondition;
    }
}
