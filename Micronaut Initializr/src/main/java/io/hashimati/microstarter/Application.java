package io.hashimati.microstarter;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;


@OpenAPIDefinition(
        info = @Info(
                title = "Hello World",
                version = "1.0",
                description = "Microstarter API",
                license = @License(name = "Apache 2.0", url = "https://www.microstarter.io"),
                contact = @Contact(url = "https://wwww.microstarter.io", name = "Ahmed AlHashim",
                        email ="hashiamti.ahmed@gmail.com")
        )
)
public class Application {

    public static void main(String[] args) {
        Micronaut.run(Application.class);
    }
}