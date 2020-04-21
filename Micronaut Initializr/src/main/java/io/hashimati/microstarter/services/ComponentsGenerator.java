package io.hashimati.microstarter.services;

/**
 * @author Ahmed Al Hashmi @hashimati
 */

import io.hashimati.microstarter.entity.micronaut.MicronautProfile;
import io.hashimati.microstarter.entity.micronaut.ProjectRequest;
import io.hashimati.microstarter.repository.MicronautProfileRepository;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


/**
 * This class to generate component like: Controller, websockets, job, bean, listener, producer... etc.
 *      ${className}
 *  * ${propertyName}
 *  * {topic}
 *  * ${packageName}
 *
 */
@Singleton
public class ComponentsGenerator
{
    @Inject
    private MicronautProfileRepository micronautProfileRepository;

    @Inject
    private GeneratorUtils generatorUtils;
    public ArrayList<Tuple2<String, String>> generateComponents(ProjectRequest projectRequest)
    {
      return  new ArrayList<Tuple2<String, String>>(){{
          addAll(generateSrcComponents(projectRequest));
          addAll(generateProtoComponents(projectRequest));
      }};
    }
    public List<Tuple2<String, String>> generateSrcComponents(ProjectRequest projectRequest)
    {
//        MicronautProfile profile =
//                micronautProfileRepository.findDistinctByName(projectRequest.getProfile()).get();

        ArrayList<Tuple2<String, String>> result = new ArrayList<Tuple2<String, String>>();

        AtomicReference<String> langExtension =
                new AtomicReference<String>("___java");
        switch (projectRequest.getLanguage().toLowerCase())
        {
            case "java":
                langExtension.set( "___java");
                break;
            case "groovy":
                langExtension.set("___groovy");
                break;
            case "kotlin":
                langExtension.set("___kt");
                break;
            default:
                langExtension.set("___java");
                break;

        }

       return  projectRequest.getCompontentObjects().stream()
       //.filter(x->!x.getProfile().equalsIgnoreCase("grpc"))
       .map(x->{

            MicronautProfile profile =
                    micronautProfileRepository.findDistinctByName(x.getProfile().toLowerCase()).blockingGet();

            //get Contents by languages.
            HashMap<String, String> templatesByLanguage =
                   profile.getTemplates().get(projectRequest.getLanguage().toLowerCase());


            String mainFolder =   x.getType().toLowerCase().contains("test") ||
                    x.getType().toLowerCase().contains("spec") ?
                    "test": "main";

            String path ="src/" +
                    mainFolder + "/" + projectRequest.getLanguage().toLowerCase() +"/" +
                            (projectRequest.getPackage() +"."+ x.getPackagePath()).replace(".", "/") + "/"
                    + x.getName() + langExtension.get().replace("___", ".");
//            System.out.println("Main Path " + path);
            //get file contents;
           System.out.println("___________________");
           System.out.println(x);
           System.out.println(x.getType() + langExtension.get());
           System.out.println(templatesByLanguage.get(x.getType() + langExtension.get()) == null);
           System.out.println("___________________");

           String fileContents = templatesByLanguage
                    .get(x.getType() + langExtension.get()).replace("${packageName}",projectRequest.getPackage() +
                            "." + x.getPackagePath()).replace("/${propertyName}", x.getPropertyPath()==null?"":
                            x.getPropertyPath()).replace(

                            "{topic}",
                            x.getTopic()==null?"":x.getTopic()).replace("${className}",
                            x.getName().replaceAll(
                            " ", "")).replace("${packageName ? 'package ' + packageName + ';' : '' }",
                            "package "+ projectRequest.getPackage()+"." + x.getPackagePath() +";")
                    .replace("${packageName ? 'package ' + packageName + ';' : ''}","package " +
                            projectRequest.getPackage()+"." + x.getPackagePath()+ ";").replace("${packageName ? " +
                           "'package ' + packageName : '' }", "package " +
                           projectRequest.getPackage()+"." + x.getPackagePath()).replace("${propertyName}", x.getPropertyPath());

//            if(x.getPropertyPath() != null){
//                fileContents = fileContents.replace("${propertyName}", x.getPackagePath());
//            }
            return Tuples.of(path, fileContents);
        }).collect(Collectors.toList());

    }
    public List<Tuple2<String, String>> generateProtoComponents(ProjectRequest projectRequest)
    {
//        MicronautProfile profile =
//                micronautProfileRepository.findDistinctByName(projectRequest.getProfile()).get();

        ArrayList<Tuple2<String, String>> result = new ArrayList<Tuple2<String, String>>();

        AtomicReference<String> langExtension =
                new AtomicReference<String>("___proto");


        return  projectRequest.getCompontentObjects().stream().filter(x->x.getProfile().equalsIgnoreCase("grpc")).map(x->{

            MicronautProfile profile =
                    micronautProfileRepository.findDistinctByName(x.getProfile().toLowerCase()).blockingGet();

            //get Contents by languages.
            HashMap<String, String> templatesByLanguage =
                    profile.getTemplates().get("/");


            String mainFolder ="/src/proto/";


            String path =
                    mainFolder + x.getName() + langExtension.get().replace("___", ".");

            //get file contents;
            String fileContents = templatesByLanguage
                    .get("service" + langExtension.get()).replace("${packageName}", projectRequest.getPackage()+"." + x.getPackagePath()).replace(
                            "${className}",
                            x.getName().replace(" ", ""))
                    .replace("${packageName ? 'package ' + packageName + ';' : ''}", path);

            if(x.getPropertyPath() != null){
                fileContents = fileContents.replace("${propertyName}", x.getPackagePath());
            }
            return Tuples.of(path, fileContents);
        }).collect(Collectors.toList());

    }

}
