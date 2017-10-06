/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opendynamics;

import org.joget.apps.form.lib.WorkflowFormBinder;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.opendynamics.helper.JogetAccountHandler;

/**
 *
 * @author syeda
 */
public class AccountVerifyCPDCRMUser extends WorkflowFormBinder{
    @Override
    public String getVersion() {
        return ("1.0.0");
    }

    @Override
    public String getDescription() {
        return ("Verify CPD CRM User Profile");
    }

    @Override
    public String getLabel() {
        return ("Verify CPD CRM User Profile");
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }
    
    @Override
    public String getName() {
        return ("Verify CPD CRM User Profile");
    }
    
    @Override
    public FormRowSet store(Element element, FormRowSet rowSet, FormData formData) {
        rowSet = super.store(element, rowSet, formData);
        JogetAccountHandler jAH = new JogetAccountHandler();
        if (rowSet != null && !rowSet.isEmpty()){
            FormRow row = rowSet.get(0);
            String id = row.getId();
            jAH.updateUserGroup(id, "cpd_g08");
        }
        return rowSet;
    }
}
