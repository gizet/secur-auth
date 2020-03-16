package ro.secur.auth.configuration.documentation;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.collect.Multimap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import springfox.documentation.builders.ApiListingBuilder;
import springfox.documentation.builders.OperationBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.ApiListing;
import springfox.documentation.service.Operation;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator;
import springfox.documentation.spring.web.scanners.ApiDescriptionReader;
import springfox.documentation.spring.web.scanners.ApiListingScanner;
import springfox.documentation.spring.web.scanners.ApiListingScanningContext;
import springfox.documentation.spring.web.scanners.ApiModelReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class AuthDocumentation extends ApiListingScanner {

    @Autowired
    private TypeResolver typeResolver;

    @Autowired
    public AuthDocumentation(ApiDescriptionReader apiDescriptionReader, ApiModelReader apiModelReader, DocumentationPluginsManager pluginsManager) {
        super(apiDescriptionReader, apiModelReader, pluginsManager);
    }

    @Override
    public Multimap<String, ApiListing> scan(ApiListingScanningContext context) {

        final Multimap<String, ApiListing> def = super.scan(context);

        final List<ApiDescription> apis = new LinkedList<>();

        final List<Operation> operations = new ArrayList<>();
        operations.add(new OperationBuilder(new CachingOperationNameGenerator())
                .tags(Set.of("Authentification"))
                .position(0)
                .method(HttpMethod.POST)
                .uniqueId("login")
                .parameters(Arrays.asList(new ParameterBuilder()
                                .name("username")
                                .required(true)
                                .description("username")
                                .parameterType("body")
                                .type(typeResolver.resolve(String.class))
                                .modelRef(new ModelRef("string"))
                                .build(),
                        new ParameterBuilder()
                                .name("password")
                                .description("password")
                                .parameterType("body")
                                .required(true)
                                .type(typeResolver.resolve(String.class))
                                .modelRef(new ModelRef("string"))
                                .build()))
                .summary("Log in") //
                .notes("Login endpoint")
                .build());
        apis.add(new ApiDescription(null, "/api/v1/login", "Authentication documentation", operations, false));

        def.put("asd", new ApiListingBuilder(context.getDocumentationContext().getApiDescriptionOrdering())
                .apis(apis)
                .description("Custom authentication")
                .build());

        return def;
    }
}