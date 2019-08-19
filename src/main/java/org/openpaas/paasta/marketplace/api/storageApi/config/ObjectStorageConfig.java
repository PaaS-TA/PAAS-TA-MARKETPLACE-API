package org.openpaas.paasta.marketplace.api.storageApi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class ObjectStorageConfig {
    @Autowired
    protected Environment env;
    
    public Environment getEnvironment() {
        return this.env;
    }
}
