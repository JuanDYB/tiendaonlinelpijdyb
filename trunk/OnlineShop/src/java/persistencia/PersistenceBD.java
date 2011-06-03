package persistencia;

import beans.Carrito;
import beans.Comentario;
import beans.Usuario;
import beans.Producto;
import control.Tools;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

/**
 * Clase que sigue el modelo singleton que impide tener más de un objeto de esa clase en la aplicación
 * @author Juan Díez-Yanguas Barber
 */
public class PersistenceBD implements PersistenceInterface {

    private static PersistenceBD instance = null;
    private DataSource pool;
    private String nameBD;
    private static final Logger logger = Logger.getLogger(PersistenceBD.class.getName());

    PersistenceBD() {
    }

    public static PersistenceBD getInstance() {
        if (instance == null) {
            instance = new PersistenceBD();
        }
        return instance;
    }

    @Override
    public boolean init(String datos, String historiales, String log, String recover) {
        try {
            nameBD = historiales;
            Context env = (Context) new InitialContext().lookup("java:comp/env");
            pool = (DataSource) env.lookup(datos);
            if (pool == null) {
                logger.log(Level.SEVERE, "DataSource no encontrado");
                return false;
            }
            return true;
        } catch (NamingException ex) {
            logger.log(Level.SEVERE, "No se pudo abrir la conexión contra la base de datos", ex);
            return false;
        }
    }

    @Override
    public boolean addUser(Usuario user) {
        Connection conexion = null;
        try {
            conexion = pool.getConnection();

            PreparedStatement insert = conexion.prepareStatement("INSERT INTO " + nameBD + ".Usuarios VALUES (?,?,?,?,?)");
            insert.setString(1, user.getMail());
            insert.setString(2, user.getNombre());
            insert.setString(3, user.getDir());
            insert.setString(4, user.getPass());
            insert.setObject(5, user.getPermisos(), java.sql.Types.CHAR, 1);

            int filasAfectadas = insert.executeUpdate();
            insert.close();
            conexion.close();
            if (filasAfectadas == 1) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            try {  
                logger.log(Level.SEVERE, ex.getMessage());
                conexion.close();
            } catch (SQLException ex1) {
                logger.log(Level.SEVERE, ex1.getMessage());
            }
            return false;
        }
    }

    @Override
    public boolean addProduct(Producto prod) {
        Connection conexion = null;
        try {
            conexion = pool.getConnection();

            PreparedStatement insert = conexion.prepareCall("INSERT INTO " + nameBD + ".Productos VALUES (?,?,?,?,?,?)");
            insert.setString(1, prod.getCodigo());
            insert.setString(2, prod.getNombre());
            insert.setDouble(3, prod.getPrecio());
            insert.setInt(4, prod.getStock());
            insert.setString(5, prod.getDesc());
            insert.setString(6, prod.getDetalles());

            int filasAfectadas = insert.executeUpdate();
            insert.close();
            conexion.close();
            if (filasAfectadas == 1) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            try {
                logger.log(Level.SEVERE, ex.getMessage());
                conexion.close();     
            } catch (SQLException ex1) {
                logger.log(Level.SEVERE, ex1.getMessage());
            }
            return false;
        }
    }

    @Override
    public boolean delUser(String mail) {
        Connection conexion = null;
        try {
            conexion = pool.getConnection();

            PreparedStatement delete = conexion.prepareStatement("DELETE FROM " + nameBD + ".Usuarios WHERE Email=?");
            delete.setString(1, mail);

            int filasAfectadas = delete.executeUpdate();
            delete.close();
            conexion.close();
            if (filasAfectadas == 1) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            try {
                logger.log(Level.SEVERE, ex.getMessage());
                conexion.close();
            } catch (SQLException ex1) {
                logger.log(Level.SEVERE, ex1.getMessage());
            }
            return false;
        }
    }

    @Override
    public boolean delProduct(String codigo) {
        Connection conexion = null;
        try {
            conexion = pool.getConnection();

            PreparedStatement delete = conexion.prepareStatement("DELETE FROM " + nameBD + ".Productos WHERE Codigo=?");
            delete.setString(1, codigo);

            PreparedStatement deleteComments = conexion.prepareStatement("DELETE FROM " + nameBD + ".Comentarios WHERE CodigoProducto=?");
            deleteComments.setString(1, codigo);


            int filasAfectadas = delete.executeUpdate();
            delete.close();
            if (filasAfectadas == 1) {
                deleteComments.executeUpdate();
                deleteComments.close();
                conexion.close();
                return true;
            } else {
                deleteComments.close();
                conexion.close();
                return false;
            }
        } catch (SQLException ex) {
            try {
                logger.log(Level.SEVERE, ex.getMessage());
                conexion.close();
            } catch (SQLException ex1) {
                logger.log(Level.SEVERE, ex1.getMessage());
            }
            return false;
        }
    }

