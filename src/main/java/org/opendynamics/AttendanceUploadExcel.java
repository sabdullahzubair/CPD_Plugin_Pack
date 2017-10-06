/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opendynamics;

/**
 *
 * @author syeda
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import javax.sql.DataSource;
import org.joget.apps.form.model.*;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.service.FormUtil;
import org.joget.plugin.base.PluginManager;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joget.apps.form.lib.WorkflowFormBinder;
import org.joget.commons.util.UuidGenerator;
import org.opendynamics.helper.QueryHandler;
import org.springframework.beans.BeansException;



public class AttendanceUploadExcel extends WorkflowFormBinder {
    private Matcher matcher;
    private Pattern pattern;
        
    
    @Override
    public String getName() {
        return "Upload Delegate Attendace";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getDescription() {
        return "Upload Delegate Attendace";
    }

    @Override
    public String getLabel() {
        return "Upload Delegate Attendace";
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }

    @Override
    public String getPropertyOptions() {
        return "";
    }

    @Override
    public Object getProperty(String property) {
        return super.getProperty(property); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public FormRowSet store(Element element, FormRowSet rows, FormData formData) {
        //check for empty data
        if (rows == null || rows.isEmpty()) {
            return rows;
        }

	Connection con = null;
        QueryHandler qH = new QueryHandler();
        
	try {
            PluginManager pluginManager = (PluginManager) AppUtil.getApplicationContext().getBean("pluginManager");
            FormStoreBinder binder = (FormStoreBinder) pluginManager.getPlugin("org.joget.apps.form.lib.WorkflowFormBinder");
            binder.store(element, rows, formData);

//            AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
//            AppDefinition appDef = AppUtil.getCurrentAppDefinition();

            Form form = FormUtil.findRootForm(element);
            String formTableName = form.getProperties().get("tableName").toString();

            DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
            con = ds.getConnection();
            String sql = "";
            PreparedStatement stmt = null;
            ResultSet rSet = null;

            FormRow formRow = rows.get(0);

            String id = formRow.getProperty("id");
            String event_id = formRow.getProperty("event_id");
            String provider_id = formRow.getProperty("provider_id");
            String event_cat_id = formRow.getProperty("event_cat_id");
            String event_subcat_id = formRow.getProperty("event_subcat_id");
            String cat_max_points = formRow.getProperty("cat_max_points");
            String gained_points = formRow.getProperty("gained_points");
            String attendance_type = formRow.getProperty("attendance_type");
            String excel = formRow.getProperty("excel");
            String filePath = "./wflow/app_formuploads/" + formTableName + File.separator + id + File.separator + excel;
            StringBuilder errorString = new StringBuilder();
            File file = new File(filePath);

            if (file.exists()) {
                boolean throwError = false;
                DataFormatter formatter = new DataFormatter();

                //FileInputStream fileStream = new FileInputStream(file);
                FileInputStream fileStream = new FileInputStream(new File(filePath));
                //Create Workbook instance holding reference to .xlsx file
                XSSFWorkbook workbook = new XSSFWorkbook(fileStream);

                Iterator<Sheet> sheetIterator = workbook.iterator();
                while (sheetIterator.hasNext()) {
                    boolean firstRow = true;
                    Sheet sheet = sheetIterator.next();

                    //String userType = sheet.getSheetName();

                    //Iterate through each rows one by one
                    Iterator<Row> rowIterator = sheet.iterator();
//                    int a =0; int eic=0; int etim=0; int edat=0; int eem=0;
                    int a = 0;
                    while (rowIterator.hasNext()) {
                        Row row = rowIterator.next();

                        if (firstRow) {
                            firstRow = false;
                        }
                        else {
                            String attendee_email = "";
                            String name = "";
                            String ic_number = "";
                            String attendance_date = "";
                            String attendance_time = "";
                            String mmc_number = "";
                            String mobile_no = "";
                            String temp = "";
                            a++;
                            //For each row, iterate through all the columns
                            Iterator<Cell> cellIterator = row.cellIterator();
                            while (cellIterator.hasNext()) {
                                Cell cell = cellIterator.next();

                                switch (cell.getColumnIndex())
                                {
                                    case 0: //id (email)
//                                        String k;
                                        temp = formatter.formatCellValue(cell);
                                        String regex = "^([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$";
                                        Pattern pattern = Pattern.compile(regex);
                                        Matcher matcher = pattern.matcher(temp);
                                        
                                        if(!(temp.trim().length() == 0)){
                                            System.out.println("Processing Email");
                                            System.out.println("Email Matcher : " + matcher.matches());
                                            if(!matcher.matches()){
                                                System.out.println("Email Missing Or Not Correct");
                                                errorString.append("The Email at Row number ").append(a).append(" is ").append(temp).append(". Whereas Required Values should be in this format abc@xyz.com. ");
                                                throwError = true;
                                            }else attendee_email = temp;
                                            
                                        }
                                        break;
                                        
                                    case 1: //name
                                        name = formatter.formatCellValue(cell);
                                        break;
                                        
                                    case 2: //icNumber
                                        temp = formatter.formatCellValue(cell);
                                        if (temp.trim().length() == 0){
                                            errorString.append("IC Number Missing. Required at Row no ").append(a).append(". The Required Format is Numeric or Alphanumeric. ");
                                            throwError = true;
                                        }else if (!isValidIC(temp)){
                                            errorString.append("The IC Number at Row number ").append(a).append(" is ").append(temp).append(". Whereas Required Values should be in Numeric or Alphanumeric format. ");
                                            throwError = true;
                                        }else ic_number = temp;
                                        break;
                                        
                                    case 3: //attendanceDate
                                        temp = formatter.formatCellValue(cell);
                                        if (temp.trim().length() == 0){
                                            errorString.append("Attendance Date Missing. Required at Row no ").append(a).append(". The Required Format is DD/MM/YYYY. ");
                                            throwError = true;
                                        }else if (!validDate(temp)){
                                            errorString.append("The Attendance Date at Row number ").append(a).append(" is ").append(temp).append(". Whereas Required Values should be in DD/MM/YYYY format. ");
                                            throwError = true;
                                        }else attendance_date = temp;
                                        break;
                                        
                                    case 4: //attendanceTime
                                        temp = formatter.formatCellValue(cell);
                                        if (temp.trim().length() == 0){
                                            errorString.append("Attendance Time Missing. Required at Row no ").append(a).append(". The Required Format is HH:MM. ");
                                            throwError = true;
                                        }else if (!validDate(temp)){
                                            errorString.append("The Attendance Time at Row number ").append(a).append(" is ").append(temp).append(". Whereas Required Values should be in HH:MM format. ");
                                            throwError = true;
                                        }else
                                        attendance_time = formatter.formatCellValue(cell);
                                        break;
                                        
                                    case 5: //mmcNumber
                                        mmc_number = formatter.formatCellValue(cell);
                                        break;
                                        
                                    case 6: //phone
                                        mobile_no = formatter.formatCellValue(cell);
                                        break;
                                }
                            }

                            if (!throwError) {
                                
                                String[] selectColumns = {"attendance_date","attendance_time"};
                                String[] filterValues = {ic_number, event_id, attendance_type};
                                String[] filterColumns = {"ic_number", "event_id", "attendance_type"};
                                String condition = "And";
                                List<String []> attendance = qH.selectColumns(selectColumns, "cpd_event_attendance", filterColumns, filterValues, condition);
        
                                if(attendance.size() >= 1 ){
//                                    formData.addFormError("error", "Duplicate Entr");
                                    System.out.println("Duplicate Attendance For IC : " + ic_number + " For Event ID : " + event_id);
                                }else{
                                    String paramsName[] = {"ic_number", "mmc_number", "name", "mobile_no", "attendance_date",
                                        "attendance_time", "event_id", "gained_points", "cat_max_points", "event_subcat_id",
                                        "event_cat_id", "attendee_email", "provider_id", "attendance_type"};
                                    String paramsValues[] = {ic_number, mmc_number, name, mobile_no, attendance_date,
                                        attendance_time, event_id, gained_points, cat_max_points, event_subcat_id,
                                        event_cat_id, attendee_email, provider_id, attendance_type};
                                    
                                    String rowId = UuidGenerator.getInstance().getUuid();
                                    qH.insertInto("cpd_event_attendance", paramsName, paramsValues, rowId);
                                    
                                    String params[] = {"sub_category_points", "category_max_points"};
                                    String [] points = qH.selectRowUsingId(event_subcat_id, params, "cpd_event_subcat");
                                    
                                    qH.updateFormColumnValue("cpd_event_attendance", "gained_points", points[0], rowId);
                                    qH.updateFormColumnValue("cpd_event_attendance", "cat_max_points", points[1], rowId);
                                    
                                }
                            }else {
                                formData.addFormError("error", errorString.toString());
                                return rows;
                            }
                        }
                    }

                }
                fileStream.close();
            }
	} catch(IOException | SQLException | BeansException e) {
            e.printStackTrace();
	} finally {
            try {
                if(con != null) {
                    con.close();
                }
            } catch(SQLException e) {
                e.printStackTrace();
            }
	}

        return rows;
    }
    
        
    private boolean isValidIC(String values){
        return values.matches("[a-zA-Z0-9]*");
    }
    
    private boolean isValidTime(String values){
        return values.matches("([01]?[0-9]|2[0-3]):[0-5][0-9]") && values.length() == 5;
    }
    
    private boolean validDate(final String date){
        String DATE_PATTERN = "(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)";
        pattern = Pattern.compile(DATE_PATTERN);
        
        matcher = pattern.matcher(date);
        
        if(matcher.matches()){
            matcher.reset();
            
            if(matcher.find()){
                String day = matcher.group(1);
                String month = matcher.group(2);
                int year = Integer.parseInt(matcher.group(3));
                if (day.equals("31") && (month.equals("4") || month .equals("6") || month.equals("9") ||
                    month.equals("11") || month.equals("04") || month .equals("06") || month.equals("09"))) {
                          return false; // only 1,3,5,7,8,10,12 has 31 days
                          
                }else if (month.equals("2") || month.equals("02")){
                    if(year % 4==0) return day.equals("30") || day.equals("31");
                    
                    else return day.equals("29")||day.equals("30")||day.equals("31");
                    
                }else return true;
                
            }else return false;
            
        }else return false;
        
    }
}