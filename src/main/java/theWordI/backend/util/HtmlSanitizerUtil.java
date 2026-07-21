package theWordI.backend.util;

import org.owasp.html.CssSchema;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

public class HtmlSanitizerUtil {

    private static final PolicyFactory POLICY = Sanitizers.FORMATTING
            .and(Sanitizers.BLOCKS)
            .and(Sanitizers.LINKS)
            .and(Sanitizers.IMAGES)
            .and(Sanitizers.TABLES)
            .and(new HtmlPolicyBuilder()
                    .allowElements("mark", "span", "u", "s", "sub", "sup")
                    .allowAttributes("style").onElements("mark", "span")
                    .allowAttributes("data-verse-id").onElements("span") // 성경 구절 삽입용 커스텀 속성
                    .allowStyling(CssSchema.DEFAULT)
                    .toFactory());

    public static String sanitize(String unsafeHtml)
    {
        if (unsafeHtml == null) return "";
        return POLICY.sanitize(unsafeHtml);
    }
}
