/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opendynamics;

import java.util.Map;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppService;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.FormRow;
import org.joget.apps.form.model.FormRowSet;
import org.joget.commons.util.SetupManager;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.joget.workflow.model.WorkflowAssignment;
import java.io.*;
import org.apache.commons.io.FileUtils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ImageIcon;
/**
 *
 * @author syeda
 */
public class ImageCompressor extends DefaultApplicationPlugin{

    @Override
    public Object execute(Map props) {
        try {
            // Form Definition Id
            String formDefId = (String) props.get("formDefId");
            
            // Record Id from Process
            WorkflowAssignment wfAssignment = (WorkflowAssignment) props.get("workflowAssignment");
            AppService appService = (AppService) AppUtil.getApplicationContext().getBean("appService");
            String id = appService.getOriginProcessId(wfAssignment.getProcessId());
            
            // Load Form Data
            AppDefinition appDef = (AppDefinition) props.get("appDef");
            FormRow row = new FormRow();
            FormRowSet rowSet = appService.loadFormData(appDef.getAppId(), appDef.getVersion().toString(), formDefId, id);
            if (!rowSet.isEmpty()) {
                row = rowSet.get(0);
            }
            
            // Source Image Field Id
            String sourceImage = row.getProperty((String) props.get("sourceImage"));
            
            // Target Image Field Id
            String targetImage = row.getProperty((String) props.get("targetImage"));
            
            // Functions To Be Performed
            String function = (String) props.get("function");
            
            // Performing Compression
            if(function != null && function.contains("compression")){
                // Compression Ratio
                float compressionRatio = Float.parseFloat((String) props.get("compressionRatio"))/10;
                
                // Image Path on disk
                String imagePath = new StringBuilder().append(SetupManager.getBaseDirectory()).append("app_formuploads/").
                        append(appService.getFormTableName(appDef.getAppId(), appDef.getVersion().toString(), formDefId)).
                        append("/").append(id).append("/").append(sourceImage).toString();
                String imageType = imagePath.substring(imagePath.lastIndexOf(".") + 1);
                
                // Creating File From Path
                File input = new File(imagePath);
                BufferedImage image = ImageIO.read(input);
                
                // Creating Output File
                File compressedImageFile = new File(imagePath.substring(0,imagePath.lastIndexOf(".")) + "_processed." + imageType);
                OutputStream os =new FileOutputStream(compressedImageFile);
                
                // Processing
                Iterator<ImageWriter>writers =  ImageIO.getImageWritersByFormatName("jpg");
                ImageWriter writer = (ImageWriter) writers.next();

                ImageOutputStream ios = ImageIO.createImageOutputStream(os);
                writer.setOutput(ios);

                ImageWriteParam param = writer.getDefaultWriteParam();

                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(compressionRatio);
                writer.write(null, new IIOImage(image, null, null), param);
                
                
                // Closiing Connections
                os.close();
                ios.close();
                writer.dispose();
                
                // Creating Thumbnail for processed image
                File inputThumbnail = new File(imagePath + ".thumb.jpg");
                File outputThumbnail = new File(imagePath.substring(0,imagePath.lastIndexOf(".")) + "_processed." + imageType + ".thumb.jpg");
                FileUtils.copyFile(inputThumbnail, outputThumbnail);
                
                // Updating Form
                row.setProperty(targetImage, sourceImage.substring(0, sourceImage.lastIndexOf(".")) + "_processed." + imageType);
                rowSet.set(0, row);
                appService.storeFormData(appDef.getAppId(), appDef.getVersion().toString(), formDefId, rowSet, id);
                
            }else if(function != null && function.contains("resize")){
                // To Be Done By Muneeb
                
                // For Image Resize Use Image Resize function at line no 217
            }else if(function != null && function.contains("resize") && function.contains("compress")){
                // To Be Done By Muneeb
                
                
            }
            return null;
        } catch (IOException ex) {
            Logger.getLogger(ImageCompressor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public String getName() {
        return "Image Compression Tool";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getDescription() {
        return "Compresses Image In a Given Field.";
    }

    @Override
    public String getLabel() {
        return "Image Compression Tool";
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }

    @Override
    public String getPropertyOptions() {
        return "[{"
                + "title : 'Edit Datalist Action Properties',"
                + "properties : [{"
                    + "name : 'formDefId',"
                    + "label : 'Form',"
                    + "type : 'SelectBox',"
                    + "options_ajax : '[CONTEXT_PATH]/web/json/console/app/" + AppUtil.getCurrentAppDefinition().getAppId() + "/forms/options',"
                    + "required : 'true'"
                + "},{"
                    + "name : 'sourceImage',"
                    + "label : 'Source Image Field Id',"
                    + "type : 'textfield',"
                    + "required :'true'"
                + "},{"
                    + "name : 'targetImage',"
                    + "label : 'Target Image Field Id',"
                    + "type : 'textfield',"
                    + "required :'true'"
                + "},{"
                    + "name : 'function',"
                    + "label : 'Function to Apply',"
                    + "type : 'CheckBox',"
                    + "required :'true',"
                    + "options : [{value: 'compression', label : 'Compress Image'},"
                        + "{value: 'resize', label : 'Resize Image'}]"
                + "},{"
                    + "name : 'compressionRatio',"
                    + "label : 'Compression Ratio',"
                    + "type : 'textfield',"
                    + "regex_validation : '^[1-9]{1}$',"
                    + "validation_message : 'Value Should be 1 to 9!!',"
                    + "control_field: 'function',"
                    + "control_value: 'compression',"
                    + "required :'true',"
                    + "description : '1 Being the max compression and 9 being the least compression.'"
                + "},{"
                    + "name : 'resizedWidth',"
                    + "label : 'Resized Image Width',"
                    + "type : 'textfield',"
                    + "regex_validation : '^[1-9][0-9]{0,3}$',"
                    + "validation_message : 'Value Should be between 1 to 9999!!',"
                    + "control_field: 'function',"
                    + "control_value: 'resize',"
                    + "required :'true'"
                + "},{"
                    + "name : 'resizedHeight',"
                    + "label : 'Resized Image Height',"
                    + "type : 'textfield',"
                    + "regex_validation : '^[1-9][0-9]{0,3}$',"
                    + "validation_message : 'Value Should be between 1 to 9999!!',"
                    + "control_field: 'function',"
                    + "control_value: 'resize',"
                    + "required :'true'"
                + "}]"
            + "}]";
    }
    
    public static void resizeImage(String fileSource, String fileTarget, int width, int height) {
        // TODO code application logic here
        String imagetype = "png";
        BufferedImage imgBuff = load(fileSource);

        try {
            if (imgBuff != null) {
                storeImageAsPng(imgBuff, fileTarget, width, height, imagetype);
            }
        } catch (IOException ex) {
            Logger.getLogger(ImageCompressor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static BufferedImage load(String imageUrl) {
        Image image = new ImageIcon(imageUrl).getImage();
        if (image.getHeight(null) == -1) {
            return null;
        }
        System.out.println(" Height " + image.getHeight(null));

        System.out.println(" Width " + image.getWidth(null));
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null),
                image.getHeight(null),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2D = bufferedImage.createGraphics();
        g2D.drawImage(image, 0, 0, null);
        return bufferedImage;
    }
    
    private static void storeImageAsPng(BufferedImage image, String imageUrl, int width, int height, String imagetype) throws IOException {
        int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB
                : image.getType();
        System.out.println(": " + type);
        //640 x 1136
        BufferedImage resizeImageJpg = resizeImage(image, type, width, height);
        ImageIO.write(resizeImageJpg, imagetype, new File(imageUrl));
    }
    
    private static BufferedImage resizeImage(BufferedImage originalImage, int type, Integer img_width, Integer img_height) {
        BufferedImage resizedImage = new BufferedImage(img_width, img_height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, img_width, img_height, null);
        g.dispose();
        return resizedImage;
    }
}
