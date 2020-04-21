package io.hashimati.microstarter.util;

import groovy.text.SimpleTemplateEngine;
import io.hashimati.microstarter.entity.micronaut.ProjectRequest;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
/**
 * @author Ahmed Al Hashmi @hashimati
 */

public class GeneratorUtils
{
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

    public  static String getApplicationPackage(ProjectRequest projectRequest)
    {

        return projectRequest.getGroup().trim()+ "." + projectRequest.getArtifact().trim();
    }


    public static String invokeProjectRequestMethod(ProjectRequest projectRequest, String method){

        Class<? extends ProjectRequest> pClass = projectRequest.getClass();
        try {
            Method invokedMethod = pClass.getDeclaredMethod(method);
            if(invokedMethod !=null){
                return (String)invokedMethod.invoke(projectRequest);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String removeDuplicateLines(String snippet){

        return new HashSet<String>(){{
            addAll(Arrays.asList(snippet.split("\n")));
        }}.stream().sorted((x,y)->x.compareTo(y)).reduce("", (x,y)->x + "\n" +y ); 
    }

    /**
     *
     * @param Tuple2<String, String>
     * @return zip file
     * @throws Exception
     */
	 
	 
	  /**
     *
     * @return zip file
     * @throws Exception
     */
    public static String generateFromTemplate(String template, HashMap<String, String> binder)
    {
        try {
            return new SimpleTemplateEngine().createTemplate(template).make(binder).toString();
        } catch (ClassNotFoundException e) {

            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;

        }
    }
}
