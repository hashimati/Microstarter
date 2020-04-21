package io.hashimati.microstarter.services;

/**
 * @author Ahmed Al Hashmi @hashimati
 */

import io.hashimati.microstarter.entity.micronaut.MicronautFeature;
import io.hashimati.microstarter.entity.micronaut.ProjectRequest;
import io.hashimati.microstarter.entity.micronaut.profiles.ProfileDetails;
import io.hashimati.microstarter.repository.MicronautFeatureRepository;
import org.apache.commons.io.FileUtils;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import reactor.util.function.Tuple2;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Singleton
public class GeneratorUtils
{
    @Inject
    private MicronautFeatureRepository micronautFeatureRepository;

    public String generateMNCommand(ProjectRequest projectRequest, ProfileDetails profileDetails,
                                    ProfileDetails... baseProfileDetails)
    {
        HashSet<String> allDependencies = marshalAllDependencies(projectRequest, null, null);
        allDependencies.remove(projectRequest.getLanguage().toLowerCase());

        allDependencies.removeAll(Arrays.asList("http-client", "junit", "annotation-api", "application","http-server"
                ,"logback"));




        String features = allDependencies.stream().map(x-> " --features " + x)
                .collect(Collectors.joining(" "));

        String appOrFunction =!projectRequest.getProfile().toLowerCase().contains("function")? "create-app": "create-function";
        String alexaProvider = projectRequest.getProfile().toLowerCase().contains("alexa")?" --provider alexa":"";


        String command = "mn "+appOrFunction + " " +projectRequest.getPackage().replace(".", "-")
                + alexaProvider
                + (appOrFunction.contains("app")? " --profile " + projectRequest.getProfile().toLowerCase():"")
                + " --lang " + projectRequest.getLanguage().toLowerCase()
                + " --build " + projectRequest.getBuild().toLowerCase()
                 + features;



        return command;

    }
    public HashSet<String> marshalAllDependencies(ProjectRequest projectRequest, ProfileDetails profileDetails,
                                                  ProfileDetails... baseProfileDetails)
    {

        //store dependencies in one place
        HashSet<String> dependencies = new HashSet<String>(){{
            add(projectRequest.getLanguage());
            add(projectRequest.getTestframework());
            addAll(projectRequest.getDependencies());
        }};


        //the denpendent dependencies to the list.
        projectRequest.getDependencies().forEach(x->{
            MicronautFeature f = micronautFeatureRepository.findDistinctByName(x);
            if(f.getFeatures()!=null)

                f.getFeatures().getDependent()
                        .forEach(y->{
                            dependencies.add(y);
                        });
        });
        //todo check profile file for dependencies
        if(profileDetails
         != null) {
            dependencies.addAll(profileDetails.getFeatures().getRequired());
            dependencies.addAll(profileDetails.getFeatures().getDefaults().stream()
                    .map(x -> x.replace("java", projectRequest.getLanguage())
                            .replace("junit", projectRequest.getTestframework())).collect(Collectors.toList()));
        }

        if(baseProfileDetails != null)
        //todo check base profile for dependencies.
        {
            for (ProfileDetails p : baseProfileDetails) {
                dependencies.addAll(p.getFeatures().getRequired());
                dependencies.addAll(p.getFeatures().getDefaults().stream().map(x -> x.replace("java",
                        projectRequest.getLanguage())).collect(Collectors.toList()));
            }

        }
        return dependencies;
    }


