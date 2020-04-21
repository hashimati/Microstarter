package io.hashimati.microstarter.rest;
/**
 * @author Ahmed Al Hashmi @hashimati
 */


import com.google.googlejavaformat.java.FormatterException;
import io.hashimati.microstarter.constants.ProjectConstants;
import io.hashimati.microstarter.entity.Entity;
import io.hashimati.microstarter.entity.EntityAttribute;
import io.hashimati.microstarter.entity.EntityRelation;
import io.hashimati.microstarter.entity.EntityRelationType;
import io.hashimati.microstarter.entity.micronaut.MicronautFeature;
import io.hashimati.microstarter.repository.MicronautFeatureRepository;
import io.hashimati.microstarter.repository.ProfileDetailsRepository;
import io.hashimati.microstarter.services.MicronautEntityGenerator;
import io.hashimati.microstarter.services.TemplatesService;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.validation.Validated;
import io.reactivex.Flowable;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;

import static io.hashimati.microstarter.constants.ProjectConstants.BuildConstants.BUILDS;
import static io.hashimati.microstarter.constants.ProjectConstants.LanguagesConstants.GROOVY_LANG;
import static io.hashimati.microstarter.constants.ProjectConstants.LanguagesConstants.LANGUAGES;
import static io.hashimati.microstarter.constants.ProjectConstants.MicronautProfileConstants.PROFILES;

@Controller("/api")
@Validated
public class DependencyController {



    @Inject
    private MicronautFeatureRepository micronautFeaturesRepository;

    @Inject
    private MicronautEntityGenerator micronautEntityGenerator;

    @Inject
    private TemplatesService templatesService;

    @Inject
    private ProfileDetailsRepository profileDetailsRepository;




    @Get("/loaddependicies/{micronaut}")
    @Operation(summary = "Micronaut Features List",
            description = "To get a list of Micronaut features"
    )
    @ApiResponse(
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(type="ArrayJson"))
    )
    @Tag(name = "features")
    public List<MicronautFeature> loadDependencies(@PathVariable("micronaut") String micronaut)
    {


        return micronautFeaturesRepository.findAll().filter(
                x-> needToBeRemoved(x.getName())
        ).toList().blockingGet();//.filter(x->!x.getName().contains("pico"))

        
    }










    private boolean needToBeRemoved(String x)
    {
       return !Arrays.asList("java", "kotlin", "groovy", "junit", "spek", "spock").contains(x.toLowerCase().trim());
    }

//    @GetMapping("/loadprofiles")
//    public ArrayList<ProfileDetails> loadProfiles()
//    {
//
//        return profileDetailsRepository.findAll();
//
//    }
    @Get("/getallprofiles")
    public List<String> profiles()
    {
        return PROFILES;
    }
    @Get("/getlanguages")
    public List<String> languages()
    {
        return LANGUAGES;
    }
    @Get("/getbuilds")
    public List<String> builds()
    {
        return BUILDS;
    }
    
    @Get("gettemplatecategories")
    public List<String> getTemplatesCategory()
    {
        return Arrays.asList("Service","Base", "Kafka", "GRPC","RabbitMQ");

    }
    @Get("/testEntity")
    public String testEntity() throws IOException, ClassNotFoundException, FormatterException {
 //       templatesService.getEntityTemplates().get("java");

        Entity entity = new Entity(){{
            setName("Ahmed");
            setDatabaseType("mysql");
            setEntityPackage("com.ahmed.ahl");
            setRepoPackage("com.ahmed.ah1.repo");
            setServicePackage("com.ahmed.ah1.services");
            setRestPackage("com.ahmed.ah1.resources");

            getAttributes().add(new EntityAttribute(){{
                setName("ie");
                setType(ProjectConstants.EntityAttributeType.INTEGER);
                setCollectionName("Ahmed");
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

        return micronautEntityGenerator.generateEntity(entity, relations, "java")
        + micronautEntityGenerator.generateRepository(entity, "java")
        + micronautEntityGenerator.generateService(entity, "java")
                + micronautEntityGenerator.generateController(entity, "java")
                + micronautEntityGenerator.generateEntity(entity, relations ,GROOVY_LANG)
                + micronautEntityGenerator.generateRepository(entity, "groovy")

                + micronautEntityGenerator.generateService(entity, "groovy")
                + micronautEntityGenerator.generateController(entity, "groovy")
                + micronautEntityGenerator.generateEntity(entity,relations ,"kotlin")

                + micronautEntityGenerator.generateRepository(entity, "kotlin")
                + micronautEntityGenerator.generateService(entity, "kotlin")
                + micronautEntityGenerator.generateController(entity, "kotlin");

    }
    @Get("/getcomponentstypes")
    public HashMap<String, List<String>> getComponentsList()
    {
        HashMap<String, List<String>> result = new HashMap<>();
       for(String s: getTemplatesCategory())
        switch (s.toLowerCase())
        {
            case "base":
                result.put(s, ProjectConstants.BaseTemplates.LIST);
                break;
            case "kafka":
                result.put(s, ProjectConstants.KafkaTemplates.LIST);
                break;
            case "service":
                result.put(s, ProjectConstants.ServiceTemplates.LIST);
                break;
            case "grpc":
                result.put(s, ProjectConstants.GRPCTemplates.LIST);
                break;
            case "rabbitmq":
                result.put(s, ProjectConstants.RabbitmqTemplates.LIST);
                break;
            default:
                result = null;
                break;
        }
        return result;
    }
}
