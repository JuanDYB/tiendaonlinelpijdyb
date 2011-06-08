package persistencia;

import modelo.Carrito;
import modelo.Comentario;
import modelo.Usuario;
import modelo.Producto;
import control.Tools;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Juan Díez-Yanguas Barber
 */
public class PersistenceFile implements PersistenceInterface {

    private static final PersistenceFile persistence = new PersistenceFile();
    private static final Logger logger = Logger.getLogger(PersistenceFile.class.getName());
    private String file;
    private String historiales;
    private String fileLog;
    private String recoverFile;
    
    private Map<String, Producto> productos = new HashMap<String, Producto>();
    private Map<String, Usuario> usuarios = new HashMap<String, Usuario>();
    private Map<String, Comentario> comentarios = new HashMap<String, Comentario>();
    
    private final Object lockProductos = new Object();
    private final Object lockUsuarios = new Object();
    private final Object lockCarritos = new Object();
    private final Object lockComentarios = new Object();
    private final Object lockLog = new Object();

    private PersistenceFile() {
    }

    public static PersistenceFile getInstance() {
        return persistence;
    }

    @Override
    public boolean init(String datos, String historiales, String log, String recover) {
        this.file = datos;
        this.historiales = historiales;
        this.fileLog = log;
        this.recoverFile = recover;
        try {
            File archivoDatos = new File(file);

            if (archivoDatos.exists()) {
                FileInputStream inputStream = new FileInputStream(archivoDatos);
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

                productos = (Map<String, Producto>) objectInputStream.readObject();
                usuarios = (Map<String, Usuario>) objectInputStream.readObject();
                comentarios = (Map<String, Comentario>) objectInputStream.readObject();
            }
            return true;
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error entrada Salida", ex);
            return false;
        } catch (ClassNotFoundException ex) {
            logger.log(Level.SEVERE, "Clase no encontrada", ex);
            return false;
        }
    }

