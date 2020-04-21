package io.hashimati.microstarter.entity.micronaut.features;

/**
 * @author Ahmed Al Hashmi @hashimati
 */

import static io.hashimati.microstarter.util.ScanningUtils.space;

public class Exclude {
    private String group,
    module;

    public Exclude(){

    }
    public Exclude(String group, String module){
        this.group = group;
        this.module = module;
    }
    @Override
    public String toString() {
        return "Exclude{" +
                "group='" + group + '\'' +
                ", module='" + module + '\'' +
                '}';
    }


    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getGradle()
    {
        return "exclude group: '"+group+"', module: '"+module+"'";

    }
    public String getMaven()
    {

        return space(5) +"<exclusion>\n" +
                space(6) +"<groupId>"+group+"</groupId>\n" +
                space(6) +"<artifactId>"+module+"</artifactId>\n" +
                space(5) +"</exclusion>";
    }
}
