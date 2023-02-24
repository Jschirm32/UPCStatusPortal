package com.astralbrands.upc.service;

import javax.annotation.PostConstruct;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.util.Arrays;
import java.util.Properties;

import com.astralbrands.upc.dao.UpcRepository;
import com.astralbrands.upc.dto.Brand;
import com.astralbrands.upc.dto.EmailData;
import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.Message.RecipientType;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import javax.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;


@Service
public class EmailNotification {

    JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
    Logger log = LoggerFactory.getLogger(EmailNotification.class);

    @Value("${smtp.host}")
    private String host;
    @Value("${smtp.port}")
    private int port;
    @Value("${smtp.username}")
    private String userName;

    @Value("${smtp.password}")
    private String password;
    @Value("${smtp.from}")
    private String from;
    @Value("${smtp.to}")
    private String toList;

    @Autowired
    UpcRepository upcRepository;


    Brand brand;

    @PostConstruct
    public void init() {
        javaMailSender.setHost(host);
        javaMailSender.setPort(port);
        javaMailSender.setUsername(userName);
        javaMailSender.setPassword(password);

        Properties props = javaMailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtpClient.EnableSsl", "false");
        props.put("mail.debug", "true");
    }

    public void sendEmailUpc(Brand brand, int count) throws MessagingException, jakarta.mail.MessagingException {

        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(host);
        javaMailSender.setPort(587);

        javaMailSender.setUsername(userName);
        javaMailSender.setPassword(password);

        Properties props = javaMailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtpClient.EnableSsl", "false");
        props.put("mail.debug", "true");

        try {
            log.info("sending .........");

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            String[] to;
            if (toList != null && toList.contains(";")) {
                to = toList.split(";");
                for(String x : to) {
                    mimeMessage.addRecipient(RecipientType.TO, new InternetAddress(x));
                }
            } else {
                to = new String[1];
                to[0] = toList;
            }
            String emailBody = "<html> Hi Team, <br>" + "<br><b style='color:black;'>**ALERT--UPC PORTAL STATUS - UPC COUNT--ALERT**</b><br>"
                    + "<br><b style='color:black;'>Brand's UPC COUNT : </b>" + "<i style='color:black;'>" + count + "</i><br>"
                    + "<br><b style='color:black;'>Brand : </b>" + "<i style='color:black;'>" + brand.getName() + "</i><br>"
                    + "<br><b style='color:black;'> UPC COUNT :  </b>" + "<i style='color:black;'>" + brand.getTotalUpcCode() + "</i><br><br>"
                    + "Regards,<br><br>" + "Jacob Schirm </html>";
            String subject = "****--ALERT--UPC COUNT IS LOW--ALERT--****";
            BodyPart msgBody = new MimeBodyPart();
            msgBody.setText(emailBody);
            mimeMessage.setSubject(subject);
            mimeMessage.setContent(msgBody, "text/html");
            mimeMessage.setFrom(from);

            javaMailSender.send(mimeMessage);
            System.out.println("email send with subject : " + subject);
        } catch (MessagingException e) {
            System.err.println("---------------MESSAGING EXCEPTION STACK TRACE----------------  \n\r\n\r");
            e.printStackTrace();
            System.err.println("\n\r\n\r\n\r ---------------LOCAL MESSAGE----------------  \n\r\n\r" + e.getLocalizedMessage() + "\n\r\n\r\n\r");
            System.err.println("---------------MESSAGE-------------------- \n\r\n\r" + e.getMessage() + "\n\r\n\r\n\r");
            System.err.println("---------------ROOT CAUSE-------------------- \n\r\n\r" + e.getCause() + "\n\r\n\r\n\r");
            System.err.println("---------------ROOT CAUSE--------------------- \n\r\n\r" + e.getNextException() + "\n\r\n\r\n\r");
            System.err.println("---------------FILL-IN STACK TRACE--------------------- \n\r\n\r" + e.fillInStackTrace() + "\n\r\n\r\n\r");
            throw new RuntimeException(e.toString());
        } catch (MailSendException e) {
            System.err.println("---------------PRINT STACK TRACE-------------------- \n\r\n\r");
            e.printStackTrace();
            System.err.println("\n\r\n\r\n\r ---------------LOCAL MESSAGE-------------------- \n\r\n\r" + e.getLocalizedMessage() + "\n\r\n\r\n\r");
            System.err.println("---------------MESSAGE---------------------  \n\r\n\r" + e.getMessage() + "\n\r\n\r\n\r");
            System.err.println("---------------ROOT CAUSE--------------------  \n\r\n\r" + e.getCause() + "\n\r\n\r\n\r");
            System.err.println("---------------ROOT CAUSE--------------------  \n\r\n\r" + e.getFailedMessages() + "\n\r\n\r\n\r");
            System.err.println("---------------FILL-IN STACK TRACE-------------------- \n\r\n\r" + Arrays.toString(e.getMessageExceptions()) + "\n\r\n\r\n\r");
            throw new RuntimeException(e.toString());
        } catch (MailException e) {
            System.err.println("---------------PRINT STACK TRACE-------------------- \n\r\n\r");
            e.printStackTrace();
            System.err.println("\n\r\n\r\n\r ---------------LOCAL MESSAGE----------------  \n\r\n\r" + e.getLocalizedMessage() + "\n\r\n\r\n\r");
            System.err.println("---------------MESSAGE----------------  \n\r\n\r" + e.getMessage() + "\n\r\n\r\n\r");
            System.err.println("---------------ROOT CAUSE----------------  \n\r\n\r" + e.getRootCause() + "\n\r\n\r\n\r");
            System.err.println("---------------ROOT CAUSE----------------  \n\r\n\r" + e.getRootCause() + "\n\r\n\r\n\r");
            System.err.println("---------------MOST SPECIFIC CAUSE----------------  \n\r\n\r" + e.getMostSpecificCause() + "\n\r\n\r\n\r");
            throw new RuntimeException(e.toString());
        }
    }