    @Override
    public Usuario getUser(String mail) {
        Connection conexion = null;
        try {
            conexion = pool.getConnection();

            PreparedStatement select = conexion.prepareStatement("SELECT* FROM " + nameBD + ".Usuarios WHERE Email=?");
            select.setString(1, mail);

            ResultSet rs = select.executeQuery();
            Usuario user = null;
            while (rs.next()) {
                user = new Usuario(rs.getString("Nombre"), rs.getString("Direccion"), rs.getString("Email"), rs.getString("Pass"), rs.getString("Permisos").charAt(0));
            }
            rs.close();
            conexion.close();
            return user;
        } catch (SQLException ex) {
            try {
                logger.log(Level.SEVERE, ex.getMessage());
                conexion.close();
            } catch (SQLException ex1) {
                logger.log(Level.SEVERE, ex1.getMessage());
            }
            return null;
        }
    }

    @Override
    public Producto getProduct(String codigo) {
        Connection conexion = null;
        try {
            conexion = pool.getConnection();

            PreparedStatement select = conexion.prepareStatement("SELECT* FROM " + nameBD + ".Productos WHERE Codigo=?");
            select.setString(1, codigo);

            ResultSet rs = select.executeQuery();
            Producto prod = null;
            while (rs.next()) {
                prod = new Producto(rs.getString("Codigo"), rs.getString("Nombre"), rs.getDouble("Precio"), rs.getInt("Stock"), rs.getString("Descripcion"), rs.getString("Detalles"));
            }
            rs.close();
            conexion.close();
            return prod;
        } catch (SQLException ex) {
            try {
                logger.log(Level.SEVERE, ex.getMessage());
                conexion.close();
            } catch (SQLException ex1) {
                logger.log(Level.SEVERE, ex1.getMessage());
            }
            return null;
        }
    }

    @Override
    public boolean updateUser(String mail, Usuario user) {
        Connection conexion = null;
        try {
            conexion = pool.getConnection();

            PreparedStatement update = conexion.prepareStatement("UPDATE " + nameBD + ".Usuarios SET Nombre=?, Direccion=?, Pass=?, Permisos=? WHERE Email=?");
            update.setString(1, user.getNombre());
            update.setString(2, user.getDir());
            update.setString(3, user.getPass());
            update.setObject(4, user.getPermisos(), java.sql.Types.CHAR, 1);
            update.setString(5, mail);

            int filasAfectadas = update.executeUpdate();
            update.close();
            if (filasAfectadas != 1) {
                update.close();
                conexion.close();
            }

            //Update nombres de los comentarios
            PreparedStatement updateComentarios = conexion.prepareStatement("UPDATE " + nameBD + ".Comentarios SET Nombre=? WHERE Email=?");
            updateComentarios.setString(1, user.getNombre());
            updateComentarios.setString(2, mail);

            updateComentarios.executeUpdate();
            conexion.close();

            return true;
        } catch (SQLException ex) {
            try {
                logger.log(Level.SEVERE, ex.getMessage());
                conexion.close();
            } catch (SQLException ex1) {
                logger.log(Level.SEVERE, ex1.getMessage());
            }
            return false;
        }
    }

    @Override
    public boolean updateProduct(String codigo, Producto prod) {
        Connection conexion = null;
        try {
            conexion = pool.getConnection();

            PreparedStatement update = conexion.prepareStatement("UPDATE " + nameBD + ".Productos SET Nombre=?, Precio=?, Stock=?, Descripcion=?, Detalles=? WHERE Codigo=?");
            update.setString(1, prod.getNombre());
            update.setDouble(2, prod.getPrecio());
            update.setInt(3, prod.getStock());
            update.setObject(4, prod.getDesc());
            update.setString(5, prod.getDetalles());
            update.setString(6, codigo);

            int filasAfectadas = update.executeUpdate();
            update.close();
            conexion.close();
            if (filasAfectadas == 1) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            try {
                logger.log(Level.SEVERE, ex.getMessage());
                conexion.close();
            } catch (SQLException ex1) {
                logger.log(Level.SEVERE, ex1.getMessage());
            }
            return false;
        }
    }

