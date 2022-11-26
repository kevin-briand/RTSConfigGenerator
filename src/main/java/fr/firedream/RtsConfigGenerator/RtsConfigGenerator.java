package fr.firedream.RtsConfigGenerator;

import fr.firedream.RtsConfigGenerator.Console.ConsoleUsb;
import fr.firedream.RtsConfigGenerator.ImportExport.ImportExport;
import fr.firedream.RtsConfigGenerator.ihm.EcranControler;
import fr.firedream.RtsConfigGenerator.ihm.EcranImExControler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.List;

public class RtsConfigGenerator extends Application {

    private final String newShutter =  "10;rts;010;0;PROG;";
    private final String addShutter =  "10;rts;%s;0;MULTIPROG;";
    private final String testShutter = "10;rts;%s;0;DOWN;";

    private ConsoleUsb cu;
    private static RtsConfigGenerator instance;
    private EcranControler ec;
    private int nextId = 0;
    private String lastCommand;
    private static Stage stage;

    public RtsConfigGenerator() {
        if(instance == null)
            instance = this;
    }

    //fenêtre principale
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RtsConfigGenerator.class.getResource("Ecran.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Rts Config Generator");
        stage.setScene(scene);
        stage.show();

        ec = fxmlLoader.getController();
        RtsConfigGenerator.stage = stage;
        ec.setPort();
    }

    //fenêtre Import/Export
    public void openExport(TableView<Entity> table) throws IOException {
        String importStr = ImportExport.Export(table);
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("EcranImEx.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        Stage stage = new Stage();
        stage.setTitle("Import/Export");
        stage.setScene(scene);

        stage.setOnCloseRequest(windowEvent -> importData(fxmlLoader.getController()));

        stage.show();

        EcranImExControler IEControler = fxmlLoader.getController();
        Platform.runLater(() -> IEControler.setText(importStr));
    }

    //Création tableau suivant import
    public void importData(EcranImExControler eic) {
        List<Entity> entities = ImportExport.Import(eic.getText());
        ec.setTableRows(entities);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if(cu != null)
            cu.close();
    }

    public static RtsConfigGenerator getInstance() {
        return instance;
    }

    //Connexion à l'arduino
    public void openConsole(String port) {
        System.out.println("Run");
        cu = new ConsoleUsb(port, 56700) {
            @Override
            public void receiptCommand(String command) {
                receiptCommandFromConsole(command);
            }
        };
        cu.start();
    }

    //Reception données de l'arduino
    public void receiptCommandFromConsole(String command) {
        String[] cmdSplitted = command.split(";");
        String status = "";

        if(command.contains("Somfy RTS link")) { //GET NEXT ID
            String[] getStrId = cmdSplitted[cmdSplitted.length - 1].split(" ");
            nextId = Integer.parseInt(getStrId[getStrId.length-1]);
            System.out.println("Next ID is " + nextId);
            status = "ID";
        } else {
            if(!lastCommand.contains("MULTIPROG")) {
                for (String cmd : cmdSplitted) {
                    if (cmd.contains("ID")) {
                        String id = cmd.split("=")[1];
                        ec.updateTable(new Entity("Nouveau Volet", id));
                        status = "";
                        break;
                    } else if (cmd.contains("OK")) {
                        status = "OK";
                        break;
                    } else {
                        status = "ERROR";
                    }
                }
            }
        }
        String finalStatus = status;
        Platform.runLater(() -> updateState(finalStatus));
    }

    //Affichage Info
    public void updateState(String command) {
        String state = command;
        if(lastCommand != null) {
            if (command.contains("OK"))
                state = ec.getState() + " OK";
            else if (command.contains("ERROR"))
                state = ec.getState() + " ERREUR";
            else
                state = command;
        } else if(command.contains("ID")) {
            state = "Next ID = " + nextId;
            ec.setDisableBtnAction(false);
        }

        ec.updateState(state);
    }

    public void testing(String id) {
        cu.sendCommand(testShutter.formatted(id));
        lastCommand = testShutter.formatted(id);
        ec.updateState("Fermeture volet " + id);
    }

    public void add() {
        cu.sendCommand(newShutter);
        lastCommand = newShutter;
        ec.updateState("Ajout volet " + nextId);
        nextId++;
    }

    public void addMultiProg(String id) {
        cu.sendCommand(addShutter.formatted(id));
        lastCommand = addShutter.formatted(id);
        ec.updateState("Ajout volet " + id);
    }

    public static void main(final String[] args) throws Exception {
        launch();
    }
}

