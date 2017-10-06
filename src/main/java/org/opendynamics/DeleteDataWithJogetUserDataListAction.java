/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opendynamics;

import org.joget.apps.datalist.model.DataList;
import org.joget.apps.datalist.model.DataListActionDefault;
import org.joget.apps.datalist.model.DataListActionResult;
import org.opendynamics.helper.JogetAccountHandler;
import org.opendynamics.helper.JogetFormHandler;
import org.opendynamics.helper.QueryHandler;
import org.joget.apps.app.service.AppUtil;

/**
 *
 * @author syeda
 */
public class DeleteDataWithJogetUserDataListAction extends DataListActionDefault{
    @Override
    public String getName() {
        return "Delete Data and Joget User Datalist Action";

    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String getDescription() {
        return "Delete Data and Joget User Datalist Action";
    }

    @Override
    public String getLinkLabel() {
        String label = getPropertyString("label");
        if (label == null || label.isEmpty()) {
            label = "Delete";
        }
        return label;

    }

    @Override
    public String getHref() {
        return getPropertyString("href");
    }

    @Override
    public String getTarget() {
        return getPropertyString("target");
    }

    @Override
    public String getHrefParam() {
        return getPropertyString("hrefParam");
    }

    @Override
    public String getHrefColumn() {
        return getPropertyString("hrefColumn");
    }

    @Override
    public String getConfirmation() {
        String confirm = getPropertyString("confirmation");
        if (confirm == null || confirm.isEmpty()) {
            confirm = "Please Confirm";
        }
        return confirm;
    }

    @Override
    public String getLabel() {
        return "Delete Data and Joget User Datalist Action";
    }

    @Override
    public String getClassName() {
        return this.getClass().getName();
    }

    @Override
    public String getPropertyOptions() {
        String properties = "[{"
                + "title : 'Edit Datalist Action Properties',"
                + "properties : [{"
                + "name : 'label',"
                + "label : 'Label',"
                + "type : 'textfield',"
                + "value : 'Delete',"
                + "required :'true'"
            + "},{"
                + "name : 'formDefId',"
                + "label : 'Form',"
                + "type : 'SelectBox',"
                + "options_ajax : '[CONTEXT_PATH]/web/json/console/app/" + AppUtil.getCurrentAppDefinition().getAppId() + "/forms/options',"
                + "required : 'true'"
            + "},{"
                + "name : 'deleteUser',"
                + "label : 'Delete Joget User',"
                + "type : 'CheckBox',"
                + "options : [{value: 'true', label : 'Delete User'}]"
            + "}]"
        + "}]";
        return properties;
    }

    @Override
    public DataListActionResult executeAction(DataList dataList, String[] rowKeys) {
        JogetFormHandler jFH = new JogetFormHandler();
        JogetAccountHandler jAH = new JogetAccountHandler();
        QueryHandler qH = new QueryHandler();
        DataListActionResult result = new DataListActionResult();
        
        String formDefId = getPropertyString("formDefId");
        
//        System.out.println("Form table : " + formDefId);
        
        for (String rowKey : rowKeys){
            qH.DeleteRowUsingId(formDefId, rowKey);
            if("true".equals(getPropertyString("deleteUser"))){
                jAH.removeUser(rowKey);
            }
        }
        
        
        result.setType(DataListActionResult.TYPE_REDIRECT);
        dataList.setReloadRequired(true);
        result.setUrl("REFERER");
        return result;
    }
}