    public File generateZip(List<Tuple2<String, String>> files, String rootFolder, boolean includeWrapper) throws Exception {

        HashMap<String, Boolean> addedEntry = new HashMap<String, Boolean>();


        File result = File.createTempFile("temp", ".zip");

        FileOutputStream fos = new FileOutputStream(result);
        ZipOutputStream zipOutputStream = new ZipOutputStream(fos);


        zippingLoop:
        for (Tuple2<String, String> x : files) {


            File fileToZip ;
            if(x.getT1().contains("jar")){
                if(!includeWrapper)
                {
                    if(x.getT1().toLowerCase().contains("wrapper"))
                        continue zippingLoop;
                }
//                System.out.println("XXXXXXXXXXXXXX" + x.getT1() + " " + x.getT2());
                fileToZip = new File(x.getT2());

            }
            else{
                fileToZip= File.createTempFile("xxxx", ".txt");
                FileWriter writer = new FileWriter(fileToZip);


                writer.write(x.getT2().trim());
                writer.close();
            }

            if(x.getT1().equalsIgnoreCase(File.separator) )
                throw new Exception("cannot zip these list");

            String entry =  x.getT1().startsWith("/")? rootFolder+ "/"+x.getT1().substring(1) :rootFolder+ "/"+x.getT1();

            if(x.getT2().trim().isEmpty())
            {
//                System.out.println(entry);
                entry = entry.substring(0, entry.lastIndexOf("/") + 1);
//                System.out.println(entry +"312321");
//                System.out.println("================");


            }

            if(entry.toLowerCase().indexOf("dockerfile")>= 0){
                String entryBackup = entry;
                int counter  =0;
                String attachedString="";
                while(addedEntry.containsKey(entry)) {
                    attachedString= (counter++ == 0)?"":"("+counter+")";
                    entry = entryBackup + attachedString;
                }



            }
            ZipEntry zipEntry = new ZipEntry(entry);
            if(addedEntry.containsKey(entry)) {

                continue;
            }
            else
                addedEntry.put(entry, true);


            zipOutputStream.putNextEntry(zipEntry);
            InputStream fis;
            if(fileToZip.getName().contains("maven-wrapper.jar"))
                fis = new URL("https://github.com/micronaut-projects/micronaut-profiles/raw/master/base/skeleton" +
                        "/maven-build/.mvn/wrapper/maven-wrapper.jar").openStream();
            else if(x.getT1().contains("MavenWrapperDownloader.java"))
                fis = new URL("https://raw.githubusercontent.com/micronaut-projects/micronaut-profiles/master/base/skeleton/maven-build/.mvn/wrapper/MavenWrapperDownloader.java").openStream();
            else if(x.getT1().contains("asciidoc.gradle"))
                fis = new URL("https://raw.githubusercontent.com/micronaut-projects/micronaut-profiles/master/base/features/asciidoctor/skeleton/gradle-build/gradle/asciidoc.gradle").openStream();

            else
                fis = new FileInputStream(new File(fileToZip.getParent(), fileToZip.getName()));
            byte[] bytes = new byte[1024];
            int length;

            while((length = fis.read(bytes)) >= 0) {
                zipOutputStream.write(bytes, 0, length);
            }
            zipOutputStream.closeEntry();
            fis.close();
            //deleting the file after writing it to the zip file.
            if(x.getT1().indexOf(".jar") < 0)
                FileUtils.forceDelete(fileToZip);
        }

        zipOutputStream.close();
        fos.close();
        return result;
    }


    public String replacePlaceHolders(ProjectRequest projectRequest, String string)
    {

        return string.replace("@service@", projectRequest.getArtifact())
                .replace("@app.name@", projectRequest.getArtifact())
                .replace("@mainClassName@", projectRequest.getPackage()+".Application")
                .replace("@jarPath", projectRequest.getBuild().toLowerCase().contains("maven")? 
                "target/" + projectRequest.getPackage() + "-*.jar": "build/lib/" + projectRequest.getPackage() + "-*.jar" ); 
                
    }
    public static Document loadXMLFromString(String xml) throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml));
        return builder.parse(is);
    }

    public static String xmlToString(Document document) throws ParserConfigurationException, TransformerException {



        DOMSource domSource = new DOMSource(document);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.transform(domSource, result);
//        System.out.println("XML IN String format is: \n" + writer.toString());

        return writer.toString();
    }
}
