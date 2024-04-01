module com.example.ai_gui {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.ai_gui to javafx.fxml;
    exports com.example.ai_gui;
}