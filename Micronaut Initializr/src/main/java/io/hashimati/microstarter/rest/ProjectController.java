package io.hashimati.microstarter.rest;

/**
 * @author Ahmed Al Hashmi @hashimati
 */

import io.hashimati.microstarter.entity.Entity;
import io.hashimati.microstarter.entity.micronaut.ProjectRequest;
import io.hashimati.microstarter.services.GeneratorUtils;
import io.hashimati.microstarter.services.GradleProjectGenerator;
import io.hashimati.microstarter.services.MavenProjectGenerator;
import io.hashimati.microstarter.services.MicronautEntityGenerator;
import io.hashimati.microstarter.util.ProjectRequestValidators;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.runtime.Micronaut;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.io.FileUtils;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.hashimati.microstarter.constants.ProjectConstants.BuildConstants.GRADLE;

@Controller("/api/project")
public class ProjectController {


    @Value("${micronaut.currentVersion}")
    private String micronautVersion;



    @Inject
    private GeneratorUtils generatorUtils;

    @Inject
    private GradleProjectGenerator gradleProjectGenerator;
    @Inject
    private MavenProjectGenerator mavenProjectGenerator;

    @Inject
    private ProjectRequestValidators projectRequestValidators;
    @Inject
    private MicronautEntityGenerator micronautEntityGenerator;


    @Post(value ="/submit/entityrequest", produces = MediaType.APPLICATION_OCTET_STREAM)
    @Operation(summary = "Entities Files",
            description = "This service retrieves entities files only: \n+1.Class Files\n2.Repository\n3.Services\n4" +
                    ".Controll0ers"
    )

    @ApiResponse(
            content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM,
                    schema = @Schema(type="file"))
    )
    @Tag(name = "projects")
    public Single<File> getEntityFiles(@Body ProjectRequest projectRequest) throws Exception {
        for(Entity e : projectRequest.getEntities())
        {
            e.setEntityPackage(projectRequest.getDefaultPackage() + "."+"domains");
            e.setRepoPackage(projectRequest.getDefaultPackage() + "."+"repositories");
            e.setServicePackage(projectRequest.getDefaultPackage() + "."+"services");
            e.setRestPackage(projectRequest.getDefaultPackage() + "." + "resources");
            e.setClientPackage(projectRequest.getDefaultPackage() + "."+"clients");

            e.setDatabaseType(projectRequest.getDatabaseType());
            e.setDatabaseName(projectRequest.getDatabaseName());
        }
        File file = micronautEntityGenerator.generateEntityFilesOnly(projectRequest);
        Path path = Paths.get(file.getAbsolutePath());
        return Single.just(file)
                .doOnSuccess(x->{
                    System.out.println("The file is delivered Successfully");
                })
                .doOnError(x->{
                    System.out.println("Something is going wrong");
                }).doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                        FileUtils.forceDelete(file);
                    }
                });
    }

    @Post(value = "/submit/projectrequest", produces = MediaType.APPLICATION_OCTET_STREAM)
    @Operation(summary = "Entities Files",
            description = "This retrieves a project files based on Project requests parameters"
    )

    @ApiResponse(
            content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM,
                    schema = @Schema(type="file"))
    )
    @Tag(name = "projects")
    public Single<File> submitProjectRequest(@Body ProjectRequest projectRequest) throws Exception {

        System.out.println(projectRequest);
        if(projectRequest.getFunctionName() == null)
            projectRequest.setFunctionName(projectRequest.getArtifact());
        projectRequest.setVersion(micronautVersion);
        System.out.println(projectRequest);

        projectRequestValidators.removingIncompatibleDependcies(projectRequest);
        System.out.println(projectRequest);
        File file = projectRequest.getBuild().toLowerCase().equalsIgnoreCase(GRADLE)?   gradleProjectGenerator.generateProject(projectRequest)
                :mavenProjectGenerator.generateMavenProject(projectRequest);
        return Single.just(file)
                .doOnSuccess(x->{
                    System.out.println("The file is delivered Successfully");
                })
                .doOnError(x->{
                    System.out.println("Something is going wrong");
            }).doFinally(new Action() {
                    @Override
                    public void run() throws Exception {
                       FileUtils.forceDelete(file);
                    }
            });
    }//


    @Post("/get/MnCommand")
    public String mnCommand(@Body ProjectRequest projectRequest){
        String command = generatorUtils.generateMNCommand(projectRequest, null, null)
                .replace("--features null", "");

        System.out.println(command);
        return command;
    }




}
