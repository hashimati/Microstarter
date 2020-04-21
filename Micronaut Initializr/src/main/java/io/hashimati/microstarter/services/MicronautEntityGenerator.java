package io.hashimati.microstarter.services;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;
import groovy.text.SimpleTemplateEngine;

import io.hashimati.microstarter.constants.ProjectConstants;
import io.hashimati.microstarter.entity.*;
import io.hashimati.microstarter.entity.micronaut.ProjectRequest;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

import static io.hashimati.microstarter.constants.ProjectConstants.LanguagesConstants.*;

;

//

/**
 * @author Ahmed Al Hashmi @hashimati
 */

@Singleton
public class MicronautEntityGenerator
{



    @Inject
    private TemplatesService templatesService;

//    @PostConstruct
    public void test() throws IOException, ClassNotFoundException, FormatterException {

        System.out.println("--------------------test entity----");

        Entity entity = new Entity(){{
            setName("Ahmed");
            setCollectionName("Ahmed");
            setDatabaseType("mysql");
            setEntityPackage("com.ahmed.ahl");
            setRepoPackage("com.ahmed.ah1.repo");
            setServicePackage("com.ahmed.ah1.services");
            setRestPackage("com.ahmed.ah1.resources");

            getAttributes().add(new EntityAttribute(){{
                setName("ie");
                setType(ProjectConstants.EntityAttributeType.INTEGER);
                setConstraints(new EntityConstraints(){{
                    setMin(Long.valueOf(32));
                    setMax(Long.valueOf(55555));
                }});
            }});

            setCollectionName("Ahmeds");
        }};
        EntityRelation entityRelation = new EntityRelation();
        entityRelation.setE1("Ahmed");
        entityRelation.setE1Package("com.ahmed.ahl");
        entityRelation.setE2("Ali");
        entityRelation.setE2Package("com.ahmed.ah1");
        entityRelation.setRelationType(EntityRelationType.OneToOne);
        ArrayList<EntityRelation> relations = new ArrayList<>();
        relations.add(entityRelation);
        System.out.println("-----------------java-------");

        System.out.println(generateEntity(entity, relations, "java"));
        System.out.println("***");
        System.out.println(generateRepository(entity, "java"));
        System.out.println("***");
        System.out.println(generateService(entity, "java"));
        System.out.println("***");
        System.out.println(generateController(entity, "java"));
        System.out.println("-----------------groovy-------");
        System.out.println(generateEntity(entity, relations ,GROOVY_LANG));
        System.out.println("***");
        System.out.println(generateRepository(entity, "groovy"));
        System.out.println("***");
        System.out.println(generateService(entity, "groovy"));
        System.out.println("***");
        System.out.println(generateController(entity, "groovy"));

        System.out.println("--------------kotlin----------");
        System.out.println(generateEntity(entity,relations ,"kotlin"));
        System.out.println("***");
        System.out.println(generateRepository(entity, "kotlin"));
        System.out.println("***");
        System.out.println(generateService(entity, "kotlin"));
        System.out.println("***");
        System.out.println(generateController(entity, "kotlin"));

        System.out.println("--------------------end entity test----");
    }

