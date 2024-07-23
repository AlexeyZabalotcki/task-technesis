package com.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainApp extends Application {
    private RestTemplate restTemplate = new RestTemplate();
    private ListView<Float> temperatureList = new ListView<>();
    private TextField temperatureField = new TextField();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        VBox root = new VBox();
        Button getButton = new Button("Get Temperatures");
        getButton.setOnAction(e -> getTemperatures());

        Button setButton = new Button("Set Temperature");
        setButton.setOnAction(e -> setTemperature());

        root.getChildren().addAll(temperatureList, temperatureField, setButton, getButton);

        Scene scene = new Scene(root, 400, 300);
        stage.setScene(scene);
        stage.setTitle("Temperature Regulator");
        stage.show();
    }

    private void getTemperatures() {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();

        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);
        List<Float> temperatures = restTemplate.getForObject("http://localhost:8080/api/temperature/recent?offset=0&count=10", List.class);
        temperatureList.getItems().setAll(temperatures);
    }

    private void setTemperature() {
        float temperature = Float.parseFloat(temperatureField.getText());
        String response = restTemplate.postForObject("http://localhost:8080/api/temperature/set?temperature=" + temperature, null, String.class);
        Alert alert = new Alert(Alert.AlertType.INFORMATION, response);
        alert.show();
        if (temperature > 1000 || temperature < -200) {
            showAlert();
        }
    }

    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING, "Critical temperature reached!");
        alert.show();
    }
}
