/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opendynamics;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joget.plugin.base.DefaultPlugin;
import org.joget.plugin.base.PluginProperty;
import org.joget.plugin.base.PluginWebSupport;
import org.json.JSONException;
import org.json.JSONObject;
import org.opendynamics.helper.AndroidNotificationHandler;
import org.opendynamics.helper.NotificationHandler;
import org.opendynamics.helper.QueryHandler;
import org.opendynamics.helper.iOSNotificationHandler;

/**
 *
 * @author syeda
 */
public class DummyWebService extends DefaultPlugin implements PluginWebSupport{

    @Override
    public String getName() {
        return "MMA Dummy WebServices";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getDescription() {
        return "To Blast Android Notification.";
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
        return "MMA Dummy WebServices";
    }

    public String getClassName() {
        return getClass().getName();
    }

    public String getPropertyOptions() {
        return "";
    }

    @Override
    //http://membership.mma.org.my/jw/web/json/plugin/org.opendynamics.DummyWebService/service?device_type=Android&message=
    public void webService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        iOSNotificationHandler iNH = new iOSNotificationHandler();
        QueryHandler qH = new QueryHandler();
        NotificationHandler nH = new NotificationHandler();
        System.out.println("Service Called!!!");
        
        String method = request.getParameter("method") != null ? request.getParameter("method") : "";
        String device_type = request.getParameter("device_type") != null ? request.getParameter("device_type") : "";
        
        switch(device_type){
            case "Android":
                System.out.println("Sending Android Notification");
                sendAndroidGeneralNotification();
                break;
            
            case "iOS":
                System.out.println("Sending iOS Notification");
                iNH.sendGeneralNotification("1234567890");
                break;
            
            default:
                break;
        }
        
        switch(method){
            // To Send Email Notification for CPD User to Verify the email address
            case "1":
                String email = request.getParameter("email");
                qH.selectRowUsingId(email, new String[]{"verification_code"}, "generic_signup");
                nH.sendVerificationCodeUser(email, (qH.selectRowUsingId(email, new String[]{"verification_code"}, "generic_signup"))[0]);
                break;
        }
        
    }
    
    public void sendiOSGeneralNotification(){
        QueryHandler qH = new QueryHandler();
        AndroidNotificationHandler aNH = new AndroidNotificationHandler();
        iOSNotificationHandler iNH = new iOSNotificationHandler();
//        iOSNotificationHandler.GCMPayloadPack gcmPaylod = new iOSNotificationHandler.GCMPayloadPack();
        
        String[] column = {"gcm_id"};
        String[] deviceType = {"Android"};
        String[] deviceTypeColumn = {"device_type"};
        List<String []> gcimIds = qH.selectColumns(column, "generic_signup", deviceTypeColumn, deviceType, "");
        
        try{
            JSONObject alert = new JSONObject();
            alert.put("title", "Notification");
            alert.put("body", "A new version of MMA application is available, please update the application via the AppStore");
            alert.put("sound", "default");
            
            JSONObject properties = new JSONObject();
            properties.put("aps", alert);
            properties.put("aps", alert);
            
            
            JSONObject dataJson = new JSONObject();
            dataJson.put("title", "Application Update");
            dataJson.put("message", "A new version of MMA application is available, please update the application from the AppStore");
            dataJson.put("articleId", "111");
            dataJson.put("action", "0");
            dataJson.put("id", "");

            JSONObject dataObj = new JSONObject();
            dataObj.put("message", dataJson);


            JSONObject mainJson = new JSONObject();
            mainJson.put("to", "");
//            mainJson.put("to", gcim[0]);
            mainJson.put("data", dataObj);

            aNH.sendNotification(mainJson.toString());
        } catch (JSONException | IOException ex) {
            Logger.getLogger(DummyWebService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void sendAndroidGeneralNotification() throws IOException{
        QueryHandler qH = new QueryHandler();
        AndroidNotificationHandler aNH = new AndroidNotificationHandler();
        
        String[] column = {"gcm_id"};
        String[] deviceType = {"mmahmoor@gmail.com"};
        String[] deviceTypeColumn = {"email"};
        List<String []> gcimIds = qH.selectColumns(column, "generic_signup", deviceTypeColumn, deviceType, "");
        
        
        
        
        for(String[] gcim: gcimIds){
            System.out.println("Length of Array: " + gcim.length);
            if(gcim[0] != null && !gcim[0].equals("")){
                System.out.println("Sending Message to this GCIM ID" + gcim[0]);
                try{
                    JSONObject dataJson = new JSONObject();
                    dataJson.put("title", "Application Update");
                    dataJson.put("message", "A new version of MMA application is available, please update the application from Play Store");
                    dataJson.put("articleId", "111");
                    dataJson.put("action", "0");
                    dataJson.put("id", "");
                    dataJson.put("type", "NOTIFICATION");

                    JSONObject dataObj = new JSONObject();
                    dataObj.put("message", dataJson);


                    JSONObject mainJson = new JSONObject();
//                    mainJson.put("to", "dw9_-MZTJmI:APA91bGJvaCEfAoWatuLZD6fcHvFVSotcz6v3GOYE0zjHr9gmwtppD4VruphFUwwss5Y5wx8DosgkCBCNO_uRIyEV9J_DvDywfQFWg6p5gjCPzKSlmCo7O86Nmdz5JopV6e5HqgVREef");
                    mainJson.put("to", gcim[0]);
                    mainJson.put("data", dataObj);

                    aNH.sendNotification(mainJson.toString());
                } catch (JSONException ex) {
                    Logger.getLogger(DummyWebService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        }
    }
    
}
