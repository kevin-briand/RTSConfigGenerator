package fr.firedream.RtsConfigGenerator.ImportExport;

import fr.firedream.RtsConfigGenerator.Entity;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.List;

public class ImportExport {

    public ImportExport() {
    }

    public static String Export(TableView<Entity> table) {
        StringBuffer sb = new StringBuffer();
        sb.append("cover:\n");
        sb.append("    platform: rflink\n");
        sb.append("    devices:\n");

        for(Entity e : table.getItems()) {
            sb.append(String.format("        rts_%s_0:\n",e.getId()));
            sb.append(String.format("            name: %s\n",e.getName()));
        }
        return sb.toString();
    }

    public static List<Entity> Import(String data) {
        String[] dataSplitted = data.split("\n");
        List<Entity> entities = new ArrayList<>();

        String nom;
        String id = "";
        for(String ligne : dataSplitted) {
            if(ligne.contains("rts")) {
                id = ligne.split("_")[1];
            } else if(!id.isEmpty()) {
                if(ligne.contains("name")) {
                    nom = ligne.split(":")[1].trim();
                    entities.add(new Entity(nom,id));
                }
            }
        }
        return entities;
    }
}
