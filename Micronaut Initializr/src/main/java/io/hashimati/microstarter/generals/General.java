package io.hashimati.microstarter.generals;

/**
 * @author Ahmed Al Hashmi @hashimati
 *
 * @implNote This class need to be reimplemented with Groovy Templates with files
 */


public class General
{
    public static String homePageTemplate = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>Microstarter.io</title>\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">\n" +
            "\n" +
            "    <!-- Bootstrap CSS -->\n" +
            "    <link rel=\"stylesheet\" href=\"https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css\" integrity=\"sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh\" crossorigin=\"anonymous\">\n" +
            "</head>\n" +
            "<style>\n" +
            "    /* Sticky Footer Classes */\n" +
            "\n" +
            "    html,\n" +
            "    body {\n" +
            "        height: 100%;\n" +
            "    }\n" +
            "\n" +
            "    #page-content {\n" +
            "        flex: 1 0 auto;\n" +
            "    }\n" +
            "\n" +
            "    #sticky-footer {\n" +
            "        flex-shrink: none;\n" +
            "\n" +
            "    }\n" +
            "\n" +
            "    /* Other Classes for Page Styling */\n" +
            "\n" +
            "    body {\n" +
            "        background: black;\n" +
            "        background: linear-gradient(to right, #7d7d7d, #000000);\n" +
            "    }\n" +
            "\n" +
            "</style>\n" +
            "<body class=\"d-flex flex-column\">\n" +
            "<div id=\"page-content\">\n" +
            "    <div class=\"container text-center\">\n" +
            "        <div class=\"row justify-content-center\">\n" +
            "            <div class=\"col-md-7\">\n" +
            "                <h1 class=\"font-weight-light mt-4 text-white\">Congratulation!</h1>\n" +
            "                <p class=\"lead text-white-50\">You've created a <b>Micronaut</b> Application</p>\n" +
            "            </div>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "</div>\n" +
            "<footer id=\"sticky-footer\" class=\"py-4 bg-dark text-white-50\">\n" +
            "    <div class=\"container text-center\">\n" +
            "        <small>Generated Using <a href=\"https://www.microstarter.io\">Microstarter.IO</a></small>\n" +
            "    </div>\n" +
            "</footer>\n" +
            "<!-- Optional JavaScript -->\n" +
            "<!-- jQuery first, then Popper.js, then Bootstrap JS -->\n" +
            "<script src=\"https://code.jquery.com/jquery-3.4.1.slim.min.js\" integrity=\"sha384-J6qa4849blE2+poT4WnyKhv5vZF5SrPo0iEjwBvKU7imGFAV0wwj1yYfoRSJoZ+n\" crossorigin=\"anonymous\"></script>\n" +
            "<script src=\"https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js\" integrity=\"sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo\" crossorigin=\"anonymous\"></script>\n" +
            "<script src=\"https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js\" integrity=\"sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6\" crossorigin=\"anonymous\"></script>\n" +
            "\n" +
            "</body>\n" +
            "</html>",
        Controller_java = "package ${package};\n" +
                "\n" +
                "import io.micronaut.http.HttpResponse;\n" +
                "import io.micronaut.http.HttpStatus;\n" +
                "import io.micronaut.http.annotation.Controller;\n" +
                "import io.micronaut.http.annotation.Error;\n" +
                "import io.micronaut.http.annotation.Get;\n" +
                "import io.micronaut.views.ModelAndView;\n" +
                "import io.micronaut.views.View;\n" +
                "\n" +
                "@Controller(\"/\")\n" +
                "public class ViewController {\n" +
                "\n" +
                "\n" +
                "    @View(\"index\")\n" +
                "    @Get(\"/\")\n" +
                "    public HttpResponse home()\n" +
                "    {\n" +
                "            return HttpResponse.ok(); \n" +
                "    }\n" +
                "    \n" +
                "}",
        Controller_kotlin = "package ${package};\n" +
                "\n" +
                "import io.micronaut.http.HttpResponse;\n" +
                "import io.micronaut.http.annotation.Controller;\n" +
                "import io.micronaut.http.annotation.Get;\n" +
                "import io.micronaut.views.View;\n" +
                "\n" +
                "\n" +
                "@Controller(\"/\")\n" +
                "class ViewController {\n" +
                "\n" +
                "    @View(\"index\")\n" +
                "    @Get(\"/\")\n" +
                "    fun index() : HttpResponse<String> = HttpResponse.ok()\n" +
                "}",
        Controller_groovy = "package ${package}\n" +
                "\n" +
                "import io.micronaut.http.HttpResponse;\n" +
                "import io.micronaut.http.annotation.Controller;\n" +
                "import io.micronaut.http.annotation.Get;\n" +
                "import io.micronaut.views.View;\n" +
                "\n" +
                "\n" +
                "\n" +
                "@Controller(\"/\")\n" +
                "class ViewController {\n" +
                "\n" +
                "        @View(\"index\")\n" +
                "        @Get(\"/\")\n" +
                "        public HttpResponse home()\n" +
                "        {\n" +
                "                return HttpResponse.ok(); \n" +
                "        }\n" +
                "}\n";

}
