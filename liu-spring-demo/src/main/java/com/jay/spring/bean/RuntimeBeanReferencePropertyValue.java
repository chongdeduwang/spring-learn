package com.jay.spring.bean;

import com.jay.spring.bean.factory.BeanFactory;

/**
 * Created by xiang.wei on 2018/7/8
 *
 * @author xiang.wei
 */
public class RuntimeBeanReferencePropertyValue extends PropertyValue {

    public RuntimeBeanReferencePropertyValue(String name,String value) {
        this.name = name;
        this.value = value;
    }


    @Override
    public Object resolve(BeanFactory factory) {
        return factory.getBean(name);
    }

}
