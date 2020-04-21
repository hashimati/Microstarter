package io.hashimati.microstarter.util;


/**
 * @author Ahmed Al Hashmi @hashimati
 */


import io.hashimati.microstarter.constants.ProjectConstants;
import io.hashimati.microstarter.entity.Entity;
import io.hashimati.microstarter.entity.micronaut.ComponentObject;
import io.hashimati.microstarter.entity.micronaut.MicronautFeature;
import io.hashimati.microstarter.entity.micronaut.ProjectRequest;
import io.hashimati.microstarter.repository.MicronautFeatureRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static io.hashimati.microstarter.constants.ProjectConstants.LanguagesConstants.*;
import static io.hashimati.microstarter.constants.ProjectConstants.MicronautProfileConstants.CLI;

@Singleton
public class ProjectRequestValidators {


    @Inject
    private MicronautFeatureRepository micronautFeatureRepository;
    public void removingIncompatibleDependcies(ProjectRequest projectRequest) {
        String lang = projectRequest.getLanguage().toLowerCase();
        int javaVersion = Integer.parseInt(projectRequest.getJavaVersion());


        projectRequest.setTestframework(lang.equalsIgnoreCase(JAVA_LANG) ? "junit" :
                lang.equalsIgnoreCase(GROOVY_LANG) ? "spock" : "spek");

        if (projectRequest.getProfile().equalsIgnoreCase(CLI)) {

            projectRequest.getDependencies().addAll(Arrays.asList("picocli", "picocli-" + lang,
                    "test-picocli-" + projectRequest.getTestframework().toLowerCase()));

        }

        List<String> listOfLanguages = ProjectConstants
                .LanguagesConstants
                .LANGUAGES
                .stream()
                .filter(x -> !x.equalsIgnoreCase(lang))
                .collect(Collectors.toList());

        List<String> refinedDependenciesList = projectRequest
                .getDependencies()
                .stream()
                .filter(
                        x -> {
                            boolean isLanguageOk = true, isJavaVerOk = true;
                            MicronautFeature feature = micronautFeatureRepository.findDistinctByName(x);
                            for (String l : listOfLanguages) {

                                if (feature.getFeatures() != null && feature.getFeatures().getDependent() != null) {
                                    isLanguageOk &= !feature
                                            .getFeatures()
                                            .getDependent()
                                            .contains(l);
                                }
                            }
                            if (feature.getJava() != null) {
                                if (feature.getJava().getMax() != null) {
                                    isJavaVerOk &= (javaVersion <= Integer.parseInt(feature.getJava().getMax()));
                                }
                                if (feature.getJava().getMin() != null) {
                                    isJavaVerOk &= (javaVersion >= Integer.parseInt(feature.getJava().getMin()));
                                }
                            }
                            return isJavaVerOk && isLanguageOk;
                        })
                .collect(Collectors.toList());
        projectRequest.setDependencies(new ArrayList<String>(refinedDependenciesList));


        if (projectRequest.getLanguage().equalsIgnoreCase(GROOVY_LANG)) {
//           projectRequest.getDependencies().add("groovy-configuration");
            for (ComponentObject co : projectRequest.getCompontentObjects()) {
                if (co.getType().indexOf("Test") >= 0) {
                    co.setType(co.getType().replace("Test", "Spec"));

                }
            }
            projectRequest.getDependencies().remove(KOTLIN_LANG);
            projectRequest.getDependencies().remove(JAVA_LANG);
        }
        if (projectRequest.getLanguage().equalsIgnoreCase(JAVA_LANG)) {
//            projectRequest.getDependencies().add("java-configuration");
            projectRequest.getDependencies().remove(KOTLIN_LANG);
            projectRequest.getDependencies().remove(GROOVY_LANG);
        }
        if (projectRequest.getLanguage().equalsIgnoreCase(KOTLIN_LANG)) {
//            projectRequest.getDependencies().add("kotlin-configuration");
            projectRequest.getDependencies().remove(GROOVY_LANG);
            projectRequest.getDependencies().remove(JAVA_LANG);

        }
        if (!projectRequest.getEntities().isEmpty())
        {


            for(Entity e : projectRequest.getEntities())
            {
                e.setEntityPackage(projectRequest.getDefaultPackage() + "."+"domains");
                e.setRepoPackage(projectRequest.getDefaultPackage() + "."+"repositories");
                e.setServicePackage(projectRequest.getDefaultPackage() + "."+"services");
                e.setRestPackage(projectRequest.getDefaultPackage() + "." + "resources");
                e.setDatabaseType(projectRequest.getDatabaseType());
                e.setDatabaseName(projectRequest.getDatabaseName());
            }
            switch(projectRequest.getDatabaseType().toLowerCase())
            {
                case "mongodb":
                    projectRequest.getDependencies().add("mongo-reactive");
                    break;
                case "neo4j":
                    projectRequest.getDependencies().add("neo4j-bolt");
                    break;
                case "cassandra":
                    projectRequest.getDependencies().add("cassandra");
                    break;
                case "postgressql":
                    projectRequest.getDependencies().add("postgres-reactive");
                    break;
                default:
                    projectRequest.getDependencies().add("data-hibernate-jpa");
                    break;

            }
        }
    }
}
