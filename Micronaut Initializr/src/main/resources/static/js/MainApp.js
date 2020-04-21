/**
 * @author Ahmed Al Hashmi @hashimati
 */
//This component create tag out of the selected item from the auto complete list.
// Vue.use(VueFormWizard)
// new Vue({
//     el: '#app',
//     methods: {
//         onComplete: function(){
//             alert('Yay. Done!');
//         }
//     }
// })
(function(){
    $('select').material_select();
    $('.materialSelect').on('contentChanged', function() {
        $(this).material_select();
    });
    document.addEventListener('DOMContentLoaded', function() {
        var elems = document.querySelectorAll('.tooltipped');
        var instances = M.Tooltip.init(elems, options);
    });
});
var tagComp= {
    props:['name'],
    template:'<div style="padding-bottom:3px;"><span class="tag" style="background:#004347; color:#eee;' +
        ' padding-right:' +
        ' 3px;padding-left: 3px;"' +
        ' class="badge' +
        ' d-flex' +
        ' align-items-center" >{{name}} <a class="close d-flex align-items-center"' +
        ' aria-label="Close" v-on:click="removeTag(name.toString())"><span' +
        ' aria-hidden="true">&times;</span></a></span></div>'
    ,
    methods:{
        removeTag(name){
            this.$root.$data.names.splice( this.$root.$data.names.indexOf(name), 1 );
            this.$root.generateMnCommandLocal();
        }
    }
};

var tagComp2= {
    props:['name'],
    template:'<li v-on:click="removeTag(name.toString())" '+
        ' class="collection-item avatar">' +
        ' <i class="material-icons circle green">code</i>' +
        ' <span class="title">{{name}}</span>' +
        ' <a href="#!" class="secondary-content"><i class="material-icons">delete</i></a></li>'
    ,
    methods:{
        removeTag(name){
            this.$root.$data.names.splice( this.$root.$data.names.indexOf(name), 1 );
            this.$root.generateMnCommandLocal();

        }
    }
};


// This component make the auto complete list.
var autoCompleteList={
    props:['items'],
    template:'<div class="list-group"><a v-for="item in items"' +
        ' v-on:click="selectFromAutoComplete(item.name)"' +
        ' href="#"' +
        ' v-bind:class="item.classDescription"' +
        ' v-bind:ref="item.name"><div><div><b>{{item.name}}</b></div><span>{{item.description}}</span></div></a></div>'
    ,
    methods:{
        selectFromAutoComplete(x)
        {
            if (this.$root.$data.names.indexOf(x) >= 0)
            {

                this.$root.$data.names.splice(this.$root.$data.names.indexOf(x), 1);
            }
            else {
                this.$root.$data.names.push(x);
            }
            this.$root.restAutoCompleteList();
            this.$root.generateMnCommandLocal();

        }
    }
};

var autoCompleteList2={
    props:['items'],
    template:'<ul class="collection"><li v-for="item in items"' +
        ' v-on:click="selectFromAutoComplete(item.name)"' +
        // ' v-on:mouse' +
        ' v-bind:class="item.classDescription" v-bind:id="item.name">'+
        '<i class="material-icons circle green">code</i>' +
        '<span class="title">{{item.name}}</span>' +
        '<p>{{item.description}}</p>' +
        '<a href="#!" class="secondary-content"><i class="material-icons">add</i></a></li></ul>'
    ,
    methods:{
        selectFromAutoComplete(x)
        {
            if (this.$root.$data.names.indexOf(x) >= 0)
            {

                this.$root.$data.names.splice(this.$root.$data.names.indexOf(x), 1);
            }
            else {
                this.$root.$data.names.push(x);

            }
            this.$root.restAutoCompleteList();
            this.$root.generateMnCommandLocal();

            console.log(this.$root.$data.names)

        }
    }
};
var router = new VueRouter({

    mode: 'history',

    routes: []

});
Vue.directive("select", {
    "twoWay": true,

    "bind": function () {
        $(this.el).material_select();

        var self = this;

        $(this.el).on('change', function() {
            self.set($(self.el).val());
        });
    },

    update: function (newValue, oldValue) {
        $(this.el).val(newValue);
    },

    "unbind": function () {
        $(this.el).material_select('destroy');
    }
});