    public boolean isBelow(int i) {
//        int threshHold = 150;
        //        int total = brand.getTotalUpcCode();

//        List a;
//        int b = 0;
//        a = upcRepository.getAllAvailableUpc();
//        for(int o = 0; o <= a.size(); o++) {
//            b = b + 1;
//        }
        return i < 150;
    }


//    public void sendEmail(Brand brand1) {
//        log.info("sending .........");
//
//        String emailBody = "<html> Hi Team, <br>" + "<br><b style='color:black;'>**ALERT--UPC PORTAL STATUS - UPC COUNT--ALERT**</b><br>"
//                + "<br><b style='color:black;'>Brand : </b>" + "<i style='color:black;'>" + brand1.getName() + "</i><br>"
//                + "<br><b style='color:black;'> UPC COUNT :  </b>" + "<i style='color:black;'>" + brand1.getTotalUpcCode() + "</i><br><br>"
//                + "Regards,<br><br>" + "Jacob Schirm </html>";
//        boolean thresh = false;
//        jakarta.mail.internet.MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//        if (toList != null && toList.length() > 0 && toList.contains(";")) {
//            String[] toAdd = toList.split(";");
//            for (String to : toAdd) {
//                mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
//            }
//        } else {
//            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(toList));
//        }
//
//        if (isBelow(brand1.getTotalUpcCode())) {
////            EmailData.EmailDataBuilder emailData = EmailData.builder().subject("******--UPC PORTAL STATUS COUNT--******")
////                    .emailBody(emailBody);
//            System.err.println("-------------SENDING EMAIL --> LOW COUNT-------------");
////            String[] emailList = toList.split(";");
//
//            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//            simpleMailMessage.setSubject("******--UPC PORTAL STATUS COUNT--******");
//            simpleMailMessage.setText(emailBody);
//            simpleMailMessage.setFrom(from);
////            simpleMailMessage.setTo(emailList);
//
//            javaMailSender.send(simpleMailMessage);
//        }
//    }
}
