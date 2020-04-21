package io.hashimati.microstarter.services;
/**
 * @author Ahmed Al Hashmi @hashimati
 */

public class FeatureReader {
    private String buildPlugins="",
            dependencies ="",buildDependencies = "";


    public FeatureReader(){}

    public FeatureReader(String buildPlugins, String dependencies, String buildDependencies) {
        this.buildPlugins = buildPlugins;
        this.dependencies = dependencies;
        this.buildDependencies = buildDependencies;
    }
    public FeatureReader(String dependencies)
    {
        this.dependencies = dependencies;
    }

    public String getBuildDependencies() {
        return buildDependencies;
    }

    public void setBuildDependencies(String buildDependencies) {
        this.buildDependencies = buildDependencies;
    }

    public String getDependencies() {
        return dependencies;
    }

    public void setDependencies(String dependencies) {
        this.dependencies = dependencies;
    }

    public String getBuildPlugins() {
        return buildPlugins;
    }

    public void setBuildPlugins(String buildPlugins) {
        this.buildPlugins = buildPlugins;
    }

    @Override
    public String toString() {
        return "FeatureReader{" +
                "buildPlugins='" + buildPlugins + '\'' +
                ", dependencies='" + dependencies + '\'' +
                ", buildDependencies='" + buildDependencies + '\'' +
                '}';
    }
}