    @Override
    public boolean updateProductIfAvailable(Map<String, Integer> carro, HttpServletRequest request, Map <Producto, Integer> listado) {
        Connection conexion = null;
        PreparedStatement select = null;
        PreparedStatement update = null;
        ResultSet rs = null;
        
        try {
            conexion = pool.getConnection();
            conexion.setAutoCommit(false);

            //Consultas
            select = conexion.prepareStatement("SELECT* FROM " + nameBD + ".Productos WHERE Codigo=?");
            update = conexion.prepareStatement("UPDATE " + nameBD + ".Productos SET Stock=? WHERE Codigo=?");
            
            
            String codigoProd;
            int filasAfectadas = 0;

            Iterator<String> iterador = carro.keySet().iterator();
            while (iterador.hasNext()) {
                codigoProd = iterador.next();

                select.setString(1, codigoProd);
                rs = select.executeQuery();
                
                if (rs.next() == false){
                    Tools.anadirMensaje(request, "No existe el producto con codigo: " + codigoProd + "(producto eliminado de la cesta)");
                    iterador.remove();
                    conexion.rollback();
                    conexion.close();
                    return false;
                }else{
                    Producto prod = new Producto(rs.getString("Codigo"), rs.getString("Nombre"), rs.getDouble("Precio"), rs.getInt("Stock"), rs.getString("Descripcion"), rs.getString("Detalles"));
                    rs.close();
                    select.clearParameters();
                    
                    if (carro.get(codigoProd) > prod.getStock()){
                        Tools.anadirMensaje(request, "No hay unidades suficientes de: " + prod.getNombre() + "(producto eliminado de la cesta)");
                        iterador.remove();
                        conexion.rollback();
                        conexion.close();
                        return false;
                    }else{
                        update.setInt(1, prod.getStock() - carro.get(codigoProd));
                        update.setString(2, codigoProd);
                        filasAfectadas = update.executeUpdate();
                        if (filasAfectadas != 1){
                            Tools.anadirMensaje(request, "Ocurrio un error en el catalogo");
                            conexion.rollback();
                            conexion.close();
                            return false;
                        }
                        update.clearParameters();
                        listado.put(prod, carro.get(codigoProd));
                    }
                }        
            }
            select.close();
            update.close();
            conexion.commit();
            conexion.close();
            return true;
        } catch (SQLException ex) {
            try {
                logger.log(Level.SEVERE, ex.getMessage());
                rs.close();
                select.close();
                update.close();
                conexion.rollback();
                conexion.close();
            } catch (SQLException ex1) {
                logger.log(Level.SEVERE, ex1.getMessage());
            }
            return false;
        }
    }

    @Override
    public boolean exit() {
        return true;
    }

    @Override
    public int anyAdmin() {
        Connection conexion = null;
        try {
            conexion = pool.getConnection();

            PreparedStatement select = conexion.prepareStatement("SELECT COUNT(Permisos) AS num FROM " + nameBD + ".Usuarios WHERE Permisos = 'a'");
            ResultSet rs = select.executeQuery();
            int numAdmin = 0;
            while (rs.next()) {
                numAdmin = rs.getInt("num");
            }
            rs.close();
            select.close();
            return numAdmin;
        } catch (SQLException ex) {
            try {
                logger.log(Level.SEVERE, ex.getMessage());
                conexion.close();
            } catch (SQLException ex1) {
                logger.log(Level.SEVERE, ex1.getMessage());
            }
            return -1;
        }
    }

    @Override
    public Map<String, Producto> getProducts() {
        Map<String, Producto> productos = new HashMap<String, Producto>();
        Connection conexion = null;
        try {
            conexion = pool.getConnection();

            PreparedStatement select = conexion.prepareCall("SELECT* FROM " + nameBD + ".Productos");
            ResultSet rs = select.executeQuery();
            while (rs.next()) {
                Producto prod = new Producto(rs.getString("Codigo"), rs.getString("Nombre"), rs.getDouble("Precio"), rs.getInt("Stock"), rs.getString("Descripcion"), rs.getString("Detalles"));
                productos.put(prod.getCodigo(), prod);
            }
            rs.close();
            conexion.close();
            if (productos.size() > 0) {
                return productos;
            } else {
                return null;
            }
        } catch (SQLException ex) {
            try {
                logger.log(Level.SEVERE, ex.getMessage());
                conexion.close();
            } catch (SQLException ex1) {
                logger.log(Level.SEVERE, ex1.getMessage());
            }
            return null;
        }
    }

