package io.hashimati.microstarter.rest;

/**
 * @author Ahmed Al Hashmi @hashimati
 */


import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.views.View;

import java.net.URI;
import java.net.URISyntaxException;


@Controller
public class ViewController{


        @View("NoIE")
        @Get("/notSupported")
        public HttpResponse home()
        {
                return HttpResponse.ok();
        }
        @View("newUI")
        @Get(uris = {"/", "/m", "micronaut", "micronautio", "mn"})
        public HttpResponse index() throws URISyntaxException {


                return HttpResponse.ok();
        }
}
