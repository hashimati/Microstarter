package io.hashimati.microstarter.services;
/**
 * @author Ahmed Al Hashmi @hashimati
 */


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.hashimati.microstarter.config.MappedVariable;
import io.hashimati.microstarter.entity.micronaut.FeaturesSkeleton;
import io.hashimati.microstarter.entity.micronaut.MicronautFeature;
import io.hashimati.microstarter.entity.micronaut.MicronautProfile;
import io.hashimati.microstarter.entity.micronaut.ProjectRequest;
import io.hashimati.microstarter.entity.micronaut.features.Dependency;
import io.hashimati.microstarter.entity.micronaut.profiles.ProfileDetails;
import io.hashimati.microstarter.repository.FeatureSkeletonRepository;
import io.hashimati.microstarter.repository.MicronautFeatureRepository;
import io.hashimati.microstarter.repository.MicronautProfileRepository;
import io.hashimati.microstarter.repository.ProfileDetailsRepository;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.zip.ZipOutputStream;

import static io.hashimati.microstarter.constants.ProjectConstants.BuildConstants.GRADLE;
import static io.hashimati.microstarter.constants.ProjectConstants.GradleFilesConstants.BUILD;
import static io.hashimati.microstarter.constants.ProjectConstants.LanguagesConstants.*;
import static io.hashimati.microstarter.constants.ProjectConstants.MicronautProfileConstants.BASE;
import static io.hashimati.microstarter.constants.ProjectConstants.MicronautProfileConstants.SERVICE;
import static io.hashimati.microstarter.constants.ProjectConstants.SrcFolders.*;
import static io.hashimati.microstarter.constants.ProjectConstants.ViewFramework.*;
import static io.hashimati.microstarter.services.CommonFilesGenerator.*;
import static io.hashimati.microstarter.util.GeneratorUtils.invokeProjectRequestMethod;
import static io.hashimati.microstarter.util.GeneratorUtils.removeDuplicateLines;
import static io.hashimati.microstarter.util.ScanningUtils.resolveEntry;

@Singleton
public class GradleProjectGenerator implements ProjectGenerator {

    @Inject
    private FeatureSkeletonRepository featureSkeletonRepository;

    @Inject
    private MicronautProfileRepository micronautProfileRepository;

    @Inject
    private MicronautFeatureRepository micronautFeatureRepository;

    @Inject
    private ProfileDetailsRepository profileDetailsRepository;

    @Inject
    private CommonFilesGenerator commonFilesGenerator;
    @Inject
    private GeneratorUtils generatorUtils;
    @Inject
    private ComponentsGenerator componentsGenerator;

    @Inject
    private ArrayList<MappedVariable> mappedVariables;

    @Inject
    private MicronautEntityGenerator micronautEntityGenerator;

    @Inject
    private TemplatesService templatesService;

    @Override
    public File generateProject(ProjectRequest projectRequest) throws Exception {

        return generateGradleProject(projectRequest);

    }

