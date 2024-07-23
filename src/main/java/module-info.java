module zeggiotti.guitartuner {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens zeggiotti.guitartuner to javafx.fxml;
    exports zeggiotti.guitartuner;
    exports zeggiotti;
    opens zeggiotti to javafx.fxml;
}