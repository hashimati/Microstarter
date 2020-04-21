package io.hashimati.microstarter.constants;

import java.util.HashMap;

import static io.hashimati.microstarter.constants.ProjectConstants.EntityObject.*;

/**
 * @author Ahmed Al Hashmi @hashimati
 */

@Deprecated
public class MicronautEntityTemplates {


    public static class JavaTemplates{

        public static HashMap<String, String> templates = new HashMap<String, String>(){{
            put(ENTITY, "package ${entitypackage}\n" +
                    "\n" +
                    "\n" +
                    "import javax.persistence.*;\n" +
                    "import lombok.*;\n" +
                    "\n" +
                    "@Data\n" +
                    "@ToString\n" +
                    "@NoArgsConstructor\n" +
                    "@AllArgsConstructor\n" +
                    "@EqualsAndHashCode\n" +
                    "@Builder\n" +
                    "public class ${className}{\n" +
                    "\n" +
                    "\t${instances}\n" +
                    "}\n");
            put(SERVICE, "${packageName ? 'package ' + packageName + ';' : '' }\n" +
                    "\n" +
                    "import javax.inject.Singleton;\n" +
                    "\n" +
                    "@Singleton\n" +
                    "public class ${className} {\n" +
                    "\n" +
                    "    ${methods}\n" +
                    "}\n");
            put(REPOSITORY, "package ${entityRepositoryPackage};\n" +
                    "\n" +
                    "import io.micronaut.data.annotation.*;\n" +
                    "import io.micronaut.data.model.*;\n" +
                    "import io.micronaut.data.repository.CrudRepository;\n" +
                    "import java.util.List;\n" +
                    "\n" +
                    "@Repository\n" +
                    "interface ${className}Repository extends CrudRepository<${className}, Long> {\n" +
                    "    ${className} find(long id);\n" +
                    "}");
            put(CONTROLLER, "");
            put(TEST, "");
            put(METHOD, "public ${return_type} ${methodname}" +
                    "{ \n${method_logic}\n\n\treturn ${return_value}; \n}");
            put(INSTANCE_VAR , "private ${var_type} ${var_name};");
        }};
    }
    public static class GroovyTemplates{
        public static HashMap<String, String> templates = new HashMap<String, String>(){{
            put(ENTITY, "");
            put(SERVICE, "");
            put(REPOSITORY, "");
            put(CONTROLLER, "");
            put(TEST, "");
            put(METHOD, "");
            put(INSTANCE_VAR , "${var_type} ${var_name}\n");

        }};
    }
    public static class KotlinTemplates{
        public static HashMap<String, String> templates = new HashMap<String, String>(){{
            put(ENTITY, "");
            put(SERVICE, "");
            put(REPOSITORY, "");
            put(CONTROLLER, "");
            put(TEST, "");
            put(METHOD, "");
            put(INSTANCE_VAR , "var ${var_name}:${var_type}\n");

        }};
    }
}
