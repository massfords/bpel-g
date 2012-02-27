package org.activebpel.rt.bpel;

import org.activebpel.rt.bpel.expr.BpelExpressionLanguageFactory;
import org.activebpel.rt.expr.validation.functions.IAeFunctionValidator;
import org.activebpel.rt.expr.validation.functions.IAeFunctionValidatorFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import javax.xml.namespace.QName;

/**
 * @author markford
 *         Date: 2/26/12
 */
public class SpringExtFunctionValidatorsTest extends Assert {
    @Test
    public void functions() throws Exception {
        ApplicationContext context = new FileSystemXmlApplicationContext(
                "src/test/resources/functionValidators.xml");
        IAeFunctionValidatorFactory factory = context.getBean(IAeFunctionValidatorFactory.class);
        assertNotNull(factory);

        IAeFunctionValidator validator = factory.getValidator(
                new QName(
                "http://docs.oasis-open.org/wsbpel/2.0/process/executable",
                "getVariableProperty"));

        assertNotNull(validator);
    }
    @Test
    public void bpelExpressionLanguageFactory() throws Exception {
        ApplicationContext context = new FileSystemXmlApplicationContext(
                "src/test/resources/bpelExpressionLanguageFactory.xml");
        BpelExpressionLanguageFactory factory = context.getBean(BpelExpressionLanguageFactory.class);
        assertNotNull(factory);
        assertEquals("http://www.w3.org/TR/1999/REC-xpath-19991116", factory.getBpelDefaultLanguage());
    }

}
