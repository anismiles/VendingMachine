package org.animesh.vmachine.utils;

import java.net.URL;
import java.util.Map.Entry;
import java.util.Properties;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Binder;
import com.google.inject.ProvisionException;
import com.google.inject.name.Names;

// TODO: Auto-generated Javadoc
/**
 * Utility class to load properties from a file in classpath, and/or from System, and also to bind them to
 * Guice binder.
 * 
 * 
 * @author "Animesh Kumar <animesh@strumsoft.com>"
 * 
 */
@Singleton
public class PropertyLoader {
    // log
    /** The Constant LOG. */
    private static final Logger LOG = LoggerFactory.getLogger(PropertyLoader.class);

    /**
     * Load properties.
     *
     * @param binder the binder
     * @param configs the configs
     * @return the properties
     */
    public static Properties loadProperties(Binder binder, String... configs) {
        try {
            Properties props = new Properties();
            for (String config : configs) {
                Properties p = loadProperties(config);
                Names.bindProperties(binder, p);
                props.putAll(p);
            }
            return props;
        } catch (Exception e) {
            throw new ProvisionException("Failed to load property from file", e);
        }
    }

    /**
     * Load properties.
     *
     * @param classpathConfig the classpath config
     * @return the properties
     * @throws Exception the exception
     */
    public static Properties loadProperties(String classpathConfig) throws Exception {
        LOG.info("loadProperties from classpathConfig={}", classpathConfig);

        Properties props = new Properties();

        // Load from class-path
        ClassLoader loader = PropertyLoader.class.getClassLoader();
        URL url = loader.getResource(classpathConfig);
        LOG.info("url={}", url.getPath());
        props.load(url.openStream());

        // Override explicitly provided properties
        for (Entry<Object, Object> entry : props.entrySet()) {
            String key = (String) entry.getKey();
            String value = System.getProperty(key, (String) entry.getValue());
            props.put(key, value.trim()); // trim values to avoid accidental whitespaces

            // Update System
            System.setProperty(key, value);
        }

        return props;
    }

}