// This the main application.
var app = new Vue({
    router,
    el:"#startapp",
    mounted: function() {

        var isloading = false;
        if(this.$route.query.artifact!= null) {
            this.artifact = this.$route.query.artifact;
            isloading = true;
        }
        if(this.$route.query.g!= null) {
            this.group = this.$route.query.g;
            isloading = true;

        }
        if(this.$route.query.build!= null) {
            this.build = this.$route.query.build;
            isloading = true;

        }
        if(this.$route.query.viewFramework!= null) {
            this.viewFramework = this.$route.query.viewFramework;
            isloading = true;

        }
        if(this.$route.query.javaVersion!= null) {

            this.javaVersion = this.$route.query.javaVersion;
            isloading = true;

        }
        if(this.$route.query.language!= null) {

            this.language = this.$route.query.language;
            isloading = true;

        }
        if(this.$route.query.profile!= null) {

            this.profile = this.$route.query.profile;
            isloading = true;

        }
        if(this.$route.query.port!=null)
        {
            this.port = this.$route.query.port;
            isloading = true;

        }
        if(this.$route.query.d!= null) {
            this.names= this.$route.query.d.split(",");
            isloading = true;

        }


        console.log(this.artifact);

        if(isloading == true){

            this.generateMnCommandLocal();
            this.openSuccessMessage("Configurations Have Been Loaded Successfully!");


        }
    },
    data:{

        group:'com.mygroup',
        artifact:'myapp',
        port: -1,
        names:[],
        viewFramework:"None",
        isPS: true,
        // classInfo:{name:"Ahmed", profile:"Taqi", type:"Alhashmi", packagePath:"ee", propertyPath:"ee"},
        classesList:[],
        version:'1.3.3', language:'Java', build:'Gradle',profile:'service',testframework:'junit',
        requiredBuildWrapper:true,
        submitMessage:'',
        dependenciesList:[],
        viewAutoComplete:false,  // toggle for auto complete list.
        selectedItemInAutoCompleted:0, // the index of the current item in the auto complete list.
        dependencySearchQuery:'',// The search query.... based on it the auto complete list should be filtered.
        fullMode:false,
        javaVersion:'8',
        fullDependciesList:[],
        showCliTemplate:false,
        hideViewModeText:'More',
        mnCommand:"",//"mn create-app com-mygroup-myapp",
        mnUrl: "",//https://www.microstarter.io/",
        currentLayout:"projectMetadataLayout",
        listItems:[
            // { /// the list item of the auto complete list.
            //     //first Item should be always active.
            //     name:'Mongo',
            //     description:'NoSQL Database',
            //     classDescription:'list-group-item list-group-item-action active'
            // },{
            //     name:'JWT',
            //     description:'JSON Web Token',
            //     classDescription:'list-group-item list-group-item-action'
            // },{
            //     name:'Security',
            //     description:'Security dependency',
            //     classDescription:'list-group-item list-group-item-action'
            // }
        ],
        //templatesCategories:["test", "test2", "test3"],
        fullTemplatesList: { "RabbitMQ": [ "Listener", "Producer" ], "Kafka": [ "Listener", "Producer" ], "Service": [ "Client", "Controller", "ControllerTest", "WebsocketClient", "WebsocketServer" ], "Base": [ "Bean","Job", "Test" ], "GRPC": [ "Service" ] , "CLI":["Command", "CommandTest"]} ,
        selectedTemplatesList:[],
        selectedTemplateCatgory:"Base",
        selectedTemplate:"",
        templateName: "ClassName",
        templatePackage:"classpackage",
        templateMapPath:"",
        templateSocketTopic:"",
        templateCommandPropertiy:"",
        databaseTypeForm:"MySQL",
        databaseNameForm:"",
        entityNameForm:"",
        collectionNameForm:"",
        attributeNameForm:"",
        attributeTypeForm:"String",
        entityAttributes:[],
        entities:[],
        databaseType:"",
        databaseName:"",
        entityOneSelectLabel: "Select Entity One",
        entityTwoSelectLabel:"Select Entity Two",
        entityOneSelectForm:"",
        entityTwoSelectForm:"",
        relationshipForm:"One-To-Many",
        entityRelationShips:[]


    },
    computed:{

        isPSC:function()
        {
            return this.isPS;
        }
    },
    //registered components.
    components:{
        'ahmed':tagComp,
        'ahmed2':tagComp2,
        'auto':autoCompleteList,
        'auto2':autoCompleteList2
    },
    created: function () {
        window.addEventListener('keyup', this.sendGenerateRequestAltEnter)
    },
    methods:{


        constructFullDependenciesList(){
            this.fullDependciesList = [];
            Rx.Observable.from(this.dependenciesList)
                .filter(x=>
                    (this.language.indexOf("Java")>=0 && ((x.name.toLowerCase().indexOf("groovy") <0 && x.description.toLowerCase().indexOf("groovy") <0)
                        &&(x.name.toLowerCase().indexOf("kotlin")<0 && x.description.toLowerCase().indexOf("kotlin")<0) &&
                        (x.description.toLowerCase().indexOf("gorm")<0 && x.description.toLowerCase().indexOf("gorm")<0) && (x.description.toLowerCase().indexOf("gorm")<0 && x.description.toLowerCase().indexOf("gorm")<0) && (x.description.toLowerCase().indexOf("spock")<0 && x.description.toLowerCase().indexOf("spock")<0) && (x.description.toLowerCase().indexOf("spek")<0 && x.description.toLowerCase().indexOf("spek")<0)))


                    ||(this.language.indexOf("Groovy")>=0 &&((x.name.toLowerCase().indexOf("java") <0 && x.description.toLowerCase().indexOf("java") <0)
                    &&(x.name.toLowerCase().indexOf("kotlin")<0 && x.description.toLowerCase().indexOf("kotlin")<0) &&  (x.description.toLowerCase().indexOf("junit")<0 && x.description.toLowerCase().indexOf("junit")<0) && (x.description.toLowerCase().indexOf("spek")<0 && x.description.toLowerCase().indexOf("spek")<0)))

                    ||(this.language.indexOf("Kotlin")>=0 && ((x.name.toLowerCase().indexOf("groovy") <0 && x.description.toLowerCase().indexOf("groovy") <0)
                    &&(x.name.toLowerCase().indexOf("java")<0 && x.description.toLowerCase().indexOf("java")<0) &&
                    (x.description.toLowerCase().indexOf("gorm")<0 && x.description.toLowerCase().indexOf("gorm")<0) && (x.description.toLowerCase().indexOf("spock")<0 && x.description.toLowerCase().indexOf("spock")<0) && (x.description.toLowerCase().indexOf("junit")<0 && x.description.toLowerCase().indexOf("junit")<0)))
                )//.take(5)
                .forEach(x=> {


                    if(this.profile.indexOf("service") >=0 || this.profile.indexOf("grpc") >=0){

                        if(x.name.indexOf("function") < 0 && x.description.toLowerCase().indexOf("function")< 0
                            && x.name.indexOf("function-aws") < 0 && x.description.toLowerCase().indexOf("function")< 0 )
                        {
                            this.fullDependciesList.push({
                                name: x.name,
                                description: x.description,
                                // classDescription: 'list-group-item list-group-item-action'
                                classDescription:'collection-item avatar'
                            });
                        }
                    }
                    else
                    {
                        this.fullDependciesList.push({
                            name: x.name,
                            description: x.description,
                            classDescription:'collection-item avatar'
                            // classDescription: 'list-group-item list-group-item-action',

                        });
                    }
                });
        },
        filterListItems(){

            this.listItems = [];
            Rx.Observable.from(this.dependenciesList)

                .filter(x=>x.name.toLowerCase().indexOf(this.dependencySearchQuery.toLowerCase())>=0)
                .filter(x=>(x.name.toLowerCase().indexOf("openfaas")<0 && this.profile.toLowerCase().indexOf("function")<0) ||  this.profile.toLowerCase().indexOf("function")>=0)
                .filter(x=>(this.language.indexOf("Java")>=0 && ((x.name.toLowerCase().indexOf("groovy") <0 && x.description.toLowerCase().indexOf("groovy") <0)
                    &&(x.name.toLowerCase().indexOf("kotlin")<0 && x.description.toLowerCase().indexOf("kotlin")<0) &&
                    (x.description.toLowerCase().indexOf("gorm")<0 && x.description.toLowerCase().indexOf("gorm")<0) && (x.description.toLowerCase().indexOf("gorm")<0 && x.description.toLowerCase().indexOf("gorm")<0) && (x.description.toLowerCase().indexOf("spock")<0 && x.description.toLowerCase().indexOf("spock")<0) && (x.description.toLowerCase().indexOf("spek")<0 && x.description.toLowerCase().indexOf("spek")<0)))


                    ||(this.language.indexOf("Groovy")>=0 &&((x.name.toLowerCase().indexOf("java") <0 && x.description.toLowerCase().indexOf("java") <0)
                        &&(x.name.toLowerCase().indexOf("kotlin")<0 && x.description.toLowerCase().indexOf("kotlin")<0) &&  (x.description.toLowerCase().indexOf("junit")<0 && x.description.toLowerCase().indexOf("junit")<0) && (x.description.toLowerCase().indexOf("spek")<0 && x.description.toLowerCase().indexOf("spek")<0)))

                    ||(this.language.indexOf("Kotlin")>=0 && ((x.name.toLowerCase().indexOf("groovy") <0 && x.description.toLowerCase().indexOf("groovy") <0)
                        &&(x.name.toLowerCase().indexOf("java")<0 && x.description.toLowerCase().indexOf("java")<0) &&
                        (x.description.toLowerCase().indexOf("gorm")<0 && x.description.toLowerCase().indexOf("gorm")<0) && (x.description.toLowerCase().indexOf("spock")<0 && x.description.toLowerCase().indexOf("spock")<0) && (x.description.toLowerCase().indexOf("junit")<0 && x.description.toLowerCase().indexOf("junit")<0)))
                )//.take(5)
                .forEach(x=> {


                    if(this.profile.indexOf("service") >=0 || this.profile.indexOf("grpc") >=0){

                        if(x.name.indexOf("function") < 0 && x.description.toLowerCase().indexOf("function")< 0
                            && x.name.indexOf("function-aws") < 0 && x.description.toLowerCase().indexOf("function")< 0 && x.name.indexOf("test-aws") < 0 )
                        {
                            this.listItems.push({
                                name: x.name,
                                description: x.description,
                                // classDescription: 'list-group-item list-group-item-action'
                                classDescription:'collection-item avatar',


                            });
                        }
                    }
                    else
                    {
                        this.listItems.push({
                            name: x.name,
                            description: x.description,
                            classDescription:'collection-item avatar',

                            // classDescription: 'list-group-item list-group-item-action',

                        });
                    }
                });

        },
        sendGenerateRequestAltEnter(e)
        {

            //to make aware about alt+enter event in all the page.
            if(e.keyCode===13 && e.altKey)
            {

                //  alert('Alt + Enter pressed!');
                this.sendGenerateRequest();
            }
        }
        ,
        sendGenerateRequest()
        {
            while(this.group.trim().endsWith("."))
            {
                this.group = this.group.trim().substring(0, this.group.length-1)
            }
            while(this.group.trim().startsWith("."))
            {
                this.group = this.group.trim().substring(1, this.group.length)

            }
            if(this.group.trim()==""){
                this.openErrorMessage("Group field cannot be empty");
                return;
            }

            var regex = new RegExp("package\\s+([a-zA_Z_][\\.\\w]*);")
            var resutl = regex.test("package " +this.group.trim() + ";");
            if( resutl== false)
            {
                this.openErrorMessage("The Group isn't valid");
                return;
            }




            var regexArt = new RegExp("^[A-Za-z]+$");
            var resultArt = regexArt.test(this.artifact.trim());
            if( resultArt== false)
            {
                this.openErrorMessage("The Artifact isn't valid");
                return;
            }

            if(this.artifact.trim()==""){
                this.openErrorMessage("Artifact field cannot be empty");
                return;
            }
            //continue the work here.
            let formData = {
                group:this.group.trim(),
                artifact: this.artifact.trim(),
                port: this.port,
                viewFramework:this.viewFramework,
                dependencies: this.names,
                language: this.language,
                requiredBuildWrapper:this.requiredBuildWrapper,
                version:this.version,
                build:this.build,
                javaVersion: this.javaVersion,
                profile: this.profile,
                compontentObjects:this.classesList,
                databaseName: this.databaseNameForm,
                databaseType: this.databaseTypeForm,
                entities: this.entities,
                entityRelations: this.entityRelationShips

            };
            this.openSuccessMessage("The Project will be generated shortly!");
            //   alert(formData);
            axios({url:'/api/project/submit/projectrequest'
                , data:formData, method:"POST", responseType: 'arraybuffer'})
                .then(response=>{
                    const url = window.URL.createObjectURL(new Blob([response.data], {type:"arraybuffer"}));
                    const link = document.createElement('a');
                    link.href = url;
                    link.setAttribute("download", formData.artifact+'.zip');
                    document.body.appendChild(link);
                    link.click();

                    // let blob = new Blob([response.data], { type: 'application/octet-stream' }),
                    // // url = window.URL.createObjectURL(blob)
                    // const url = window.URL.createObjectURL(new Blob([response.data]));
                    // //window.open(url)
                    // window.open(url);
                    // let blob = new Blob([response.data], { type: 'application/octet-stream', name:this.artifact+".zip" } ),
                    // url = window.URL.createObjectURL(blob)
                    //
                    // window.open(url);
                });
        },
        generateEntitiesRequest(){

            //submit/entityrequest

            while(this.group.trim().endsWith("."))
            {
                this.group = this.group.trim().substring(0, this.group.length-1)
            }
            while(this.group.trim().startsWith("."))
            {
                this.group = this.group.trim().substring(1, this.group.length)

            }
            if(this.group.trim()==""){
                this.openErrorMessage("Group field cannot be empty");
                return;
            }

            var regex = new RegExp("package\\s+([a-zA_Z_][\\.\\w]*);")
            var resutl = regex.test("package " +this.group.trim() + ";");
            if( resutl== false)
            {
                this.openErrorMessage("The Group isn't valid");
                return;
            }




            var regexArt = new RegExp("^[A-Za-z]+$");
            var resultArt = regexArt.test(this.artifact.trim());
            if( resultArt== false)
            {
                this.openErrorMessage("The Artifact isn't valid");
                return;
            }

            if(this.artifact.trim()==""){
                this.openErrorMessage("Artifact field cannot be empty");
                return;
            }
            //continue the work here.
            let formData = {
                group:this.group.trim(),
                artifact: this.artifact.trim(),
                language: this.language,
                databaseName: this.databaseNameForm,
                databaseType: this.databaseTypeForm,
                entities: this.entities,
                entityRelations: this.entityRelationShips

            };
            this.openSuccessMessage("The Project will be generated shortly!");
            //   alert(formData);
            axios({url:'/api/project/submit/entityrequest'
                , data:formData, method:"POST", responseType: 'arraybuffer'})
                .then(response=>{
                    const url = window.URL.createObjectURL(new Blob([response.data], {type:"arraybuffer"}));
                    const link = document.createElement('a');
                    link.href = url;
                    link.setAttribute("download", formData.artifact+'.zip');
                    document.body.appendChild(link);
                    link.click();

                    // let blob = new Blob([response.data], { type: 'application/octet-stream' }),
                    // // url = window.URL.createObjectURL(blob)
                    // const url = window.URL.createObjectURL(new Blob([response.data]));
                    // //window.open(url)
                    // window.open(url);
                    // let blob = new Blob([response.data], { type: 'application/octet-stream', name:this.artifact+".zip" } ),
                    // url = window.URL.createObjectURL(blob)
                    //
                    // window.open(url);
                });

        },
        removeDependency(name){
            this.names.splice(this.names.indexOf(name), 1 );
            this.generateMnCommandLocal();
            this.restAutoCompleteList();
        }
        ,generateMnCommand(){




            let formData = {
                group:this.group.trim(),
                artifact: this.artifact.trim(),
                dependencies: this.names,
                language: this.language,
                requiredBuildWrapper:this.requiredBuildWrapper,
                version:this.version,
                build:this.build,
                javaVersion: this.javaVersion,
                profile: this.profile,
                compontentObjects:this.classesList
            };
            //   alert(formData);
            axios({url:'/api/project/get/MnCommand'
                , data:formData, method:"POST"})
                .then(response=>{
                    this.mnCommand = response.data;
                });
        },
        generateMnCommandLocal(){

            var appOrFunction =this.profile.toLowerCase().indexOf("function")<0? "create-app": "create-function";
            var alexaProvider = this.profile.toLowerCase().indexOf("alexa")>=0?" --provider alexa":"";

            var g = this.group;
            var a = this.artifact;
            var featuresParams = "";
            if(this.names.length >=1) {
                for (var i = 0; i < this.names.length; i++) {
                    featuresParams = featuresParams + "--features " + this.names[i] + " ";
                }
            }
            this.mnCommand =  "mn "+appOrFunction + " " +(g +"-" +a).replace(".", "-")
                + alexaProvider
                + (appOrFunction.indexOf("app")>=0? " --profile " +this.profile.toLowerCase():"")
                + " --lang " + this.language.toLowerCase()
                + " --build " + this.build.toLowerCase() + " "
                + featuresParams;
            // this.mnUrl = "https://www.microstarter.io/?g="+g
            // +"&artifact="+ this.a
            // + "&build=" + this.build
            // + "&language=" + this.language
            // + "&profile=" + this.profile
            // + "&port=" + this.port
            // + "&javaVersion="+ this.javaVersion
            // +"&d="+featuresDelemated;

            this.generateMnUrlLocal();

        },
        generateMnUrlLocal(){
            let featuresDelemated = "";
            if(this.names.length >=1) {
                featuresDelemated ="&d="+this.names[0];
                for (var i = 1; i < this.names.length; i++) {
                    featuresDelemated = featuresDelemated +","+  this.names[i];
                }
            }

            this.mnUrl = "https://www.microstarter.io/?g="+this.group
                +"&artifact="+ this.artifact
                + "&build="+this.build
                + "&language="+ this.language
                + "&profile=" + this.profile
                + "&port=" + this.port
                + "&javaVersion="+ this.javaVersion
                + "&viewFramework="+ this.viewFramework
                +featuresDelemated;


        },

        copynNcommand(){
            this.generateMnCommandLocal();

            setTimeout(function(){

            }, 3000);
            var element = document.getElementById("mncommand");

            element.select();
            document.execCommand("copy");
            this.openSuccessMessage("The mn command is copied to clipboard");

        },
        copyMnUrl()
        {
            this.generateMnCommandLocal();

            setTimeout(function(){

            }, 3000);
            var element = document.getElementById("mnurl");

            element.select();
            document.execCommand("copy");
            this.openSuccessMessage("The shared URL is copied to clipboard");

        },
        searchEvent(e){

            if (e.keyCode===27){
                this.listItems = [];
                this.dependencySearchQuery="";
                this.viewAutoComplete = false;
                this.selectedItemInAutoCompleted = 0;
            }else if(e.keyCode === 13) {
                if (this.viewAutoComplete) {
                    if (this.names.indexOf(this.listItems[this.selectedItemInAutoCompleted].name) >= 0)
                    {
                        //delete tag.
                        this.names.splice(this.names.indexOf(this.listItems[this.selectedItemInAutoCompleted].name), 1);
                    }
                    else {
                        this.names.push(this.listItems[this.selectedItemInAutoCompleted].name);
                    }
                    this.restAutoCompleteList();
                    return;
                }
            }
            else if(e.keyCode === 38 || e.keyCode === 37) // up/left
            {
                if(this.selectedItemInAutoCompleted == 0)
                {
                    return;
                }
                else
                {
                    this.listItems[this.selectedItemInAutoCompleted].classDescription="collection-item avatar";

                    this.selectedItemInAutoCompleted--;
                    this.listItems[this.selectedItemInAutoCompleted].classDescription="collection-item avatar active";

                    console.log(this.listItems[this.selectedItemInAutoCompleted].name);
                    var elmnt =  document.getElementById(this.listItems[this.selectedItemInAutoCompleted].name);


                    elmnt.scrollIntoView(false);

                    // $('html, body').animate({
                    //     scrollTop: $('.list-group-item.list-group-item-action.active').offset().top
                    // }, 200);
                    // var scrollTo = $(".list-group-item.list-group-item-action.active");
                    // $container.animate({scrollTop: $scrollTo.offset().top - $container.offset().top + $container.scrollTop(), scrollLeft: 0},300);


                }
            }
            else if(e.keyCode === 40 || e.keyCode === 39) // down/right
            {
                if(this.selectedItemInAutoCompleted == this.listItems.length-1)
                {
                    return;
                }
                else
                {
                    // this.listItems[this.selectedItemInAutoCompleted].classDescription="list-group-item" +
                    //     " list-group-item-action";
                    this.listItems[this.selectedItemInAutoCompleted].classDescription="collection-item avatar";

                    this.selectedItemInAutoCompleted++;
                    this.listItems[this.selectedItemInAutoCompleted].classDescription="collection-item avatar active";

                    console.log(this.listItems[this.selectedItemInAutoCompleted].name);
                    var elmnt =  document.getElementById(this.listItems[this.selectedItemInAutoCompleted].name);


                    elmnt.scrollIntoView(false);
                    // $('html, body').animate({
                    //     scrollBottom: $('.list-group-item.list-group-item-action.active').offset().bottom
                    // }, -200);
                    // var scrollTo = $(".list-group-item.list-group-item-action.active");
                    // $container.animate({scrollTop: $scrollTo.offset().top - $container.offset().top + $container.scrollTop(), scrollLeft: 0},300);
                }
            }else{
                if(!this.dependencySearchQuery ||
                    this.dependencySearchQuery == undefined ||
                    this.dependencySearchQuery== "" || this.dependencySearchQuery.length == 0){
                    this.viewAutoComplete=false ;
                }
                else{
                    // this.filterListItems();
                    this.filterListItems();
                    this.listItems[this.selectedItemInAutoCompleted].classDescription="collection-item avatar";

                    this.selectedItemInAutoCompleted=0;
                    this.listItems[this.selectedItemInAutoCompleted].classDescription="collection-item avatar active";
                    this.viewAutoComplete= true;}}


        },
        addFirstSelected(e){

        },
        restAutoCompleteList(){
            this.generateMnCommandLocal();
            this.isProfileService();
            if(this.profile.toLowerCase().indexOf("cli")>=0){
                this.showCliTemplate = true;
            }
            else{
                this.showCliTemplate = false;
                this.generateMnCommandLocal();

            }
            this.viewAutoComplete=false;  // toggle for auto complete list.
            this.dependencySearchQuery='';
            this.generateMnCommandLocal();

            this.listItems[this.selectedItemInAutoCompleted].classDescription="collection-item avatar";
            this.selectedItemInAutoCompleted = 0;
            this.listItems[this.selectedItemInAutoCompleted].classDescription="collection-item avatar active";
            console.log(this.showCliTemplate);

        },
        loadMicronautDependencies(){
            axios.get('/api/loaddependicies/micronaut')
                .then((response)=>{
                    this.dependenciesList = response.data;



                });
        },
        isProfileService(){
            if(this.profile.indexOf("service") >=0) {
                this.isPS = true;

            }
            else
            {
                this.isPS = false;
            }
        },
        loadTemplates(){


            axios.get( '/api/gettemplatecategories')
                .then((response)=>{
                    this.templatesCategories = response.data;
                });

            // axios.get('/api/getcomponentstypes')
            //     .then((response)=>{
            //         this.fullTemplatesList = response.data;
            // });
            this.selectedTemplatesList = this.fullTemplatesList.Base;
            this.selectedTemplate = this.selectedTemplatesList[0];
        },
        changeTemplateList() {

            if(this.selectedTemplateCatgory.toLowerCase().indexOf("base")>=0)
            {
                this.selectedTemplatesList = this.fullTemplatesList.Base;
                console.log("base")

            }
            else if(this.selectedTemplateCatgory.toLowerCase().indexOf("service")>=0)
            {
                this.selectedTemplatesList = this.fullTemplatesList.Service;
                console.log("service")
                this.$forceUpdate();

            }
            else if(this.selectedTemplateCatgory.toLowerCase().indexOf("kafka")>=0)
            {
                this.selectedTemplatesList = this.fullTemplatesList.Kafka;
                console.log("kafka")

            }
            else if(this.selectedTemplateCatgory.toLowerCase().indexOf("rabbitmq")>=0)
            {
                this.selectedTemplatesList = this.fullTemplatesList.RabbitMQ;

                console.log("rabbitmq");
            }
            else if(this.selectedTemplateCatgory.toLowerCase().indexOf("grpc")>=0)
            {
                this.selectedTemplatesList = this.fullTemplatesList.GRPC;
                console.log("grpc")


            }
            else if(this.selectedTemplateCatgory.toLowerCase().indexOf("cli")>=0)
            {
                this.selectedTemplatesList = this.fullTemplatesList.CLI;
                console.log("grpc")


            }
            this.templateMapPath = "";
            this.templateSocketTopic = "";
            this.$forceUpdate();
            this.selectedTemplate = this.selectedTemplatesList[0];

        },
        resetTemplateParms(){
            this.templateMapPath = "";
            this.templateSocketTopic = "";
            this.templateCommandPropertiy = "";
        },
        viewHideFullMode(){

            this.fullMode = !this.fullMode;
            this.hideViewModeText=this.fullMode?"Less":"More"
        },
        showTemplatePath(){

            var lower = this.selectedTemplate.toLowerCase();
            return lower.indexOf("socket") >=0 ||
                lower.indexOf("controller") >=0 ||
                lower.indexOf("client") >=0;
        },
        showTemplateProperty(){
            var lower = this.selectedTemplate.toLowerCase();
            return lower.indexOf("command") >=0;
        },
        showTemplateTopic(){
            // this.templateMapPath = "";
            // this.templateSocketTopic = "";
            var lower = this.selectedTemplate.toLowerCase();
            return lower.indexOf("socket") >=0;
        },
        addComponent(){
            var notfound =true;
            for(var i = 0; i < this.classesList.length; i++)
            {
                if(this.templateName==this.classesList[i].name
                    && this.templatePackage == this.classesList[i].packagePath)
                {
                    notfound = false;
                    this.openErrorMessage(this.templateName + " class is already added to the list");
                }
            }
            if(notfound) {
                this.classesList.push({
                    name: this.templateName,
                    profile: this.selectedTemplateCatgory,
                    type: this.selectedTemplate,
                    packagePath: this.templatePackage,
                    propertyPath: this.templateMapPath,
                    topic: this.templateSocketTopic,
                    commandProperty: this.templateCommandPropertiy
                });
            }
        },

        deleteComponent(name, packagePath)
        {
            var index = -1;
            var notfound =true;
            for(var i = 0; i < this.classesList.length; i++)
            {
                if(name==this.classesList[i].name && packagePath == this.classesList[i].packagePath)
                {
                    notfound = false;
                    index = i;
                    break;
                }
            }
            this.classesList.splice(index, 1);
        },
        openErrorMessage(errormsg){
            var toastHTML = '<span><i class="material-icons left">error</i>'+errormsg+'</span>';
            M.toast({html: toastHTML, classes:'errorToast rounded'});


        },
        openSuccessMessage(successmsg){
            var toastHTML = '<span><i class="material-icons left">done</i>'+successmsg+'</span>';
            M.toast({html: toastHTML, classes:'successToast rounded'});

        },
        showCliOption(){
            var lower = this.profile.toLowerCase();
            console.log(lower)
            return lower.indexOf("cli") >=0;
        },

        elementFadeOut(nextLayout,$event){


            if(nextLayout == this.currentLayout)
                return ;
            var currentTab1="";
            var currentTab2 = "";

            var nextTab1="";
            var nextTab2="";

            var current = document.getElementById(this.currentLayout);
            var currentTab1=document.getElementById(this.currentLayout.concat("Tab1"));
            var currentTab2 = document.getElementById(this.currentLayout.concat("Tab2"));
            current.classList.add("scale-out");
            current.classList.add("hide")
            currentTab1.classList.remove("active");
            currentTab2.classList.remove("active");



            this.currentLayout=nextLayout;
            var next = document.getElementById(this.currentLayout);
            currentTab1=document.getElementById(this.currentLayout.concat("Tab1"));
            currentTab2 = document.getElementById(this.currentLayout.concat("Tab2"));
            currentTab1.classList.add("active");
            currentTab2.classList.add("active");
            next.classList.remove("hide");
            next.classList.remove("scale-out");



            //console.log(e2);

        },
        addAttribute()
        {

            if(this.attributeNameForm.trim()=="" || this.attributeTypeForm.trim() == "")
            {
                return;
            }
            if(this.entityNameForm.trim()=="" || this.collectionNameForm.trim() == "")
            {
                this.openErrorMessage("Please, enter entity name and collection name");
            }
            let attribute = {name: this.attributeNameForm,
                            type : this.attributeTypeForm}

            var index = -1;
            var notfound =true;
            for(var i = 0; i < this.entityAttributes.length; i++)
            {
                if(attribute.name==this.entityAttributes[i].name)
                {
                    notfound = false;
                    index = i;
                    break;
                }
            }
            if(notfound == true) {
                this.entityAttributes.push(attribute);
            }
            this.attributeNameForm = "";
            this.attributeTypeForm= "String";
        },
        deleteAttribute(name)
        {
            var index = -1;
            var notfound =true;
            for(var i = 0; i < this.entityAttributes.length; i++)
            {
                if(name==this.entityAttributes[i].name)
                {
                    notfound = false;
                    index = i;
                    break;
                }
            }
            this.entityAttributes.splice(index, 1);
        },
        addEntity(){


            if(this.entityNameForm.trim()=="" || this.collectionNameForm.trim() == "")
            {
                this.openErrorMessage("The entity name and collection name shouldn't be empty!");
                return ;
            }
            if(this.isCollectionFound(this.collectionNameForm))
            {
                this.openErrorMessage("The collection name is already exist");
                return;
            }
            let entity = {
                name: this.entityNameForm,
                collectionName : this.collectionNameForm,
                attributes: this.entityAttributes.slice()
            }

            var index = -1;
            var notfound =true;
            for(var i = 0; i < this.entities.length; i++)
            {
                if(this.entityNameForm==this.entities[i].name)
                {
                    notfound = false;
                    index = i;
                    break;
                }
            }

            if(notfound == true) {
                this.entities.push(entity);
                console.log(this.entities);
            }
            else {

                this.openErrorMessage(this.entityNameForm + " is already defined");
                return ;
            }
            //clean up
            this.entityNameForm="";
            this.collectionNameForm = "";
            this.entityAttributes = [] ;
        },
        deleteEntity(name){
            var index = -1;
            var notfound =true;
            for(var i = 0; i < this.entities.length; i++)
            {
                if(name==this.entities[i].name)
                {
                    notfound = false;
                    index = i;
                    break;
                }
            }
            this.entities.splice(index, 1);
            this.deleteRelation(name);
        },
        deleteByName(array, name){
            var index = -1;
            var notfound =true;
            for(var i = 0; i < array; i++)
            {
                if(name==array[i].name)
                {
                    notfound = false;
                    index = i;
                    break;
                }
            }
            array.splice(index, 1);
        },
        selectEntityInRelationship(side, entity)
        {
            if(side == "1"){
                this.entityOneSelectLabel = entity;
                this.entityOneSelectForm = entity;
            }
            else
            {
                this.entityTwoSelectLabel = entity;
                this.entityTwoSelectForm = entity;
            }
        },
        addRelations(){
            if(this.entityOneSelectForm.trim()=="" || this.entityTwoSelectForm.trim() == "")
            {
                return ;
                this.entityOneSelectLabel= "Select Entity One";
                this.entityTwoSelectLabel="Select Entity Two";
                this.entityOneSelectForm="";
                this.entityTwoSelectForm="";
                this.relationshipForm="One-To-Many";
            }
            let relationship = {
                e1: this.entityOneSelectForm,
                e2 : this.entityTwoSelectForm,
                relationType: this.relationshipForm.replace("-", "").replace("-", "")
            }

            var index = -1;
            var notfound =true;
            for(var i = 0; i < this.entityRelationShips.length; i++)
            {
                if((this.entityOneSelectForm==this.entityRelationShips[i].e1 && this.entityTwoSelectForm==this.entityRelationShips[i].e2 ) ||
                    ((this.entityTwoSelectForm==this.entityRelationShips[i].e1 && this.entityOneSelectForm==this.entityRelationShips[i].e2 )))
                {
                    notfound = false;
                    index = i;
                    break;
                }

            }
            if(notfound == true) {
                this.entityRelationShips.push(relationship);
            }
            else{
                this.openErrorMessage("Already, there is a relationship between " + this.entityOneSelectForm + " and " +
                    this.entityTwoSelectForm);
            }
            this.entityOneSelectLabel= "Select Entity One";
            this.entityTwoSelectLabel="Select Entity Two";
            this.entityOneSelectForm="";
            this.entityTwoSelectForm="";
            this.relationshipForm="One-To-Many";
        },
        deleteRelation(e1, e2){

            var index = -1;
            var notfound =true;
            for(var i = 0; i < this.entityRelationShips.length; i++)
            {
                if(e1==this.entityRelationShips[i].e1 && e2==this.entityRelationShips[i].e2)
                {
                    notfound = false;
                    index = i;
                    break;
                }
            }
            this.entityRelationShips.splice(index, 1);
        },
        deleteRelation(e1){
            var index = -1;
            var notfound =true;
            for(;;) {
                notfound= true;
                for (var i = 0; i < this.entityRelationShips.length; i++) {
                    if (e1 == this.entityRelationShips[i].e1 || e1 == this.entityRelationShips[i].e2) {
                        notfound = false;
                        index = i;
                        break;
                    }
                }
                if(notfound == false)
                {
                    this.entityRelationShips.splice(index, 1);
                }
                else
                {
                    break;
                }
            }
        },
        isCollectionFound(collectionName){

            var found= false;

            for(var i = 0; i < this.entities.length; i++)
            {

                if(this.entities[i].collectionName == collectionName)
                {
                    found = true;
                    break;
                }
            }
            return found;

        }
    },
    beforeMount(){


        this.loadMicronautDependencies();
        this.loadTemplates();


    }
});