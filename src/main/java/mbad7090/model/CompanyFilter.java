package mbad7090.model;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;

/**
 * Created by Rajah on 9/25/2015.
 */
public class CompanyFilter {
    // Following stems must be all uppercase.
    static final private String _OURSTEM =   "3M INNOVAT"; // allows for 3M Innovative or 3M Innovation
    static final private String _THEIRSTEM = "HONEYWELL"; // allows for subsidiaries, such as HONEYWELL FEDERAL MANUFACTURING TECHNOLOGIES, LLC

    static private String[] matchingCompanies = { _OURSTEM, _THEIRSTEM };
    /**
     * Determine whether this company is one of the target companies.
     * @param companyToTest     String of company to test, like "HONEYWELL INTERNATIONAL INC."
     * @return                  true if companyToTest, after being capitalized, is an exact match for the static array.
     */
    static public boolean isTarget(final String companyToTest) {
        if (StringUtils.isBlank(companyToTest)) { return false; }
        String allCaps = StringUtils.upperCase(companyToTest).trim();

        return StringUtils.startsWithAny(companyToTest, matchingCompanies);
    }
}
