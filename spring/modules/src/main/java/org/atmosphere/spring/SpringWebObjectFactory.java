package org.atmosphere.spring;

import org.atmosphere.cpr.AtmosphereConfig;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereObjectFactory;
import org.atmosphere.inject.AtmosphereProducers;
import org.atmosphere.inject.InjectableObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * An {@link AtmosphereObjectFactory} for Spring to be used in Servlet Base Java Apps.
 *
 * @author Aparup Banerjee
 */
public class SpringWebObjectFactory implements AtmosphereObjectFactory {

    private static final Logger logger = LoggerFactory.getLogger(SpringObjectFactory.class);
    private AtmosphereConfig config;

    @Override
    public <T, U extends T> U newClassInstance(Class<T> classType,
                                               Class<U> classToInstantiate)
            throws InstantiationException, IllegalAccessException {

        WebApplicationContext parent = WebApplicationContextUtils.getWebApplicationContext(config.framework().getServletContext());
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.setParent(parent);
        context.register(classToInstantiate);
        context.refresh();
        U t = context.getBean(classToInstantiate);

        if (t == null) {
            logger.info("Unable to find {}. Creating the object directly."
                    + classToInstantiate.getName());
            return classToInstantiate.newInstance();
        }
        return t;
    }

    public String toString() {
        return "Spring Web ObjectFactory";
    }

    @Override
    public void configure(AtmosphereConfig config) {
        this.config = config;

        try {
            AtmosphereProducers p = newClassInstance(AtmosphereProducers.class,AtmosphereProducers.class);
            p.configure(config);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

}
