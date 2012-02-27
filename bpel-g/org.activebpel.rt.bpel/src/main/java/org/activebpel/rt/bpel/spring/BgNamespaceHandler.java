package org.activebpel.rt.bpel.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author markford
 *         Date: 2/26/12
 */
public class BgNamespaceHandler extends NamespaceHandlerSupport {
    @Override
    public void init() {
        registerBeanDefinitionParser("functionValidatorFactory", new FunctionValidatorFactoryBeanDefParser());
        registerBeanDefinitionParser("bpelExpressionLanguageFactory", new BpelExpressionLanguageFactoryBeanDefParser());
        registerBeanDefinitionParser("extensionFunctionContext", new ExtensionFunctionContextBeanDefParser());

    }
}
