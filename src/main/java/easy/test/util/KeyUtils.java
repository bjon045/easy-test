package easy.test.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.Keys;

public class KeyUtils {

    private static Pattern compiledRegex = Pattern.compile("\\[\\w*\\]");

    public static String getKeySequence(String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        Matcher regexMatcher = compiledRegex.matcher(value);
        while (regexMatcher.find()) {
            String keyToLookFor = StringUtils.strip(regexMatcher.group(), "[]");
            Keys match = null;
            try {
                match = Keys.valueOf(keyToLookFor);
            } catch (IllegalArgumentException e) {
                // do nothing
                continue;
            }
            regexMatcher.appendReplacement(sb, match.toString());

        }
        regexMatcher.appendTail(sb);
        return sb.toString();

    }

}