    public String generateEntity(Entity entity, ArrayList<EntityRelation> relations, String language) throws IOException,
            ClassNotFoundException, FormatterException {

        String declarrationSperator = language.equalsIgnoreCase(KOTLIN_LANG)? ",": "\n";
        Set<EntityRelation> entityRelations = getRelations(entity, relations);
        String attributesDeclaration ="";
        String importedPackages = "";
        boolean containDate = false;

        if(entity.getAttributes()!= null)
        for(EntityAttribute eA: entity.getAttributes())
        {

            if(eA.isDate())
            {
                containDate = true;
            }

            String attributeDeclaration = "";




            if(eA.getConstraints() != null)
            if(eA.getConstraints().isEnabled()){
                if(eA.isString())
                {
                    attributeDeclaration +=eA.getConstraints().getSizeExpression();
                    attributeDeclaration+=eA.getConstraints().getNotBlankExpression();
                    attributeDeclaration+= eA.getConstraints().getNotNullExpression();
                    attributeDeclaration +=eA.getConstraints().getPattern();
                    attributeDeclaration+= eA.getConstraints().getEmailExpression();
                }
                else if(eA.isInteger() || eA.isByte() || eA.isShort() || eA.isLong())
                {
                    attributeDeclaration+=eA.getConstraints().getMaxExpression();
                    attributeDeclaration +=eA.getConstraints().getMinExpression();
                }
                else if(eA.isDouble() || eA.isFloat())
                {
                    attributeDeclaration+=eA.getConstraints().getDecimalMaxExpression();
                    attributeDeclaration +=eA.getConstraints().getDecimalMinExpression();
                }
                else if(eA.isDate())
                {
                    attributeDeclaration +=eA.getConstraints().getFutureExpression();
                }
                else if(eA.isClass())
                {
                    attributeDeclaration +=eA.getConstraints().getNotNullExpression();
                }
            }
            attributeDeclaration +=eA.getDeclaration(language) + declarrationSperator;
            if(!eA.isPremetive() && eA.getTypePackage()!= null )
            {
               importedPackages += eA.getPackageSyntax(language)+"\n";


            }

            attributesDeclaration +=   attributeDeclaration +"\n";
        }
        if(entityRelations != null)
        for(EntityRelation r: entityRelations)
        {
            switch(r.getRelationType())
            {
                case OneToOne:
                    if(entity.getName().equals(r.getE1()) && entity.getEntityPackage().equals(r.getE1Package()))
                    {

                        importedPackages +=(importedPackages.contains(r.getE1Package()+"."+r.getE1())? "": "\nimport " + r.getE1Package()+"."+r.getE1()+ ";");
                        attributesDeclaration += "\n" + r.generateE1OneToOneTemplate(language)+declarrationSperator;
                    }
                    if(entity.getName().equals(r.getE2()) && entity.getEntityPackage().equals(r.getE2Package()))
                    {


                        importedPackages +=(importedPackages.contains(r.getE2Package()+"."+r.getE2())? "": "\nimport" +
                                " " + r.getE2Package()+"."+r.getE2()+ ";");
                        attributesDeclaration += "\n" + r.generateE2OneToOneTemplate(language)+declarrationSperator;
                    }
                    break;
                case OneToMany:
                    if(entity.getName().equals(r.getE1()) && entity.getEntityPackage().equals(r.getE1Package()))
                    {

                        importedPackages +=(importedPackages.contains(r.getE1Package()+"."+r.getE1())? "": "\nimport " + r.getE1Package()+"."+r.getE1()+ ";");
                        attributesDeclaration += "\n" + r.generateE1OneToManyTemplate(language)+declarrationSperator;
                    }
                    if(entity.getName().equals(r.getE2()) && entity.getEntityPackage().equals(r.getE2Package()))
                    {

                        importedPackages +=(importedPackages.contains(r.getE2Package()+"."+r.getE2())? "": "\nimport " + r.getE2Package()+"."+r.getE2()+ ";");
                        attributesDeclaration += "\n" + r.generateE2OneToManyTemplate(language)+declarrationSperator;
                    }
                    break;
//                case ManyToMany:
//
//                    break;

            }
        }
        if((attributesDeclaration = attributesDeclaration.trim()).endsWith(","))
        {
            attributesDeclaration = attributesDeclaration.substring(0, attributesDeclaration.lastIndexOf(','));
        }
        HashMap<String, Object> binder = new HashMap<String, Object>();
        String entityAnnotation= "";
        boolean isJdbc = false;
        switch (entity.getDatabaseType().toLowerCase())
        {
            case "mysql":
            case "oracle":
            case "postgres":
            case "h2":
            case "mariadb":
                isJdbc = true;
                break;
            default:
                entityAnnotation = "";
        }
        binder.put("entityAnnotation",entityAnnotation );
        binder.put("tableAnnotation","" );
        binder.put("entitypackage", entity.getEntityPackage());
        binder.put("jdbc", isJdbc);
        binder.put("collectionName", entity.getCollectionName()); 
        binder.put("className",entity.getName() );
        binder.put("instances", attributesDeclaration);
        binder.put("importedPackages",importedPackages );
        binder.put("containDate", containDate);

        String entityTemplate  =templatesService.getEntityTemplates().get(language.toLowerCase());

        String result = new SimpleTemplateEngine().createTemplate(entityTemplate).make(binder).toString();
        if(!language.equalsIgnoreCase(JAVA_LANG))
            result = result.replace(";", "");
        else
            result  =new Formatter().formatSource(result);

        return result;
    }
    public String generateRepository(Entity entity, String language) throws IOException, ClassNotFoundException {

        HashMap<String, String> binder = new HashMap<>();

        if(!entity.getDatabaseType().toLowerCase().equalsIgnoreCase("mongodb")){


            binder.put("entityRepositoryPackage", entity.getRepoPackage() );
            binder.put("importEntity", entity.getEntityPackage()+"." + entity.getName());
            binder.put("className", entity.getName());
            String repositoryTemplate =  templatesService.getRepositoryTemplates().get(language.toLowerCase());

            return new SimpleTemplateEngine().createTemplate(repositoryTemplate).make(binder).toString();

        }
        else if(entity.getDatabaseType().toLowerCase().equalsIgnoreCase("mongodb"))
        {
            binder.put("entityRepositoryPackage", entity.getRepoPackage());
            binder.put("entityPackage", entity.getEntityPackage());
            binder.put("entityClass", entity.getName());
            binder.put("databaseName", entity.getDatabaseName());
            binder.put("collectionName", entity.getCollectionName());
            String repositoryTemplate = templatesService.getMongoRepositoryTemplates().get(language.toLowerCase());
            return new SimpleTemplateEngine().createTemplate(repositoryTemplate).make(binder).toString();
        }
        return "";
    }

