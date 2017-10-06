/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opendynamics;

import org.opendynamics.helper.JogetAccountHandler;
import org.joget.apps.form.lib.WorkflowFormBinder;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.commons.util.SecurityUtil;
import org.opendynamics.helper.QueryHandler;

/**
 *
 * @author syeda
 */
public class AccountCreateCPDProvider extends WorkflowFormBinder{
    @Override
    public String getVersion() {
        return ("1.0.0");
    }

    @Override
    public String getDescription() {
        return ("Create CPD Provider Joget Account");
    }

    @Override
    public String getLabel() {
        return ("Create CPD Provider Joget Account");
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }
    
    @Override
    public String getName() {
        return ("Create CPD Provider Joget Account");
    }
    
    @Override
    public FormRowSet store(Element element, FormRowSet rowSet, FormData formData) {
        QueryHandler qH = new QueryHandler();
        JogetAccountHandler jAH = new JogetAccountHandler();
        rowSet = super.store(element, rowSet, formData);
        if (rowSet != null && !rowSet.isEmpty()){
            FormRow row = rowSet.get(0);
            String id = row.getId();
            
            String username = row.getProperty("username");
            String password = row.getProperty("password");
            
//            System.out.println("->Username: " + username);
//            System.out.println("-->password: " + password);
//            System.out.println("--->password: " + SecurityUtil.decrypt(password));
            
            String name = row.getProperty("organization_name");
            String group_id = "cpd_g00";
            if(jAH.createAccount(username, password, name, "", name, group_id)){
//                System.out.println("Account Created For User : " + name + "\n Username : " + username
//                    + "\nPassword : " + password);
                if(qH.updateFormId("cpd_provider", id, username)){
//                    System.out.println("ID Update of the Cpd Provider From " + id + " to " + username);
                    if (qH.updateFormColumnValue("cpd_provider", "application_stage", "Incomplete Profile", username)){
//                        System.out.println("Application Stage Variable value set Successful for user : " + username);
                    }//else System.out.println("Application Stage Variable value set Unuccessful");
                }else {
//                    System.out.println("ID Update of the Cpd Provider Failed");
                }
            }else{
//                System.out.println("Account Creation failed for user : " + username);
            }
            
            
            
        }
        return rowSet;
    }
}
