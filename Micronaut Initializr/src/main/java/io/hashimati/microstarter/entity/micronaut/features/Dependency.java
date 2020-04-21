package io.hashimati.microstarter.entity.micronaut.features;
/**
 * @author Ahmed Al Hashmi @hashimati
 */

import java.util.ArrayList;

import static io.hashimati.microstarter.util.ScanningUtils.space;


public class Dependency
{
    private String scope,
    coords;
    private String os;
    private ArrayList<Exclude> excludes = new ArrayList<Exclude>();

    public Dependency(String scope, String coords) {
        this.scope = scope;
        this.coords = coords;
    }

    public Dependency() {
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getCoords() {
        return coords;
    }

    public void setCoords(String coords) {
        this.coords = coords;
    }

    @Override
    public String toString() {
        return "Dependency{" +
                "scope='" + scope + '\'' +
                ", coords='" + coords + '\'' +
                ", excludes=" + excludes +
                '}';
    }

    public String getGradle(){
        String excludesStr = "{";
        if(excludes.isEmpty()== false)
        {
            for (Exclude e : excludes)
            {
                excludesStr += e.getGradle()+"\n";
            }
            excludesStr +="}";
        }
        else
            excludesStr = null;
        return scope + " " + "\"" +coords+ "\"" + (excludesStr !=null? excludesStr:"");
    }
    public ArrayList<Exclude> getExcludes() {
        return excludes;
    }

    public void setExcludes(ArrayList<Exclude> excludes) {
        this.excludes = excludes;
    }

    public String getMaven() {
        String[] coordSplit = coords.split(":");
        String excludesStr = "<exclusions>\n";
        if(excludes.isEmpty()== false)
        {
            for (Exclude e : excludes)
            {
                excludesStr += e.getMaven()+"\n";
            }
            excludesStr +=space(4)+"</exclusions>\n";
        }
        else
            excludesStr = null;

        String scope = "compile";
        switch(getScope())
        {
            case "implementation":
            case "kapt":
                scope = "compile";
                break;
            case "testRuntime":
            case "testAnnotationProcessor":
            case "testCompile":
            case "kaptTest":
                scope = "test";
                break;
            case "runtimeOnly":
                scope = "runtime";
                break;
            default:
                scope = "compile";
                break;

        }
        String version ="";
        if(coordSplit.length == 3)
        {
            version = "<version>" + coordSplit[2] + "</version>";
        }
        // return space(3)+"<dependency>\n" +
        //         space(5) + "<groupId>"+coordSplit[0].trim() +"</groupId>\n" +
        //         space(5) +"<artifactId>"+coordSplit[1]+"</artifactId>\n" +
        //         space(5) +version +
        //         space(5) +"<scope>"+scope+"</scope>\n" +
        //          ((excludesStr!= null)? space(5) +excludesStr:"")+
        //         space(3)+"</dependency>\n";
         return "<dependency>" +
                 "<groupId>"+coordSplit[0].trim() +"</groupId>" +
                "<artifactId>"+coordSplit[1]+"</artifactId>" +
                version +
                "<scope>"+scope+"</scope>" +
                 ((excludesStr!= null)? excludesStr:"")+
                "</dependency>";
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }
}