    public File generateGradleProject(ProjectRequest projectRequest) throws Exception {

        MicronautProfile baseProfile = micronautProfileRepository.findDistinctByName(BASE).blockingGet();

        MicronautProfile profile = micronautProfileRepository.findDistinctByName(projectRequest.getProfile()).blockingGet();

        ProfileDetails profileDetails = profileDetailsRepository.findDistinctByProfileName(projectRequest.getProfile());

        ProfileDetails baseProfileDetails = profileDetailsRepository.findDistinctByProfileName(BASE);


        String buildGradleFileCnt = getGradleFile(projectRequest, profile, baseProfile, profileDetails,
                baseProfileDetails);


        List<Tuple2<String, String>> gradleRestFile = getRestOfGradleFiles(projectRequest);


        // Tuple2<String, String> applicationClassFile =
        // commonFilesGenerator.getApplicationFile(projectRequest,
        // profileDetails,
        // baseProfileDetails);
        ArrayList<Tuple2<String, String>> srcFiles = commonFilesGenerator.getSRCFiles(projectRequest, profileDetails,
                baseProfileDetails);
        ArrayList<Tuple2<String, String>> agentFiles = commonFilesGenerator.getAgentFiles(projectRequest,
                profileDetails, baseProfileDetails);

        Tuple2<String, String> applicationYmlContent = commonFilesGenerator.getYmlResource(projectRequest,
                profileDetails, baseProfileDetails, APPLICATION, APPLICATION_YML_FULL);

        Tuple2<String, String> bootstrapYmlContent = commonFilesGenerator.getYmlResource(projectRequest, profileDetails,
                baseProfileDetails, BOOTSTRAP, BOOTSTRAP_YML_FULL);
        Tuple2<String, String> functionYmlContent = commonFilesGenerator.getYmlResource(projectRequest, profileDetails,
                baseProfileDetails, FUNCTION_YML, FUNCTION_YML_FULL);

        // System.out.println("TTTTTTTTTTTTTTTTTT" + agentFiles);
        Tuple2<String, String> cliContent = commonFilesGenerator.generateMicronautCli(projectRequest);
        ArrayList<Tuple2<String, String>> componentsObjects = componentsGenerator.generateComponents(projectRequest);


        // todo add them to zip file1
        String entryPath = projectRequest.getArtifact();

        Tuple2<String, String> buildspec = Tuples.of("buildspec.yml", getBuildSpecYmlFile("gradle"));

        Tuple2<String, String> docker = Tuples.of("Dockerfile",
                getDockerFile("gradle").replace("@app.name@", projectRequest.getArtifact()).replace("@jarPath@",
                        "build/libs/" + projectRequest.getPackage() + "-*-all.jar"));

        ArrayList<Tuple2<String, String>> entitiesFiles =
                micronautEntityGenerator.generateEntityFiles(projectRequest);


        ArrayList<Tuple2<String, String>> tuplesList = new ArrayList<Tuple2<String, String>>() {
            {
                addAll(Arrays.asList(
                        // applicationClassFile,
                        applicationYmlContent, docker, buildspec, getLogBack(), bootstrapYmlContent, functionYmlContent,
                        cliContent, Tuples.of(BUILD, buildGradleFileCnt)));
                addAll(srcFiles);
                addAll(agentFiles);
                addAll(componentsObjects);
                add(Tuples.of("mnCommand.txt",
                        generatorUtils.generateMNCommand(projectRequest, profileDetails, baseProfileDetails)));

                addAll(entitiesFiles);
            }
        };

        for (Tuple2<String, String> t : gradleRestFile) {
            tuplesList.add(t);
        }
        return generatorUtils.generateZip(tuplesList, projectRequest.getArtifact(),
                projectRequest.isRequiredBuildWrapper());
    }