    @Override
    public boolean exit() {
        try {
            FileOutputStream outputStream = new FileOutputStream(new File(file));
            ObjectOutputStream objectOutput = new ObjectOutputStream(outputStream);
            objectOutput.writeObject(productos);
            objectOutput.writeObject(usuarios);
            objectOutput.writeObject(comentarios);
            objectOutput.flush();
            objectOutput.close();
            return true;
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, "Fichero no enontrado", ex);
            return false;
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error de entrada / salida", ex);
            return false;
        }
    }

    @Override
    public boolean addUser(Usuario user) {
        synchronized (lockUsuarios) {
            if (usuarios.containsKey(user.getMail())) {
                return false;
            } else {
                usuarios.put(user.getMail(), user);
                return true;
            }
        }
    }

    @Override
    public boolean addProduct(Producto prod) {
        synchronized (lockProductos) {
            if (productos.containsKey(prod.getCodigo())) {
                return false;
            } else {
                productos.put(prod.getCodigo(), prod);
                return true;
            }
        }
    }

    @Override
    public boolean delUser(String mail) {
        synchronized (lockUsuarios) {
            if (!usuarios.containsKey(mail)) {
                return false;
            } else {
                usuarios.remove(mail);
                return true;
            }
        }
    }

    @Override
    public boolean delProduct(String codigo) {
        synchronized (lockProductos) {
            if (!productos.containsKey(codigo)) {
                return false;
            } else {
                productos.remove(codigo);
            }
        }
        
        ArrayList<String> borradoComentarios = new ArrayList<String>();
        synchronized (lockComentarios) {
            for (Comentario comment : comentarios.values()) {
                if (comment.getCodigoProducto().equals(codigo)) {
                    borradoComentarios.add(comment.getCodigoComentario());
                }
            }
        }        
        for (int i = 0; i < borradoComentarios.size(); i++) {
            comentarios.remove(borradoComentarios.get(i));
        }
        return true;
    }

    @Override
    public Usuario getUser(String mail) {
        synchronized (lockUsuarios) {
            return usuarios.get(mail);
        }
    }

    @Override
    public Producto getProduct(String codigo) {
        synchronized (lockProductos) {
            return productos.get(codigo);
        }
    }

    @Override
    public boolean updateUser(String mail, Usuario user) {
        synchronized (lockUsuarios) {
            if (!usuarios.containsKey(mail)) {
                return false;
            } else {
                usuarios.put(mail, user);
                return true;
            }
        }
    }

    @Override
    public boolean updateProduct(String codigo, Producto prod) {
        synchronized (lockProductos) {
            if (!productos.containsKey(codigo)) {
                return false;
            } else {
                productos.put(codigo, prod);
                return true;
            }
        }
    }

    @Override
    public int anyAdmin() {
        synchronized (lockUsuarios) {
            int contador = 0;
            for (Usuario user : usuarios.values()) {
                if (user.getPermisos() == 'a') {
                    contador++;
                }
            }
            return contador;
        }
    }

    @Override
    public Map<String, Producto> getProducts() {
        synchronized (lockProductos) {
            if (productos.size() > 0) {
                return productos;
            } else {
                return null;
            }
        }
    }

    @Override
    public Map<String, Usuario> getUsers() {
        synchronized (lockUsuarios) {
            if (usuarios.size() > 0) {
                return usuarios;
            } else {
                return null;
            }
        }
    }

    @Override
    public Map<String, Producto> searchProd(String campo, String term) {
        Map<String, Producto> resultados = new HashMap<String, Producto>();
        if (productos.isEmpty()) {
            return null;
        } else {
            for (Producto prod : productos.values()) {
                if (campo.equals("Nombre") == true) {
                    if (prod.getNombre().toUpperCase().contains(term.toUpperCase())) {
                        resultados.put(prod.getCodigo(), prod);
                    }
                } else if (campo.equals("Descripcion") == true) {
                    if (prod.getDesc().toUpperCase().contains(term.toUpperCase())) {
                        resultados.put(prod.getCodigo(), prod);
                    }
                } else if (campo.equals("Detalles") == true) {
                    if (prod.getDetalles().toUpperCase().contains(term.toUpperCase())) {
                        resultados.put(prod.getCodigo(), prod);
                    }
                }
            }
            return resultados;
        }
    }

    @Override
    public boolean saveRequest(String fechaHora, String requestedURL, String remoteAddr,
            String remoteHost, String method, String param, String userAgent) {
        PrintWriter escritorLog = null;
        String add = fechaHora + "#" + requestedURL + "#" + remoteAddr + "#" + remoteHost + "#" +
                method + "#" + param + "#" + userAgent;
        if (param == null) {
            param = "";
        }
        synchronized (lockLog) {
            try {
                escritorLog = new PrintWriter(new FileWriter(fileLog, true), true);
                escritorLog.println(add);
                escritorLog.close();
                return true;
            } catch (IOException ex) {
                logger.log(Level.SEVERE, ex.getMessage());
                escritorLog.close();
                return false;
            }
        }
    }

    @Override
    public boolean saveCart(Carrito cart, boolean completado, String date, String formPago) {
        String ruta;
        if (formPago == null) {
            formPago = "";
            ruta = recoverFile;
        } else {
            ruta = historiales;
        }
        cart.setFormaPago(formPago);
        String[] fecha = Tools.getDate().split(" ");
        cart.setFecha(fecha[0]);
        cart.setHora(fecha[1]);

        synchronized (lockCarritos) {
            try {
                File historia = new File(ruta);
                FileOutputStream out;
                ObjectOutputStream objectOut = null;
                if (historia.exists() == true) {
                    out = new FileOutputStream(historia, true);
                    objectOut = new ObjectOutputStream(out) {

                        @Override
                        protected void writeStreamHeader() throws IOException {
                            //No escribir cabeceras
                        }
                    };
                } else {
                    out = new FileOutputStream(historia);
                    objectOut = new ObjectOutputStream(out);
                }


                objectOut.writeObject(cart);
                Iterator<String> iteradorArticulos = cart.getArticulos().keySet().iterator();
                while (iteradorArticulos.hasNext()) {
                    String codigoProducto = iteradorArticulos.next();
                    Producto prod = getProduct(codigoProducto);
                    objectOut.writeObject(new Producto(codigoProducto, prod.getNombre(),
                            prod.getPrecio(), cart.getArticulos().get(codigoProducto)));
                }
                objectOut.flush();
                objectOut.close();
                return true;
            } catch (FileNotFoundException ex) {
                logger.log(Level.SEVERE, ex.getMessage());
                return false;
            } catch (IOException ex) {
                logger.log(Level.SEVERE, ex.getMessage());
                return false;
            }
        }
    }

    @Override
    public Carrito requestLastIncompleteCart(String mail) {
        Map<String, Carrito> carritos = readCartsHistory(true, null, null);
        if (carritos == null) {
            return null;
        }
        String codigoCarro = null;
        for (Carrito cart : carritos.values()) {
            if (cart.getUser().equals(mail)) {
                codigoCarro = cart.getCodigo();
                break;
            }
        }

        if (codigoCarro == null) {
            return null;
        } else {
            return carritos.get(codigoCarro);
        }
    }

    @Override
    public boolean deleteImcompleteCartsClient(String mailClient) {
        ArrayList<String> codigosIncompletos = requestIncompleteCarts(mailClient);
        Map<String, Carrito> carros = readCartsHistory(true, null, null);
        if (carros == null || codigosIncompletos == null) {
            return true;
        }
        for (int i = 0; i < codigosIncompletos.size(); i++) {
            carros.remove(codigosIncompletos.get(i));
        }
        if (carros.isEmpty()) {
            File archivo = new File(recoverFile);
            return archivo.delete();
        }
        for (Carrito cart : carros.values()) {
            if (saveCart(cart, false, cart.getFecha() + " " + cart.getHora(), "") == false) {
                return false;
            }
        }
        return true;
    }

    private ArrayList<String> requestIncompleteCarts(String mail) {
        ArrayList<String> codigosIncompletos = new ArrayList<String>();
        Map<String, Carrito> carritos = readCartsHistory(true, null, null);
        if (carritos == null) {
            return null;
        }
        for (Carrito cart : carritos.values()) {
            if (cart.getUser().equals(mail)) {
                codigosIncompletos.add(cart.getCodigo());
            }
        }
        if (codigosIncompletos.size() > 0) {
            return codigosIncompletos;
        } else {
            return null;
        }
    }

    @Override
    public ArrayList<Carrito> requestSalesRecord(String campo, String term) {
        ArrayList<Carrito> history = new ArrayList<Carrito>();
        Map<String, Carrito> carritos = readCartsHistory(false, null, null);
        if (carritos == null) {
            return null;
        }
        for (Carrito cart : carritos.values()) {
            if (cart.getFormaPago().equals("") == false) {
                if (campo.equals("1") && term.equals("1")) {
                    history.add(cart);
                } else if (campo.equals("CodigoCarrito") && term.equals(cart.getCodigo()) == true) {
                    history.add(cart);
                }
            }
        }
        if (history.size() > 0) {
            return history;
        } else {
            return null;
        }
    }

    @Override
    public ArrayList<Producto> getDetailsCartRecord(String codigo) {
        ArrayList<Producto> productosCarro = new ArrayList<Producto>();
        readCartsHistory(false, codigo, productosCarro);
        if (productosCarro.size() > 0) {
            return productosCarro;
        } else {
            return null;
        }
    }

    @Override
    public boolean newComment(Usuario user, String codigoProducto, String codigoComentario,
            String fechaHora, String comentario) {
        String[] fecha = fechaHora.split(" ");
        Comentario comment = new Comentario(codigoComentario, fecha[0], fecha[1], codigoProducto,
                user.getMail(), user.getNombre(), comentario);
        synchronized (lockComentarios) {
            if (comentarios.containsKey(codigoComentario) == true) {
                return false;
            } else {
                comentarios.put(codigoComentario, comment);
                return true;
            }
        }
    }

    @Override
    public boolean deleteComment(String codigoComentario) {
        synchronized (lockComentarios) {
            if (comentarios.containsKey(codigoComentario)) {
                comentarios.remove(codigoComentario);
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean updateComment(String codComentario, Comentario comentario) {
        synchronized (lockComentarios) {
            if (comentarios.containsKey(codComentario)) {
                comentarios.put(codComentario, comentario);
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public LinkedList<Comentario> getComentarios(String campo, String valor) {
        LinkedList<Comentario> resultados = new LinkedList<Comentario>();
        if (campo.equals("CodigoProducto") == true) {
            synchronized (lockComentarios) {
                for (Comentario comment : comentarios.values()) {
                    if (comment.getCodigoProducto().equals(valor)) {
                        resultados.add(comment);
                    }
                }
            }
        }
        if (resultados.size() > 0) {
            return resultados;
        } else {
            return null;
        }
    }

    @Override
    public Comentario getComment(String codComentario) {
        synchronized (lockComentarios) {
            return comentarios.get(codComentario);
        }
    }

    @Override
    public boolean updateProductIfAvailable(Map<String, Integer> carro, HttpServletRequest request, Map<Producto, Integer> listado) {
        Iterator<String> iterador = carro.keySet().iterator();
        String codigoProducto;
        synchronized (lockProductos) {
            while (iterador.hasNext()) {
                codigoProducto = iterador.next();
                Producto prod = productos.get(codigoProducto);

                if (prod == null) {
                    Tools.anadirMensaje(request, "No se ha encontrado el producto: " + codigoProducto + ", lo hemos eliminado de su cesta");
                    iterador.remove();
                    return false;
                } else {
                    if (carro.get(codigoProducto) > prod.getStock()) {
                        Tools.anadirMensaje(request, "No tenemos unidades suficientes de " + prod.getNombre() + ", lo hemos eliminado de su cesta");
                        iterador.remove();
                        return false;
                    } else {
                        listado.put(prod, carro.get(codigoProducto));
                    }
                }
            }
            Iterator<String> iteradorUpdate = carro.keySet().iterator();
            while (iteradorUpdate.hasNext()) {
                codigoProducto = iteradorUpdate.next();
                Producto prod = productos.get(codigoProducto);
                Producto update = new Producto(codigoProducto, prod.getNombre(), prod.getPrecio(),
                        prod.getStock() - carro.get(codigoProducto), prod.getDesc(), prod.getDetalles());
                productos.put(codigoProducto, update);
            }
            return true;
        }
    }

    private Map<String, Carrito> readCartsHistory(boolean archivo, String codigo, ArrayList<Producto> productosCarro) {
        Map<String, Carrito> historia = new HashMap<String, Carrito>();
        File archivoCarritos;
        if (archivo == false) {
            archivoCarritos = new File(historiales);
        } else {
            archivoCarritos = new File(recoverFile);
        }
        synchronized (lockCarritos) {
            try {
                ObjectInputStream objectInput = new ObjectInputStream(new FileInputStream(archivoCarritos));
                Carrito cart = (Carrito) objectInput.readObject();
                while (cart != null) {
                    historia.put(cart.getCodigo(), cart);

                    for (int i = 0; i < cart.getLenght(); i++) {
                        if (codigo != null && productosCarro != null && cart.getCodigo().equals(codigo)) {
                            productosCarro.add((Producto) objectInput.readObject());
                        } else {
                            objectInput.readObject();
                        }
                    }
                    cart = (Carrito) objectInput.readObject();
                }
                objectInput.close();
                if (historia.size() > 0) {
                    return historia;
                } else {
                    return null;
                }

            } catch (FileNotFoundException ex) {
                logger.log(Level.SEVERE, ex.getMessage());
                return null;
            } catch (EOFException ex) {
                logger.log(Level.SEVERE, ex.getMessage());
                if (historia.size() > 0) {
                    return historia;
                } else {
                    return null;
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, ex.getMessage());
                return null;
            } catch (ClassNotFoundException ex) {
                logger.log(Level.SEVERE, ex.getMessage());
                return null;
            }
        }
    }
}