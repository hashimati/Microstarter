package io.hashimati.microstarter.services;

/**
 * @author Ahmed Al Hashmi @hashimati
 */

import groovy.text.SimpleTemplateEngine;
import io.hashimati.microstarter.constants.ProjectConstants;
import io.hashimati.microstarter.entity.micronaut.FeaturesSkeleton;
import io.hashimati.microstarter.entity.micronaut.MicronautProfile;
import io.hashimati.microstarter.entity.micronaut.ProjectRequest;
import io.hashimati.microstarter.entity.micronaut.profiles.ProfileDetails;
import io.hashimati.microstarter.repository.FeatureSkeletonRepository;
import io.hashimati.microstarter.repository.MicronautProfileRepository;
import io.hashimati.microstarter.repository.ProfileDetailsRepository;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.hashimati.microstarter.constants.ProjectConstants.BuildConstants.MAVEN;
import static io.hashimati.microstarter.constants.ProjectConstants.LanguagesConstants.*;
import static io.hashimati.microstarter.constants.ProjectConstants.MicronautProfileConstants.*;
import static io.hashimati.microstarter.constants.ProjectConstants.MicronautServiceInfoTemplate;
import static io.hashimati.microstarter.constants.ProjectConstants.MicronautSkeletonFolders.SRC;
import static io.hashimati.microstarter.constants.ProjectConstants.SrcFolders.PACKAGE_DIR;
import static io.hashimati.microstarter.constants.ProjectConstants.ViewFramework.*;
import static io.hashimati.microstarter.generals.General.*;
import static io.hashimati.microstarter.util.GeneratorUtils.removeDuplicateLines;
import static io.hashimati.microstarter.util.ScanningUtils.resolveEntry;

@Singleton
public class CommonFilesGenerator {

    @Inject
    private FeatureSkeletonRepository featureSkeletonRepository;

    @Inject
    private MicronautProfileRepository micronautProfileRepository;
    @Inject
    private GeneratorUtils generatorUtils;

    @Inject
    private ProfileDetailsRepository profileDetailsRepository;

    @Inject
    private TemplatesService templatesService;


