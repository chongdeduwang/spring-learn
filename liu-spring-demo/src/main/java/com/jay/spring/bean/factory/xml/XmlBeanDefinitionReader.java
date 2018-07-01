package com.jay.spring.bean.factory.xml;

import com.jay.spring.Exception.BeanDefinitionException;
import com.jay.spring.bean.BeanDefinition;
import com.jay.spring.bean.PropertyValue;
import com.jay.spring.bean.factory.config.RuntimeBeanReference;
import com.jay.spring.bean.factory.config.TypedStringValue;
import com.jay.spring.bean.factory.support.BeanDefinitionRegistry;
import com.jay.spring.bean.factory.support.GenericBeanDefinition;
import com.jay.spring.core.io.Resource;
import com.jay.spring.util.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.Iterator;

/**
 * Created by xiang.wei on 2018/6/18
 *
 * @author xiang.wei
 */
public class XmlBeanDefinitionReader {
    private final static Logger logger = Logger.getLogger(XmlBeanDefinitionReader.class);


    private static String ID_ATTRIBUTE = "id";
    private static String CLASS_ATTRIBUTE = "class";
    private static String SCOPE_ATTRIBUTE = "scope";
    private BeanDefinitionRegistry registry;
    public static final String PROPERTY_ELEMENT = "property";

    public static final String REF_ATTRIBUTE = "ref";

    public static final String VALUE_ATTRIBUTE = "value";

    public static final String NAME_ATTRIBUTE = "name";


    public XmlBeanDefinitionReader(BeanDefinitionRegistry registry) {
        this.registry = registry;
    }


    public void loadBeanDefinition(Resource resource) {
        InputStream inputStream = null;
        try {
            try {
                //获取文件输入流
                inputStream = resource.getInputStream();

                SAXReader saxReader = new SAXReader();
                Document document = saxReader.read(inputStream);

//            获取根节点
                Element rootElement = document.getRootElement();
                Iterator<Element> iterator = rootElement.elementIterator();
                while (iterator.hasNext()) {
                    Element element = iterator.next();
                    String id = element.attributeValue(ID_ATTRIBUTE);
                    String className = element.attributeValue(CLASS_ATTRIBUTE);
                    BeanDefinition beanDefinition = new GenericBeanDefinition(id, className);
                    if (element.attribute(SCOPE_ATTRIBUTE) != null) {
                        beanDefinition.setScope(element.attributeValue(SCOPE_ATTRIBUTE));
                    }
                    parsePropertyElement(element, beanDefinition);
                    registry.registerBeanDefinition(id, beanDefinition);
                }
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
        } catch (Exception e) {
            throw new BeanDefinitionException("读取xml出错");
        }
    }

    public void parsePropertyElement(Element beanElem, BeanDefinition bd) {
        Iterator iterator = beanElem.elementIterator(PROPERTY_ELEMENT);
        while (iterator.hasNext()) {
            Element propElem = (Element) iterator.next();
            String propertyName = propElem.attributeValue(NAME_ATTRIBUTE);
            if (!StringUtils.hasLength(propertyName)) {
                logger.fatal("Tag 'property' must have a 'name' attribute");
                return;
            }
            Object val = parsePropertyValue(propElem, bd, propertyName);
            PropertyValue pv = new PropertyValue(propertyName, val);
            bd.getPropertyValues().add(pv);
        }
    }

    public Object parsePropertyValue(Element ele, BeanDefinition bd, String propertyName) {
        String elementName = (propertyName != null) ?
                "<property> element for property '" + propertyName + "'" :
                "<constructor-arg> element";

        boolean hasRefAttribute = (ele.attribute(REF_ATTRIBUTE) != null);
        boolean hasValueAttribute = (ele.attribute(VALUE_ATTRIBUTE) != null);
        if (hasRefAttribute) {
            String refName = ele.attributeValue(REF_ATTRIBUTE);
            if (!StringUtils.hasText(refName)) {
                logger.error(elementName + " contains empty 'ref' attribute");
            }
            RuntimeBeanReference ref = new RuntimeBeanReference(refName);
            return ref;
        } else if (hasValueAttribute) {
            TypedStringValue typedStringValue = new TypedStringValue(ele.attributeValue(VALUE_ATTRIBUTE));
            return typedStringValue;

        } else {
            throw new RuntimeException(elementName + " must specify a ref or value");
        }
    }
}