package mbad7090.model;

import org.apache.commons.lang.StringUtils;

/**
 * Created by Rajah on 9/25/2015.
 */
public class CompanyFilter {
    // Following stems must be all uppercase.
    // settled on this regexp for all the companies to retrieve:
    // '3M |Siemens|SIEMENS|General Electric|GENERAL ELECTRIC|Johnson\&|JOHNSON\&|^Dow |^DOW '
    // Adding Honeywell 7Nov15.

    static final private String _OURSTEM = "3M "; // allows for 3M Innovative or 3M Innovation as a stem
    static final private String _SIEMENS_STEM = "SIEMENS";
    static final private String _GE_STEM = "GENERAL ELECTRIC";
    static final private String _J_N_J_STEM = "JOHNSON&JOHNSON";
    static final private String _DOW_STEM = "DOW ";
    static final private String _HONEYWELL_STEM = "HONEYWELL";

    static private String[] matchingCompanies = { _OURSTEM, _SIEMENS_STEM, _GE_STEM, _J_N_J_STEM, _DOW_STEM, _HONEYWELL_STEM};

    static private String[] ourCompanies = {_OURSTEM};
    /**
     * Determine whether this company is one of the target companies.
     * @param companyToTest     String of company to test, like "HONEYWELL INTERNATIONAL INC."
     * @return                  true if companyToTest, after being capitalized, is an exact match for the static array.
     */
    static private boolean isTarget(final String companyToTest, String[] companiesToMatch) {
        if (StringUtils.isBlank(companyToTest)) { return false; }
        String allCaps = StringUtils.upperCase(companyToTest).trim();

        return StringUtils.startsWithAny(allCaps, companiesToMatch);
    }

    static public boolean isTarget(final String companyToTest) {
        return isTarget(companyToTest, matchingCompanies);
    }

    public static boolean is3M(final String companyToTest) {
        return isTarget(companyToTest, ourCompanies);
    }
}