    public Tuple2<String, String> getApplicationFile(ProjectRequest projectRequest, ProfileDetails profileDetails,
                                                     ProfileDetails baseProfileDetails)
    {

        String applicationLangFeature =JAVA_LANG; //

        switch (projectRequest.getProfile()){

            case FUNCTION_AWS_ALEXA:
                applicationLangFeature = FUNCTION_AWS_ALEXA + "-"+projectRequest.getLanguage();
                break;
            case FUNCTION_AWS:
                applicationLangFeature = FUNCTION_AWS +"-"+projectRequest.getLanguage();
                break;
            default:
                applicationLangFeature = projectRequest.getLanguage();
        }
        FeaturesSkeleton featureSkeletonDetails = featureSkeletonRepository.findDistinctByName(applicationLangFeature);


        String t1=
                featureSkeletonDetails.getSkeleton()
                        .get(SRC)
                        .keySet()
                        .stream()
                        .filter(x->x.contains(ProjectConstants.Extensions.JAVA)
                                || x.contains(ProjectConstants.Extensions.GROOVY)
                                || x.contains(ProjectConstants.Extensions.KOTLIN))
                        .findFirst()
                        .get();
        String content = featureSkeletonDetails.getSkeleton().get(SRC).get(t1).replace("@project.propertyName@",
                projectRequest.getArtifact()+"Function");
        t1 = t1.replaceAll( PACKAGE_DIR.replace(".","___"), projectRequest.getPackage());

        content = content.replaceAll("@defaultPackage@", projectRequest.getPackage());
        Tuple2<String, String> result = Tuples.of(t1.replace(".", "/").replaceAll("___", "."), content);
        return result;
    }
    public ArrayList<Tuple2<String, String>> getSRCFiles(ProjectRequest projectRequest, ProfileDetails profileDetails, ProfileDetails baseProfileDetails){

        ArrayList<Tuple2<String, String>> result = new ArrayList<Tuple2<String, String>>();

        HashSet<String> dependencies = generatorUtils.marshalAllDependencies(projectRequest, profileDetails,
                getRequiredProfileDetailsAsArray(profileDetails));

        //To exclude Application file of the Language feature
        if(projectRequest.getProfile().equalsIgnoreCase(FUNCTION_AWS_ALEXA) || projectRequest.getProfile().equalsIgnoreCase(CLI))
        {
            dependencies.remove(projectRequest.getLanguage().toLowerCase());

        }
//System.out.println("These are the dependencies" + dependencies);
        for(String d : dependencies){
            if(LANGUAGES.contains(d.toLowerCase()))
            {
                if(!d.equalsIgnoreCase(projectRequest.getLanguage()))
                    continue;
            }
            FeaturesSkeleton featuresSkeleton = featureSkeletonRepository.findDistinctByName(d);
            if(featuresSkeleton.getSkeleton().get(SRC) == null) continue;
            if(featuresSkeleton.getSkeleton().get(SRC) != null) {

//                System.out.println(featuresSkeleton.getSkeleton().get(SRC));
                HashMap<String, String> src = featuresSkeleton.getSkeleton().get(SRC);
                List<Tuple2<String, String>> tuples = new ArrayList<Tuple2<String, String>>();


//                System.out.println(src.keySet());
                for (String x : src.keySet()) {

                   // System.out.println(x + " :::" + src.get(x));
                    Tuple2<String, String> of = Tuples.of(
                                x.replace(PACKAGE_DIR.replace(".", "___"),
                                        projectRequest.getPackage()).replace(".", "/").replace("___", ".").replace(
                                        "@project.className@", projectRequest.getFunctionName())
                            .replace("@app.name@", projectRequest.getArtifact())
                                        .replace("@defaultPackage@", projectRequest.getPackage())
                                ,

                                src.get(x).replace("@defaultPackage@", projectRequest.getPackage()).replace(
                                        "@project.className@", projectRequest.getFunctionName()).replace(
                                                "@project" +
                                        ".name@", projectRequest.getFunctionName())
                                        .replace("@mainClassName@", profileDetails.getMainClassName(projectRequest.getPackage()))

                                        .replace("@app.name@", projectRequest.getArtifact()));


                        tuples.add(of);
                }
                //add view Controller here
                if(!projectRequest.getViewFramework().equalsIgnoreCase("none")){
                    tuples.add(Tuples.of(
                            SRC+"/main/resources/views/index.html", homePageTemplate
                    ));

                    tuples.add(Tuples.of(
                            SRC+"/main/"+projectRequest.getLanguage().toLowerCase()+"/"
                            +projectRequest.getPackage().replace(".", "/") + "/controllers/ViewController."
                            +(projectRequest.getLanguage().equalsIgnoreCase(KOTLIN_LANG)?"kt":
                                    projectRequest.getLanguage().toLowerCase()),
                            (projectRequest.getLanguage().equalsIgnoreCase(KOTLIN_LANG)?Controller_kotlin:(
                                    (projectRequest.getLanguage().equalsIgnoreCase(JAVA_LANG)?Controller_java:Controller_groovy
                                    ))).replace("${package}",projectRequest.getPackage()+".controllers")
                                        ));

                }

                // end adding viewController


                result.addAll(tuples);
            }
        }
//
//        for(String d : dependencies)
//        {
//
//            if(LANGUAGES.contains(d.toLowerCase())){
//                if(!d.equalsIgnoreCase(projectRequest.getLanguage()))
//                    continue;
//            }
//
//
//            FeaturesSkeleton featureSkeletonDetails = featureSkeletonRepository.findDistinctByName(d);
//
//            if(featureSkeletonDetails.getSkeleton().get(SRC) != null) {
//                HashMap<String, String> src = featureSkeletonDetails.getSkeleton()
//                        .get(SRC);
//                List<Tuple2<String, String>> tuples = new ArrayList<>();
//              if(src != null)
//                for (String x : src.keySet()) {
//
//                    if(x.contains(PACKAGE_DIR)) {
//                        Tuple2<String, String> of = Tuples.of(
//                                x.replaceAll(PACKAGE_DIR.replace(".", "___"),
//                                        projectRequest.getPackage()).replace(".", "/").replaceAll("___", ".").replaceAll(
//                                        "@project.className@", projectRequest.getFunctionName())
//                                ,
//
//                                src.get(x).replaceAll("@defaultPackage@", projectRequest.getPackage()).replaceAll(
//                                        "@project.className@", projectRequest.getFunctionName()).replaceAll("@project" +
//                                        ".name@", projectRequest.getFunctionName()));
//                        tuples.add(of);
//                    }
//                }
//
//                  result.addAll(tuples);
//
//
//            }

            return result;
        }

