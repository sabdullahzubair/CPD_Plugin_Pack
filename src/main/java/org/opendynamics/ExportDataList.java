/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opendynamics;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.pdf.PdfWriter;
import javax.servlet.http.HttpServletRequest;
import org.joget.apps.datalist.model.DataList;
import org.joget.apps.datalist.model.DataListActionDefault;
import org.joget.apps.datalist.model.DataListActionResult;
import org.joget.workflow.util.WorkflowUtil;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import org.joget.apps.datalist.model.DataListCollection;
import org.joget.apps.datalist.model.DataListColumn;
import org.joget.apps.form.model.FormRow;
import org.joget.commons.util.LogUtil;

/**
 *
 * @author syeda
 */
public class ExportDataList extends DataListActionDefault {

    @Override
    public String getName() {
        return "Export Datalist Action";

    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String getDescription() {
        return "Export Datalist Action";
    }

    @Override
    public String getLinkLabel() {
        String label = getPropertyString("label");
        if (label == null || label.isEmpty()) {
            label = "Export Datalist";
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
        return "Export Datalist Action";
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
                + "name : 'listHeading',"
                + "label : 'List Heading',"
                + "type : 'textfield',"
                + "description : 'Heading of Export Document'"
                + "},{"
                + "name : 'listDescription',"
                + "label : 'List Description',"
                + "type : 'textfield',"
                + "description : 'Description of Export Document'"
                + "}]"
                + "}]";
        return properties;
    }

    @Override
    public DataListActionResult executeAction(DataList dataList, String[] rowKeys) {
        HttpServletRequest request = WorkflowUtil.getHttpServletRequest();
        HttpServletResponse response = WorkflowUtil.getHttpServletResponse();

        response.setContentType("application/pdf");

        try {
            Document document = new Document(PageSize.A4.rotate());
            document.setMargins(20, 20, 20, 20);
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // Setting up Fonts
            Font headingFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 18, Font.BOLD, BaseColor.BLACK);
            Font descriptionFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Font.BOLD, BaseColor.BLACK);
            Font tableHeadingFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, Font.BOLD, BaseColor.WHITE);
            Font tableDataFont = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.BLACK);

            // Writing Heading
            Paragraph heading = new Paragraph(getPropertyString("listHeading"), headingFont);
            document.add(heading);

            // Writing Description
            Paragraph description = new Paragraph(getPropertyString("listDescription"), descriptionFont);
            document.add(description);

            // Setting up Pre-requsites For Table
            DataListColumn[] listColumns = dataList.getColumns();
            DataListCollection dataListCollection = dataList.getRows(dataList.getSize(), 0);
            String[] columnNames = new String[listColumns.length];
            float[] columnSizes = new float[listColumns.length + 1];
            columnSizes[0] = 1f;
            for (int i = 0; i < listColumns.length; i++) {
                columnNames[i] = listColumns[i].getName();
                columnSizes[i + 1] = 5f;
            }

            // Writing Table
            PdfPTable table = new PdfPTable(columnSizes);
//                table.setWidthPercentage(100);
            table.setSpacingBefore(25);
            table.setSpacingAfter(25);
            table.setWidthPercentage(100);
            table.setHeaderRows(1);

            // Writing Table Headings
            Phrase p = new Phrase(new Chunk("Sr #", tableHeadingFont));
            p.setFont(headingFont);
            PdfPCell cell = new PdfPCell(p);
            cell.setBackgroundColor(new BaseColor(45, 53, 137));
            cell.setPadding(5f);
            table.addCell(cell);
            for (DataListColumn listColumn : listColumns) {
                p = new Phrase(new Chunk(listColumn.getLabel(), tableHeadingFont));
                cell = new PdfPCell(p);
                cell.setBackgroundColor(new BaseColor(45, 53, 137));
                cell.setPadding(5f);
                table.addCell(cell);
            }

            // Writing Table Rows
            int srNumber = 1;
            for (Object o : dataListCollection) {
                FormRow fr = (FormRow) o;
                if (Arrays.asList(rowKeys).contains(fr.getId())) {
                    cell = new PdfPCell(new Phrase(new Chunk(String.valueOf(srNumber), tableDataFont)));
                    cell.setPadding(5f);
                    if(table.getRows().size()%2 == 0){
                        cell.setBackgroundColor(new BaseColor(220,220,220));
                    }
                    table.addCell(cell);
                    srNumber++;
                    for (String columnName : columnNames) {
                        String columnValue = fr.getProperty(columnName) != null ? fr.getProperty(columnName) : "";
                        if("dateCreated".equals(columnName)){
                            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                            Date date = formatter.parse(columnValue.substring(0, 10));
                            columnValue = new SimpleDateFormat("dd/MM/yyyy").format(date);
                        }
                        cell = new PdfPCell(new Phrase(new Chunk(columnValue, tableDataFont)));
                        cell.setPadding(5f);
                        if(table.getRows().size()%2 == 0){
                            cell.setBackgroundColor(new BaseColor(220,220,220));
                        }
                        table.addCell(cell);
                    }
                }

            }

            // Adding Table to The Document
            document.add(table);

            // Closing Documnet
            document.close();

        } catch (DocumentException | FileNotFoundException ex) {
            Logger.getLogger(ExportDataList.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ParseException ex) {
            Logger.getLogger(ExportDataList.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        DataListActionResult result = new DataListActionResult();
        return result;
    }

}
