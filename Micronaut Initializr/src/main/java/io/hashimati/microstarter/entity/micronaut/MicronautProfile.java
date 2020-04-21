package io.hashimati.microstarter.entity.micronaut;

/**
 * @author Ahmed Al Hashmi @hashimati
 */

/**
 * collectionName = "micronaut_profiles"
 *
 */

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashMap;

@NoArgsConstructor
@Data
@ToString
@EqualsAndHashCode
public class MicronautProfile
{
//    @JsonIgnoreProperties
//    private String id;
    private String name;
    private HashMap<String, HashMap<String, String>> skeleton= new HashMap<String, HashMap<String, String>>();
    private HashMap<String, HashMap<String, String>> templates = new HashMap<String, HashMap<String, String>>();
    private HashMap<String, String> rootFiles = new HashMap<String, String>();

//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
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
//    public HashMap<String, HashMap<String, String>> getTemplates() {
//        return templates;
//    }
//
//    public void setTemplates(HashMap<String, HashMap<String, String>> templates) {
//        this.templates = templates;
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
//        return "MicronautProfile{" +
//                "name='" + name + '\'' +
//                ", skeleton=" + skeleton +
//                ", templates=" + templates +
//                ", rootFiles=" + rootFiles +
//                '}';
//    }
//
//    public MicronautProfile() {
//        super();
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        MicronautProfile that = (MicronautProfile) o;
//        return Objects.equals(getName(), that.getName()) &&
//                Objects.equals(getSkeleton(), that.getSkeleton()) &&
//                Objects.equals(getTemplates(), that.getTemplates()) &&
//                Objects.equals(getRootFiles(), that.getRootFiles());
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(getName(), getSkeleton(), getTemplates(), getRootFiles());
//    }

//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
}
