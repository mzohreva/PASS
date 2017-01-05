package pass.core.common;

import org.apache.commons.lang3.StringEscapeUtils;
import org.pegdown.PegDownProcessor;

public class Tools
{

    public static String markdownToHTML(String md)
    {
        PegDownProcessor pdp = new PegDownProcessor();
        String html = pdp.markdownToHtml(md);
        return html;
    }

    public static String escapeHtml(String input)
    {
        return StringEscapeUtils.escapeHtml4(input);
    }
}