    @Override
    public Map<String, Usuario> getUsers() {
        Map<String, Usuario> usuarios = new HashMap<String, Usuario>();
        Connection conexion = null;
        try {
            conexion = pool.getConnection();

            PreparedStatement select = conexion.prepareStatement("SELECT* FROM " + nameBD + ".Usuarios");
            ResultSet rs = select.executeQuery();
            while (rs.next()) {
                Usuario user = new Usuario(rs.getString("Nombre"), rs.getString("Direccion"), rs.getString("Email"), rs.getString("Pass"), rs.getString("Permisos").charAt(0));
                usuarios.put(user.getMail(), user);
            }
            rs.close();
            conexion.close();

            if (usuarios.size() > 0) {
                return usuarios;
            } else {
                return null;
            }

        } catch (SQLException ex) {
            try {
                logger.log(Level.SEVERE, ex.getMessage());
                conexion.close();
            } catch (SQLException ex1) {
                logger.log(Level.SEVERE, ex1.getMessage());
            }
            return null;
        }
    }

    @Override
    public Map<String, Producto> searchProd(String campo, String term) {
        Map<String, Producto> productos = new HashMap<String, Producto>();
        Connection conexion = null;
        try {
            conexion = pool.getConnection();
            PreparedStatement select = conexion.prepareStatement("SELECT* FROM " + nameBD + ".Productos WHERE " + campo + " LIKE ?");
            select.setString(1, "%" + term + "%");

            ResultSet rs = select.executeQuery();

            while (rs.next()) {
                Producto prod = new Producto(rs.getString("Codigo"), rs.getString("Nombre"), rs.getDouble("Precio"), rs.getInt("Stock"), rs.getString("Descripcion"), rs.getString("Detalles"));
                productos.put(prod.getCodigo(), prod);
            }
            rs.close();
            conexion.close();

            if (productos.size() > 0) {
                return productos;
            } else {
                return null;
            }


        } catch (SQLException ex) {
            try {
                logger.log(Level.SEVERE, ex.getMessage());
                conexion.close();
            } catch (SQLException ex1) {
                logger.log(Level.SEVERE, ex1.getMessage());
            }
            return null;
        }
    }

    @Override
    public boolean saveRequest(String fechaHora, String requestedURL, String remoteAddr, String remoteHost, String method, String param, String userAgent) {
        Connection conexion = null;
        try {
            conexion = pool.getConnection();
            PreparedStatement insert = conexion.prepareStatement("INSERT INTO " + nameBD + ".Log VALUES (?,?,?,?,?,?,?)");
            insert.setString(1, fechaHora);
            insert.setString(2, requestedURL);
            insert.setString(3, remoteAddr);
            insert.setString(4, remoteHost);
            insert.setString(5, method);
            insert.setString(6, param);
            insert.setString(7, userAgent);

            int filasAfectadas = insert.executeUpdate();
            insert.close();
            conexion.close();

            if (filasAfectadas == 1) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            try {
                logger.log(Level.SEVERE, ex.getMessage());
                conexion.close();
            } catch (SQLException ex1) {
                logger.log(Level.SEVERE, ex1.getMessage());
            }
            return false;
        }
    }