    private ProfileDetails[] getRequiredProfileDetailsAsArray(ProfileDetails profileDetails) {
        return profileDetails.getRequiredProfiles().stream().map(f->profileDetailsRepository.findDistinctByProfileName(f)).collect(Collectors.toList()).toArray(new ProfileDetails[profileDetails.getRequiredProfiles().size()]);

    }


    public ArrayList<Tuple2<String, String>> getAgentFiles(ProjectRequest projectRequest,
                                                           ProfileDetails profileDetails,
                                                           ProfileDetails baseProfileDetails){

        ArrayList<Tuple2<String, String>> result = new ArrayList<Tuple2<String, String>>();

        HashSet<String> dependencies = generatorUtils.marshalAllDependencies(projectRequest, profileDetails,
                getRequiredProfileDetailsAsArray(profileDetails));

        String agent = "agent";
        for(String d : dependencies)
        {
            FeaturesSkeleton featureSkeletonDetails = featureSkeletonRepository.findDistinctByName(d);
            if(featureSkeletonDetails.getSkeleton().get(agent) != null) {
                HashMap<String, String> agents = featureSkeletonDetails.getSkeleton()
                        .get(agent);
                List<Tuple2<String, String>> tuples = agents.keySet().stream()
                        .map(x-> Tuples.of(x.replaceAll("___","."), agents.get(x)))
                        .collect(Collectors.toList());
                result.addAll(tuples);
            }
        }
        return result;
    }