    public String getGradleFile(ProjectRequest projectRequest, MicronautProfile profile, MicronautProfile baseProfile,
                                ProfileDetails profileDetails, ProfileDetails baseProfileDetails) throws IOException {

        /**
         * 1. get Base gradle file. 2. append all required gradle files. 3. collect all
         * dependencies. 4. append the rest of gradle files. 5- get the coordinates of
         * the dependencies. 6- scan required feature from the profile (pendening).
         *
         */
        // MicronautProfile baseProfile =
        // micronautProfileRepository.findDistinctByName(BASE ) .get();
        // MicronautProfile profile =
        // micronautProfileRepository.findDistinctByName(projectRequest.getProfile()).get();
        // ProfileDetails profileDetails =
        // profileDetailsRepository.findDistinctByProfileName(projectRequest.getProfile());
        // ProfileDetails baseProfileDetails =
        // profileDetailsRepository.findDistinctByProfileName(BASE);
        //

        AtomicReference<String> gradleFile = new AtomicReference<>(
                baseProfile.getSkeleton().get(GRADLE).get("/build___gradle"));

        // concat the profile gradle file to the main file

        gradleFile.set(gradleFile.get().concat("\n" + profile.getSkeleton().get(GRADLE).get("/build___gradle") + "\n"));

        // store dependencies in one place
        HashSet<String> dependencies = generatorUtils.marshalAllDependencies(projectRequest, profileDetails,
                getRequiredProfileDetailsAsArray(profileDetails));

        // HashSet<String> dependencies = new HashSet<String>(){{
        // add(projectRequest.getLanguage());
        // addAll(projectRequest.getDependencies());
        // }};
        //
        //
        // //the denpendent dependencies to the list.
        // projectRequest.getDependencies().forEach(x->{
        // MicronautFeature f = micronautFeatureRepository.findDistinctByName(x);
        // if(f.getFeatures()!=null)
        //
        // f.getFeatures().getDependent()
        // .forEach(y->{
        // dependencies.add(y);
        // });
        // });
        // //todo check profile file for dependencies
        // dependencies.addAll(profileDetails.getFeatures().getRequired());
        // dependencies.addAll(profileDetails.getFeatures().getDefaults());
        // //todo check base profile for dependencies.
        // dependencies.addAll(baseProfileDetails.getFeatures().getRequired());
        // dependencies.addAll(baseProfileDetails.getFeatures().getDefaults());

        // concat all required Skeletons
        HashSet<String> dependencies2 = new HashSet<String>();
        dependencies2.addAll(dependencies);
        if (projectRequest.getProfile().equalsIgnoreCase("function-aws-alexa")) {
            dependencies2.remove("application");
            dependencies2.remove("function-aws-" + projectRequest.getLanguage().toLowerCase());
        } else if (projectRequest.getProfile().equalsIgnoreCase("function-aws")) {
            dependencies2.remove("application");
        }
        for (String x : dependencies2) {

            FeaturesSkeleton fs = featureSkeletonRepository.findDistinctByName(x);



            if (fs.getSkeleton() == null || fs.getSkeleton().get(GRADLE) == null
                    || fs.getSkeleton().get(GRADLE).get("/build___gradle") == null)
                continue;

            String d = "";
            if (fs != null)

                d = fs.getSkeleton().get(GRADLE).get("/build___gradle");
            gradleFile.set(gradleFile.get().concat(d == null ? "" : "\n" + d));

        }


        // adding spotless task
        String spotlessTask = "";
        switch (projectRequest.getLanguage().toLowerCase()) {
        case JAVA_LANG:
            // spotlessTask =         "spotless {\n" +
            // "  java {\n" +
            // "\tgoogleJavaFormat()\n" +
            // "\timportOrder 'java', 'javax', 'org', 'com',''\n" +
            // "    \tremoveUnusedImports() \n" +
            // "  }\n" +
            // " format 'misc', {\n" +
            // "    target '**/*.gradle'\n" +
            // "    trimTrailingWhitespace()\n" +
            // "    indentWithTabs() // or spaces. Takes an integer argument if you don't like 4\n" +
            // "    endWithNewline()\n" +
            // "  }\n" +
            // "}\n" +
            // "\n" +
            // "\n";
            break; 
        case GROOVY_LANG:
            spotlessTask = ""; 
            break; 

        case KOTLIN_LANG:
            spotlessTask= ""; 
            break; 
        }
        gradleFile.set(gradleFile.get().concat("\n" + spotlessTask + "\n")); 


        //reading dependencies gradle.
        FeatureReader mainFeatureReader = dependencies
                .stream().map(x->{
                    System.out.println(" XXX " + x);
                    return mapGradleFeatures(micronautFeatureRepository.findDistinctByName(x));
                })
                .reduce(new FeatureReader(),
                        (x,y)-> new FeatureReader(x.getBuildPlugins() + "\n" + y.getBuildPlugins(),
                                x.getDependencies() + "\n" + y.getDependencies(),
                                x.getBuildDependencies() +"\n"+ y.getBuildDependencies()));
        System.out.println("finished XXXXXXXXXXX");

        //Add the depenedcies from the profile.
        mainFeatureReader.setDependencies(mainFeatureReader.getDependencies() +
                "\n"+ mapGradleFeatures(new MicronautFeature(){{
            getDependencies().addAll(baseProfileDetails.getDependencies());
            getDependencies().addAll(profileDetails.getDependencies());
        }}).getDependencies());
        System.out.println("finished XXXXXXXXXXX");




        //    mainFeatureReader.setDependencies(removeDuplicateLines(mainFeatureReader.getDependencies()));
        String repositories= "\n\t"+baseProfileDetails.getRepositories().stream()
                .reduce("", (x,y)->x + "\n" + y)+
                "\n\tmaven { url \"https://jcenter.bintray.com\" }";

        if(projectRequest.getLanguage().equals("java")){

            String sourceTarget = "\n\nsourceCompatibility='" + (projectRequest.getJavaVersion().trim().equalsIgnoreCase("8")?"1.8'":
                    projectRequest.getJavaVersion()+"'")
                    +       "\ntargetCompatibility='" + (projectRequest.getJavaVersion().trim().equalsIgnoreCase("8")?"1.8'":
                    projectRequest.getJavaVersion() +"'");


            mainFeatureReader.setBuildPlugins( mainFeatureReader.getBuildPlugins() + "\n" + sourceTarget + "\n");

        }

        mainFeatureReader.setBuildPlugins( mainFeatureReader.getBuildPlugins()+"\n"+
                baseProfileDetails.getBuild().getPlugins()
                        .stream().filter(x->!x.contains(":"))
                        .map(x-> "apply plugin:\"" + x+"\"")
                        .reduce("", (x,y)->x+"\n" + y));

        String buildDependencies = baseProfileDetails.getBuild().getPlugins().stream()
                .filter(x->x.contains(":"))
                .map(x-> {
                    String i[] = x.split(":");
                    return "id " + "\""+i[0] + "\" version \""+i[1] + "\"";
                })
                .reduce("", (x, y)-> x +"\n"+ y);




        if(profileDetails.getBuild() != null) {
            mainFeatureReader.setBuildPlugins(mainFeatureReader.getBuildPlugins() + "\n" +
                    profileDetails.getBuild().getPlugins()
                            .stream().filter(x -> !x.contains(":"))
                            .map(x -> "apply plugin:\"" + x + "\"\n")
                            .reduce("", (x, y) -> x + "\n" + y));


            buildDependencies += profileDetails.getBuild().getPlugins().stream()
                    .filter(x -> x.contains(":"))
                    .map(x -> {
                        String i[] = x.split(":");
                        return "id " + "\"" + i[0] + "\" version \"" + i[1] + "\"\n";
                    })
                    .reduce("", (x, y) -> x +"\n"+ y);
        }



        mappedVariables.stream().filter(x->x.getClassName()!=null).filter(x->
                x.getClassName().equalsIgnoreCase(projectRequest.getClass().getName())
                        && x.getFile().equalsIgnoreCase(BUILD)
        ).forEach(x->{
            x.getVariabelMethod().keySet().forEach(y->{
                        gradleFile.set(gradleFile.get().replace(y,
                                invokeProjectRequestMethod(projectRequest,
                                        x.getVariabelMethod().get(y)) ));
                    }
            );
        });



        //--------------- Adding view Framworkd;
        String viewDependencies = "";
        if(projectRequest.getProfile().equalsIgnoreCase(SERVICE) && !projectRequest.getViewFramework().equalsIgnoreCase("nono")) {
            switch (projectRequest.getViewFramework()) {
                case THYMELEAF:
                    viewDependencies = "\t" + VIEW_GRADLE + "\n\t" + THYMELEAF_GRADLE;
                    break;
                case HANDLEBARS:
                    viewDependencies = "\t" + VIEW_GRADLE + "\n\t" + HANDLEBARS_GRADLE;
                    break;
                case VELOCITY:
                    viewDependencies = "\t" + VIEW_GRADLE + "\n\t" + VELOCITY_GRADLE;
                    break;
                case FREEMARKER:
                    viewDependencies = "\t" + VIEW_GRADLE + "\n\t" + FREEMARKER_GRADLE;
                    break;

            }
        }

        //add spotless to the build script in order to clean the source code. 
        mainFeatureReader.setBuildDependencies(mainFeatureReader.getBuildDependencies() +"id \"com.diffplug.gradle.spotless\" version \"3.27.0\"\n");  

        //---------------
        String annotationProcesserDependencies = "\tannotationProcessor platform(\"io.micronaut:micronaut-bom:$micronautVersion\")\n" +
                "\timplementation platform(\"io.micronaut:micronaut-bom:$micronautVersion\")\n" +
                "\ttestAnnotationProcessor platform(\"io.micronaut:micronaut-bom:$micronautVersion\")\n\tcompile 'com.google.googlejavaformat:google-java-format:1.7'\n";
        
        switch(projectRequest.getLanguage().toLowerCase())
        {
            case "kotlin":
                annotationProcesserDependencies = "\timplementation platform(\"io.micronaut:micronaut-bom:$micronautVersion\")\n" +
                "\timplementation \"org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}\"\n" +
                "\timplementation \"org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}\"\n" +
                "\ttestImplementation platform(\"io.micronaut:micronaut-bom:$micronautVersion\")\n" +
                "\ttestAnnotationProcessor platform(\"io.micronaut:micronaut-bom:$micronautVersion\")\n" +
                "\ttestAnnotationProcessor platform(\"io.micronaut:micronaut-bom:$micronautVersion\")\n";
                break;
            case "groovy":
                annotationProcesserDependencies = "\tannotationProcessor platform(\"io.micronaut:micronaut-bom:$micronautVersion\")\n" +
                "\tannotationProcessor \"io.micronaut:micronaut-inject-groovy\"\n" +
                "\timplementation platform(\"io.micronaut:micronaut-bom:$micronautVersion\")\n" +
                "\ttestAnnotationProcessor platform(\"io.micronaut:micronaut-bom:$micronautVersion\")\n" +
                "\ttestImplementation platform(\"io.micronaut:micronaut-bom:$micronautVersion\")\n" +
                "\ttestImplementation \"io.micronaut:micronaut-inject-groovy\"\n";
                break; 
            default:
                annotationProcesserDependencies = "\tannotationProcessor platform(\"io.micronaut:micronaut-bom:$micronautVersion\")\n" +
                    "\timplementation platform(\"io.micronaut:micronaut-bom:$micronautVersion\")\n" +
                "\ttestAnnotationProcessor platform(\"io.micronaut:micronaut-bom:$micronautVersion\")\n\tcompile 'com.google.googlejavaformat:google-java-format:1.7'\n";
                break; 

        }


        // ---- lobmok Dependencies
        String lombokDependencies = "", lombokAnnotationProcessor="";

        if(projectRequest.getLanguage().equalsIgnoreCase(JAVA_LANG))
        {
            lombokDependencies = " \tcompileOnly 'org.projectlombok:lombok:1.18.12'\n" +

                                "\ttestCompileOnly 'org.projectlombok:lombok:1.18.12'\n" +
                                "\ttestAnnotationProcessor 'org.projectlombok:lombok:1.18.12'\n";
            lombokAnnotationProcessor ="\tannotationProcessor 'org.projectlombok:lombok:1.18.12'\n" ;
        }


        //=======



        String reactorDependencies =
        "\tcompile group: 'io.projectreactor', name: 'reactor-core', version: '3.3.1.RELEASE'\n" +
        "\ttestCompile group: 'io.projectreactor', name: 'reactor-test', version: '3.3.1.RELEASE'"; 

        // JDBC Driver
        String jdbcDriverDependencies = "";
        if(!projectRequest.getEntities().isEmpty()) {

            if (!projectRequest.getDatabaseType().equalsIgnoreCase("mongodb")) {
               if(!projectRequest.getDatabaseType().equalsIgnoreCase("h2")){
                String jdbcFeatureStr =
                        templatesService.getJdbcFeacures().get(projectRequest.getDatabaseType().toLowerCase().replace(" ", "")).toLowerCase()
                                .replace("${javaversion}", projectRequest.getJavaVersion());

                ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

                MicronautFeature jdbcFeature = mapper.readValue(jdbcFeatureStr,
                        MicronautFeature.class);
                jdbcDriverDependencies = jdbcFeature.getDependencies().get(0).getGradle();
            }
            }
        }
        ///



        String allDependencies =
                lombokAnnotationProcessor + "\n" +removeDuplicateLines(annotationProcesserDependencies+
                "\n" + reactorDependencies+
                "\n" +mainFeatureReader.getDependencies()+lombokDependencies + viewDependencies + "\n\t" + jdbcDriverDependencies);
        
    
        
        gradleFile.set(gradleFile.get().replace("@repositories@", repositories)
                .replace("@buildDependencies@", buildDependencies +"\n"+mainFeatureReader.getBuildDependencies())

                .replace("@buildPlugins@", mainFeatureReader.getBuildPlugins())
                .replace("@dependencies@",allDependencies
                        )
                .replace("@defaultPackage@", projectRequest.getPackage())
                .replace("@app.name@", projectRequest.getArtifact())
                .replace("@mainClassName@", profileDetails.getMainClassName(projectRequest.getPackage()))
                .replace("@version@", projectRequest.getVersion())
                .replace("@app.group@", projectRequest.getGroup() +
                        "\n\nsourceCompatibility" + (projectRequest.getJavaVersion().trim().equalsIgnoreCase("8")?"1.8":
                        projectRequest.getJavaVersion())
                        +       "\ntargetCompatibility" + (projectRequest.getJavaVersion().trim().equalsIgnoreCase("8")?"1.8":
                        projectRequest.getJavaVersion())));


        //for testing
        gradleFile.set(gradleFile.get().replaceAll("(?m)^[ \t]*\r?\n", ""));




        //todo not yet completed.
        return gradleFile.get();
    }