    public String generateService(Entity entity, String language) throws IOException, ClassNotFoundException {
        HashMap<String, String> binder = new HashMap<>();
        binder.put("servicePackage", entity.getServicePackage() );
        binder.put("entityPackage", entity.getEntityPackage()+"." + entity.getName());
        binder.put("repoPackage", entity.getRepoPackage()+"."+entity.getName()+"Repository");
        binder.put("entityName", entity.getName().toLowerCase());
        binder.put("className", entity.getName());
        String serviceTemplate = "";
        switch (entity.getDatabaseType().toLowerCase())
        {
            case "mongodb":
                serviceTemplate = templatesService.getMongoServiceTemplates().get(language.toLowerCase());
                break;
            default:
                serviceTemplate = templatesService.getServiceTemplates().get(language.toLowerCase());
                break;
        }

        return new SimpleTemplateEngine().createTemplate(serviceTemplate).make(binder).toString();
    }
    public String generateController(Entity entity, String language) throws IOException, ClassNotFoundException {
        HashMap<String, String> binder = new HashMap<>();
        binder.put("controllerPackage", entity.getRestPackage() );
        binder.put("entityPackage", entity.getEntityPackage()+"." + entity.getName());
        binder.put("servicePackage", entity.getServicePackage()+"."+entity.getName()+"Service");
        binder.put("entityName", entity.getName().toLowerCase());
        binder.put("entities", entity.getName().toLowerCase());

        binder.put("className", entity.getName());
        String serviceTemplate ;
        switch (entity.getDatabaseType().toLowerCase())
        {
            case "mongodb":
                serviceTemplate = templatesService.getMongoControllerTemplates().get(language.toLowerCase());
                break;
            default:
                serviceTemplate = templatesService.getControllerTemplates().get(language.toLowerCase());
                break;
        }
        return new SimpleTemplateEngine().createTemplate(serviceTemplate).make(binder).toString();
    }



