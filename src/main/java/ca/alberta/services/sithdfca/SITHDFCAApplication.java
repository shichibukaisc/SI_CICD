package ca.alberta.services.sithdfca;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.ForwardedHeaderFilter;



//
//@OpenAPIDefinition(
//	info = @Info(        		
//        title = "Skeleton API",
//        version = "${application.version}",
//        description = "Basic RESTful API service skeleton used for rapid RESTful API development.  A functional JWT token to test the secured APIs is:\n"
//        		+ "<code>${example.jwt}</code>"
//        ,
//        license = @License(name = "License Name", url = "http://my.license.url.com"),
//        contact = @Contact(url = "https://www.alberta.ca", name = "Integration Services Admin", email = "Integration.Services.Admin@gov.ab.ca")
//    ),
//	
////	servers = @Server(
////		description = "Valid Server URLs", 
////		url = "https://{domain}{port}/",
////		variables = {
////			@ServerVariable(name="domain",defaultValue="localhost", allowableValues= {"localhost","devihs1.agdev.gov.ab.ca"}),
////			@ServerVariable(name="port",defaultValue=":8443", allowableValues= {":8443",""})
////		}
////	),
//	externalDocs = @ExternalDocumentation(
//		description = "Onboarding docs desc",
//		url = "http://url.to.onboarding"

//	),

//	tags= {
//		@Tag(name=Constants.TAG_1_NAME, description=Constants.TAG_1_DESCRIPTION),
//		@Tag(name=Constants.TAG_2_NAME, description=Constants.TAG_2_DESCRIPTION),
//		@Tag(name=Constants.TAG_3_NAME, description=Constants.TAG_3_DESCRIPTION)
//		
//	}
//)

//This field enables the insertion of a JWT on the Swagger documentation page
//It also allows for more fine grained documentation around which endpoints are secured.  In most cases, all endpoints will be secured with the exception of
//the healthcheck endpoint and the swagger documentation endpoint
//
//@SecurityScheme(
//	type=SecuritySchemeType.HTTP,
//	description="Enter JWT Bearer token **_only_**",
//	name="Bearer",
//	scheme="bearer",
//	bearerFormat="JWT"
//)

@SpringBootApplication
public class SITHDFCAApplication {
	

	
//	private static boolean runMe = true;
	
	public static void main(String[] args) {
		SpringApplication.run(SITHDFCAApplication.class, args);
	}
	
	@Bean
	ForwardedHeaderFilter forwardedHeaderFilter() {
	   return new ForwardedHeaderFilter();
	}

// Different ways to access Environment variables at startup
	
//	@Bean
//	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
//		return args -> {
//			log.error("In command line runner.  calling dl? " + runMe);
//			if(runMe) {
//				DataLoader dl = new DataLoader(siRepo, autoLoaderPath);
//				dl.load();
//
//				log.error("In command line runner.  Loaded Data");
//				runMe = false;
//			}
//		};
//	}
	


//    private static void printEnvironmentsProperties(PaymentApplication p) {
//        StringBuilder stringBuilder = new StringBuilder("Properties Files ......\n");
//        stringBuilder.append("env.name").append(" : ").append(p.envName).append("\n")
//        .append("server.port").append(" : ").append(p.serverPort).append("\n")
//        .append("secret").append(" : ").append(p.myMsg).append("\n");
//        System.out.println(stringBuilder.toString());
//    }
	

}
