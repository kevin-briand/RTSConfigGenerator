module fr.firedream.RtsConfigGenerator {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fazecast.jSerialComm;


    opens fr.firedream.RtsConfigGenerator to javafx.fxml;
    exports fr.firedream.RtsConfigGenerator;
    exports fr.firedream.RtsConfigGenerator.Console;
    opens fr.firedream.RtsConfigGenerator.Console to javafx.fxml;
    exports fr.firedream.RtsConfigGenerator.ihm;
    opens fr.firedream.RtsConfigGenerator.ihm to javafx.fxml;
}