package org.activebpel.rt.bpel.def.validation;

import org.activebpel.rt.bpel.def.AeBaseContainer;

import java.util.List;

/**
 * @author ma21633
 */
public class AeContainerValidatorUtil {
    public static void checkForDupes(IAeValidationProblemReporter reporter, String resourceName, AeBaseContainer<String,?> def) {
        List<String> dupes = def.consumeDupes();
        if (!dupes.isEmpty()) {
            for(String dupe : dupes) {
                String[] args = {resourceName, dupe};
                reporter.reportProblem(IAeValidationProblemCodes.DUPLICATE_NAME, IAeValidationDefs.ERROR_DUPE_NAME, args, def.getParent());
            }
        }
    }

}
