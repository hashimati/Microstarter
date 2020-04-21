package io.hashimati.microstarter.entity;
/**
 * @author Ahmed Al Hashmi @hashimati
 */

import java.util.ArrayList;



public class VariablesSection {

    private String name;

    private ArrayList<EntityAttribute> variableList = new ArrayList<EntityAttribute>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<EntityAttribute> getVariableList() {
        return variableList;
    }

    public void setVariableList(ArrayList<EntityAttribute> variableList) {
        this.variableList = variableList;
    }
}
