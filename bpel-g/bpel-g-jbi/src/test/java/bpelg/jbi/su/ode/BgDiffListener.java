package bpelg.jbi.su.ode;

import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceConstants;
import org.custommonkey.xmlunit.DifferenceListener;
import org.w3c.dom.Node;

public class BgDiffListener implements DifferenceListener {
    
    public int differenceFound(Difference aDifference)
    {
        if (aDifference.getId() == DifferenceConstants.NAMESPACE_PREFIX_ID)
            return DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
        return DifferenceListener.RETURN_ACCEPT_DIFFERENCE;
    }

    public void skippedComparison(Node aControl, Node aTest)
    {
    }

}
