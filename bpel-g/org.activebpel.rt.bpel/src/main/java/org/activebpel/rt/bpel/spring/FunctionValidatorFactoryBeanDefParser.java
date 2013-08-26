package org.activebpel.rt.bpel.spring;

import org.activebpel.rt.bpel.def.validation.expr.functions.AeFunctionValidatorFactory;
import org.activebpel.rt.expr.validation.functions.IAeFunctionValidator;
import org.activebpel.rt.util.AeXPathUtil;
import org.activebpel.rt.util.AeXmlUtil;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author markford
 *         Date: 2/26/12
 */
public class FunctionValidatorFactoryBeanDefParser extends AbstractSingleBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return AeFunctionValidatorFactory.class;
    }

    protected void doParse(Element element, BeanDefinitionBuilder bean) {

        Map<QName, IAeFunctionValidator> map = new HashMap<>();

        try {
            List<Element> elements = AeXPathUtil.selectNodes(element, "./bpelg:validator", "bpelg", "urn:bpel-g:spring-ext");
            for (Element e : elements) {
                Class c = Class.forName(e.getAttribute("class"));
                IAeFunctionValidator v = (IAeFunctionValidator) c.newInstance();
                map.put(AeXmlUtil.getAttributeQName(e, "name"), v);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        bean.addPropertyValue("functionValidators", map);
    }
}
