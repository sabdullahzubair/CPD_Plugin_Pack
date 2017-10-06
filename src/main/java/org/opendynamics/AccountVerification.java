/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opendynamics;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joget.apps.app.dao.EnvironmentVariableDao;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.model.EnvironmentVariable;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.plugin.base.DefaultPlugin;
import org.joget.plugin.base.PluginProperty;
import org.joget.plugin.base.PluginWebSupport;
import org.opendynamics.helper.JogetAccountHandler;
import org.opendynamics.helper.QueryHandler;

/**
 *
 * @author syeda
 */
public class AccountVerification extends DefaultPlugin implements PluginWebSupport{
    @Override
    public String getName() {
        return "MMA Accounts Verification WebService";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getDescription() {
        return "To Verify Different Types of account of CPD Module.";
    }

    @Override
    public PluginProperty[] getPluginProperties() {
        return null; // not relevant
    }

    @Override
    public Object execute(Map properties) {
        return null; // not relevant
    }

    public String getLabel() {
        return "MMA Accounts Verification WebService";
    }

    public String getClassName() {
        return getClass().getName();
    }

    public String getPropertyOptions() {
        return "";
    }
    
    
//    http://membership.mma.org.my/jw/web/json/plugin/org.opendynamics.AccountVerification/service
//    http://membership.mma.org.my/jw/web/json/plugin/org.opendynamics.AccountVerification/service?at=cpdp&acId=testProvider1115&vc=1c1993a9-a08c0020-61b5b123-55af6efd
    public void webService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        String accountType = request.getParameter("at");
        QueryHandler qH = new QueryHandler();
        JogetAccountHandler jAH = new JogetAccountHandler();
        AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
        AppDefinition appDef = appService.getAppDefinition("mma_cpd","");
        EnvironmentVariableDao environmentVariableDao = (EnvironmentVariableDao) AppUtil.getApplicationContext().getBean("environmentVariableDao");
        EnvironmentVariable enVar = environmentVariableDao.loadById("serverURL", appDef);
        
        if(accountType != null && !accountType.equals("")){
            switch(accountType){
                case "cpdp":
                    Logger.getLogger(DummyWebService.class.getName()).log(Level.SEVERE, null, "Verifing The Provider Account");
                    String id = request.getParameter("acId");
                    String verificationCode = request.getParameter("vc");

                    String [] dbcode = qH.selectRowUsingId(id, new String[]{"email_verification_code"}, "cpd_provider");
                    if(id == null || id.equals("") || verificationCode == null || verificationCode.equals("")){
                        redirectLink(request, response, "e01672b0-a08c0020-78ac5a09-575f5e3b");
                    }else if(verificationCode.equals(dbcode[0])){

                        Logger.getLogger(AccountVerification.class.getName()).log(Level.SEVERE, null, "Verification Code is correct");
                        Date date = new Date();
                        String modifiedDate= new SimpleDateFormat("dd/MM/yyyy").format(date);
                        
                        qH.updateFormColumnValue("cpd_provider", "email_verified", "Yes", id);
                        qH.updateFormColumnValue("cpd_provider", "application_stage", "Pending Admin Approval", id);
                        qH.updateFormColumnValue("cpd_provider", "email_verification_date", modifiedDate, id);
                        jAH.updateUserGroup(id, "cpd_g02");
                        response.sendRedirect(enVar.getValue() + "/jw/web/userview/mma_cpd/cpd/_/app_message?msgId=dc45dcff-a08c0020-78ac5a09-46adb226");
                    }else{
                        redirectLink(request, response, "e00232f4-a08c0020-78ac5a09-aa78424f");
                    }
                    break;
                default:
                    redirectLink(request, response, "e01672b0-a08c0020-78ac5a09-575f5e3b");
                    break;
            }
        }else{
            redirectLink(request, response, "e01672b0-a08c0020-78ac5a09-575f5e3b");
        }
        
    }
    
    private void redirectLink(HttpServletRequest request, HttpServletResponse response, String msgId){
        AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
        AppDefinition appDef = appService.getAppDefinition("mma_cpd","");
        EnvironmentVariableDao environmentVariableDao = (EnvironmentVariableDao) AppUtil.getApplicationContext().getBean("environmentVariableDao");
        EnvironmentVariable enVar = environmentVariableDao.loadById("serverURL", appDef);
        
        
        try {
            Logger.getLogger(AccountVerification.class.getName()).log(Level.SEVERE, null, "Error With URL" + request.getRequestURL().toString());
            response.sendRedirect(enVar.getValue() + "/jw/web/userview/mma_cpd/cpd/_/app_message?msgId=" + msgId);
        } catch (IOException ex) {
            Logger.getLogger(AccountVerification.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
