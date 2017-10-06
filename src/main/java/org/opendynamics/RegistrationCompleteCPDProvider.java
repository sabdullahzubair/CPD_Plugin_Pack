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
import org.joget.commons.util.UuidGenerator;
import org.opendynamics.helper.JogetAccountHandler;
import org.opendynamics.helper.NotificationHandler;
import org.opendynamics.helper.QueryHandler;

/**
 *
 * @author syeda
 */
public class RegistrationCompleteCPDProvider extends WorkflowFormBinder{
    @Override
    public String getVersion() {
        return ("1.0.0");
    }

    @Override
    public String getDescription() {
        return ("Complete CPD Provider Profile");
    }

    @Override
    public String getLabel() {
        return ("Complete CPD Provider Profile");
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }
    
    @Override
    public String getName() {
        return ("Complete CPD Provider Profile");
    }
    
    @Override
    public FormRowSet store(Element element, FormRowSet rowSet, FormData formData) {
        QueryHandler qH = new QueryHandler();
        JogetAccountHandler jAH = new JogetAccountHandler();
        NotificationHandler nH = new NotificationHandler();
        
        
        rowSet = super.store(element, rowSet, formData);
        UuidGenerator uuid = UuidGenerator.getInstance();
        String uuidString = uuid.getUuid();
        
        if (rowSet != null && !rowSet.isEmpty()){
            FormRow row = rowSet.get(0);
            String id = row.getId();
            String emailAddress = row.getProperty("email");
            
//            System.out.println(uuidString);
//            System.out.println(emailAddress);
            
            qH.updateFormColumnValue("cpd_provider", "application_stage", "Pending Email Verification", id);
            qH.updateFormColumnValue("cpd_provider", "email_verification_code", uuidString, id);
            qH.updateFormColumnValue("cpd_provider", "email_verified", "No", id);
            nH.sendVerificationCode(id, emailAddress, uuidString);
            jAH.updateUserGroup(id, "cpd_g01");
        }
        return rowSet;
    }
}
