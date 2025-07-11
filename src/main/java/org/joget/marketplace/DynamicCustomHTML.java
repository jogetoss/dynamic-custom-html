package org.joget.marketplace;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringEscapeUtils;
import org.joget.apps.form.lib.CustomHTML;
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
        return "Replaces {tokens} using form-level Load Binder data.";
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
            FormLoadBinder binder = FormUtil.findLoadBinder(this);
            if (binder == null) {
                Element root = FormUtil.findRootForm(this);
                if (root != null && root.getLoadBinder() instanceof FormLoadBinder) {
                    binder = (FormLoadBinder) root.getLoadBinder();
                } else {
                    return super.renderTemplate(formData, dataModel);
                }
            }
            FormRowSet rows = binder.load(this, formData.getPrimaryKeyValue(), formData);
            if (rows != null && !rows.isEmpty()) {
                int index = 0;
                for (FormRow rowItem : rows) {
                    for (Map.Entry<Object, Object> entry : rowItem.entrySet()) {
                        String key = entry.getKey().toString();
                        String value = (entry.getValue() != null) ? entry.getValue().toString() : "null";
                    }
                }
                FormRow row = rows.get(0);
                Matcher matcher = Pattern.compile("\\{([A-Za-z0-9_]+)}").matcher(rawHtml);
                StringBuffer result = new StringBuffer();

                while (matcher.find()) {
                    String token = matcher.group(1);
                    String value = row.getProperty(token);

                    matcher.appendReplacement(result, Matcher.quoteReplacement(StringEscapeUtils.escapeHtml(value)));
                }
                matcher.appendTail(result);

                setProperty("value", result.toString());
                String finalHtml = super.renderTemplate(formData, dataModel);
                setProperty("value", rawHtml); // restore original
                return finalHtml;
            } 
        } catch (Exception e) {
            LogUtil.error(getClassName(), e, "Exception during token replacement.");
        }

        return super.renderTemplate(formData, dataModel);
    }

}
