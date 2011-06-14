package control;

import modelo.Carrito;
import modelo.Usuario;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import persistencia.PersistenceInterface;

/**
 * @author Juan Díez-Yanguas Barber
 */
public class SaveSessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        if (session.getAttribute("auth") != null && (Boolean)session.getAttribute("auth") == true &&
                session.getAttribute("usuario") != null){
            PersistenceInterface persistencia = (PersistenceInterface)
                    session.getServletContext().getAttribute("persistence");
            Usuario user = persistencia.getUser((String)session.getAttribute("usuario"));
            Carrito cart = (Carrito) session.getAttribute("carrito");
            if (user != null && cart != null && cart.getLenght() > 0){
                persistencia.deleteImcompleteCartsClient(user.getMail());
                cart.setUser(user.getMail());
                persistencia.saveCart(cart, false, Tools.getDate(), null);
            }
        }
    }
}
