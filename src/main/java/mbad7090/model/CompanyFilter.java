package mbad7090.model;

import org.apache.commons.lang.StringUtils;

/**
 * Created by Rajah on 9/25/2015.
 */
public class CompanyFilter {
    // Following stems must be all uppercase.
    static final private String _OURSTEM1 = "3M INNOVATIVE"; // allows for 3M Innovative as a stem
    static final private String _OURSTEM2 = "3M INNOVATION"; // allows for 3M Innovation
    static final private String _OURSTEM3 = "3M INNOVATVE";  // an unfortunate misspelling that exists in the data.

    static private String[] matchingCompanies = { _OURSTEM1, _OURSTEM2 , _OURSTEM3};
    /**
     * Determine whether this company is one of the target companies.
     * @param companyToTest     String of company to test, like "HONEYWELL INTERNATIONAL INC."
     * @return                  true if companyToTest, after being capitalized, is an exact match for the static array.
     */
    static public boolean isTarget(final String companyToTest) {
        if (StringUtils.isBlank(companyToTest)) { return false; }
        String allCaps = StringUtils.upperCase(companyToTest).trim();

        return StringUtils.startsWithAny(allCaps, matchingCompanies);
    }
}
