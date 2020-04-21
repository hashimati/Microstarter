package io.hashimati.microstarter.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.hashimati.microstarter.entity.micronaut.FeaturesSkeleton;
import io.hashimati.microstarter.entity.micronaut.MicronautFeature;
import io.hashimati.microstarter.entity.micronaut.MicronautProfile;
import io.hashimati.microstarter.entity.micronaut.profiles.ProfileDetails;
import io.hashimati.microstarter.repository.FeatureSkeletonRepository;
import io.hashimati.microstarter.repository.MicronautFeatureRepository;
import io.hashimati.microstarter.repository.MicronautProfileRepository;
import io.hashimati.microstarter.repository.ProfileDetailsRepository;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import static io.hashimati.microstarter.constants.ProjectConstants.BuildConstants.GRADLE;
import static io.hashimati.microstarter.constants.ProjectConstants.BuildConstants.MAVEN;
import static io.hashimati.microstarter.constants.ProjectConstants.LanguagesConstants.*;
import static io.hashimati.microstarter.constants.ProjectConstants.MicronautProfileConstants.*;
import static io.hashimati.microstarter.constants.ProjectConstants.MicronautSkeletonFolders.GRADLE_BUILD;
import static io.hashimati.microstarter.constants.ProjectConstants.MicronautSkeletonFolders.MAVEN_BUILD;
import static io.hashimati.microstarter.constants.ProjectConstants.ProfileConstants.*;
import static io.hashimati.microstarter.constants.ProjectConstants.SrcFolders.*;
import static io.hashimati.microstarter.util.ScanningUtils.*;
/**
 * @author Ahmed Al Hashmi @hashimati
 *
 * @implNote This class will read Micronaut Profile Github Repository and it will create the database
 */

@Singleton
public class MicronautProfileScanner
{

    //Prepare
    @Inject
    private FeatureSkeletonRepository featureSkeletonRepository;
    @Inject
    private MicronautFeatureRepository micronautFeatureRepository;
    @Inject
    private MicronautProfileRepository micronautProfileRepository;
    @Inject
    private ProfileDetailsRepository profileDetailsRepository;
    private ArrayList<FeaturesSkeleton> featuresSkeletons = new ArrayList<FeaturesSkeleton>();
    private ArrayList<MicronautFeature> micronautFeatures = new ArrayList<MicronautFeature>();
    private ArrayList<MicronautProfile> micronautProfiles = new ArrayList<MicronautProfile>();
    private ArrayList<ProfileDetails> profileDetails = new ArrayList<ProfileDetails>();
    // finish preparation.



    @EventListener
    public void startScanning(StartupEvent event) throws Exception {
        //Getting the repository from github unzip and pass it to scanProfiles() method.
        String homeDirPath = ScanningUtils.unZipRepository();

        System.out.println("=====================");
        System.out.println("This home " + homeDirPath);
        System.out.println("_________________________");
        File homeDir = new File(homeDirPath);
        if(!homeDir.isDirectory() && !homeDir.listFiles()[0].getAbsolutePath().contains("pro"))
            throw new Exception("Cannot scan files!");

        scanProfiles(homeDir.listFiles()[0].getAbsolutePath());


//        scanProfiles("C:\\MyProjects\\micronaut-profiles");
//      System.out.println("======================================?");
//        System.out.println(this.micronautFeatures);
        this.micronautFeatures.forEach(micronautFeatureRepository::save);
//        System.out.println("_----------------------------------------_");
//        System.out.println(featuresSkeletons);

        System.out.println("These Feature Skeleton"); 
        this.featuresSkeletons.forEach(featureSkeletonRepository::save);
//        System.out.println("_----------------------------------------_");
//        System.out.println(micronautProfiles);


        micronautProfiles.forEach(micronautProfileRepository::save);
//        System.out.println("======================================?");
//
//        System.out.println("\n\n\n\n\n\n\n\n\n\n\n");
        //  profileDetails.forEach(System.out::println);
        profileDetails.forEach(profileDetailsRepository::save);
        //    System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n");


        FileUtils.deleteDirectory(homeDir);
    }

