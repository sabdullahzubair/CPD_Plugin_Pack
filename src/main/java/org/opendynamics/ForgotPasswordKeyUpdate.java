/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opendynamics;

import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.lib.WorkflowFormBinder;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.commons.util.UuidGenerator;
import org.joget.directory.dao.UserDao;
import org.joget.directory.model.User;
import org.opendynamics.helper.QueryHandler;

/**
 *
 * @author syeda
 */
public class ForgotPasswordKeyUpdate extends WorkflowFormBinder{
    @Override
    public String getName() {
        return ("Update CPDP & CPDU Password Key");
    }

    @Override
    public String getVersion() {
        return ("1.0.0");
    }

    @Override
    public String getDescription() {
        return ("Update CPDP & CPDU Password Key");
    }

    @Override
    public String getLabel() {
        return ("Update CPDP & CPDU Password Key");
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }
    
    @Override
    public FormRowSet store(Element element, FormRowSet rows, FormData formData) {
        
        QueryHandler qH = new QueryHandler();
        //check the rows is not empty before store it
        if (rows != null && !rows.isEmpty()) {
            //Get the submitted data
            FormRow row = rows.get(0);
            String id = row.getId();
            UserDao ud = (UserDao) AppUtil.getApplicationContext().getBean("userDao");
            User user = ud.getUser(id);
            if(!qH.isEmpty(id, "email", "cpd_user") && user != null){
//                System.out.println("User Does Exist!!!");
                String changePwdKey = UuidGenerator.getInstance().getUuid();
                qH.updateFormColumnValue("generic_signup", "", changePwdKey, id);
                
                
//                updatePasswordKey(id, changePwdKey);
//                emailApplicant(id,changePwdKey);
            }else {
//                System.out.println("User Does Not Exist!!!");
            }

            //Reuse Workflow Form Binder to store data
            //PluginManager pluginManager = (PluginManager) AppUtil.getApplicationContext().getBean("pluginManager");
            //FormStoreBinder binder = (FormStoreBinder) pluginManager.getPlugin("org.joget.apps.form.lib.WorkflowFormBinder");
            //binder.store(element, rows, formData);
        }

        return rows;
    }
}