    public Tuple2<String, String> getYmlResource2(ProjectRequest projectRequest, ProfileDetails profileDetails,
                                                  ProfileDetails baseProfileDetails, String filename, String fileFullPath) {

        //1) get all dependencies
        HashSet<String> dependencies = generatorUtils.marshalAllDependencies(projectRequest, profileDetails,
                getRequiredProfileDetailsAsArray(profileDetails));
        //2) dependencies skeletons.
        Stream<FeaturesSkeleton> dependenciesSkeletons = dependencies.stream()
                .map(x -> featureSkeletonRepository.findDistinctByName(x)).filter(x -> x.getSkeleton().get(SRC) != null)
                ;


        //3)
        return null;

    }
        public Tuple2<String, String> getYmlResource(ProjectRequest projectRequest, ProfileDetails profileDetails,
                                                     ProfileDetails baseProfileDetails, String filename, String fileFullPath) throws IOException, ClassNotFoundException {

        String serviceInfo = new SimpleTemplateEngine()
                .createTemplate(MicronautServiceInfoTemplate).make(
                        new HashMap<String, String>(){{
                            put("servicename", projectRequest.getArtifact());
                            put("port", ""+projectRequest.getPort());
            }}).toString() + "\n";
            //--------------- Adding view Framwork;
            if(projectRequest.getProfile().equalsIgnoreCase(SERVICE) && !projectRequest.getViewFramework().equalsIgnoreCase("none")) {
                serviceInfo += "\n" +  VIEW_PROPERTIES.replace("${framework}",
                        projectRequest.getViewFramework().toLowerCase())
                        +"\n" +  STATIC_PATH ;
            }
            //---------------

            //=============add Jdbc

            String h2Dependencies = "datasources:\n" +
                    "  default:\n" +
                    "    url: jdbc:h2:mem:devDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE\n" +
                    "    driverClassName: org.h2.Driver\n" +
                    "    username: sa\n" +
                    "    password: ''\n" +
                    "    dialect: H2";
            String jdbcProperty ="";
            if(!projectRequest.getEntities().isEmpty()) {
                if (!projectRequest.getDatabaseType().equalsIgnoreCase("mongodb") ) {
                    if (!projectRequest.getDatabaseType().equalsIgnoreCase("h2")) {
                        String jdbcTemplate =
                                templatesService.getJdbcPropertiesTemplates().get(projectRequest.getDatabaseType().toLowerCase());

                        jdbcProperty = jdbcTemplate.replace("${databasename}", projectRequest.getDatabaseName()) + "\n";
                    }
                }
                else if(projectRequest.getDatabaseType().equalsIgnoreCase("h2"))
                {
                    jdbcProperty = h2Dependencies + "\n";
                }

            }
            //===============


        DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        final Yaml yaml = new Yaml(options);

        HashSet<String> dependencies = generatorUtils.marshalAllDependencies(projectRequest, profileDetails,
                getRequiredProfileDetailsAsArray(profileDetails));



        if(dependencies.contains("data-jdbc") && dependencies.contains("data-hibernate-jpa")){
            dependencies.remove("data-jdbc");
        }



        Tuple2<String, String> fff = Tuples.of(fileFullPath, (fileFullPath.contains("application")?serviceInfo:"") + dependencies.stream().map(x ->
                featureSkeletonRepository.findDistinctByName(x)
        ).filter(x -> x.getSkeleton().get(SRC) != null).filter(x -> x.getSkeleton().get(SRC)
                .containsKey(fileFullPath.replace(".", "___")))
                .map(x -> {

                    String result = x.getSkeleton()
                            .get(SRC).get(fileFullPath.replace(".", "___")) == null ? "" : x.getSkeleton()
                            .get(SRC).get(fileFullPath.replace(".", "___"));

                    return result.trim();
                })
                .filter(x -> !x.equalsIgnoreCase("") || x.isEmpty())
                .reduce("",
                        (x, y) ->
                                x + "\n\n---\n\n" + y).replaceAll("@project.name@",
                        projectRequest.getArtifact()).replaceAll("@app.name@",
                        projectRequest.getArtifact()).replace(h2Dependencies, jdbcProperty));


        return fff;

    }

    public Tuple2<String, String> generateMicronautCli(ProjectRequest projectRequest)
    {
        MicronautProfile profile = micronautProfileRepository.findDistinctByName(BASE).blockingGet();
        String t2 = profile.getSkeleton().get("cli").get("/micronaut-cli___yml")
                .replace("@profile@", projectRequest.getProfile())
                .replace("@defaultPackage@", projectRequest.getPackage())
                .replace("@testFramework@", projectRequest.getTestframework())
                .replace("@sourceLanguage@", projectRequest.getLanguage());
        return Tuples.of(resolveEntry("/micronaut-cli.yml"), t2);
    }