    @Override
    public boolean saveCart(Carrito cart, boolean completado, String date, String formPago) {
        Connection conexion = null;
        try {
            conexion = pool.getConnection();
            conexion.setAutoCommit(false);
            PreparedStatement insertHistorial = conexion.prepareStatement("INSERT INTO " + nameBD + ".HistorialCarritos VALUES (?,?,?,?,?,?)");
            insertHistorial.setString(1, cart.getUser());
            insertHistorial.setString(2, cart.getCodigo());
            insertHistorial.setString(3, date);
            insertHistorial.setDouble(4, cart.getPrecio());
            insertHistorial.setString(5, formPago);
            insertHistorial.setBoolean(6, completado);

            int filasAfectadas = insertHistorial.executeUpdate();
            insertHistorial.close();
            if (filasAfectadas != 1) {
                insertHistorial.close();
                conexion.rollback();
                conexion.close();
                return false;
            }

            PreparedStatement insertCarrito = conexion.prepareStatement("INSERT INTO " + nameBD + ".Carritos VALUES (?,?,?,?,?)");

            Iterator<String> iteradorProductos = cart.getArticulos().keySet().iterator();
            while (iteradorProductos.hasNext()) {
                String key = iteradorProductos.next();
                Producto prod = getProduct(key);
                int cantidad = cart.getArticulos().get(key);

                insertCarrito.setString(1, cart.getCodigo());
                insertCarrito.setString(2, prod.getCodigo());
                insertCarrito.setString(3, prod.getNombre());
                insertCarrito.setDouble(4, prod.getPrecio());
                insertCarrito.setInt(5, cantidad);

                filasAfectadas = insertCarrito.executeUpdate();
                if (filasAfectadas != 1) {
                    insertCarrito.close();
                    conexion.rollback();
                    conexion.close();
                    return false;
                }
                insertCarrito.clearParameters();
            }
            insertCarrito.close();
            conexion.commit();
            conexion.close();
            return true;

        } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            try {
                conexion.rollback();
                conexion.close();
            } catch (SQLException ex1) {
                logger.log(Level.SEVERE, ex1.getMessage());
            }
            return false;
        }
    }

    @Override
    public Carrito requestLastIncompleteCart(String mail) {
        Connection conexion = null;
        try {
            conexion = pool.getConnection();
            conexion.setAutoCommit(false);

            ArrayList<String> carrosIncompletos = this.requestIncompleteCarts(mail);

            if (carrosIncompletos != null) {
                //Obtengo datos carro
                PreparedStatement selectCarro = conexion.prepareStatement("SELECT * FROM " + nameBD + ".HistorialCarritos WHERE CodigoCarrito=?");
                selectCarro.setString(1, carrosIncompletos.get(0));
                ResultSet consultaDatosCarro = selectCarro.executeQuery();
                Carrito carro = null;
                while (consultaDatosCarro.next()) {
                    carro = new Carrito(consultaDatosCarro.getString("CodigoCarrito"), consultaDatosCarro.getString("Email"), consultaDatosCarro.getDouble("Precio"));
                }
                consultaDatosCarro.close();
                selectCarro.close();
                if (carro == null){
                    conexion.rollback();
                    conexion.close();
                    return null;
                }

                //Obtengo productos carro
                Map<String, Integer> productosCarro = new HashMap<String, Integer>();
                PreparedStatement selectProductos = conexion.prepareCall("SELECT CodigoProducto, Cantidad FROM " + nameBD + ".Carritos WHERE CodigoCarrito=?");
                selectProductos.setString(1, carrosIncompletos.get(0));
                ResultSet rs = selectProductos.executeQuery();
                while (rs.next()) {
                    productosCarro.put(rs.getString("CodigoProducto"), rs.getInt("Cantidad"));
                }
                rs.close();
                selectProductos.close();
                conexion.commit();
                conexion.close();
                carro.setArticulos(productosCarro);
                return carro;
            }
            conexion.rollback();
            conexion.close();
            return null;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            try {
                conexion.rollback();
                conexion.close();
            } catch (SQLException ex1) {
                logger.log(Level.SEVERE, ex1.getMessage());
            }
            return null;
        }
    }

    @Override
    public boolean deleteImcompleteCartsClient(String mailClient) {
        Connection conexion = null;
        try {
            conexion = pool.getConnection();
            conexion.setAutoCommit(false);
            ArrayList<String> carrosIncompletos = this.requestIncompleteCarts(mailClient);

            if (carrosIncompletos != null) {
                PreparedStatement deleteHistorialCarros = conexion.prepareStatement("DELETE FROM "
                        + nameBD + ".HistorialCarritos WHERE CodigoCarrito=?");
                PreparedStatement deleteProdCarro = conexion.prepareStatement("DELETE FROM "
                        + nameBD + ".Carritos WHERE CodigoCarrito=?");
                for (int i = 0; i < carrosIncompletos.size(); i++) {
                    deleteHistorialCarros.setString(1, carrosIncompletos.get(i));
                    deleteHistorialCarros.execute();
                    deleteHistorialCarros.clearParameters();

                    deleteProdCarro.setString(1, carrosIncompletos.get(i));
                    deleteProdCarro.execute();
                    deleteProdCarro.clearParameters();
                }
                deleteHistorialCarros.close();
                deleteProdCarro.close();
            }
            conexion.commit();
            conexion.close();
            return true;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            try {
                conexion.rollback();
                conexion.close();
            } catch (SQLException ex1) {
                logger.log(Level.SEVERE, ex1.getMessage());
            }

            return false;
        }
    }

    @Override
    public ArrayList<Carrito> requestSalesRecord(String campo, String term) {
        if (campo.equals("1") == true) {
            campo = "'1'";
        }
        Connection conexion = null;
        try {
            Calendar cal = Calendar.getInstance(new Locale("es", "ES"));
            conexion = pool.getConnection();
            PreparedStatement select = conexion.prepareStatement("SELECT* FROM " + nameBD + ".HistorialCarritos WHERE Completado=true AND " + campo + "=?");
            select.setString(1, term);
            ResultSet rs = select.executeQuery();

            ArrayList<Carrito> historial = new ArrayList<Carrito>();
            while (rs.next()) {
                Carrito carro = new Carrito(rs.getString("CodigoCarrito"), rs.getString("Email"), rs.getDouble("Precio"), rs.getDate("FechaHora", cal).toString(), rs.getTime("FechaHora", cal).toString(), rs.getString("Pago"));
                historial.add(carro);
            }
            rs.close();
            select.close();
            conexion.close();
            if (historial.size() > 0) {
                return historial;
            } else {
                return null;
            }
        } catch (SQLException ex) {
            try {
                logger.log(Level.SEVERE, ex.getMessage());
                conexion.close();
            } catch (SQLException ex1) {
                logger.log(Level.SEVERE, ex1.getMessage());
            }
            return null;
        }
    }

    @Override
    public ArrayList<Producto> getDetailsCartRecord(String codigo) {
        ArrayList<Producto> listado = new ArrayList<Producto>();
        Connection conexion = null;
        try {
            conexion = pool.getConnection();
            PreparedStatement select = conexion.prepareStatement("SELECT* FROM " + nameBD + ".Carritos WHERE CodigoCarrito=?");
            select.setString(1, codigo);
            ResultSet rs = select.executeQuery();

            while (rs.next()) {
                Producto prod = new Producto(rs.getString("CodigoProducto"), rs.getString("Nombre"), rs.getDouble("Precio"), rs.getInt("Cantidad"));
                listado.add(prod);
            }
            rs.close();
            select.close();
            conexion.close();

            if (listado.size() > 0) {
                return listado;
            } else {
                return null;
            }
        } catch (SQLException ex) {
            try {
                logger.log(Level.SEVERE, ex.getMessage());
                conexion.close();
            } catch (SQLException ex1) {
                logger.log(Level.SEVERE, ex1.getMessage());
            }
            return null;
        }
    }

    private ArrayList<String> requestIncompleteCarts(String mail) {
        Connection conexion = null;
        try {
            conexion = pool.getConnection();

            PreparedStatement selectHistorial = conexion.prepareStatement("SELECT CodigoCarrito FROM " + nameBD + ".HistorialCarritos WHERE Email=? AND Completado=false");
            selectHistorial.setString(1, mail);
            ResultSet rs = selectHistorial.executeQuery();
            String codigo = null;
            ArrayList<String> carrosIncompletos = new ArrayList<String>();
            while (rs.next()) {
                carrosIncompletos.add(rs.getString("CodigoCarrito"));
            }
            rs.close();
            selectHistorial.close();
            conexion.close();
            if (carrosIncompletos.size() > 0) {
                return carrosIncompletos;
            } else {
                return null;
            }
        } catch (SQLException ex) {
            try {
                logger.log(Level.SEVERE, ex.getMessage());
                conexion.close();
            } catch (SQLException ex1) {
                logger.log(Level.SEVERE, ex1.getMessage());
            }
            return null;
        }
    }

    @Override
    public boolean newComment(Usuario user, String codigoProducto, String codigoComentario, String fechaHora, String comentario) {
        Connection conexion = null;
        try {
            conexion = pool.getConnection();
            PreparedStatement insert = conexion.prepareStatement("INSERT INTO " + nameBD + ".Comentarios VALUES (?,?,?,?,?,?)");
            insert.setString(1, codigoComentario);
            insert.setString(2, fechaHora);
            insert.setString(3, codigoProducto);
            insert.setString(4, user.getMail());
            insert.setString(5, user.getNombre());
            insert.setString(6, comentario);

            int filasAfectadas = insert.executeUpdate();
            insert.close();
            conexion.close();
            if (filasAfectadas == 1) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            try {
                logger.log(Level.WARNING, ex.getMessage());
                conexion.close();
            } catch (SQLException ex1) {
                logger.log(Level.SEVERE, ex1.getMessage());
            }
            return false;
        }
    }

    @Override
    public boolean deleteComment(String codigoComentario) {
        Connection conexion = null;
        try {
            conexion = pool.getConnection();
            PreparedStatement delete = conexion.prepareStatement("DELETE FROM " + nameBD + ".Comentarios WHERE CodigoComentario=?");
            delete.setString(1, codigoComentario);

            int filasAfectadas = delete.executeUpdate();
            delete.close();
            conexion.close();
            if (filasAfectadas == 1) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            try {
                logger.log(Level.WARNING, ex.getMessage());
                conexion.close();
            } catch (SQLException ex1) {
                logger.log(Level.SEVERE, ex1.getMessage());
            }
            return false;
        }
    }

    @Override
    public boolean updateComment(String codComentario, Comentario comentario) {
        Connection conexion = null;
        try {
            conexion = pool.getConnection();
            PreparedStatement update = conexion.prepareStatement("UPDATE " + nameBD + ".Comentarios SET FechaHora=?, CodigoProducto=?, Email=?, Nombre=?, Comentario=? WHERE CodigoComentario=?");
            update.setString(1, comentario.getFechaHora());
            update.setString(2, comentario.getCodigoProducto());
            update.setString(3, comentario.getEmail());
            update.setString(4, comentario.getNombre());
            update.setString(5, comentario.getComentario());
            update.setString(6, comentario.getCodigoComentario());

            int filasAfectadas = update.executeUpdate();
            update.close();
            conexion.close();
            if (filasAfectadas == 1) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            try {
                logger.log(Level.WARNING, ex.getMessage());
                conexion.close();
            } catch (SQLException ex1) {
                logger.log(Level.SEVERE, ex1.getMessage());  
            }
            return false;
        }
    }

    @Override
    public LinkedList<Comentario> getComentarios(String campo, String valor) {
        Connection conexion = null;
        try {
            LinkedList<Comentario> comentarios = new LinkedList<Comentario>();
            conexion = pool.getConnection();
            PreparedStatement select = conexion.prepareStatement("SELECT * FROM " + nameBD + ".Comentarios WHERE " + campo + "=? ORDER BY FechaHora DESC");
            select.setString(1, valor);
            ResultSet rs = select.executeQuery();

            while (rs.next()) {
                Comentario comment = new Comentario(rs.getString("CodigoComentario"), rs.getDate("FechaHora").toString(),
                        rs.getTime("FechaHora").toString(), rs.getString("CodigoProducto"), rs.getString("Email"),
                        rs.getString("Nombre"), rs.getString("Comentario"));
                comentarios.add(comment);
            }

            rs.close();
            select.close();
            conexion.close();

            if (comentarios.size() > 0) {
                return comentarios;
            } else {
                return null;
            }
        } catch (SQLException ex) {
            try {
                logger.log(Level.SEVERE, ex.getMessage());
                conexion.close();
            } catch (SQLException ex1) {
                logger.log(Level.SEVERE, ex1.getMessage());
            }
            return null;
        }
    }

    @Override
    public Comentario getComment(String codComentario) {
        Connection conexion = null;
        try {
            conexion = pool.getConnection();

            PreparedStatement select = conexion.prepareStatement("SELECT * FROM " + nameBD + ".Comentarios WHERE CodigoComentario=?");
            select.setString(1, codComentario);

            ResultSet rs = select.executeQuery();
            Comentario comment = null;
            while (rs.next()) {
                comment = new Comentario(rs.getString("CodigoComentario"), rs.getDate("FechaHora").toString(), rs.getTime("FechaHora").toString(), rs.getString("CodigoProducto"), rs.getString("Email"), rs.getString("Nombre"), rs.getString("Comentario"));
            }

            rs.close();
            select.close();
            conexion.close();

            return comment;
        } catch (SQLException ex) {
            try {
                logger.log(Level.SEVERE, ex.getMessage());
                conexion.close();
            } catch (SQLException ex1) {
                logger.log(Level.SEVERE, ex1.getMessage());
            }
            return null;
        }
    }
}
