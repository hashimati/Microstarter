package io.hashimati.microstarter.entity.micronaut;

/**
 * @author Ahmed Al Hashmi @hashimati
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashMap;



/**
 * This collection will store the basic skeleton of features of all profiles.
 *  collectionName = "features_skeletons"
 */

@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
@Data
public class FeaturesSkeleton
{

//    @JsonIgnoreProperties
//    private String id;

    private String name;
    private String profile;
    private HashMap<String, HashMap<String, String>> skeleton= new HashMap<String, HashMap<String, String>>();
    // private HashMap<String, HashMap<String, String>> templates = new HashMap<String, HashMap<String, String>>();
    private HashMap<String, String> rootFiles = new HashMap<String, String>();

//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getProfile() {
//        return profile;
//    }
//
//    public void setProfile(String profile) {
//        this.profile = profile;
//    }
//
//    public HashMap<String, HashMap<String, String>> getSkeleton() {
//        return skeleton;
//    }
//
//    public void setSkeleton(HashMap<String, HashMap<String, String>> skeleton) {
//        this.skeleton = skeleton;
//    }
//
//    public HashMap<String, String> getRootFiles() {
//        return rootFiles;
//    }
//
//    public void setRootFiles(HashMap<String, String> rootFiles) {
//        this.rootFiles = rootFiles;
//    }
//
//    @Override
//    public String toString() {
//        return "FeaturesSkeleton{" +
//                "name='" + name + '\'' +
//                ", profile='" + profile + '\'' +
//                ", skeleton=" + skeleton +
//                ", rootFiles=" + rootFiles +
//                '}';
//    }
//
//    public FeaturesSkeleton() {
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        FeaturesSkeleton that = (FeaturesSkeleton) o;
//        return Objects.equals(getName(), that.getName()) &&
//                Objects.equals(getProfile(), that.getProfile()) &&
//                Objects.equals(getSkeleton(), that.getSkeleton()) &&
//                Objects.equals(getRootFiles(), that.getRootFiles());
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(getName(), getProfile(), getSkeleton(), getRootFiles());
//    }
//
////    public String getId() {
////        return id;
////    }
////
////    public void setId(String id) {
////        this.id = id;
////    }
}
