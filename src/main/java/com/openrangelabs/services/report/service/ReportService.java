package com.openrangelabs.services.report.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportService.class);
    String fontName = "Arial";
    String fromEmail = "Orl@openrangelabs.com";

    Workbook workbook;
    Sheet sheet;
    CellStyle infoStyle;
    CellStyle headerStyle;
    CellStyle bodyStyle;
    CellStyle bodyStyleZebra1;
    CellStyle bodyStyleZebra2;
    int nextRow;
    String organization;
    String reportName;
    String dateTime;
    String file;

    @Autowired
    ReportService() {
        this.nextRow = 0;
        this.workbook = new XSSFWorkbook();
        this.sheet = workbook.createSheet("Data");
        this.infoStyle = setInfoFormat();
        this.headerStyle = setHeaderFormat();
        this.bodyStyle = setBodyFormat();
        this.bodyStyleZebra1 = setBodyFormatZebra1();
        this.bodyStyleZebra2 = setBodyFormatZebra2();
    }

    ReportService(String sheetName) {
        this.workbook = new XSSFWorkbook();
        this.sheet = workbook.createSheet(sheetName);
    }

    protected CellStyle setInfoFormat() {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = ((XSSFWorkbook) this.workbook).createFont();
        font.setFontName(fontName);
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);
        font.setColor(IndexedColors.VIOLET.getIndex());
        cellStyle.setFont(font);
        return cellStyle;
    }

    protected CellStyle setHeaderFormat() {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(IndexedColors.VIOLET.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = ((XSSFWorkbook) this.workbook).createFont();
        font.setFontName(fontName);
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        cellStyle.setFont(font);
        return cellStyle;
    }

    protected CellStyle setBodyFormat() {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = ((XSSFWorkbook) this.workbook).createFont();
        font.setFontName(fontName);
        font.setFontHeightInPoints((short) 11);
        font.setBold(false);
        cellStyle.setFont(font);
        return cellStyle;
    }

    protected CellStyle setBodyFormatZebra1() {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = ((XSSFWorkbook) this.workbook).createFont();
        font.setFontName(fontName);
        font.setFontHeightInPoints((short) 11);
        font.setBold(false);
        cellStyle.setFont(font);
        return cellStyle;
    }

    protected CellStyle setBodyFormatZebra2() {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = ((XSSFWorkbook) this.workbook).createFont();
        font.setFontName(fontName);
        font.setFontHeightInPoints((short) 11);
        font.setBold(false);
        cellStyle.setFont(font);
        return cellStyle;
    }

    public void setReportHeader(List<String> columns) {
        this.nextRow = 5;
        Row row = sheet.createRow(this.nextRow);
        for (int i = 0; i < columns.size(); i++) {
            addCell(row, i, columns.get(i), this.headerStyle);
        }
        this.nextRow++;
    }

    public void addCell(Row row, int cell, String data, CellStyle style) {
        Cell reportCell = row.createCell(cell);
        reportCell.setCellValue(data);
        reportCell.setCellStyle(style);
    }

    public void addCell(Row row, int cell, String data, int zebraStyle) {
        Cell reportCell = row.createCell(cell);
        reportCell.setCellValue(data);
        if (zebraStyle > 0) {
            reportCell.setCellStyle(this.bodyStyleZebra2);
        } else {
            reportCell.setCellStyle(this.bodyStyleZebra1);
        }
    }

    public void addReportInformation(String reportName, String reportDate, String organization, String creator) {
        this.reportName  = reportName.replaceAll("[^a-zA-Z0-9]","-").replace("--","-");
        this.organization = organization.replaceAll("[^a-zA-Z0-9]","-").replace("--","-");
        this.dateTime = reportDate;
        Row reportNameRow = sheet.createRow(0);
        addCell(reportNameRow, 0, "Report:", this.infoStyle);
        addCell(reportNameRow, 1, reportName, this.infoStyle);
        addCell(reportNameRow, 2, "", this.infoStyle);
        addCell(reportNameRow, 3, "", this.infoStyle);
        addCell(reportNameRow, 4, "Date:", this.infoStyle);
        addCell(reportNameRow, 5, reportDate, this.infoStyle);

        Row reportOrgRow = sheet.createRow(1);
        addCell(reportOrgRow, 0, "Company:", this.infoStyle);
        addCell(reportOrgRow, 1, organization, this.infoStyle);
        addCell(reportOrgRow, 2, "", this.infoStyle);
        addCell(reportOrgRow, 3, "", this.infoStyle);
        addCell(reportOrgRow, 4, "Created By:", this.infoStyle);
        addCell(reportOrgRow, 5, creator, this.infoStyle);
    }

    public void addReportBodyLine(List<String> data, int zebraLine) {
        if (data.size() > 0) {
            Row row = sheet.createRow(this.nextRow);
            for (int i = 0; i < data.size(); i++) {
                addCell(row, i, data.get(i), zebraLine);
            }
            this.nextRow++;
        }
    }

    public boolean execute() {
        File currDir = new File("/opt/onedrive/PortalReporting");
        String path = currDir.getAbsolutePath();
        String fileName = "/" + this.reportName + "_" + this.organization + "_" + this.dateTime + ".xlsx";
        this.file = path + fileName;
        log.info("Create SOC report for "+this.organization+" at "+this.file);
        try {
            FileOutputStream outputStream = new FileOutputStream(this.file);
            this.workbook.write(outputStream);
            this.workbook.close();
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
        boolean checkFileExists = new File(path, fileName).exists();
        if (!checkFileExists) {
            /** TODO
             * Add messaging
             */
            log.error("The file " + fileName + "was not found in the /opt/onedrive directory. It may have not been created properly");
            return false;
        }
        return true;
    }

    public void email(String emailAddress) throws Exception {
        Session session = initSession();
        Message message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailAddress));
            message.setSubject("Requested Report via MyORL");


            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("This report was requested for on the Myportal.\r\n\r\nIf you did not request for this report please forward it to support@openrangelabs.com");

            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(new File(this.file));

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(attachmentPart);

            message.setContent(multipart);
            Transport.send(message);
        } catch (AddressException ae) {
            log.error(ae.getMessage());
            throw new Exception("The email address " + emailAddress + " could not be set or is invalid.");
        } catch (MessagingException me) {
            log.error(me.getMessage());
            throw new Exception("The email could not be sent for technical reasons. Please try again after several minutes.");
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception("There was an error beyond our control. We will contact our mail carrier and have a resolution shortly.");
        }
    }

    private Session initSession() {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "smtp.office365.com");
        prop.put("mail.smtp.port", "587");
        return Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, "Cbeyond1!");
            }
        });
    }

}