    public List<Tuple2<String, String>> auxGetRestOfGradleFiles(ProjectRequest projectRequest, String p)
    {
        MicronautProfile profile = micronautProfileRepository.findDistinctByName(p).blockingGet();
        HashMap<String, String> skeleton= profile.getSkeleton().get(GRADLE);


        String langGradleProperties =
                featureSkeletonRepository.findDistinctByName(projectRequest.getLanguage()).getSkeleton().get(GRADLE)
                        .get("/gradle___properties");

        if(langGradleProperties != null) {
            if(skeleton.get("/gradle___properties") != null)
            {
                String r = skeleton.get("/gradle___properties") +
                        "\n" + langGradleProperties;
                skeleton.put("/gradle___properties", removeDuplicateLines(r));
            }

        }
        List<Tuple2<String, String>> tuples = skeleton.keySet().stream()
                .filter(x->!x.equalsIgnoreCase("/build___gradle"))
                .map(x->{
//                    System.out.println(x);
                    return Tuples.of(resolveEntry(x),generatorUtils.replacePlaceHolders(projectRequest,
                            skeleton.get(x).replace("@version@", projectRequest.getVersion())));
                }).collect(Collectors.toList());

        return tuples;
    }
    private ProfileDetails[] getRequiredProfileDetailsAsArray(ProfileDetails profileDetails) {


        System.out.println("This required profiles!");
        System.out.println(profileDetails == null);
        System.out.println(profileDetails.getName());
        System.out.println("-----------------");
        return profileDetails.getRequiredProfiles().stream().map(f->profileDetailsRepository.findDistinctByProfileName(f)).collect(Collectors.toList()).toArray(new ProfileDetails[profileDetails.getRequiredProfiles().size()]);

    }
    public ArrayList<Tuple2<String, String>> auxGetRestOfGradleFilesFeatures(ProjectRequest projectRequest,
                                                                             ProfileDetails profileDetails,
                                                                             ProfileDetails baseProfiles)
    {

        ArrayList<Tuple2<String, String>> result = new ArrayList<Tuple2<String, String>>();
        HashSet<String> allDependencies = generatorUtils.marshalAllDependencies(projectRequest, profileDetails,
                getRequiredProfileDetailsAsArray(profileDetails) );

        //     System.out.println("ttt "+ allDependencies);

        loop:
        for(String f : allDependencies)
        {

            FeaturesSkeleton feature = featureSkeletonRepository.findDistinctByName(f);
            if(feature.getSkeleton() == null) continue ;
            HashMap<String, String> skeleton= feature.getSkeleton().get(GRADLE);
//          System.out.println("The skeleton of "+f+" is " + skeleton);
            if(skeleton == null)
                continue loop ;
            List<Tuple2<String, String>> tuples = skeleton.keySet().stream()
                    .filter(x->!x.equalsIgnoreCase("/build___gradle"))
                    .map(x->{
//                        System.out.println("ttt " +x);
                        return Tuples.of(resolveEntry(x),generatorUtils.replacePlaceHolders(projectRequest,
                                skeleton.get(x)));
                    }).collect(Collectors.toList());

            result.addAll(tuples);
        }

        return result;

    }

