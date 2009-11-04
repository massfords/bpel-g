package bpelg.jbi.util;

import javax.jbi.messaging.MessageExchange;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;

import org.w3c.dom.Document;

import bpelg.jbi.BgMessageExchangePattern;

/**
 * Some utils for converting stuff to and from JBI objects
 * 
 * @author markford
 */
public class BgJbiUtil {
    public static Document getData(MessageExchange aMex, String aMessageName) throws Exception {
        Source source = aMex.getMessage(aMessageName).getContent();
        return toDocument(source);
    }

    public static boolean isOneWay(MessageExchange aMex) {
        return aMex.getPattern().equals(BgMessageExchangePattern.IN_ONLY);
    }
    
    public static boolean isTwoWay(MessageExchange aMex) {
        return aMex.getPattern().equals(BgMessageExchangePattern.IN_OUT);
    }

    public static Document toDocument(Source aSource) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        DOMResult result = new DOMResult();
        t.transform(aSource, result);
        return (Document) result.getNode();
    }

}
