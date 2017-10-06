/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opendynamics;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.lib.WorkflowFormBinder;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.apps.form.model.FormStoreBinder;
import org.joget.commons.util.UuidGenerator;
import org.joget.plugin.base.PluginManager;
import org.opendynamics.helper.JogetAccountHandler;
import org.opendynamics.helper.NotificationHandler;
import org.opendynamics.helper.QueryHandler;

/**
 *
 * @author syeda
 */
public class MarkAttendanceCPDProvider extends WorkflowFormBinder{
    @Override
    public String getVersion() {
        return ("1.0.0");
    }

    @Override
    public String getDescription() {
        return ("Mark Attendance of CPD Event");
    }

    @Override
    public String getLabel() {
        return ("Mark Attendance of CPD Event");
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }
    
    @Override
    public String getName() {
        return ("Mark Attendance of CPD Event");
    }
    
    @Override
    public FormRowSet store(Element element, FormRowSet rowSet, FormData formData) {
        QueryHandler qH = new QueryHandler();
        JogetAccountHandler jAH = new JogetAccountHandler();
        NotificationHandler nH = new NotificationHandler();
        
        
        FormRow row = rowSet.get(0);
        
        
        String[] selectColumns = {"attendance_date","attendance_time"};
        String[] filterValues = {row.getProperty("ic_number"),row.getProperty("event_id"),row.getProperty("attendance_type") };
        String[] filterColumns = {"ic_number", "event_id", "attendance_type"};
        String condition = "And";
        List<String []> attendance = qH.selectColumns(selectColumns, "cpd_event_attendance", filterColumns, filterValues, condition);
        try{
            if(attendance.size() >= 1 ){
//                System.out.println("Duplicate Attendance For IC : " + row.getProperty("ic_number") + " For Event ID : " + row.getProperty("event_id"));
            }else{
                rowSet = super.store(element, rowSet, formData);
                if (rowSet != null && !rowSet.isEmpty()){
                    row = rowSet.get(0);
                    String id = row.getId();
                    
                    String[] column = {"sub_category_points", "category_max_points"};
                    String[] columnValues =  qH.selectRowUsingId(row.getProperty("event_subcat_id"), column, "cpd_event_subcat");
                    
                    qH.updateFormColumnValue("cpd_event_attendance", "gained_points", columnValues[0], id);
                    qH.updateFormColumnValue("cpd_event_attendance", "cat_max_points", columnValues[1], id);
                }
            }
        }
        catch(Exception ex){
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return rowSet;
    }
    
    
}
