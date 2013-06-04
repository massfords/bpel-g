package org.activebpel.rt.bpel.spring;

import org.activebpel.rt.bpel.expr.AeExpressionBundle;
import org.activebpel.rt.bpel.expr.BpelExpressionLanguageFactory;
import org.activebpel.rt.bpel.impl.expr.IAeExpressionRunner;
import org.activebpel.rt.expr.def.IAeExpressionAnalyzer;
import org.activebpel.rt.expr.validation.IAeExpressionValidator;
import org.activebpel.rt.util.AeXPathUtil;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author markford
 *         Date: 2/26/12
 */
public class BpelExpressionLanguageFactoryBeanDefParser extends AbstractSingleBeanDefinitionParser {

    @Override
    protected Class getBeanClass(Element element) {
        return BpelExpressionLanguageFactory.class;
    }

    protected void doParse(Element element, BeanDefinitionBuilder bean) {
        
        bean.addPropertyValue("bpelDefaultLanguage", element.getAttribute("defaultLanguage"));
        Map<String,AeExpressionBundle> map = new HashMap<>();
        
        try {
            List<Element> elements = AeXPathUtil.selectNodes(element, "./bpelg:bundle", "bpelg", "urn:bpel-g:spring-ext");
            for(Element e : elements) {
                String uri = e.getAttribute("uri");
                Class validator = Class.forName(e.getAttribute("validator"));
                Class runner = Class.forName(e.getAttribute("runner"));
                Class analyzer = Class.forName(e.getAttribute("analyzer"));
                AeExpressionBundle bundle = new AeExpressionBundle();
                bundle.setValidator((IAeExpressionValidator) validator.newInstance());
                bundle.setRunner((IAeExpressionRunner) runner.newInstance());
                bundle.setAnalyzer((IAeExpressionAnalyzer) analyzer.newInstance());
                map.put(uri, bundle);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        bean.addPropertyValue("bundles", map);
    }
}
