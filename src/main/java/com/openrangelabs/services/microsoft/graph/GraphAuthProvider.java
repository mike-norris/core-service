package com.openrangelabs.services.microsoft.graph;

import com.microsoft.graph.auth.enums.NationalCloud;
import com.microsoft.graph.auth.publicClient.UsernamePasswordProvider;
import com.microsoft.graph.logger.DefaultLogger;
import com.microsoft.graph.logger.LoggerLevel;
import com.microsoft.graph.models.extensions.IGraphServiceClient;
import com.microsoft.graph.requests.extensions.GraphServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class GraphAuthProvider {
    private static GraphAuthProvider authenticationProvider = null;

    @Value("${microsoft.clientId}")
    String clientId;

    @Value("${microsoft.scope}")
    String scopes;

    @Value("${microsoft.userName}")
    String userName;

    @Value("${microsoft.userPassword}")
    String userPassword;

    @Value("${microsoft.tenantId}")
    String tenantId;

    @Value("${microsoft.clientSecret}")
    String clientSecret;

    public static GraphAuthProvider getInstance(){
        if (authenticationProvider == null) {
            authenticationProvider = new GraphAuthProvider();
        }
        return authenticationProvider;
    }

    public IGraphServiceClient getAuthProvider() throws IOException {
        final List<String> appScopes = Arrays.asList(scopes.split(","));
        final UsernamePasswordProvider authProvider = new UsernamePasswordProvider(clientId,appScopes,
                userName,
                userPassword,
                NationalCloud.Global,
                tenantId,
                clientSecret
        );

        // Create default logger to only log errors
        DefaultLogger logger = new DefaultLogger();
        logger.setLoggingLevel(LoggerLevel.ERROR);

        IGraphServiceClient graphClient = GraphServiceClient.builder()
                .authenticationProvider(authProvider)
                .logger(logger)
                .buildClient();

        return graphClient;
    }

}
