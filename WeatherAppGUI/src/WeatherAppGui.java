import org.json.simple.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;

// This is the GUI of the application. It displays data.
public class WeatherAppGui extends JFrame {
    private JSONObject weatherData;

    public WeatherAppGui() {
        super("Weather App");

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setSize(450, 650);

        // Load gui at te center of the screen
        setLocationRelativeTo(null);

        // Make our layout manager null to manually position our components within the gui
        setLayout(null);

        setResizable(false);

        addGuiComponents();
    }

    private void addGuiComponents() {
        // Search field
        JTextField searchTextField = new JTextField();
        searchTextField.setBounds(15, 15, 351, 45);
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));
        add(searchTextField);

        JLabel weatherConditionImage = new JLabel(loadImage("/assets/clear.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);

        // Temperature Text
        JLabel temperatureText = new JLabel("10 °C");
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font("DIALOG", Font.BOLD, 48));
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);

        // Weather condition description
        JLabel weatherConditionDesc = new JLabel("Clear");
        weatherConditionDesc.setBounds(0, 405, 450, 36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        // Humidity image
        JLabel humidityImage = new JLabel(loadImage("/assets/humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);

        // Humidity text
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 0%</html>");
        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("DIALOG", Font.PLAIN, 16));
        add(humidityText);

        // Windspeed image
        JLabel windspeedImage = new JLabel(loadImage("/assets/windspeed.png"));
        windspeedImage.setBounds(220, 500, 74, 66);
        add(windspeedImage);

        // Windspeed text
        JLabel windspeedText = new JLabel("<html><b>Windspeed</b><br>15 km/h</html>");
        windspeedText.setBounds(310, 500, 150, 60);
        windspeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windspeedText);

        // Search button
        JButton searchButton = new JButton(loadImage("/assets/search.png"));
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375, 13, 47, 45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get location from user
                String userInput = searchTextField.getText();

                // Validate input - remove whitespace to ensure non-empty text
                if (userInput.replaceAll("\\s", "").isEmpty()) {
                    return;
                }

                // Retrieve weather data
                weatherData = WeatherApp.getWeatherData(userInput);

                // Update gui

                // Update weather image
                String weatherCondition = (String) weatherData.get("weather_condition");
                weatherConditionImage.setIcon(loadImage("/assets/" + weatherCondition + ".png"));

                // Update fields
                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + "°C");

                weatherConditionDesc.setText(weatherCondition.substring(0, 1).toUpperCase() + weatherCondition.substring(1));

                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");

                double windspeed = (double) weatherData.get("windspeed");
                windspeedText.setText("<html><b>Windspeed</b><br>" + windspeed + " km/h</html>");

            }
        });
        add(searchButton);
    }

    // Used to create images in our gui components
    private ImageIcon loadImage(String resourcePath) {
        try {
            BufferedImage image = ImageIO.read(getClass().getResourceAsStream(resourcePath));

            return new ImageIcon(image);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Could not find resource");

        return null;
    }
}
