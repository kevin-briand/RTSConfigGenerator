package fr.firedream.RtsConfigGenerator.ihm;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class EcranImExControler {

    @FXML
    private TextArea txt;

    public String getText() {
        return txt.getText();
    }

    public void setText(String text) {
        this.txt.setText(text);
    }
}
