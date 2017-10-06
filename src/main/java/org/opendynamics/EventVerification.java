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
import org.opendynamics.helper.CustomHandler;
import org.opendynamics.helper.NotificationHandler;
import org.opendynamics.helper.QueryHandler;

/**
 *
 * @author syeda
 */
public class EventVerification extends WorkflowFormBinder{
    @Override
    public String getVersion() {
        return ("1.0.0");
    }

    @Override
    public String getDescription() {
        return ("Approve CPD Event");
    }

    @Override
    public String getLabel() {
        return ("Approve CPD Event");
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }
    
    @Override
    public String getName() {
        return ("Approve CPD Event");
    }
    
    @Override
    public FormRowSet store(Element element, FormRowSet rowSet, FormData formData) {
        rowSet = super.store(element, rowSet, formData);
        QueryHandler qH = new QueryHandler();
        NotificationHandler nH = new NotificationHandler();
        CustomHandler cH = new CustomHandler();
        
        if (rowSet != null && !rowSet.isEmpty()){
            FormRow row = rowSet.get(0);
            String id = row.getId();
            String notificationTitle = row.getProperty("notification_title");
            String notificationText = row.getProperty("notification_text");
            String isNotification = row.getProperty("is_notification");
            String[] param1 = {"organizer_id"};
            String organizer_id = qH.selectRowUsingId(id, param1, "cpd_event")[0];
            
            String[] param2 = {"email"};
            String organizer_email = qH.selectRowUsingId(organizer_id, param2, "cpd_provider")[0];
            
            nH.sendEventApproved(organizer_email);
            
//            System.out.println("isNotification : " + isNotification);
            
            if("Yes".equals(isNotification)){
                cH.sendAdroidEventNotification(id, notificationTitle, notificationText);
            }
            
        }
        return rowSet;
    }
}
