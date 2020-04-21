package io.hashimati.microstarter.services;


import io.micronaut.context.event.StartupEvent;
import io.micronaut.core.io.ResourceResolver;
import io.micronaut.core.io.scan.ClassPathResourceLoader;
import io.micronaut.runtime.event.annotation.EventListener;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
/**
 * @author Ahmed Al Hashmi @hashimati
 */

@Singleton
public class TemplatesService {

    List<String> entitysFileNames = Arrays.asList("java_entity.txt", "groovy_entity.txt", "kotlin_entity.txt",
            "sql_entity.txt", "sqlcolumns_entity.txt", "javaenum_entity.txt", "groovyenum_entity.txt",
            "kotlinenum_entity.txt");

    List<String> controllersFileNames = Arrays.asList("java_controller.txt", "groovy_controller.txt", "kotlin_controller.txt");
    List<String> clientsFileNames = Arrays.asList("java_client.txt", "groovy_client.txt", "kotlin_client.txt");

    List<String> servicesFileNames = Arrays.asList("java_service.txt", "groovy_service.txt", "kotlin_service.txt");

    List<String> repositoriesFileNames = Arrays.asList("java_repository.txt", "groovy_repository.txt",
            "kotlin_repository.txt", "JdbcConfig_repository.txt");
    List<String> mongoRepositoryFileNames = Arrays.asList("java_mongorepository.txt", "groovy_mongorepository.txt",
            "kotlin_mongorepository.txt");
    List<String> mongoServiceFileNames = Arrays.asList("java_mongoservice.txt", "groovy_mongoservice.txt",
            "kotlin_mongoservice.txt");
    List<String> mongoControllerFileNames = Arrays.asList("java_mongocontroller.txt", "groovy_mongocontroller.txt",
            "kotlin_mongocontroller.txt");


    List<String> jdbcPropertiesFiles = Arrays.asList("oracle_jdbc_properties.txt", "sqlserver_jdbc_properties.txt",
            "mysql_jdbc_properties.txt", "postgres_jdbc_properties.txt", "mariadb_jdbc_properties.txt");

    List<String> jdbcFeatureFiles = Arrays.asList("oracle_feature.yml", "sqlserver_feature.yml",
            "mysql_feature.yml", "postgres_feature.yml", "mariadb_feature.yml");

    private HashMap<String, String> entityTemplates = new HashMap<String, String>();
    private HashMap<String, String> repositoryTemplates = new HashMap<String, String>(),
            serviceTemplates = new HashMap<String, String>()
            ,controllerTemplates = new HashMap<String, String>(),
            clientTemplates  = new HashMap<String, String>(),
            mongoRepositoryTemplates  = new HashMap<String, String>(),
            mongoServiceTemplates  = new HashMap<String, String>(),
            mongoControllerTemplates  = new HashMap<String, String>(),
            jdbcPropertiesTemplates = new HashMap<String, String>(),
            jdbcFeacures = new HashMap<String, String>();

    public HashMap<String, String> getEntityTemplates() {
        return entityTemplates;
    }

    public HashMap<String, String> getControllerTemplates() {
        return controllerTemplates;
    }

    public HashMap<String, String> getRepositoryTemplates() {
        return repositoryTemplates;
    }

    public HashMap<String, String> getServiceTemplates() {
        return serviceTemplates;
    }

    @EventListener
    public void loadTemplates(StartupEvent event) throws IOException {
        auxLoadTemplates(entitysFileNames, entityTemplates);
        auxLoadTemplates(controllersFileNames,controllerTemplates);
        auxLoadTemplates(servicesFileNames, serviceTemplates);
        auxLoadTemplates(repositoriesFileNames, repositoryTemplates);
        auxLoadTemplates(mongoRepositoryFileNames, mongoRepositoryTemplates);
        auxLoadTemplates(jdbcPropertiesFiles, jdbcPropertiesTemplates);
        auxLoadTemplates(jdbcFeatureFiles, jdbcFeacures);
        auxLoadTemplates(mongoServiceFileNames, mongoServiceTemplates);

        auxLoadTemplates(mongoControllerFileNames, mongoControllerTemplates);

        auxLoadTemplates(clientsFileNames, clientTemplates);

        System.out.println(entityTemplates);

    }

    public void auxLoadTemplates(List<String> fileNames, HashMap<String, String> templates)
    {
        fileNames.forEach(x->{

            ClassPathResourceLoader loader = new ResourceResolver().getLoader(ClassPathResourceLoader.class).get();

            String key = x.substring(0, x.indexOf('_'));
            try {
                System.out.println(x);
                String template = "";
                Scanner scanner = new Scanner(loader.getResource("classpath:entityTemplates/" + x).get().openStream());
                while (scanner.hasNextLine()) {
                    template += scanner.nextLine() + "\n";
                }
                templates.put(key, template);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }


    public HashMap<String, String> getJdbcPropertiesTemplates() {
        return jdbcPropertiesTemplates;
    }

    public HashMap<String, String> getJdbcFeacures() {
        return jdbcFeacures;
    }

    public HashMap<String, String> getMongoRepositoryTemplates() {
        return mongoRepositoryTemplates;
    }

    public void setMongoRepositoryTemplates(HashMap<String, String> mongoRepositoryTemplates) {
        this.mongoRepositoryTemplates = mongoRepositoryTemplates;
    }

    public HashMap<String, String> getMongoServiceTemplates() {
        return mongoServiceTemplates;
    }

    public void setMongoServiceTemplates(HashMap<String, String> mongoServiceTemplates) {
        this.mongoServiceTemplates = mongoServiceTemplates;
    }

    public HashMap<String, String> getMongoControllerTemplates() {
        return mongoControllerTemplates;
    }

    public void setMongoControllerTemplates(HashMap<String, String> mongoControllerTemplates) {
        this.mongoControllerTemplates = mongoControllerTemplates;
    }
    public HashMap<String, String> getClientTemplates() {
        return clientTemplates;
    }

    public void setClientTemplates(HashMap<String, String> clientTemplates) {
        this.clientTemplates = clientTemplates;
    }

}






