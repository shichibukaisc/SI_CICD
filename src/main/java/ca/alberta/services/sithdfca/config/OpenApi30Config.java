package ca.alberta.services.sithdfca.config;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ca.alberta.services.sithdfca.Constants;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.servers.ServerVariable;
import io.swagger.v3.oas.models.servers.ServerVariables;
import io.swagger.v3.oas.models.tags.Tag;


@Configuration
public class OpenApi30Config {
	
	@Value("${application.swagger.info.title}")
	private String apiTitle;
	@Value("${application.version}")
	private String apiVersion;
	@Value("${application.swagger.info.description}")
	private String apiDescription;
	private String securitySchemeName = Constants.SECURITY_SCHEME_NAME;
	@Value("${application.swagger.contact.name}")
	private String contactName;
	@Value("${application.swagger.contact.url}")
	private String contactUrl;
	@Value("${application.swagger.contact.email}")
	private String contactEmail;
	@Value("${application.swagger.license.name}")
	private String licenseName;
	@Value("${application.swagger.license.url}")
	private String licenseUrl;
	@Value("${application.swagger.termsOfService}")
	private String termsOfService;
	@Value("${application.swagger.externalDocumentation.description}")
	private String externalDocDesc;
	@Value("${application.swagger.externalDocumentation.url}")
	private String externalDocUrl;
	
	@Value("${oidc.well-known.uri}")
	private String oidcWellKnownUrl;
	@Value("${oidc.token.uri}")
	private String oidcTokenUrl;
	@Value("${application.swagger.servers}")
	private String serverListString;

	public OpenApi30Config() {
	}
	
	/**
	 * The structure of this class and its elements follow the document structure for OpenAPI3.0
	 * For example, The OpenAPI object model (https://swagger.io/specification/#openapi-object) has a Component field that contains Security Schemes. 
	 * We would create a Component Object and add a Security Scheme to it 
	 * @return
	 */

	@Bean
	public OpenAPI customOpenAPI() {
		final String apiTitleFormatted = String.format("%s API", StringUtils.capitalize(apiTitle));
		final List<String> serverList = Arrays.asList(serverListString.split(","));
		return new OpenAPI()
			//
			// If you want to have security added to every endpoint in the swagger documentation, uncomment the line below
			// NOTE** This does not secure your applicaiton!  This only decorates the endpoint in swagger as requiring authorization
			// .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
			//
			.components(
				new Components().addSecuritySchemes(securitySchemeName,
					new SecurityScheme()
						.name(securitySchemeName)
						.type(SecurityScheme.Type.HTTP)
						.scheme("bearer")
						.bearerFormat("JWT")
						.description("Enter JWT Bearer token **_only_**")
//						.type(SecurityScheme.Type.OPENIDCONNECT)
//						.openIdConnectUrl(oidcWellKnownUrl)
//						.flows(new OAuthFlows().clientCredentials(new OAuthFlow().tokenUrl(oidcTokenUrl)))
				)
			)
			.info(
				new Info()
					.title(apiTitleFormatted)
					.version(apiVersion)
					.description(apiDescription)
					.contact(new Contact().name(contactName).url(contactUrl).email(contactEmail))
					.license(new License().name(licenseName).url(licenseUrl))
					.termsOfService(termsOfService)
			)
			//
			// Creating Tags will allow you to group specific endpoints by Tag Name.
			// By default, tags are grouped by controller but this approach allows you more flexibility over how endpoints are grouped.
			//
			.addTagsItem(new Tag().name(Constants.TAG_1_NAME).description(Constants.TAG_1_DESCRIPTION))
			.addTagsItem(new Tag().name(Constants.TAG_2_NAME).description(Constants.TAG_2_DESCRIPTION))
			.addTagsItem(new Tag().name(Constants.TAG_3_NAME).description(Constants.TAG_3_DESCRIPTION))
			.externalDocs(new ExternalDocumentation().description(externalDocDesc).url(externalDocUrl))
			//.servers(Arrays.asList(new Server().description("My Server Description").url( appContext.getApplicationName()+ "/relative/path")))
			.servers(Arrays.asList(
				new Server()
				//.description("My Server Description")
				.url("{server}")
				.variables(new ServerVariables()
					.addServerVariable("server",
						new ServerVariable()
						._default(serverList.get(0))
						._enum(serverList)
					)
				)
			))
			;
		
	}

}
