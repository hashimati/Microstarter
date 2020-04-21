package io.hashimati.microstarter.entity.micronaut;


/**
 * @author Ahmed Al Hashmi @hashimati
 */

import io.hashimati.microstarter.entity.Entity;
import io.hashimati.microstarter.entity.EntityRelation;

import java.util.ArrayList;

/*
{
	"profile":"service",
	"group":"com.ahmed",
	"artifact":"test",
	"language":"java",
	"javaVersion":"8",
	"dependencies":["mongo-reactive"],
	"build":"gradle",
	"version":"1.0.1"



}
 */
public class ProjectRequest {
    private String group, artifact,
    version, build, language, viewFramework,
    javaVersion, profile, testframework;
    private ArrayList<String> dependencies = new ArrayList<String>();
    private String functionName;
    private ArrayList<ComponentObject> componentObjects = new ArrayList<ComponentObject>();

    private String databaseName, databaseType;
    private ArrayList<Entity> entities = new ArrayList<Entity>();
    private ArrayList<EntityRelation> entityRelations = new ArrayList<EntityRelation>();

    private int port = -1;
    // to determine to include build(Maven/gradle) wrapper files.
    private boolean requiredBuildWrapper = false;


    public ProjectRequest(String group, String artifact) {
        this.group = group;
        this.artifact = artifact;
    }

    public ProjectRequest() {
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getArtifact() {
        return artifact;
    }

    public void setArtifact(String artifact) {
        this.artifact = artifact;
    }

    public ArrayList<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(ArrayList<String> dependencies) {
        this.dependencies = dependencies;
    }


    public String getDefaultPackage(){return getPackage();}

    @Override
    public String toString() {
        return "ProjectRequest{" +
                "group='" + group + '\'' +
                ", artifact='" + artifact + '\'' +
                ", version='" + version + '\'' +
                ", build='" + build + '\'' +
                ", language='" + language + '\'' +
                ", viewFramework='" + viewFramework + '\'' +
                ", javaVersion='" + javaVersion + '\'' +
                ", profile='" + profile + '\'' +
                ", testframework='" + testframework + '\'' +
                ", dependencies=" + dependencies +
                ", functionName='" + functionName + '\'' +
                ", componentObjects=" + componentObjects +
                ", databaseName='" + databaseName + '\'' +
                ", databaseType='" + databaseType + '\'' +
                ", entities=" + entities +
                ", entityRelations=" + entityRelations +
                ", port=" + port +
                ", requiredBuildWrapper=" + requiredBuildWrapper +
                '}';
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public String getLanguage() {
        return language.toLowerCase();
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getPackage() {
        return getGroup()+"." + getArtifact().toLowerCase();

    }

    public String getTestframework() {
        return testframework;
    }

    public void setTestframework(String testframework) {
        this.testframework = testframework;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public ArrayList<ComponentObject> getCompontentObjects() {
        return componentObjects;
    }

    public void setComponentObjects(ArrayList<ComponentObject> componentObjects) {
        this.componentObjects = componentObjects;
    }

    public boolean isRequiredBuildWrapper() {
        return requiredBuildWrapper;
    }

    public void setRequiredBuildWrapper(boolean requiredBuildWrapper) {
        this.requiredBuildWrapper = requiredBuildWrapper;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getViewFramework() {
        return viewFramework;
    }

    public void setViewFramework(String viewFramework) {
        this.viewFramework = viewFramework;
    }



    public ArrayList<EntityRelation> getEntityRelations() {
        return entityRelations;
    }

    public void setEntityRelations(ArrayList<EntityRelation> entityRelations) {
        this.entityRelations = entityRelations;
    }

    public ArrayList<Entity> getEntities() {
        return entities;
    }

    public void setEntities(ArrayList<Entity> entities) {
        this.entities = entities;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }
}
