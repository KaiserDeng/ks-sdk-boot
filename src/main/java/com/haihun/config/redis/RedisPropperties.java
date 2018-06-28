package com.haihun.config.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author kaiser·von·d
 * @version 2018/4/25
 */
@ConfigurationProperties("custom.redis")
@Component
public class RedisPropperties {

    private Map<String, Object> configuration;

    private Map<String, Object> nodes;


    public Map<String, Object> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Map<String, Object> configuration) {
        this.configuration = configuration;
    }

    public Map<String, Object> getNodes() {
        return nodes;
    }

    public void setNodes(Map<String, Object> nodes) {
        this.nodes = nodes;
    }
}
