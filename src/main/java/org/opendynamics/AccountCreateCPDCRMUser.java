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
public class AccountCreateCPDCRMUser extends WorkflowFormBinder{
    @Override
    public String getVersion() {
        return ("1.0.0");
    }

    @Override
    public String getDescription() {
        return ("Create CPD CRM User Joget Account");
    }

    @Override
    public String getLabel() {
        return ("Create CPD CRM User Joget Account");
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }
    
    @Override
    public String getName() {
        return ("Create CPD CRM User Joget Account");
    }
    
    @Override
    public FormRowSet store(Element element, FormRowSet rowSet, FormData formData) {
        QueryHandler qH = new QueryHandler();
        JogetAccountHandler jAH = new JogetAccountHandler();
        rowSet = super.store(element, rowSet, formData);
        
        UuidGenerator uuid = UuidGenerator.getInstance();
        String uuidString = uuid.getUuid();
        
        NotificationHandler nH = new NotificationHandler();
        
        if (rowSet != null && !rowSet.isEmpty()){
            FormRow row = rowSet.get(0);
            String id = row.getId();
            
            id = row.getProperty("id");
            String username = row.getProperty("email");
            String password = row.getProperty("password");
            String name = row.getProperty("organization");
            String group_id = "cpd_g07";
            
//            System.out.println("id: " + id);
//            System.out.println("username: " + username);
//            System.out.println("password: " + password);
//            System.out.println("name: " + name);
            
            
            if(jAH.createAccount(id, password, name, "", username, group_id)){
//                System.out.println("Account Created For User : " + name + "\n Username : " + username
//                    + "\nPassword : " + password);
                
                if (qH.updateFormColumnValue("cpd_crm_user", "application_stage", "Pending Email Verification", id)){
//                    System.out.println("Application Stage Variable value set Successful for user : " + id);
                    
                    qH.updateFormColumnValue("cpd_crm_user", "email_verification_code", uuidString, id);
                    qH.updateFormColumnValue("cpd_crm_user", "email_verified", "No", id);
                    
                    nH.sendVerificationCode(id, username, uuidString);
                }else {
//                    System.out.println("Application Stage Variable value set Unuccessful");
                }                
            }else{
//                System.out.println("Account Creation failed for user : " + username);
            }
            
            
            
        }
        return rowSet;
    }
}
