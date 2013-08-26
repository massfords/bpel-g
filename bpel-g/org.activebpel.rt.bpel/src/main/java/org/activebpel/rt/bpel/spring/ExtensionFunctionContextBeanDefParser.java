package org.activebpel.rt.bpel.spring;

import org.activebpel.rt.bpel.function.IAeFunction;
import org.activebpel.rt.bpel.impl.function.AeExtensionFunctionContext;
import org.activebpel.rt.util.AeXPathUtil;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author markford
 *         Date: 2/27/12
 */
public class ExtensionFunctionContextBeanDefParser extends AbstractSingleBeanDefinitionParser {
    @Override
    protected Class getBeanClass(Element element) {
        return AeExtensionFunctionContext.class;
    }

    protected void doParse(Element element, BeanDefinitionBuilder bean) {

        Map<String, IAeFunction> map = new HashMap<>();

        try {
            List<Element> elements = AeXPathUtil.selectNodes(element, "./bpelg:function", "bpelg", "urn:bpel-g:spring-ext");
            for (Element e : elements) {
                Class c = Class.forName(e.getAttribute("class"));
                IAeFunction f = (IAeFunction) c.newInstance();
                map.put(e.getAttribute("name"), f);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        bean.addPropertyValue("functions", map);
    }
}