    public ArrayList<Tuple2<String, String>> getRestOfGradleFiles(ProjectRequest projectRequest)
    {

//
//        MicronautProfile baseProfile = micronautProfileRepository.findDistinctByName(BASE).get();
//         HashMap<String, String> baseSkeleton = baseProfile.getSkeleton().get(GRADLE);
//
//
        List<Tuple2<String, String>> baseTuples = auxGetRestOfGradleFiles(projectRequest, BASE);
//        .filter(x->!x.equalsIgnoreCase("/build___gradle"))
//                        .map(x->{
//                            return Tuples.of(resolveEntry(x),generatorUtils.replacePlaceHolders(projectRequest, baseSkeleton.get(x)));
//                        }).collect(Collectors.toList());
//
//
//
//
//        MicronautProfile profile = micronautProfileRepository.findDistinctByName(projectRequest.getProfile()).get();
//
//
//        HashMap<String, String> profileSkeleton = profile.getSkeleton().get(GRADLE);
//
//        List <Tuple2<String, String>> prTuples = auxGetRestOfGradleFiles(projectRequest, projectRequest.getProfile());

        List<Tuple2<String, String>> gradleRestFile = commonFilesGenerator.getRestOfBuildFiles(projectRequest,
                "gradle");


//        System.out.println(gradleRestFile);
//                .filter(x->!x.equalsIgnoreCase("/build___gradle"))
//                .map(x->{
//                    System.out.println(x);
//                    return Tuples.of(resolveEntry(x),generatorUtils.replacePlaceHolders(projectRequest, profileSkeleton.get(x)));
//                }).collect(Collectors.toList());
//
//
//
//        MicronautProfile federation = micronautProfileRepository.findDistinctByName("federation").get();
//
//        HashMap<String, String> federationSkeleton = federation.getSkeleton().get(GRADLE);
//
//        List <Tuple2<String, String>> federationTuples = auxGetRestOfGradleFiles(projectRequest, "federation");
//                .filter(x->!x.equalsIgnoreCase("/build___gradle"))
//                .map(x->{
//                    System.out.println(x);
//                    return Tuples.of(resolveEntry(x),generatorUtils.replacePlaceHolders(projectRequest, federationSkeleton.get(x)));
//                }).collect(Collectors.toList());
//


//        ArrayList<Tuple2<String, String>> featuresTuples = auxGetRestOfGradleFilesFeatures(projectRequest,
//                profileDetailsRepository.findDistinctByProfileName(projectRequest.getProfile()),
//                profileDetailsRepository.findDistinctByProfileName(BASE));


        ArrayList<Tuple2<String, String>> result = new ArrayList<Tuple2<String, String>>();
        if(!baseTuples.isEmpty())
            result.addAll(baseTuples);
        if(!gradleRestFile.isEmpty())
            result.addAll(gradleRestFile);
//        if(!prTuples.isEmpty()) {
//            result.addAll(prTuples);
//
//        }
//        if(!federationTuples.isEmpty())
//            result.addAll(federationTuples);
//        if(!featuresTuples.isEmpty())
//            result.addAll(featuresTuples);
        return result;
    }



