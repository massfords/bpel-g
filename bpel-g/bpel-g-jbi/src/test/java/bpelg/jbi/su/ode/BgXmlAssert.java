package bpelg.jbi.su.ode;

import org.activebpel.rt.xml.AeXMLParserBase;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Document;

public class BgXmlAssert {
    public static void assertXml(Document aExpected, Document aActual) {
        
        Diff diff = XMLUnit.compareXML(aExpected, aActual);
        DetailedDiff detailedDiff = new DetailedDiff(diff);
        detailedDiff.overrideDifferenceListener(new BgDiffListener());
        
        if (!detailedDiff.identical()) {
            // for debugging, dump the text
            System.out.println(AeXMLParserBase.documentToString(aActual, true));
        }
        
        XMLAssert.assertXMLIdentical(detailedDiff, true);
        
    }
}
