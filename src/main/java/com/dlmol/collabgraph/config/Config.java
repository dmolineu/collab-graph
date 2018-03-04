package com.dlmol.collabgraph.config;

import com.dlmol.collabgraph.graph.GraphBuilder;
import com.dlmol.collabgraph.repositories.CollaboratorRepository;
import com.dlmol.collabgraph.service.CollaboratorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public CollaboratorService collaboratorService(){
        return new CollaboratorService();
    }

    @Bean
    public CollaboratorRepository collaboratorRepository(){
        return new CollaboratorRepository();
    }

    @Bean
    public GraphBuilder graphBuilder(){
        return new GraphBuilder();
    }
}
