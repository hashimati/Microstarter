package io.hashimati.microstarter.entity.micronaut.profiles;
/**
 * @author Ahmed Al Hashmi @hashimati
 */

import io.hashimati.microstarter.entity.micronaut.features.Build;
import io.hashimati.microstarter.entity.micronaut.features.Dependency;

import java.util.ArrayList;
import java.util.Objects;

//@Data
//@NoArgsConstructor
//@ToString
//@EqualsAndHashCode
//@Document(collection = "profile_details")
public class ProfileDetails {


//
//    @JsonIgnoreProperties
//    private String id;

    private String description ="";

    private boolean Abstract;
    private Features features = new Features();

    private String name; //profile name
    private Build build = new Build();
    private Skeleton skeleton;
    private ArrayList<String> jvmArgs = new ArrayList<String>(),
            repositories = new ArrayList<String>();
    private ArrayList<Dependency> dependencies = new ArrayList<Dependency>();
    private ArrayList<String> requiredProfiles  = new ArrayList<String>();
    private String mainClassName;
//    private ArrayList<Exclude> excludes = new ArrayList<Exclude>();
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfileDetails that = (ProfileDetails) o;
        return isAbstract() == that.isAbstract() &&
                Objects.equals(getDescription(), that.getDescription()) &&
                Objects.equals(getFeatures(), that.getFeatures()) &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getBuild(), that.getBuild()) &&
                Objects.equals(getSkeleton(), that.getSkeleton()) &&
                Objects.equals(getJvmArgs(), that.getJvmArgs()) &&
                Objects.equals(getRepositories(), that.getRepositories()) &&
                Objects.equals(getDependencies(), that.getDependencies());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDescription(), isAbstract(), getFeatures(), getName(), getBuild(), getSkeleton(), getJvmArgs(), getRepositories(), getDependencies());
    }

    @Override
    public String toString() {
        return "ProfileDetails{" +
                "description='" + description + '\'' +
                ", Abstract=" + Abstract +
                ", features=" + features +
                ", profileName='" + name + '\'' +
                ", build=" + build +
                ", skeleton=" + skeleton +
                ", jvmArgs=" + jvmArgs +
                ", repositories=" + repositories +
                ", dependencies=" + dependencies +
                '}';
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAbstract() {
        return Abstract;
    }

    public void setAbstract(boolean anAbstract) {
        Abstract = anAbstract;
    }

    public Features getFeatures() {
        return features;
    }

    public void setFeatures(Features features) {
        this.features = features;
    }


    public Build getBuild() {
        return build;
    }

    public void setBuild(Build build) {
        this.build = build;
    }

    public Skeleton getSkeleton() {
        return skeleton;
    }

    public void setSkeleton(Skeleton skeleton) {
        this.skeleton = skeleton;
    }

    public ArrayList<String> getJvmArgs() {
        return jvmArgs;
    }

    public void setJvmArgs(ArrayList<String> jvmArgs) {
        this.jvmArgs = jvmArgs;
    }

    public ArrayList<String> getRepositories() {
        return repositories;
    }

    public void setRepositories(ArrayList<String> repositories) {
        this.repositories = repositories;
    }

    public ArrayList<Dependency> getDependencies() {
        return dependencies;
    }

    public void setDependencies(ArrayList<Dependency> dependencies) {
        this.dependencies = dependencies;
    }

    public ArrayList<String> getRequiredProfiles() {
        return requiredProfiles;
    }

    public void setRequiredProfiles(ArrayList<String> requiredProfiles) {
        this.requiredProfiles = requiredProfiles;
    }

    public String getMainClassName() {
        return mainClassName;
    }
    public String getMainClassName(String packagename) {
        return mainClassName.replace("io.micronaut.function.executor","@defaultPackage@" ).replace("@defaultPackage@", packagename);
    }

    public void setMainClassName(String mainClassName) {
        this.mainClassName = mainClassName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }

//    public ArrayList<Exclude> getExcludes() {
//        return excludes;
//    }

//    public void setExcludes(ArrayList<Exclude> excludes) {
//        this.excludes = excludes;
//    }

    //    @NoArgsConstructor
//    @Data
//    @ToString
//    @EqualsAndHashCode
    public static class Skeleton{
        private ArrayList<String> executable = new ArrayList<String>();
        private ArrayList<String> binaryExtensions = new ArrayList<String>();
        private ArrayList<String> excludes = new ArrayList<String>();
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Skeleton skeleton = (Skeleton) o;
        return Objects.equals(getExecutable(), skeleton.getExecutable()) &&
                Objects.equals(getBinaryExtensions(), skeleton.getBinaryExtensions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getExecutable(), getBinaryExtensions());
    }

    @Override
    public String toString() {
        return "Skeleton{" +
                "executable=" + executable +
                ", binaryExtensions=" + binaryExtensions +
                '}';
    }

    public ArrayList<String> getExecutable() {
        return executable;
    }

    public void setExecutable(ArrayList<String> executable) {
        this.executable = executable;
    }

    public ArrayList<String> getBinaryExtensions() {
        return binaryExtensions;
    }

    public void setBinaryExtensions(ArrayList<String> binaryExtensions) {
        this.binaryExtensions = binaryExtensions;
    }

        public ArrayList<String> getExcludes() {
            return excludes;
        }

        public void setExcludes(ArrayList<String> excludes) {
            this.excludes = excludes;
        }
    }


}

