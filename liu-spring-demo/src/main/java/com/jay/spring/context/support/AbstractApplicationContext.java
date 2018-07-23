package com.jay.spring.context.support;

import com.jay.spring.bean.factory.support.DefaultBeanFactory;
import com.jay.spring.bean.factory.annotation.AutowiredAnnotationProcessor;
import com.jay.spring.bean.factory.config.ConfigurableBeanFactory;
import com.jay.spring.bean.factory.xml.XmlBeanDefinitionReader;
import com.jay.spring.context.ApplicationContext;
import com.jay.spring.core.io.Resource;
import com.jay.spring.util.ClassUtils;

/**
 * Created by xiang.wei on 2018/6/19
 *
 * @author xiang.wei
 */
public abstract class AbstractApplicationContext implements ApplicationContext {
    private DefaultBeanFactory factory = null;
    private ClassLoader classLoader;



    public AbstractApplicationContext(String configFile) {
        factory = new DefaultBeanFactory();
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
        Resource resource = getResourceByPath(configFile);
        reader.loadBeanDefinitions(resource);
        factory.setBeanClassLoader(this.getBeanClassLoader());
        registerBeanPostProcessors(factory);
    }

    @Override
    public Object getBean(String beanId) {
        return factory.getBean(beanId);
    }

    public abstract Resource getResourceByPath(String configFile);

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public ClassLoader getBeanClassLoader() {
        return this.classLoader != null ? this.classLoader : ClassUtils.getDefaultClassLoader();
    }

    protected void registerBeanPostProcessors(ConfigurableBeanFactory beanFactory) {
        AutowiredAnnotationProcessor postProcessor = new AutowiredAnnotationProcessor();
        postProcessor.setBeanFactory(beanFactory);
        beanFactory.addBeanPostProcessor(postProcessor);
    }
}
