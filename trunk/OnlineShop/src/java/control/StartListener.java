package control;

import modelo.Usuario;
import persistencia.PersistenceFactory;
import persistencia.PersistenceInterface;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.owasp.esapi.errors.ValidationException;

/**
 * @author Juan Díez-Yanguas Barber
 */
public class StartListener implements ServletContextListener {

    private PersistenceInterface persistence;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        if (startValidate(sce.getServletContext()) == false){
            throw new RuntimeException("No se ha podido iniar la aplicación, parámetros de contexto incorrectos");
        }
        String persistenceMethod = sce.getServletContext().getInitParameter("persistenceMethod");
        String datos = sce.getServletContext().getInitParameter("archivoDatos");
        String historiales = sce.getServletContext().getInitParameter("archivoHistoriales");
        String log = sce.getServletContext().getInitParameter("archivoLog");
        String recover = sce.getServletContext().getInitParameter("archivoRecuperacion");
        persistence = PersistenceFactory.getInstance(persistenceMethod);
        boolean exito = persistence.init(datos, historiales, log, recover);
        if (!exito) {
            throw new RuntimeException("Errores en la incialización de persistencia, imposible iniciar aplicación");
        }
        int numberAdmin = persistence.anyAdmin();
        if (numberAdmin == 0) {
            String adminMail = sce.getServletContext().getInitParameter("adminMail");
            String adminPass = sce.getServletContext().getInitParameter("adminPass");
            adminPass = Tools.generateMD5Signature(adminPass + adminPass.toLowerCase());
            if (adminPass.equals("-1")) {
                Logger.getLogger(StartListener.class.getName()).log(Level.SEVERE, "No se ha encontrado el algoritmo MD5");
                throw new RuntimeException();
            }
            Usuario admin = new Usuario("Administrador", "Calle, 1 28002-Madrid", adminMail, adminPass, 'a');
            persistence.addUser(admin);
        } else if (numberAdmin == -1) {
            throw new RuntimeException("No se pudo obtener numero de administradores, no se iniciará la aplicación");
        }        
        SendMail mail = new SendMail(sce.getServletContext());
        Authenticator autorizacionMail = mail.getAuth();
        sce.getServletContext().setAttribute("EmailSend", mail);
        sce.getServletContext().setAttribute("autorizacionMaile", autorizacionMail);
        sce.getServletContext().setAttribute("persistence", persistence);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        boolean exito = persistence.exit();
        if (exito == false) {
            Logger.getLogger(StartListener.class.getName()).log(Level.SEVERE, "Ha habido errores cerrando la persistencia (archivos)");
        }
    }

    private boolean startValidate(ServletContext context) {
        String persistencia = context.getInitParameter("persistenceMethod");        
        String datos = context.getInitParameter("archivoDatos");
        String historiales = context.getInitParameter("archivoHistoriales");
        String recover = context.getInitParameter(("archivoRecuperacion"));
        String log = context.getInitParameter("archivoLog");
        String adminMail = context.getInitParameter("adminMail");
        String adminPass = context.getInitParameter("adminPass");
        String hostMail = context.getInitParameter("hostMail");
        String TSLmail = context.getInitParameter("TSLMail");
        String mailUser = context.getInitParameter("mailUser");
        String mailPort = context.getInitParameter("mailPort");
        String mailAuth = context.getInitParameter("authMail");
        String mailFrom = context.getInitParameter("mailFrom");
        String mailPass = context.getInitParameter("mailPass");
        if (persistencia == null || adminMail == null || adminPass == null || hostMail == null 
                || TSLmail == null || mailUser == null || mailPort == null || mailAuth == null
                || mailFrom == null || mailPass == null) {
            return false;
        }
        if (persistencia.equals("file") == false && persistencia.equals("pool") == false) {
            return false;
        }        
        if (persistencia.equals("file") == true){
            if (datos == null || historiales == null || recover == null || log == null){
                return false;
            }
        }        
        if (persistencia.equals("pool") == true){
            if (datos == null || historiales == null){
                return false;
            }
        }        
        try{
            Tools.validateEmail(adminMail);
            Tools.validatePass(adminPass);            
            Tools.validateEmail(mailFrom);
//            Tools.validatePass(mailPass);
        }catch (ValidationException ex){
            Logger.getLogger(StartListener.class.getName()).log(Level.SEVERE, "Se ha detectado un posible problema de seguridad "
                    + "en los parámetros del descriptor de despliegue" ,ex);
            return false;
        }        
        return true;
    }
}
