package io.hashimati.microstarter.entity.micronaut;

/**
 * @author Ahmed Al Hashmi @hashimati
 */

import java.util.Objects;

/**
 * This class will be used in the ProjectRequest class in array list.
 * in order collect the object from the user such as:
 * Class, bean, controllers , producer, listener, Job.... etc
 *
 * ${className}
 * ${propertyName}
 * {topic}
 * ${packageName}
 * @author hashimati
 */
public class ComponentObject
{
    private String name,profile="",
    type="", packagePath="", propertyPath="", topic="", commandProperty="";

    public ComponentObject(){

    }
    public ComponentObject(String name, String profile, String type, String packagePath, String propertyPath,
                           String commandProperty){
        this.name  = name;
        this.type = type;
        this.profile = profile;
        this.packagePath = packagePath;
        this.propertyPath = propertyPath;
        this.commandProperty = commandProperty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }













    @Override
    public int hashCode() {
        return Objects.hash(getName(), getType());
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getPackagePath() {
        return packagePath;
    }

    public void setPackagePath(String packagePath) {
        this.packagePath = packagePath;
    }

    public String getPropertyPath() {
        return propertyPath;
    }

    public void setPropertyPath(String propertyPath) {
        this.propertyPath = propertyPath;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }


    @Override
    public String toString() {
        return "CompontentObject{" +
                "name='" + name + '\'' +
                ", profile='" + profile + '\'' +
                ", type='" + type + '\'' +
                ", packagePath='" + packagePath + '\'' +
                ", propertyPath='" + propertyPath + '\'' +
                ", topic='" + topic + '\'' +
                '}';
    }

    public String getCommandProperty() {
        return commandProperty;
    }

    public void setCommandProperty(String commandProperty) {
        this.commandProperty = commandProperty;
    }
}
