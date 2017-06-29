/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ReadMail;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class ForwardEmail {

   public static void main(String[] args) {
      Properties props = System.getProperties();            
        props.setProperty("mail.store.protocol", "imaps");
            props.put("mail.imap-mail.outlook.com.ssl.enable", "true");
            props.put("mail.pop3.host", "outlook.com");
            props.put("mail.pop3.port", "995");
            props.put("mail.pop3.starttls.enable", "true");
      try {
         // session.setDebug(true);
         // Get a Store object and connect to the current host
         Session session = Session.getDefaultInstance(props, null);
         Store store = session.getStore("pop3s");
         store.connect("imap-mail.outlook.com", "gr.guerra@outlook.com", "Pay3Nerd4");
        //change the user and password accordingly

         // Create a Folder object and open the folder
         Folder folder = store.getFolder("inbox");
         folder.open(Folder.READ_ONLY);
         BufferedReader reader = new BufferedReader(new InputStreamReader(
            System.in));
         Message[] messages = folder.getMessages();
         if (messages.length != 0) {

         for (int i = 0, n = messages.length; i < n; i++) {
            Message message = messages[i];
            // Get all the information from the message
            String from = InternetAddress.toString(message.getFrom());
            if (from != null) {
               System.out.println("From: " + from);
            }
            String replyTo = InternetAddress.toString(message
               .getReplyTo());
            if (replyTo != null) {
               System.out.println("Reply-to: " + replyTo);
            }
            String to = InternetAddress.toString(message
               .getRecipients(Message.RecipientType.TO));
            if (to != null) {
               System.out.println("To: " + to);
            }

            String subject = message.getSubject();
            if (subject != null) {
               System.out.println("Subject: " + subject);
            }
            Date sent = message.getSentDate();
            if (sent != null) {
               System.out.println("Sent: " + sent);
            }
            System.out.print("Do you want to reply [y/n] : ");
            String ans = reader.readLine();
            if ("Y".equals(ans) || "y".equals(ans)) {
               Message forward = new MimeMessage(session);
               // Fill in header
               forward.setRecipients(Message.RecipientType.TO,
               InternetAddress.parse(from));
               forward.setSubject("Fwd: " + message.getSubject());
               forward.setFrom(new InternetAddress(to));

               // Create the message part
               MimeBodyPart messageBodyPart = new MimeBodyPart();
               // Create a multipart message
               Multipart multipart = new MimeMultipart();
               // set content
               messageBodyPart.setContent(message, "message/rfc822");
               // Add part to multi part
               multipart.addBodyPart(messageBodyPart);
               // Associate multi-part with message
               forward.setContent(multipart);
               forward.saveChanges();

               // Send the message by authenticating the SMTP server
               // Create a Transport instance and call the sendMessage
               Transport t = session.getTransport("smtp");
               try {
                  //connect to the smpt server using transport instance
		  //change the user and password accordingly
                  t.connect("gr.guerra@outlook.com", "Pay3Nerd4");
                  t.sendMessage(forward, forward.getAllRecipients());
               } finally {
                  t.close();
               }

               System.out.println("message forwarded successfully....");

            // close the store and folder objects
            folder.close(false);
            store.close();
            }// end if

         }// end for
   }// end if
   } catch (Exception e) {
      e.printStackTrace();
   }
}

}
