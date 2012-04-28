package bpelg.packaging.ode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceConstants;
import org.custommonkey.xmlunit.DifferenceListener;
import org.w3c.dom.Node;

public class BgDiffListener implements DifferenceListener {
    
    protected final Set<String> mIgnorePaths = new HashSet<String>();
    
    public int differenceFound(Difference aDifference)
    {
        if (aDifference.getId() == DifferenceConstants.NAMESPACE_PREFIX_ID || isIgnored(aDifference.getTestNodeDetail().getXpathLocation()))
            return DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
        return DifferenceListener.RETURN_ACCEPT_DIFFERENCE;
    }

    public void skippedComparison(Node aControl, Node aTest)
    {
    }
    
    public void setIgnorePaths(String...aPaths) {
        if (aPaths !=null)
            mIgnorePaths.addAll(Arrays.asList(aPaths));
    }
    
    protected boolean isIgnored(String aXPath) {
        return mIgnorePaths.contains(aXPath);
    }

}
