package control;

import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;

/**
 * @author Juan Díez-Yanguas Barber
 */
public class SendMail {
    
    private Properties config;
    private String pass;

    public SendMail(ServletContext context) {
        Properties prop = System.getProperties();
        prop.setProperty("mail.smtp.host", context.getInitParameter("hostMail"));
        prop.setProperty("mail.smtp.starttls.enable", context.getInitParameter("TSLMail"));
        prop.setProperty("mail.smtp.port", context.getInitParameter("mailPort"));
        prop.setProperty("mail.smtp.user", context.getInitParameter("mailUser"));
        prop.setProperty("mail.smtp.auth", context.getInitParameter("authMail"));
        prop.setProperty("mail.from", context.getInitParameter("mailFrom"));  
        config = prop;
        pass = context.getInitParameter("mailPass");
    }
    
    public Authenticator getAuth (){
        return new SMTPAuthenticator();
    }
    
    public Session startSession (Authenticator auth){
        Session sesion = Session.getDefaultInstance(config, auth);
        return sesion;
    }
    
    public MimeMessage newMail (String subject, String to, String content, Session sesion){
        Calendar cal = Calendar.getInstance(Tools.getLocale());
        try {
            MimeMessage mensaje = new MimeMessage(sesion);
            mensaje.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            mensaje.setSentDate(cal.getTime());
            mensaje.setFrom(new InternetAddress(config.getProperty("mail.from")));
            mensaje.addHeader( "Content-Type", "text/html; charset=UTF-8" );
            mensaje.setSubject(subject, "UTF-8");            
            mensaje.setText(content, "UTF-8", "html");
            return mensaje;
        } catch (AddressException ex) {
            Logger.getLogger(SendMail.class.getName()).log(Level.WARNING, "Dirección de destino no válida", ex);
            return null;
        } catch (MessagingException ex){
            Logger.getLogger(SendMail.class.getName()).log(Level.WARNING, "Erro creando el mensaje", ex);
            return null;
        }    
    }
    
    public boolean sendEmail (Message mensaje, Session sesion){
        Transport transporte = null;
        try {
            transporte = sesion.getTransport("smtp");
            transporte.connect(config.getProperty("mail.smtp.user"), pass);
            transporte.sendMessage(mensaje, mensaje.getAllRecipients());
            return true;
        } catch (MessagingException ex) {
            Logger.getLogger(SendMail.class.getName()).log(Level.SEVERE, "Error conectando con SMTP", ex);
            return false;
        }
        finally{
            if(transporte != null)  {
                try {
                    transporte.close();
                } catch (MessagingException ex) {
                    Logger.getLogger(SendMail.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    private class SMTPAuthenticator extends javax.mail.Authenticator {
    @Override
        public PasswordAuthentication getPasswordAuthentication() {
           String user = config.getProperty("mail.smtp.user");
           return new PasswordAuthentication(user, pass);
        }
    }
}