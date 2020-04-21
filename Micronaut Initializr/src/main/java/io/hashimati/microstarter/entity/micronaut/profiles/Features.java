package io.hashimati.microstarter.entity.micronaut.profiles;
/**
 * @author Ahmed Al Hashmi @hashimati
 */

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Objects;

//@Data
//@NoArgsConstructor
//@ToString
//@EqualsAndHashCode
public class Features
{
    private ArrayList<String> required = new ArrayList<String>();
    private OneOf oneOf;
    private ArrayList<String> defaults = new ArrayList<String>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Features features = (Features) o;
        return Objects.equals(getRequired(), features.getRequired()) &&
                Objects.equals(getOneOf(), features.getOneOf()) &&
                Objects.equals(getDefaults(), features.getDefaults());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRequired(), getOneOf(), getDefaults());
    }

    @Override
    public String toString() {
        return "Features{" +
                "required=" + required +
                ", oneOf=" + oneOf +
                ", defaults=" + defaults +
                '}';
    }

    public ArrayList<String> getRequired() {
        return required;
    }

    public void setRequired(ArrayList<String> required) {
        this.required = required;
    }

    public OneOf getOneOf() {
        return oneOf;
    }

    public void setOneOf(OneOf oneOf) {
        this.oneOf = oneOf;
    }

    public ArrayList<String> getDefaults() {
        return defaults;
    }

    public void setDefaults(ArrayList<String> defaults) {
        this.defaults = defaults;
    }

    //    @Data
//    @NoArgsConstructor
//    @ToString
//    @EqualsAndHashCode
    public static class OneOf {
        private ArrayList<FeaturePriority> languages = new ArrayList<FeaturePriority>();

        private ArrayList<FeaturePriority> jdbc  = new ArrayList<FeaturePriority>();

    public ArrayList<FeaturePriority> getLanguages() {
        return languages;
    }

    public void setLanguages(ArrayList<FeaturePriority> languages) {
        this.languages = languages;
    }

    public ArrayList<FeaturePriority> getJdbc() {
        return jdbc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OneOf oneOf = (OneOf) o;
        return Objects.equals(getLanguages(), oneOf.getLanguages()) &&
                Objects.equals(getJdbc(), oneOf.getJdbc());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLanguages(), getJdbc());
    }

    public void setJdbc(ArrayList<FeaturePriority> jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public String toString() {
        return "OneOf{" +
                "languages=" + languages +
                ", jdbc=" + jdbc +
                '}';
    }
}


    @Data
    @NoArgsConstructor
    @ToString
    @EqualsAndHashCode
    public static class FeaturePriority {
        private String feature;
        private String priority;
    }

}
