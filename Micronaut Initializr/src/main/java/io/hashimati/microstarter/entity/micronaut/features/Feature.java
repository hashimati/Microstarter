package io.hashimati.microstarter.entity.micronaut.features;
/**
 * @author Ahmed Al Hashmi @hashimati
 */

import java.util.ArrayList;

public class Feature {
    private ArrayList<String> dependent = new ArrayList<String>();
    private ArrayList<String> Default = new ArrayList<String>();
    private ArrayList<String> excludes = new ArrayList<String>();
    public Feature(ArrayList<String> dependent) {
        this.dependent = dependent;
    }

    public Feature() {

    }

    public ArrayList<String> getDependent() {
        return dependent;
    }

    @Override
    public String toString() {
        return "Feature{" +
                "dependent=" + dependent +
                ", Default=" + Default +
                '}';
    }

    public void setDependent(ArrayList<String> dependent) {
        this.dependent = dependent;
    }

    public ArrayList<String> getDefault() {
        return Default;
    }

    public void setDefault(ArrayList<String> aDefault) {
        Default = aDefault;
    }

    public ArrayList<String> getExcludes() {
        return excludes;
    }

    public void setExcludes(ArrayList<String> excludes) {
        this.excludes = excludes;
    }
}
