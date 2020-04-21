package io.hashimati.microstarter.entity.micronaut.features;
/**
 * @author Ahmed Al Hashmi @hashimati
 */

import java.util.ArrayList;

public class Build {
    private ArrayList<String> plugins = new ArrayList<String>();

    public Build(ArrayList<String> plugins) {

        this.plugins = plugins;
    }

    public Build() {
    }

    public ArrayList<String> getPlugins() {
        return plugins;
    }

    public void setPlugins(ArrayList<String> plugins) {
        this.plugins = plugins;
    }

    @Override
    public String toString() {
        return "Build{" +
                "plugins=" + plugins +
                '}';
    }
}
