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
import io.hashimati.microstarter.util.XmlFormatter;
import org.atteo.xmlcombiner.XmlCombiner;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

import static io.hashimati.microstarter.constants.ProjectConstants.BuildConstants.MAVEN;
import static io.hashimati.microstarter.constants.ProjectConstants.LanguagesConstants.JAVA_LANG;
import static io.hashimati.microstarter.constants.ProjectConstants.MicronautProfileConstants.*;
import static io.hashimati.microstarter.constants.ProjectConstants.SrcFolders.*;
import static io.hashimati.microstarter.constants.ProjectConstants.ViewFramework.*;
import static io.hashimati.microstarter.services.CommonFilesGenerator.*;
import static io.hashimati.microstarter.services.GeneratorUtils.loadXMLFromString;
import static io.hashimati.microstarter.services.GeneratorUtils.xmlToString;
import static io.hashimati.microstarter.util.GeneratorUtils.removeDuplicateLines;

@Singleton
public class MavenProjectGenerator implements ProjectGenerator {

    @Inject
    private MicronautProfileRepository micronautProfileRepository;

    @Inject
    private MicronautFeatureRepository micronautFeatureRepository;

    @Inject
    private ProfileDetailsRepository profileDetailsRepository;

    @Inject
    private FeatureSkeletonRepository featureSkeletonRepository;

    @Inject
    private CommonFilesGenerator commonFilesGenerator;
    @Inject
    private GeneratorUtils generatorUtils;

    @Inject
    private ComponentsGenerator componentsGenerator;

    @Inject
    private MicronautEntityGenerator micronautEntityGenerator;


    @Inject
    private TemplatesService templatesService;
    @Inject
    private ArrayList<MappedVariable> mappedVariables;


    @Override
    public File generateProject(ProjectRequest projectRequest) throws Exception {


        return null; //generateMavenProject(projectRequest);

    }
    private ProfileDetails[] getRequiredProfileDetailsAsArray(ProfileDetails profileDetails) {
        return profileDetails.getRequiredProfiles().stream().map(f->profileDetailsRepository.findDistinctByProfileName(f)).collect(Collectors.toList()).toArray(new ProfileDetails[profileDetails.getRequiredProfiles().size()]);

    }
    // Modify it later to file.
    public File generateMavenProject(ProjectRequest projectRequest) throws Exception {
        MicronautProfile baseProfile = micronautProfileRepository.findDistinctByName(BASE ) .blockingGet();
        MicronautProfile profile =
                micronautProfileRepository.findDistinctByName(projectRequest.getProfile()).blockingGet();
        MicronautProfile federation =
                micronautProfileRepository.findDistinctByName(FEDERATION).blockingGet();
        ProfileDetails profileDetails =
                profileDetailsRepository.findDistinctByProfileName(projectRequest.getProfile());
        ProfileDetails baseProfileDetails =
                profileDetailsRepository.findDistinctByProfileName(BASE);

        ProfileDetails federationProfileDetails =
                profileDetailsRepository.findDistinctByProfileName(FEDERATION);

        String buildMavenFileCnt = getMavenFile(projectRequest,profile, baseProfile, federation, profileDetails,
                baseProfileDetails, federationProfileDetails);

        List<Tuple2<String, String>> mavenRestFile = commonFilesGenerator.getRestOfBuildFiles(projectRequest, "maven");


//        Tuple2<String, String> applicationClassFile = commonFilesGenerator.getApplicationFile(projectRequest,
//                profileDetails,
//                baseProfileDetails);

        ArrayList<Tuple2<String, String>> srcFiles = commonFilesGenerator.getSRCFiles(projectRequest, profileDetails,
                baseProfileDetails);
        ArrayList<Tuple2<String, String>> agentFiles = commonFilesGenerator.getAgentFiles(projectRequest,
                profileDetails, baseProfileDetails);

        Tuple2<String, String> applicationYmlContent = commonFilesGenerator.getYmlResource(projectRequest,profileDetails,
                baseProfileDetails,APPLICATION,APPLICATION_YML_FULL);
        Tuple2<String, String> bootstrapYmlContent = commonFilesGenerator.getYmlResource(projectRequest,profileDetails,
                baseProfileDetails,BOOTSTRAP,BOOTSTRAP_YML_FULL);

        Tuple2<String, String> functionYmlContent = commonFilesGenerator.getYmlResource(projectRequest,profileDetails,
                baseProfileDetails,FUNCTION_YML,FUNCTION_YML_FULL);
        Tuple2<String, String> cliContent = commonFilesGenerator.generateMicronautCli(projectRequest);

        ArrayList<Tuple2<String, String>> componentsObjects = componentsGenerator.generateComponents(projectRequest);

        //todo add them to zip file1
        String entryPath = projectRequest.getArtifact();
        Tuple2<String, String> docker = Tuples.of("Dockerfile", getDockerFile("maven").replace("@app.name@",  projectRequest.getArtifact()+ "-0.1")
                .replace("@jarPath@", "target/"+projectRequest.getPackage()+"-*-all.jar"));

        Tuple2<String, String> buildspec = Tuples.of("buildspec.yml", getBuildSpecYmlFile("maven"));

        ArrayList<Tuple2<String, String>> entitiesFiles =
                micronautEntityGenerator.generateEntityFiles(projectRequest);



        ArrayList<Tuple2<String, String>> tuplesList = new ArrayList<Tuple2<String,
                String>>(){{
            addAll(Arrays.asList(

//                    applicationClassFile,
                    getLogBack(),
                    docker,
                    buildspec,
                    applicationYmlContent,
                    bootstrapYmlContent,functionYmlContent, cliContent, Tuples.of("/pom.xml", buildMavenFileCnt)));
            addAll(srcFiles);
            addAll(agentFiles);
            addAll(componentsObjects);

            add(Tuples.of("mnCommand.txt", generatorUtils.generateMNCommand(projectRequest, profileDetails, baseProfileDetails)));
            addAll(entitiesFiles);

        }};
        for(Tuple2<String, String> t : mavenRestFile)
        {
            tuplesList.add(t);
        }
        return generatorUtils.generateZip(tuplesList, projectRequest.getArtifact(),
                projectRequest.isRequiredBuildWrapper());
    }

