package fr.firedream.RtsConfigGenerator.ihm;

import fr.firedream.RtsConfigGenerator.Console.ConsoleUsb;
import fr.firedream.RtsConfigGenerator.Entity;
import fr.firedream.RtsConfigGenerator.RtsConfigGenerator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.io.IOException;
import java.util.List;

public class EcranControler {

    private static EcranControler instance;
    private static RtsConfigGenerator rcg;
    @FXML
    Button btnConnexion;
    @FXML
    Button btnAdd;
    @FXML
    Button btnExport;

    @FXML
    ComboBox<Object> cbbPort;

    @FXML
    Label lblInfo;

    @FXML
    TableView<Entity> tblEntity;
    @FXML
    TableColumn<Entity,String> nom;
    @FXML
    TableColumn<Entity,String> id;
    @FXML
    TableColumn<Entity,Button> test;
    @FXML
    TableColumn<Entity,Button> multiProg;
    @FXML
    TableColumn<Entity,Button> remove;

    public EcranControler() {
        instance = this;
        rcg = RtsConfigGenerator.getInstance();
    }

    public static EcranControler getInstance() {
        return instance;
    }

    @FXML
    public void onConnexion() {
        if(cbbPort.getValue() == null)
            return;
        btnConnexion.setDisable(true);
        System.out.println(cbbPort.getValue().toString());
        rcg.openConsole(cbbPort.getValue().toString());
    }

    @FXML
    public void onExport() throws IOException {
        rcg.openExport(tblEntity);
    }

    @FXML
    public void addingNewShutter() {
        setDisableBtnAction(true);
        rcg.add();
    }

    @FXML
    public void setPort() {
        String currentValue = "";
        if(cbbPort.getValue() != null) {
            currentValue = cbbPort.getValue().toString();
        }

        List<String> ports = ConsoleUsb.getAvailablePorts();
        cbbPort.getItems().remove(0,cbbPort.getItems().size());
        cbbPort.getItems().addAll(ports);
        if(!currentValue.isEmpty())
            cbbPort.setValue(currentValue);
        else
            cbbPort.setValue(cbbPort.getItems().get(0));
    }

    public void updateState(String state) {
        lblInfo.setText(state);
    }

    public String getState() {
        return lblInfo.getText();
    }
    public void setTable() {
        nom.setCellValueFactory(new PropertyValueFactory<>("name"));
        id.setCellValueFactory(new PropertyValueFactory<>("id"));

        nom.setCellFactory(TextFieldTableCell.forTableColumn());
        nom.setOnEditCommit(
                (TableColumn.CellEditEvent<Entity, String> t) -> {
                    t.getTableView().getItems().get(t.getTablePosition().getRow()).setName(t.getNewValue());
                });

        tblEntity.setEditable(true);
        nom.setEditable(true);


        test.setCellFactory(ActionButtonTableCell.forTableColumn("Tester", (Entity e) -> {
            rcg.testing(e.getId());
            return e;
        }));
        multiProg.setCellFactory(ActionButtonTableCell.forTableColumn("Ajouter", (Entity e) -> {
            rcg.addMultiProg(e.getId());
            return e;
        }));
        remove.setCellFactory(ActionButtonTableCell.forTableColumn("Supprimer", (Entity e) -> {
            tblEntity.getItems().remove(e);
            return e;
        }));
    }

    public void updateTable(Entity entity) {
        if(tblEntity.getItems().size() == 0)
            setTable();
        tblEntity.getItems().add(entity);
        setDisableBtnAction(false);
    }

    public void setTableRows(List<Entity> entities) {
        tblEntity.getItems().remove(0,tblEntity.getItems().size());

        for(Entity entity : entities) {
            updateTable(entity);
        }
    }

    public void setDisableBtnAction(boolean b) {
        btnAdd.setDisable(b);
        System.out.println(tblEntity.getItems().size());
        for(int i=0; i<tblEntity.getItems().size(); i++) { //hide column
            Platform.runLater(() -> test.setVisible(b ? false : true));
            Platform.runLater(() -> multiProg.setVisible(b ? false : true));
        }
    }
}
