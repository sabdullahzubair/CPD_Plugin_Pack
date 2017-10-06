/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opendynamics;

import java.util.Map;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.datalist.model.DataList;
import org.joget.apps.datalist.model.DataListActionDefault;
import org.joget.apps.datalist.model.DataListActionResult;
import org.opendynamics.helper.JogetAccountHandler;
import org.opendynamics.helper.JogetFormHandler;

/**
 *
 * @author syeda
 */
public class AdminCPDPApprovalDataListAction extends DataListActionDefault {
    
    

    @Override
    public String getName() {
        return "Update Form Datalist Action";

    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String getDescription() {
        return "Update Form Datalist Action";
    }

    @Override
    public String getLinkLabel() {
        String label = getPropertyString("label");
        if (label == null || label.isEmpty()) {
            label = "Update Form";
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
        return "Update Form Datalist Action";
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
                + "value : 'Update Form',"
                + "required :'true'"
            + "},{"
                + "name : 'formDefId',"
                + "label : 'Form',"
                + "type : 'SelectBox',"
                + "options_ajax : '[CONTEXT_PATH]/web/json/console/app/" + AppUtil.getCurrentAppDefinition().getAppId() + "/forms/options',"
                + "required : 'true'"
            + "},{"
                + "name : 'fields',"
                + "label : 'Update Fields',"
                + "type : 'Grid',"
                + "required : 'true',"
                + "columns : [{"
                    + "key : 'field',"
                    + "label : 'Field Id'"
                + "},{"
                    + "key : 'value',"
                    + "label : 'Value'"
                + "}]"
            + "},{"
                + "name : 'redirectURL',"
                + "label : 'Redirect URL',"
                + "type : 'textfield',"
                + "description : 'Redirect After Action'"
            + "},{"
                + "name : 'changeGroup',"
                + "label : 'Change User Group',"
                + "type : 'CheckBox',"
                + "options : [{value: 'true', label : 'Change Group'}]"
            + "},{"
                + "name : 'orgId',"
                + "label : 'Organization',"
                + "type : 'SelectBox',"
                + "control_field: 'changeGroup',"
                + "control_value: 'true',"
                + "control_use_regex: 'false',"
                + "options_ajax : '[CONTEXT_PATH]/web/json/plugin/org.joget.apps.userview.lib.GroupPermission/service?action=getOrgs'"
            + "},{"
                + "name : 'groupId',"
                + "label : 'Group',"
                + "type : 'SelectBox',"
                + "control_field: 'changeGroup',"
                + "control_value: 'true',"
                + "control_use_regex: 'false',"
                + "options_ajax_on_change : 'orgId',"
                + "options_ajax : '[CONTEXT_PATH]/web/json/plugin/org.joget.apps.userview.lib.GroupPermission/service?action=getGroups'"
            + "}]"
        + "}]";
        return properties;
    }

    @Override
    public DataListActionResult executeAction(DataList dataList, String[] rowKeys) {
        JogetFormHandler jFH = new JogetFormHandler();
        JogetAccountHandler jAH = new JogetAccountHandler();
        DataListActionResult result = new DataListActionResult();
        String formDefId = getPropertyString("formDefId");
        String redirectURL = getPropertyString("redirectURL");
        String changeGroup = getPropertyString("changeGroup");
//        String orgId = getPropertyString("orgId");
        String groupId = getPropertyString("groupId");
        Map<String, Object> map = getProperties();
        Object[] serviceParameterGrid = (Object[]) map.get("fields");
        String[] fields = new String[serviceParameterGrid.length];
        String[] values = new String[serviceParameterGrid.length];
        
//        System.out.println(changeGroup);
//        System.out.println(orgId);
//        System.out.println(groupId);
        
////        System.out.println("test 1-------????????" + dataList.getActionResult());
//            Map<String, Object> map2 = getProperties();
//            Set<String> keSet = map2.keySet();
//            for (String str : keSet) {
//                System.out.println("1---> Key Set : " + str);
//                System.out.println("  2--> : Values " + map.get(str).getClass().toString());
//                System.out.println("  3--> : Values " + map.get(str).toString());
//                for ( str1 : map2.get(str)) {
//                    System.out.println("    3---> Val : " + str1);
//                }
//            }
        
        
        
        for (int i = 0; i < serviceParameterGrid.length; i++){
            fields[i] = ((Map <String, String>) serviceParameterGrid[i]).get("field");
            values[i] = ((Map <String, String>) serviceParameterGrid[i]).get("value");
//            System.out.println("Field " + i + " : " + fields[i]);
//            System.out.println("value " + i + " : " + values[i]);
        }
        
        if (formDefId != null && !formDefId.isEmpty()) {
//            System.out.println("Form Definition : " + formDefId);
//            System.out.println("No Of Selected Rows : " + rowKeys.length);
            for (String rowKey : rowKeys) {
//                System.out.println("--->>>ID of Row : " + rowKey);
                jFH.insertForm(formDefId, rowKey, fields, values);
                
                if("true".equals(changeGroup) && !"".equals(groupId) && groupId != null){
                    jAH.updateUserGroup(rowKey, groupId);
                }
                
//                System.out.println("------>>>>>>Row Updated.");
            }
        }
        
        
        
        
        result.setType(DataListActionResult.TYPE_REDIRECT);
        if(redirectURL != null && !"".equals(redirectURL)){
            result.setUrl(redirectURL);
//            System.out.println("Redirect URL : " + redirectURL);
        }else{
            dataList.setReloadRequired(true);
            result.setUrl("REFERER");
        // paycheedagi solved by BUG - aya deh
        }
        
        
        return result;
    }

}