    private String getMavenFile(ProjectRequest projectRequest, MicronautProfile profile, MicronautProfile baseProfile
            , MicronautProfile federation, ProfileDetails profileDetails, ProfileDetails baseProfileDetails,
                                ProfileDetails federationDetails) throws Exception {
        /**
         * 1. get Base maven file.
         * 2. merge all required mavenFiles files.
         * 3. collect all dependencies.
         * 4. append the rest of maven files.
         * 5- get the coordinates of the dependencies.
         * 6- scan required feature from the profile (pendening).
         */
        String mavenFile = baseProfile.getSkeleton().get(MAVEN).get("/pom___xml");
        String profileMaven =profile.getSkeleton().containsKey(MAVEN)? profile.getSkeleton().get(MAVEN).get(
                "/pom___xml"):null;
//        String federationMaven = federation.getSkeleton().get(MAVEN).get("/pom___xml");

        //get all Dependencies first.
        HashSet<String> dependencies = generatorUtils.marshalAllDependencies(projectRequest, profileDetails,
                getRequiredProfileDetailsAsArray(profileDetails));

        ArrayList<Tuple2<String, String>> featuresPomFiles = new ArrayList<Tuple2<String, String>>();

        for (String x:dependencies){
            FeaturesSkeleton fs = featureSkeletonRepository.findDistinctByName(x);;

            if(fs.getSkeleton().get(MAVEN) == null)
                continue;

            String d ="";
            if(fs!= null)
                d= fs.getSkeleton().get(MAVEN).get("/pom___xml");
            if(d != null)
                if(!d.trim().isEmpty())
                    featuresPomFiles.add(Tuples.of(x, d));
        }
        FeatureReader mainFeatureReader = dependencies
                .stream().map(x->mapMavenFeatures(micronautFeatureRepository.findDistinctByName(x)))
                .reduce(new FeatureReader(),
                        (x,y)-> new FeatureReader(x.getDependencies() + "\n" + y.getDependencies()));


        Document basePomDoc = loadXMLFromString(mavenFile);
        Document profilePomDoc = profileMaven == null? null:loadXMLFromString(profileMaven);
       // Document federationPomDoc = loadXMLFromString(federationMaven);
        List<Tuple2<String, Document>> featuresPom = null;

        if(!featuresPomFiles.isEmpty())
            featuresPom= featuresPomFiles.stream()
                    .map(x-> {
                        try {
                            return Tuples.of(x.getT1(),loadXMLFromString(x.getT2()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }).collect(Collectors.toList());

        //--------------- Adding view Framworkd;
        String viewDependencies = "";
        if(projectRequest.getProfile().equalsIgnoreCase(SERVICE) && !projectRequest.getViewFramework().equalsIgnoreCase("none")) {
            switch (projectRequest.getViewFramework()) {
                case THYMELEAF:
                    viewDependencies = VIEW_MAVEN + "\n" + THYMELEAF_MAVEN;
                    break;
                case HANDLEBARS:
                    viewDependencies =  VIEW_MAVEN + "\n" + HANDLEBARS_MAVEN;
                    break;
                case VELOCITY:
                    viewDependencies =  VIEW_MAVEN + "\n" + VELOCITY_MAVEN;
                    break;
                case FREEMARKER:
                    viewDependencies =  VIEW_MAVEN + "\n" + FREEMARKER_MAVEN;
                    break;

            }
        }

        // ---- lobmok Dependencies
        String lombokDependencies = "";

        if(projectRequest.getLanguage().equalsIgnoreCase(JAVA_LANG) && !projectRequest.getEntities().isEmpty())
        {
            lombokDependencies = "<dependency>\n" +
                    "\t\t<groupId>org.projectlombok</groupId>\n" +
                    "\t\t<artifactId>lombok</artifactId>\n" +
                    "\t\t<version>1.18.12</version>\n" +
                    "\t\t<scope>provided</scope>\n" +
                    "\t</dependency>";
        }


        // JDBC Driver
        String jdbcDriverDependencies = "";
        if(!projectRequest.getEntities().isEmpty()) {

            if (!projectRequest.getDatabaseType().equalsIgnoreCase("mongodb") || !projectRequest.getDatabaseType().equalsIgnoreCase("h2")) {
                String jdbcFeatureStr =
                        templatesService.getJdbcFeacures().get(projectRequest.getDatabaseType().toLowerCase().replace(" ", "")).toLowerCase()
                                .replace("${javaversion}", projectRequest.getJavaVersion());

                ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

                MicronautFeature jdbcFeature = mapper.readValue(jdbcFeatureStr,
                        MicronautFeature.class);
                jdbcDriverDependencies = jdbcFeature.getDependencies().get(0).getMaven();
            }
        }
        ///

        //   mergeAll(projectRequest, basePomDoc, profilePomDoc, null, featuresPom);
       XmlCombiner xmlCombiner = new XmlCombiner();
       xmlCombiner.combine(basePomDoc);
       xmlCombiner.combine(profilePomDoc);
       for(Tuple2<String, Document> x:featuresPom)
        xmlCombiner.combine(x.getT2());
        

        String fullPom = xmlToString(xmlCombiner.buildDocument());

      //  String fullPom = xmlToString(basePomDoc);

        fullPom= fullPom.replace("@app.group@", projectRequest.getGroup())
                .replace("@defaultPackage@", projectRequest.getPackage())
                .replace("@app.name@", projectRequest.getArtifact())
                .replace("@version@", projectRequest.getVersion())
                .replace("@mainClassName@", profileDetails.getMainClassName(projectRequest.getPackage()))
                .replace("@dependencies@",
                        lombokDependencies +"\n" +removeDuplicateLines(mainFeatureReader.getDependencies()) + "\n" +
                                viewDependencies + "\n" + jdbcDriverDependencies)
                .replace("@jdkversion@", projectRequest.getJavaVersion().trim().equalsIgnoreCase("8")?"1.8":projectRequest.getJavaVersion())
                .replace("@repositories@", "    <repository>\n" +
                        "      <id>jcenter.bintray.com</id>\n" +
                        "      <url>https://jcenter.bintray.com</url>\n" +
                        "    </repository>")
                .replace("@arguments@", "            <argument>-noverify</argument>\n" +
                        "            <argument>-XX:TieredStopAtLevel=1</argument>\n")
                .replace("@annotationProcessorPaths@","<path>\n" +
                        "                    <groupId>io.micronaut</groupId>\n" +
                        "                    <artifactId>micronaut-inject-java</artifactId>\n" +
                        "                    <version>${micronaut.version}</version>\n" +
                        "                  </path>\n" +
                        "                  <path>\n" +
                        "                    <groupId>io.micronaut</groupId>\n" +
                        "                    <artifactId>micronaut-validation</artifactId>\n" +
                        "                    <version>${micronaut.version}</version>\n" +
                        "                  </path>")
                .replace("@services@", projectRequest.getArtifact());

        //  System.out.println(fullPom);
//        System.out.println(new XmlFormatter().format(fullPom));
        return new XmlFormatter().format(fullPom).replace("<?xml version=\"1.0\" encoding=\"UTF-16\"?>", "").trim();

    }
    public static final String prettyPrint(Document xml) throws Exception {
        Transformer tf = TransformerFactory.newInstance().newTransformer();
        tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        Writer out = new StringWriter();
        tf.transform(new DOMSource(xml), new StreamResult(out));
        return out.toString();
    }
    public void mergeAll(ProjectRequest projectRequest, Document baseDocumet, Document profileDocument,
                         Document federationDocument,
                         List<Tuple2<String, Document>> featuresDocument) throws XPathExpressionException, ParserConfigurationException {
        List<String> tagsList = Arrays.asList("properties","plugins", "properties");

        for(Tuple2<String, Document> x:featuresDocument)
        {
            appendNodePluginRepositories(x.getT2(), baseDocumet, "modules", "project");
            appendNodePluginRepositories(x.getT2(), baseDocumet, "profiles", "project");
            appendNodePluginRepositories(x.getT2(), baseDocumet, "pluginRepositories", "project");

            mergeDependcies(x.getT2(), baseDocumet);
            mergeProperties(x.getT2(), baseDocumet);
          //   mergePluginManagement(x.getT2(), baseDocumet);
            mergeBuild(x.getT2(), baseDocumet);

//            xmlCombiner.combine(baseDocumet);
//            xmlCombiner.combine(x.getT2());
            //xmlCombiner.buildDocument(baseDocumet);
        }

//        for (Document x : Arrays.asList(federationDocument, profileDocument)) {
//            if(x == null) continue;
//            appendNodePluginRepositories(x, baseDocumet, "modules", "project");
//            appendNodePluginRepositories(x, baseDocumet, "profiles", "project");
//            appendNodePluginRepositories(x, baseDocumet, "pluginRepositories", "project");
//
//
//            mergeDependcies(x, baseDocumet);
//            mergeProperties(x, baseDocumet);
//            mergeBuild(x, baseDocumet);
//        }


    }

    private void appendNodePluginRepositories(Document from, Document to, String fromNode, String toNode)
    {
        // Node projectNode = to.getElementsByTagName(toNode).item(0);
        if(from.getElementsByTagName(fromNode).item(0)!= null) {
            Node importedNode = to.importNode(from.getElementsByTagName(fromNode).item(0),
                    true);
            to.getElementsByTagName(toNode).item(0).appendChild(importedNode);
        }


    }

    private void mergeTags(Document from, Document to , String tagName){
        Node toProjectNode = to.getElementsByTagName("project").item(0);
        Node fromProjectNode = from.getElementsByTagName("project").item(0);
        Node toDependeciesNode = ((Element)toProjectNode).getElementsByTagName(tagName).item(1);
        Node fromDependeciesNode = ((Element)fromProjectNode).getElementsByTagName(tagName).item(0);
        if(fromDependeciesNode != null)
            for(int i = 0; i < fromDependeciesNode.getChildNodes().getLength(); i++)
            {
                Node child = fromDependeciesNode.getChildNodes().item(i);
                Node imported = to.importNode(child, true);
                toDependeciesNode.appendChild(imported);
            }

    }

    private void mergeDependcies(Document from, Document to)
    {
        Node toProjectNode = to.getElementsByTagName("project").item(0);
        Node fromProjectNode = from.getElementsByTagName("project").item(0);
        Node toDependeciesNode = ((Element)toProjectNode).getElementsByTagName("dependencies").item(1);
        Node fromDependeciesNode = ((Element)fromProjectNode).getElementsByTagName("dependencies").item(0);

        if(fromDependeciesNode != null)
            for(int i = 0; i < fromDependeciesNode.getChildNodes().getLength(); i++)
            {
                Node child = fromDependeciesNode.getChildNodes().item(i);
                Node imported = to.importNode(child, true);
                toDependeciesNode.appendChild(imported);
            }

    }

    private void mergePluginManagement(Document from, Document to)
    {
        Node toProjectNode = to.getElementsByTagName("project").item(0);
        Node fromProjectNode = from.getElementsByTagName("project").item(0);

        Node toBuildNode = ((Element)toProjectNode).getElementsByTagName("build").item(0);
        Node fromBuildNode = ((Element)fromProjectNode).getElementsByTagName("build").item(0);

        Node toPluginManagementNode = ((Element)toBuildNode).getElementsByTagName("pluginManagement").item(0);
        Node fromPluginManagementNode = ((Element)fromBuildNode).getElementsByTagName("pluginManagement").item(0);
        System.out.println(toPluginManagementNode.getTextContent() + "_____________");

        System.out.println(fromPluginManagementNode.getTextContent() + "_____________");

        Node toDependeciesNode = ((Element)toPluginManagementNode).getElementsByTagName("plugins").item(0);
        Node fromDependeciesNode = ((Element)fromPluginManagementNode).getElementsByTagName("plugins").item(0);
        System.out.println(toDependeciesNode.getTextContent() + "_____________");

        System.out.println(fromDependeciesNode.getTextContent() + "_____________");


        if(fromDependeciesNode != null)
            for(int i = 0; i < fromDependeciesNode.getChildNodes().getLength(); i++)
            {
                Node child = fromDependeciesNode.getChildNodes().item(i);
                Node imported = to.importNode(child, true);
                toDependeciesNode.appendChild(imported);
            }

    }
    private void mergeBuild(Document from, Document to)
    {
        Node toProjectNode = to.getElementsByTagName("project").item(0);
        Node fromProjectNode = from.getElementsByTagName("project").item(0);
        Node toDependeciesNode = ((Element)toProjectNode).getElementsByTagName("build").item(0);
        Node fromDependeciesNode = ((Element)fromProjectNode).getElementsByTagName("build").item(0);
        mergeNodeChildren(toDependeciesNode, fromDependeciesNode);


//        if(fromDependeciesNode != null)
//            for(int i = 0; i < fromDependeciesNode.getChildNodes().getLength(); i++)
//            {
//                Node child = fromDependeciesNode.getChildNodes().item(i);
//                Node imported = to.importNode(child, true);
//                toDependeciesNode.appendChild(imported);
//            }

    }
    private void mergeProperties(Document from, Document to)
    {
        Node toProjectNode = to.getElementsByTagName("project").item(0);
        Node fromProjectNode = from.getElementsByTagName("project").item(0);
        Node toDependeciesNode = ((Element)toProjectNode).getElementsByTagName("properties").item(0);
        Node fromDependeciesNode = ((Element)fromProjectNode).getElementsByTagName("properties").item(0);

        //  System.out.println(fromDependeciesNode.getTextContent());
        if(fromDependeciesNode != null)
            for(int i = 0; i < fromDependeciesNode.getChildNodes().getLength(); i++)
            {
                Node child = fromDependeciesNode.getChildNodes().item(i);
                Node imported = to.importNode(child, true);
                toDependeciesNode.appendChild(imported);
            }
    }
    public FeatureReader mapMavenFeatures(MicronautFeature micronautFeature)
    {
        FeatureReader reader = new FeatureReader();
        if(micronautFeature.getDependencies()!= null)
            for(Dependency x: micronautFeature.getDependencies()) {
                if(x.getScope().trim().equalsIgnoreCase("build"))
                    continue;
                reader.setDependencies(reader.getDependencies() + x.getMaven() + "\n");
            }
        return reader;
    }

    public void mergeNodeChildren(Node destinationNode, Node sourceNode) {
        NodeList sourceChildren = sourceNode.getChildNodes();
        Set<String> newNodeNames = new HashSet<>();
        Map<String, Integer> nameCount = new HashMap<>();
        for (int i = 0; i < sourceChildren.getLength(); i++) {
            Node childNode = sourceChildren.item(i);
            int nameOrdinal = nameCount.containsKey(childNode.getNodeName()) ? nameCount.get(childNode.getNodeName()) : 0;
            Node destinationChildNode = getChildByName(destinationNode, childNode.getNodeName(), nameOrdinal);
            if (destinationChildNode == null) {
                // if nameOrdinal > 0 and the node name existed in the original form of the destination document,
                // that means this is an extra instance of this type of node. ignore it
                if (nameOrdinal == 0 || newNodeNames.contains(childNode.getNodeName())) {
                    // this is our first node by this name. append it
                    Node importedNode = destinationNode.getOwnerDocument().importNode(childNode, true);
                    destinationNode.appendChild(importedNode);
                    newNodeNames.add(importedNode.getNodeName());
                }
            } else {
                mergeNodeChildren(destinationChildNode, childNode);
            }
            nameCount.put(childNode.getNodeName(), nameOrdinal + 1);
        }
    }

    private Node getChildByName(Node node, String name, int ordinal) {
        Node childNode = null;
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i).getNodeName().equals(name) && childNode == null) {
                ordinal--;
                if (ordinal < 0) {
                    childNode = children.item(i);
                }
            }
        }
        return childNode;
    }
}
