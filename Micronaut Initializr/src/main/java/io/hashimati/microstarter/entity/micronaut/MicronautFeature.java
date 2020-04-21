package io.hashimati.microstarter.entity.micronaut;
/**
 * @author Ahmed Al Hashmi @hashimati
 */


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.hashimati.microstarter.entity.micronaut.features.Build;
import io.hashimati.microstarter.entity.micronaut.features.Dependency;
import io.hashimati.microstarter.entity.micronaut.features.Feature;
import io.hashimati.microstarter.entity.micronaut.features.Java;
import io.hashimati.microstarter.entity.micronaut.profiles.ProfileDetails;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;


import java.util.ArrayList;


/**
 * This class will be used for following:
 * populating the GUIs.
 * searching for micronautFeatures dependencies.
 *
 * collectionName = "micronaut_features"
 */

@NoArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class MicronautFeature
{
//    @JsonIgnoreProperties
//    private String id;

    private String name;

    @JsonIgnoreProperties
    private String profile;
    @JsonIgnoreProperties
    private String description;
    @JsonIgnoreProperties
    private Feature features;
    @JsonIgnoreProperties
    private Java java;
    @JsonIgnoreProperties
    private Build build;
    @JsonIgnoreProperties
    private ArrayList<Dependency> dependencies = new ArrayList<Dependency>();
    private ArrayList<String> excludes = new ArrayList<String>();
    private String mainClassName;
    @JsonIgnoreProperties
    private ProfileDetails.Skeleton skeleton;
    @JsonIgnoreProperties
    private String displayName;
    @JsonIgnoreProperties
    private ArrayList<String> jvmArgs;


//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//
//
//
//    public Java getJava() {
//        return java;
//    }
//
//    public void setJava(Java java) {
//        this.java = java;
//    }
//
//    public ArrayList<Dependency> getDependencies() {
//        return dependencies;
//    }
//
//    public void setDependencies(ArrayList<Dependency> dependencies) {
//        this.dependencies = dependencies;
//    }
//
//    @Override
//    public String toString() {
//        return "MicronautFeature{" +
//                "name='" + name + '\'' +
//                ", description='" + description + '\'' +
//                ", features=" + features +
//                ", java=" + java +
//                ", build=" + build +
//                ", dependencies=" + dependencies +
//                ", jvmArgs=" + jvmArgs +
//                '}';
//    }
//
//    public Build getBuild() {
//        return build;
//    }
//
//    public void setBuild(Build build) {
//        this.build = build;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public Feature getFeatures() {
//        return features;
//    }
//
//    public void setFeatures(Feature features) {
//        this.features = features;
//    }
//
//
//    public ArrayList<String> getJvmArgs() {
//        return jvmArgs;
//    }
//
//    public void setJvmArgs(ArrayList<String> jvmArgs) {
//        this.jvmArgs = jvmArgs;
//    }
//
//    public String getDisplayName() {
//        return displayName;
//    }
//
//    public void setDisplayName(String displayName) {
//        this.displayName = displayName;
//    }
//
//
//    public void setProfile(String profile) {
//        this.profile = profile;
//    }
//
//    public String getProfile(){return this.profile; }
//
//    public ProfileDetails.Skeleton getSkeleton() {
//        return skeleton;
//    }
//
//    public void setSkeleton(ProfileDetails.Skeleton skeleton) {
//        this.skeleton = skeleton;
//    }
//
//    public ArrayList<String> getExcludes() {
//        return excludes;
//    }
//
//    public void setExcludes(ArrayList<String> excludes) {
//        this.excludes = excludes;
//    }
//
//
//
//    public String getMainClassName() {
//        return mainClassName;
//    }
//
//    public void setMainClassName(String mainClassName) {
//        this.mainClassName = mainClassName;
//    }

//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
}
