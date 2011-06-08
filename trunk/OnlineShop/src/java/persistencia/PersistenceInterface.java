package persistencia;

import modelo.Carrito;
import modelo.Comentario;
import modelo.Usuario;
import modelo.Producto;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Juan Díez-Yanguas Barber
 */
public interface PersistenceInterface {
    public boolean init (String datos, String historiales, String log, String recover);
    //Pool de conexiones datos = JNDI
    //Pool de conexiones historiales = nombreBase de datos

    public boolean exit ();

    public boolean addUser (Usuario user);

    public boolean addProduct (Producto prod);

    public boolean delUser(String mail);

    public boolean delProduct(String codigo);

    public Usuario getUser (String mail);

    public Producto getProduct (String codigo);

    public boolean updateUser (String mail, Usuario user);

    public boolean updateProduct (String codigo, Producto prod);
    
    public boolean updateProductIfAvailable (Map <String, Integer> carro, HttpServletRequest request, Map <Producto, Integer> listado);

    public int anyAdmin ();

    public Map <String, Producto> getProducts();

    public Map <String, Usuario> getUsers ();

    public Map <String, Producto> searchProd (String campo, String term);
    
    public boolean saveRequest (String fechaHora, String requestedURL, String remoteAddr, String remoteHost, String method, String param, String userAgent);
    
    public boolean saveCart (Carrito cart, boolean completado, String date, String formPago);
    
    public Carrito requestLastIncompleteCart (String mail);
    
    public boolean deleteImcompleteCartsClient (String mailClient);

    public ArrayList <Carrito> requestSalesRecord (String campo, String term);
    
    public ArrayList <Producto> getDetailsCartRecord (String codigo);
    
    public boolean newComment (Usuario user, String codigoProducto, String codigoComentario, String fechaHora, String comentario);
    
    public Comentario getComment (String codComentario);
    
    public boolean deleteComment (String codigoComentario);
    
    public boolean updateComment (String codComentario, Comentario comentario);
    
    public LinkedList <Comentario> getComentarios (String campo, String valor);
}