    //to extract relations
    private Set<EntityRelation> getRelations(Entity e, ArrayList<EntityRelation> relations)
    {

        if(relations == null) return null;
        return relations.stream().filter(x->{
            return (x.getE1().equals(e.getName()) && x.getE1Package().equals(e.getEntityPackage())) ||
                    (x.getE2().equals(e.getName()) && x.getE2Package().equals(e.getEntityPackage()));
        }).collect(Collectors.toSet());
    }

    public ArrayList<Tuple2<String, String>> generateEntityFiles(ProjectRequest projectRequest) throws IOException, ClassNotFoundException, FormatterException {

        String rootPath = "src/main/"+projectRequest.getLanguage().toLowerCase() + "/";//
        ArrayList<Tuple2<String, String>> result = new ArrayList<Tuple2<String, String>>();
        for(Entity e : projectRequest.getEntities()) {
            result.addAll(generateEntityFiles(
                    e,
                    projectRequest.getEntityRelations(),
                    projectRequest.getLanguage()
            , rootPath));
        }
        return result;

    }
    public String generateClient(Entity entity, String language) throws IOException, ClassNotFoundException {



        HashMap<String, String> binder = new HashMap<>();
        binder.put("clientPackage", entity.getClientPackage() );
        binder.put("entityPackage", entity.getEntityPackage()+"." + entity.getName());
        binder.put("entityName", entity.getName().toLowerCase());
        binder.put("entities", entity.getName().toLowerCase());

        binder.put("className", entity.getDatabaseType().equalsIgnoreCase("mongodb")? "Single<"+entity.getName()+">":
                entity.getName());
        binder.put("classNameA", entity.getName());

        String  serviceTemplate = templatesService.getClientTemplates().get(language.toLowerCase());


        return new SimpleTemplateEngine().createTemplate(serviceTemplate).make(binder).toString();
    }

    public ArrayList<Tuple2<String, String>> generateEntityFiles(Entity entity,
                                                                 ArrayList<EntityRelation> entityRelations,
                                                                 String language, String rootPath) throws IOException,
            ClassNotFoundException, FormatterException {
        String fileExtension = ".java";
        switch (language)
        {
            case JAVA_LANG:
                fileExtension =".java";
                break;
            case GROOVY_LANG:
                fileExtension =".groovy";
                break;
            case KOTLIN_LANG:
                fileExtension = ".kt";
                break;
            default:
                fileExtension = ".java";
                break;
        }
        ArrayList<Tuple2<String, String>> result = new ArrayList<Tuple2<String, String>>();
        result.add(Tuples.of(rootPath+entity.getEntityPackage().replace(".", "/")+ "/"+ entity.getName() + fileExtension, generateEntity(entity,
                entityRelations,
                language )));
        result.add(Tuples.of(rootPath+entity.getRepoPackage().replace(".", "/")+ "/"+entity.getName()+
                        "Repository" + fileExtension,
                generateRepository(entity,
                language )));
        result.add(Tuples.of(rootPath+entity.getServicePackage().replace(".", "/")+ "/"+ entity.getName() +"Service" + fileExtension, generateService(entity,
                language )));
        result.add(Tuples.of(rootPath+entity.getRestPackage().replace(".", "/")+ "/"+ entity.getName() +"Controller" +fileExtension,
                generateController(entity,
                language )));
        return result;

    }
    @Inject
    private GeneratorUtils generatorUtils;
    public File generateEntityFilesOnly(ProjectRequest projectRequest) throws Exception {
        return generatorUtils.generateZip(generateEntityFiles(projectRequest), projectRequest.getArtifact(),
                false);
    }
}
