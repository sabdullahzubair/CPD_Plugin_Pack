/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opendynamics;

import java.util.Map;
import java.util.Set;
import org.joget.apps.form.lib.WorkflowFormBinder;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;

/**
 *
 * @author syeda
 */
public class RegisterForEventCPDU extends WorkflowFormBinder {
    @Override
    public String getName() {
        return "CPDU Registration For Event";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getDescription() {
        return "CPDU Registration For Event";
    }

    @Override
    public String getLabel() {
        return "CPDU Registration For Event";
    }

    @Override
    public String getPropertyOptions() {
        return "";
    }
    
    @Override
    public FormRowSet load(Element element, String key, FormData formData) {

        FormRowSet rows = super.load(element, key, formData);
        if (rows != null) {
//            System.out.println("No Of rows" + rows.size());
            
            Map<String, String[]> map = formData.getRequestParams();
            Set<String> keSet = map.keySet();
            for (String str : keSet) {
//                System.out.println("1---> Key Set : " + str);
//                System.out.println("  2--> : Values " + map.get(str).getClass().toString());
                for (String str1 : map.get(str)) {
//                    System.out.println("    3---> Val : " + str1);
                }
            }
            
            FormRow row = rows.get(0);
            
//            System.out.println("IC of User : " + row.getProperty("ic_number"));
            
        }
        return rows;
    }
}