    public List<Tuple2<String, String>> auxGetRestOfBuildFiles(ProjectRequest projectRequest, String p, String build)
    {
        MicronautProfile profile = micronautProfileRepository.findDistinctByName(p).blockingGet();
        ProfileDetails profileDetails = profileDetailsRepository.findDistinctByProfileName(p);
        HashMap<String, String> skeleton= profile.getSkeleton().get(build);
        String ignoredFile = build.equalsIgnoreCase(MAVEN)? "/pom___xml":"/build___gradle";


        HashSet<String> allDependencies = generatorUtils.marshalAllDependencies(projectRequest, profileDetails,
                getRequiredProfileDetailsAsArray(profileDetails));

//        HashMap<String, String> restOfils = new HashMap<String, String>();
//        for(String s:allDependencies){
//            FeaturesSkeleton featureSkeleton = featureSkeletonRepository.findDistinctByName(s);
//            for(String key: featureSkeleton.getSkeleton().keySet())
//            {
//
//                int counter = 0;
//                String attachedString ="";
//                while(restOfils.containsKey(key+attachedString)){
//                    attachedString  = "_"+ ++counter;
//                }
//                restOfils.put(resolveEntry(key+attachedString),generatorUtils.replacePlaceHolders(projectRequest,
//                        skeleton.get(key)));
//            }
//        }

        List<Tuple2<String, String>> tuples = skeleton.keySet().stream()
                .filter(x->!x.equalsIgnoreCase(ignoredFile))
                .map(x->{
//                    System.out.println(x);
                    return Tuples.of(resolveEntry(x),removeDuplicateLines(generatorUtils.replacePlaceHolders(projectRequest,
                            skeleton.get(x))));
                }).collect(Collectors.toList());


//        System.out.println(removeDockerFile(projectRequest));
//        if(removeDockerFile(projectRequest)) {
//            tuples = tuples.stream().filter(x -> x.getT1().equalsIgnoreCase("/Dockerfile")).collect(Collectors.toList());
//            for(String key: restOfils.keySet()){
//                tuples.add(Tuples.of(key, restOfils.get(key))) ;
//            }
//        }


        return tuples;
    }
    public boolean removeDockerFile(ProjectRequest projectRequest)
    {
        return projectRequest
                .getDependencies()
                .stream()
                .filter(x->x.toLowerCase().indexOf("graal")>= 0 || x.toLowerCase().indexOf("openfaas") >=0).collect(Collectors.toList())
                .size()>0;

    }
    public ArrayList<Tuple2<String, String>> auxGetRestOfBuildFilesFeatures(ProjectRequest projectRequest,
                                                                            ProfileDetails profileDetails,
                                                                            ProfileDetails baseProfiles, String build)
    {

        ArrayList<Tuple2<String, String>> result = new ArrayList<Tuple2<String, String>>();
        HashSet<String> allDependencies = generatorUtils.marshalAllDependencies(projectRequest, profileDetails,
                getRequiredProfileDetailsAsArray(profileDetails));

        String ignoredFile = build.equalsIgnoreCase(MAVEN)? "/pom___xml":"/build___gradle";
        //     System.out.println("ttt "+ allDependencies);
        for(String f : allDependencies)
        {
            FeaturesSkeleton feature = featureSkeletonRepository.findDistinctByName(f);


            if(feature.getSkeleton().get(build) !=null) {
                HashMap<String, String> skeleton = feature.getSkeleton().get(build);



                List<Tuple2<String, String>> tuples = skeleton.keySet().stream()
                        .filter(x -> !x.equalsIgnoreCase(ignoredFile))
                        .map(x -> {
                            //     System.out.println("ttt " +x);
                            return Tuples.of(resolveEntry(x), removeDuplicateLines(generatorUtils.replacePlaceHolders(projectRequest,
                                    skeleton.get(x))));
                        }).collect(Collectors.toList());

                result.addAll(tuples);
            }


                HashMap<String, String> rootFile = feature.getRootFiles();



                List<Tuple2<String, String>> tuples = rootFile.keySet().stream()
                        .filter(x -> !x.equalsIgnoreCase(ignoredFile))
                        .map(x -> {
                            //     System.out.println("ttt " +x);
                            return Tuples.of(resolveEntry(x), generatorUtils.replacePlaceHolders(projectRequest,
                                    rootFile.get(x)));
                        }).collect(Collectors.toList());

                result.addAll(tuples);

        }

        return result;

    }

