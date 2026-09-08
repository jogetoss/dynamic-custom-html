package org.joget.marketplace;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringEscapeUtils;
import org.joget.apps.form.lib.CustomHTML;
import org.joget.apps.form.lib.TextField;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.service.FormUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.StringUtil;

public class DynamicCustomHTML extends CustomHTML {

    static final Pattern TOKEN_PATTERN = Pattern.compile("\\{([A-Za-z0-9_]+)(\\?[^{}]+)?}");

    @Override
    public String getName() {
        return "Dynamic Custom HTML";
    }

    @Override
    public String getLabel() {
        return "Dynamic Custom HTML";
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }

    @Override
    public String getDescription() {
        return "Replaces {tokens} using form data. Supports optional escape formats, e.g. {fieldId?nl2br}, {fieldId?html}, {fieldId?nl2br;html} — see Joget's Hash Variable escaping formats for the full list.";
    }

    @Override
    public String getVersion() {
        return "8.0.0";
    }

    @Override
    public String renderTemplate(FormData formData, Map dataModel) {
        try {
            String rawHtml = (String) getProperty("value");
            if (rawHtml == null || rawHtml.isEmpty()) {
                return super.renderTemplate(formData, dataModel);
            }
            Matcher matcher = TOKEN_PATTERN.matcher(rawHtml);
            StringBuffer result = new StringBuffer();

            while (matcher.find()) {
                String token = matcher.group(1);
                String format = matcher.group(2);

                Element dummyField = new TextField();
                dummyField.setProperty("id", token);
                dummyField.setParent(this);

                String value = FormUtil.getElementPropertyValue(dummyField, formData);

                if (format != null && value != null) {
                    value = StringUtil.escapeString(value, format.substring(1));
                }

                matcher.appendReplacement(result, Matcher.quoteReplacement(value));
            }
            matcher.appendTail(result);

            setProperty("value", result.toString());
            String finalHtml = super.renderTemplate(formData, dataModel);
            setProperty("value", rawHtml); // Restore original HTML
            return finalHtml;

        } catch (Exception e) {
            LogUtil.error(getClassName(), e, "Exception during token replacement.");
        }

        return super.renderTemplate(formData, dataModel);
    }

}