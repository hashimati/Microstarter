package io.hashimati.microstarter.services;
/*
<template name="mn-f-${featureName}" value="${featureValue}" toReformat="true" toShortenFQNames="true">
  <context>
    <option name="${Option}" value="true" />
  </context>
</template>



featureValueMaven= "&lt;dependency&gt;&#10;    &lt;groupId&gt;${group}&lt;/groupId&gt;&#10;    &lt;artifactId&gt;${featureArtifact}&lt;/artifactId&gt;&#10;    &lt;scope&gt;compile&lt;/scope&gt;&#10;&lt;/dependency&gt;" shortcut="ENTER" description="${featureDescription}"
featureValueGradle="implementation &quot;"${group}:${featureArtifact}&quot;"


mavenOption = "MAVEN"
gradleOption = "GROOVY_EXPRESSION"
 */

/**
 * @author Ahmed Al Hashmi @hashimati
 */

import io.hashimati.microstarter.entity.micronaut.MicronautFeature;
import io.hashimati.microstarter.entity.micronaut.features.Dependency;
import io.hashimati.microstarter.repository.FeatureSkeletonRepository;
import io.hashimati.microstarter.repository.MicronautFeatureRepository;
import io.hashimati.microstarter.services.intellij.Template;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.reactivex.Flowable;


import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
@Deprecated
@Controller
public class CodeTemplatesService {


    private static String mavenOption = "MAVEN",
    gradleOption= "GROOVY_EXPRESSION"
            ,featureValueMaven= "&lt;dependency&gt;&#10;    &lt;groupId&gt;${group}&lt;/groupId&gt;&#10;    &lt;artifactId&gt;${featureArtifact}&lt;/artifactId&gt;&#10;    &lt;scope&gt;compile&lt;/scope&gt;&#10;&lt;/dependency&gt;" ,
    featureValueGradle= "implementation &quot;\"${group}:${featureArtifact}&quot;",
    newline = "&#10";


    @Inject
    private MicronautFeatureRepository micronautFeatureRepository;

    @Inject
    private FeatureSkeletonRepository featureSkeletonRepository;


    @Get(value = "/micronautFeaturesMavenTemplates", produces = MediaType.TEXT_PLAIN)
    public String micronautFeatures()
    {
        String header = "<templateSet group=\"Micronaut-Features-Maven\">",
                footer = "</templateSet>";

        return header + "\n" + loadAll().get("maven").stream().reduce("", (x, y)->
                x+ "\n" + y)+ "\n"+footer;
    }
    @Get("/heheh")
    public HashMap<String, ArrayList<String>> loadAll(){



        ArrayList<String> mavenTemplates = new ArrayList<String>(), gradleTemplates = new ArrayList<String>();
        Flowable<MicronautFeature> featuresList = micronautFeatureRepository.findAll();

        featuresList.filter(x->x.getDependencies().isEmpty() == false).forEach(x->{


            Template template = new Template(x);
            try {
                mavenTemplates.add(template.getMavenTemplate());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                gradleTemplates.add(template.getGradleTemplate());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


        });



        return new HashMap<String, ArrayList<String>>(){{
            put("maven", mavenTemplates);
            put("gradle", gradleTemplates);
        }};
    }

    private Map getFeatureMap(Dependency x) {
        String[] coordSplit = x.getCoords().split(":");
        String groupID = coordSplit[0];
        String artifact = coordSplit[1];

        return new HashMap<String, String>(){{
            put("group",groupID);
            put("featureArtifact", artifact);
            put("excludes", "");
            put("featureDescription", x.getCoords());

        }};
    }


}