    public ArrayList<Tuple2<String, String>> getRestOfBuildFiles(ProjectRequest projectRequest, String build)
    {


        List<Tuple2<String, String>> baseTuples = auxGetRestOfBuildFiles(projectRequest, BASE, build);

        List<Tuple2<String, String>> prTuples = auxGetRestOfBuildFiles(projectRequest, projectRequest.getProfile(),
                build);

        List<Tuple2<String, String>> federationTuples = auxGetRestOfBuildFiles(projectRequest, "federation", build);


        ArrayList<Tuple2<String, String>> featuresTuples = auxGetRestOfBuildFilesFeatures(projectRequest,
                profileDetailsRepository.findDistinctByProfileName(projectRequest.getProfile()),
                profileDetailsRepository.findDistinctByProfileName(BASE), build);


        ArrayList<Tuple2<String, String>> result = new ArrayList<Tuple2<String, String>>();
        if(!baseTuples.isEmpty())
            result.addAll(baseTuples);
        if(!prTuples.isEmpty()) {
            result.addAll(prTuples);

        }
        if(!federationTuples.isEmpty())
            result.addAll(federationTuples);
        if(!featuresTuples.isEmpty())
            result.addAll(featuresTuples);


        return result;
    }


    public static Tuple2<String, String> getLogBack()
    {

        //todo: This method need to be reimplemented later.
        return Tuples.of("/src/main/resources/logback.xml", "<configuration>\n" +
                "\n" +
                "    <appender name=\"STDOUT\" class=\"ch.qos.logback.core.ConsoleAppender\">\n" +
                "        <withJansi>false</withJansi>\n" +
                "        <!-- encoders are assigned the type\n" +
                "             ch.qos.logback.classic.encoder.PatternLayoutEncoder by default -->\n" +
                "        <encoder>\n" +
                "            <pattern>%cyan(%d{HH:mm:ss.SSS}) %gray([%thread]) %highlight(%-5level) %magenta(%logger{36}) - %msg%n</pattern>\n" +
                "        </encoder>\n" +
                "    </appender>\n" +
                "\n" +
                "    <root level=\"info\">\n" +
                "        <appender-ref ref=\"STDOUT\" />\n" +
                "    </root>\n" +
                "</configuration>\n");
    }
//    public static String getDockerFile(String build)
//    {
//            String jarPath = build.toLowerCase().contains("maven")? "target/*.jar":"build/libs/*.jar";
//            return  "FROM adoptopenjdk/openjdk11-openj9:jdk-11.0.1.13-alpine-slim\n"+
//           "COPY @jarPath@ @app.name@.jar\n"
//            +"EXPOSE 8080\n"
//            +"CMD java -Dcom.sun.management.jmxremote -noverify ${JAVA_OPTS} -jar @app.name@.jar\n".replace("@jarPath@", jarPath) ;
//    }
    public static String getDockerFile(String build)
    {
        String jarPath = build.toLowerCase().contains("maven")? "target/*.jar":"build/libs/*.jar";
        return "FROM openjdk:14-alpine\n" +
                "COPY @jarPath@ @app.name@.jar\n" +
                "EXPOSE 8080\n" +
                "CMD [\"java\", \"-Dcom.sun.management.jmxremote\", \"-Xmx128m\", \"-jar\", \"@app.name@.jar\"]".replace("@jarPath@", jarPath) ;
    }
    public static String getBuildSpecYmlFile(String build)
    {
        String command = build.toLowerCase().contains("maven")? "mvn install":"./gradlew build";
        String jarPath = build.toLowerCase().contains("maven")? "target/*.jar":"build/libs/*.jar";
        
        return "version: 0.2\n\n" +

                "phases:\n"+  
                "  build:\n" +
                "    commands:\n" +
                "      - echo building the application\n" +
                "      - @buildcommand@\n\n".replace("@buildcommand@", command)+ 
                
                // "---\n"+
                "artifacts:\n"+ 
                "  files:\n"+ 
                "    - @jarPath@\n".replace("@jarPath@", jarPath)+ 
                "  discard-paths: yes\n\n"+ 
                
                // "---\n"+ 
                "cache:\n"+ 
                "  paths:\n"+
                "    - '/root/.m2/**/*'\n"; 
    }
}
