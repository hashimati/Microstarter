package io.hashimati.microstarter.services.intellij;
/**
 * @author Ahmed Al Hashmi @hashimati
 */

import groovy.text.SimpleTemplateEngine;
import io.hashimati.microstarter.entity.micronaut.MicronautFeature;
import io.hashimati.microstarter.entity.micronaut.features.Dependency;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Deprecated
public class Template
{

    private static final String mavenOption = "MAVEN",
            gradleOption= "GROOVY_EXPRESSION",
    featureValueMaven= "&lt;dependency&gt;&#10;    &lt;groupId&gt;${group}&lt;/groupId&gt;&#10;    &lt;artifactId&gt;${featureArtifact}&lt;/artifactId&gt;&#10;    &lt;scope&gt;compile&lt;/scope&gt;&#10;&lt;/dependency&gt;" ,
    featureValueGradle= "implementation &quot;\"${group}:${featureArtifact}&quot;",
    newline = "&#10";
    private String name, value, description;
    private boolean toReformat, toShortenFQNames;

    private String contextOptionName;
    private boolean contextOptionValue;


    private MicronautFeature micronautFeature;

    public static final String templateTemplate =
            " <template name=\"${name}\" value=\" ${value}\" description=\"${description}\" toReformat=\"${toReformat}\" toShortenFQNames=\"${toShortenFQNames}\">\n" +
                    "    <context>\n" +
                    "      <option name=\"${build}\" value=\"true\" />\n" +
                    "    </context>\n" +
                    "  </template>";


    public Template(MicronautFeature micronautFeature){
        this.micronautFeature = micronautFeature;
        this.description = micronautFeature.getDescription();
        this.name = "mn-"+ micronautFeature.getName();

        this.setToReformat(true);
        this.setToShortenFQNames(true);

        this.setContextOptionValue(true);
    }


    private Map getFeatureMap(Dependency x) {
        String[] coordSplit = x.getCoords().split(":");
        String groupID = coordSplit[0];
        String artifact = coordSplit[1];

        return new HashMap<String, String>(){{
            put("group",groupID);
            put("featureArtifact", artifact);
            put("excludes", "");
        }};
    }


    public String getMavenTemplate() throws IOException, ClassNotFoundException {
        AtomicReference<String> mavenTemplate = new AtomicReference<String>("");
        micronautFeature.getDependencies().stream().forEach(x->{
            Map<String, String> featureMap = getFeatureMap(x);
            String parsedMaven = null;
            try {
                parsedMaven = new SimpleTemplateEngine().createTemplate(featureValueMaven).make(featureMap).toString();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mavenTemplate.set (mavenTemplate.get() + newline + parsedMaven);
        });
        HashMap<String, String> templateMap = new HashMap<String, String>(){{
            put("name", name);
            put("value", mavenTemplate.get());
            put("description",description);
            put("toReformat", "false");
            put("toShortenFQNames", "true");
            put("build", mavenOption);

        }};


        return new SimpleTemplateEngine().createTemplate(templateTemplate).make(templateMap).toString();
    }
   public String getGradleTemplate() throws IOException, ClassNotFoundException {
        AtomicReference<String> gradleTemplate = new AtomicReference<String>("");
        micronautFeature.getDependencies().stream().forEach(x->{
            Map<String, String> featureMap = getFeatureMap(x);
            String parsedGradle = null;
            try {
                parsedGradle = new SimpleTemplateEngine(). createTemplate(featureValueGradle).make(getFeatureMap(x)).toString();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            gradleTemplate.set(gradleTemplate.get() + newline + parsedGradle);
        });

       HashMap<String, String> templateMap = new HashMap<String, String>(){{
           put("name", name);
           put("value", gradleTemplate.get());
           put("description",description);
           put("toReformat", "false");
           put("toShortenFQNames", "true");
           put("build", gradleOption);

       }};


       return new SimpleTemplateEngine().createTemplate(templateTemplate).make(templateMap).toString();

   }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isToShortenFQNames() {
        return toShortenFQNames;
    }

    public void setToShortenFQNames(boolean toShortenFQNames) {
        this.toShortenFQNames = toShortenFQNames;
    }

    public boolean isToReformat() {
        return toReformat;
    }

    public void setToReformat(boolean toReformat) {
        this.toReformat = toReformat;
    }

    public String getContextOptionName() {
        return contextOptionName;
    }

    public void setContextOptionName(String contextOptionName) {
        this.contextOptionName = contextOptionName;
    }

    public boolean getContextOptionValue() {
        return contextOptionValue;
    }

    public void setContextOptionValue(boolean contextOptionValue) {
        this.contextOptionValue = contextOptionValue;
    }

    public MicronautFeature getMicronautFeature() {
        return micronautFeature;
    }

    public void setMicronautFeature(MicronautFeature micronautFeature) {
        this.micronautFeature = micronautFeature;
    }


}