    public void scanProfiles(String path)
    {
        File homeDir = new File(path);

        if(homeDir.exists() && homeDir.isDirectory())
        {

            Arrays.asList(SERVICE,KAFKA, CLI,RABBITMQ, FEDERATION,BASE, GRPC, FUNCTION
                    , FUNCTION_AWS,
                    FUNCTION_AWS_ALEXA, CONFIGURATION)
                    .forEach(x->{

                        MicronautProfile micronautProfile = new MicronautProfile();
//                        micronautProfile.setId(x);
                        micronautProfile.setName(x);

                        String profilePath = constructPath(path, x);
                        //  File profileDirectory = new File(profilePath);

//                        Arrays.asList(profileDirectory.listFiles()).stream()
//                                .filter(f->!f.isDirectory())
//                                .forEach(f->{
//                                    try {
//                                        micronautProfile.getRootFiles().put(refinePath("",f.getAbsolutePath()),
//                                                getFileContent(f.getAbsolutePath()));
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                });

                        marshalFiles(profilePath,"",micronautProfile.getRootFiles());
//                            System.out.println(ScanningUtils.getFileContent(constructPath(profilePath,
//                                    "profile.yml")));
                        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                        try {

                            File profileFile = new File(constructPath(profilePath,"profile.yml"));



                            ProfileDetails profileDetails= mapper.readValue(profileFile,ProfileDetails.class);

                            profileDetails.setName(x.toLowerCase());
//                            profileDetails.setId(x.toLowerCase());
                            this.profileDetails.add(profileDetails);

                            File buildFile = new File(constructPath(profilePath, "build.gradle"));
                            if(buildFile.exists())
                            {
                                String buildContent = getFileContent(buildFile.getAbsolutePath());
                                String requiredProfile = buildContent.substring(buildContent.indexOf("'"),
                                        buildContent.lastIndexOf("'"))
                                        .replace(":", "").trim();

                                profileDetails.getRequiredProfiles().add("base");
                                profileDetails.getRequiredProfiles().add(CONFIGURATION);

                                if (FUNCTION_AWS_ALEXA.equalsIgnoreCase(profileDetails.getName())
                                        || FUNCTION_AWS.equalsIgnoreCase(profileDetails.getName())) {
                                    profileDetails.getRequiredProfiles().add(FUNCTION);
                                }


                            }


                            //   System.out.println(micronautFeatureRepository.save(micronautFeature));
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                        }


                        scanTemplates(constructPath(profilePath,TEMPLATES), micronautProfile.getTemplates());
                        scanSkeleton(constructPath(profilePath, SKELETON), micronautProfile.getSkeleton());

                        try {
                            scanFeatures(profilePath,x);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        micronautProfiles.add(micronautProfile);
                    });

        }


    }
    public void scanFeatures(String path, String profile) throws Exception {
        //path is the path of the profile.
        String postfixName= "";
        if(profile.equalsIgnoreCase(CONFIGURATION))
        {
            postfixName="-configuration";
        }
        String featuresPath = constructPath(path, FEATURES);
        File featuresDir = getFile(featuresPath);
        if(featuresDir.exists() && featuresDir.isDirectory())
        {
            // start Scanning features.
            for(File f : featuresDir.listFiles())
            {
                String feature = f.getName();
                MicronautFeature micronautFeature = new MicronautFeature();
                FeaturesSkeleton featuresSkeleton = new FeaturesSkeleton();
                featuresSkeleton.setName(feature+postfixName);
//                featuresSkeleton.setId(feature+postfixName);
                featuresSkeleton.setProfile(profile);

                if(f.isDirectory() && new File(constructPath(f.getAbsolutePath(),"feature.yml")).exists())
                {

                    scanSkeleton(constructPath(f.getAbsolutePath(), SKELETON), featuresSkeleton.getSkeleton());
                    marshalFiles(constructPath(f.getAbsolutePath(), SKELETON),"",featuresSkeleton.getRootFiles());

                    featuresSkeletons.add(featuresSkeleton);
                    //todo create parser for features.
                    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                    try {

                        File featureFile = new File(constructPath(f.getAbsolutePath(),"feature.yml"));
                        micronautFeature = mapper.readValue(featureFile,
                                MicronautFeature.class);
                        micronautFeature.setName(feature+ postfixName);
//                        micronautFeature.setId(feature+postfixName);
                        micronautFeature.setProfile(profile);
                        micronautFeatures.add(micronautFeature);
                        //   System.out.println(micronautFeatureRepository.save(micronautFeature));
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();

                    }
                }


                // / System.out.println(getFileContent(constructPath(f.getAbsolutePath(), "feature.yml")));

            }
        }
    }




    // @Deprecated
    public  void scanSkeleton2(String path, HashMap<String, HashMap<String, String>> skeletons){
        // path is ~/skeleton/
        scanBuild(path, skeletons);
        scanSrc(path,skeletons);
        scanCli(path, skeletons);
    }


    public void scanSkeleton(String path, HashMap<String, HashMap<String, String>> skeletons){
//todo Make this as the official scan skeleton method.

        if(new File(path).exists() == false) return;
        Collection<File> fileList =  FileUtils.listFiles(
                new File(path),
                new RegexFileFilter("^(.*?)"),
                DirectoryFileFilter.DIRECTORY
        );
        ;   HashMap<String, String> srcContent = new HashMap<String, String>(),
                gradleContent = new HashMap<String, String>(),
                mavenContent = new HashMap<String, String>(),
                cliContent = new HashMap<String, String>();
        skeletons.put(SRC, srcContent);
        skeletons.put(GRADLE, gradleContent);
        skeletons.put(MAVEN, mavenContent);
        skeletons.put("cli", cliContent);
        fileList.stream().forEach(f->{
            if(f.getAbsolutePath().contains(GRADLE_BUILD)){

                try {
                    boolean isJar = f.getName().contains(".jar");

                    gradleContent.put("/"+f.getAbsolutePath().substring(
                            f.getAbsolutePath().indexOf(GRADLE_BUILD)+GRADLE_BUILD.length() + 1
                            ).replace(".","___").replace("\\", "/")
                            ,isJar?saveFile(f.getAbsolutePath()):getFileContent(f.getAbsolutePath()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if(f.getAbsolutePath().contains(MAVEN_BUILD)){
                try {
                    boolean isJar = f.getName().contains(".jar");

                    mavenContent.put("/"+f.getAbsolutePath().substring(
                            f.getAbsolutePath().indexOf(MAVEN_BUILD)+MAVEN_BUILD.length()+1

                            ).replace(".","___").replace("\\", "/")
                            ,isJar?saveFile(f.getAbsolutePath()):getFileContent(f.getAbsolutePath()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            else if(f.getAbsolutePath().contains(SRC))
            {
                try {
                    boolean isJar = f.getName().contains(".jar");

                    srcContent.put("/"+f.getAbsolutePath().substring(
                            f.getAbsolutePath().indexOf(SRC)
                            ).replace(".","___").replace("\\", "/")
                            ,isJar?saveFile(f.getAbsolutePath()):getFileContent(f.getAbsolutePath()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            else{
                try {
                    boolean isJar = f.getName().contains(".jar");

                    cliContent.put("/"+f.getAbsolutePath().substring(
                            f.getAbsolutePath().indexOf(SKELETON)+SKELETON.length()+1
                            ).replace(".","___").replace("\\", "/")
                            ,isJar?saveFile(f.getAbsolutePath()):getFileContent(f.getAbsolutePath()));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }



        });


    }

    private void scanCli(String path, HashMap<String, HashMap<String, String>> skeletons) {

        HashMap<String, String> content = new HashMap<String, String>();
        marshalFiles(path, "", content);

        skeletons.put("cli", content);
    }

    public  void scanBuild(String path, HashMap<String, HashMap<String, String>> skeletons){

        Arrays.asList(GRADLE_BUILD, MAVEN_BUILD, "agent")
                .forEach(x->{
                    HashMap<String, String> contents = new HashMap<String, String>();

                    File buildDir = new File(constructPath(path, x));
                    if(buildDir.exists() && buildDir.isDirectory()){

                        String root = x.equalsIgnoreCase("agent")?"agent":"";
                        marshalFiles(constructPath(path, x),root,contents);
                    }
                    switch (x)
                    {
                        case GRADLE_BUILD:
                            skeletons.put("gradle", contents);
                            break;
                        case MAVEN_BUILD:
                            skeletons.put("maven", contents);
                            break;
                        case "agent":
                            skeletons.put("agent", contents);
                    }
                });
    }

    public void scanSrc(String path, HashMap<String, HashMap<String, String>> skeletons){

        //File rootDir = new File(path);

        String mainPath = constructPath(path,"src", MAIN);
        String testPath = constructPath(path,"src",TEST );
        File mainDir = new File(mainPath);
        HashMap<String, String> contents = new HashMap<String, String>();


        //marshalFiles(rootDir, "/", null);
        File testDir = new File(testPath);
        if(testDir.exists() && testDir.isDirectory())
        {
            marshalFiles(constructPath(testPath, RESOURCES), "src", contents);
            marshalFiles(constructPath(testPath, JAVA_DIR, PACKAGE_DIR), "src", contents);
            marshalFiles(constructPath(testPath, KOTLIN_DIR, PACKAGE_DIR), "src", contents);
            marshalFiles(constructPath(testPath, GROOVY_DIR, PACKAGE_DIR), "src", contents);

        }

        if(mainDir.exists() && mainDir.isDirectory())
        {
            //marshalMainPackage

            marshalFiles(constructPath(mainPath, JAVA_DIR), "src", contents );
            marshalFiles(constructPath(mainPath, KOTLIN_DIR), "src", contents);
            marshalFiles(constructPath(mainPath, GROOVY_DIR), "src", contents);
            //marshal main resources
            marshalFiles(constructPath(mainPath, RESOURCES), "src", contents);


            marshalFiles(constructPath(mainPath, JAVA_DIR, PACKAGE_DIR), "src", contents);


            marshalFiles(constructPath(mainPath, KOTLIN_DIR, PACKAGE_DIR), "src", contents);
            marshalFiles(constructPath(mainPath, GROOVY_DIR, PACKAGE_DIR), "src", contents);

            marshalFiles(constructPath(testPath, RESOURCES), "src", contents);


            marshalFiles(constructPath(testPath, JAVA_DIR, PACKAGE_DIR), "src", contents);
            marshalFiles(constructPath(testPath, KOTLIN_DIR, PACKAGE_DIR), "src", contents);
            marshalFiles(constructPath(testPath, GROOVY_DIR, PACKAGE_DIR), "src", contents);

            //  System.out.println(constructPath(testPath, RESOURCES));


            marshalFiles(constructPath(testPath, GROOVY_DIR, PACKAGE_DIR), "src", contents);

//            System.out.println("tttttttxxxxx" + constructPath(mainPath, RESOURCES));
//            System.out.println(contents);
            //marshal test resources


            //   marshalFiles(constructPath(path, RESOURCES_TEST), "src", contents);
        }

        skeletons.put("src", contents);

    }



    public void marshalFiles(String directoryPath, String root, HashMap<String, String> storage)
    {

        File directory = new File(directoryPath);




        if(directory.exists() && directory.isDirectory())
        {

            Arrays.asList(directory.listFiles()).stream()
                    .filter(File::isFile)
                    .forEach(x->{

                        try {
                            //    System.out.println(getFileContent(x.getAbsolutePath()));
                            boolean isJar = x.getName().contains(".jar");

                            storage.put(ScanningUtils.refinePath(root.trim().isEmpty()?x.getName():root, x.getAbsolutePath()),
                                    isJar?saveFile(x.getAbsolutePath()):getFileContent(x.getAbsolutePath()));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });


        }
    }
    public void scanTemplates(String path, HashMap<String, HashMap<String, String>> filesMap){
        Arrays.asList("/", JAVA_LANG, KOTLIN_LANG,GROOVY_LANG)
                .forEach(x->{
                    String templatesPath = constructPath(path, x);
                    //  System.out.println(templatesPath);
                    File templateFolder = new File(templatesPath);
                    HashMap<String, String> filesContents  = new HashMap<String, String>();
                    if(templateFolder.isDirectory()){
                        Arrays.asList(templateFolder.listFiles()).forEach(f->{
                            try {

                                //System.out.println(ScanningUtils.getFileContent(f.getAbsolutePath()));

                                //Files.probeContentType(f.toPath());
                                boolean isJar = f.getName().contains(".jar");
                                filesContents.put(f.getName().replace(".","___"), isJar?saveFile(f.getAbsolutePath()):
                                        getFileContent(f.getAbsolutePath()));

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });

                    }
                    filesMap.put(x.replace(".","___"), filesContents);

                });

    }



//    public static void main(String... a){
//
//      //  ScanProfiles("C:\\MyProjects\\micronaut-profiles-master");
//
//    }
}
