package org.joget.marketplace;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringEscapeUtils;
import org.joget.apps.form.lib.CustomHTML;
import org.joget.apps.form.lib.TextField;
import org.joget.apps.form.model.*;
import org.joget.apps.form.service.FormUtil;
import org.joget.commons.util.LogUtil;

public class DynamicCustomHTML extends CustomHTML {

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
        return "Replaces {tokens} using form data.";
    }

    @Override
    public String getVersion() {
        return "8.0.1";
    }

    @Override
    public String renderTemplate(FormData formData, Map dataModel) {
        try {
            String rawHtml = (String) getProperty("value");
            if (rawHtml == null || rawHtml.isEmpty()) {
                return super.renderTemplate(formData, dataModel);
            }
            Matcher matcher = Pattern.compile("\\{([A-Za-z0-9_]+)}").matcher(rawHtml);
            StringBuffer result = new StringBuffer();

            while (matcher.find()) {
                String token = matcher.group(1);

                Element dummyField = new TextField();
                dummyField.setProperty("id", token);
                dummyField.setParent(this);

                String value = FormUtil.getElementPropertyValue(dummyField, formData);
                value = (value != null) ? StringEscapeUtils.escapeHtml(value) : "";

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