    //========================

    public void generateApplicationMainClass(ZipOutputStream zipOut, ProjectRequest projectRequest) throws IOException {


    }
    public FeatureReader mapGradleFeatures(MicronautFeature micronautFeature)
    {
        if(micronautFeature == null) return new FeatureReader("","","");
        FeatureReader reader = new FeatureReader();

        if(micronautFeature.getBuild()!= null)
            for(String x : micronautFeature.getBuild().getPlugins()) {
                if(x.indexOf(":") >=0)
                    reader.setBuildDependencies(reader.getBuildDependencies() +"id \""+x.split(":")[0] + "\" version \""+ x.split(":")[1] +
                            "\"\n");
                else
                    reader.setBuildPlugins(reader.getBuildPlugins() + "apply plugin:\"" + x + "\"\n");
            }
        if(micronautFeature.getDependencies()!= null)
            for(Dependency x: micronautFeature.getDependencies()) {
                //ignore "build" scope.
                if(x.getScope().trim().equalsIgnoreCase("build"))
                    continue;

                //using .replace("kapt", "compile ") is a temp resolution.
                String gg = x.getGradle().replace("kaptTest", "testAnnotationProcessor").replace("kapt ",
                        "annotationProcessor ");
                if(gg.contains("testCompile \"org.spockframework:spock-core\"")&& gg.contains("exclude"))
                    //remove exclude from spock-core.... this is temp resolution
                    gg = gg.substring(0, gg.indexOf("{"));
                reader.setDependencies(reader.getDependencies() + "\t" + gg + "\n");
            }
        

            return reader;
    }
}
