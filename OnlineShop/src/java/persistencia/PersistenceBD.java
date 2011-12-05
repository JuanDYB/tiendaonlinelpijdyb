package persistencia;

import modelo.Carrito;
import modelo.Comentario;
import modelo.Usuario;
import modelo.Producto;
import control.Tools;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
//@contador numero inicial de líneas en la clase: 987
//@contador después de modificaciones: 887

/**
 * Clase que sigue el modelo singleton que impide tener más de un objeto de esa clase en la aplicación
 * @author Juan Díez-Yanguas Barber
 */
public class PersistenceBD implements PersistenceInterface {

    private static final PersistenceBD instance = new PersistenceBD();
    private DataSource pool;
    private String nameBD;
    private static final Logger logger = Logger.getLogger(PersistenceBD.class.getName());

    private PersistenceBD() {
    }

    public static PersistenceBD getInstance() {
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
    public boolean exit() {
        return true;
    }

    @Override
    public boolean addUser(Usuario user) {
        Connection conexion = null;
        boolean exito = false;
        PreparedStatement insert = null;
        try {
            conexion = pool.getConnection();
            insert = conexion.prepareStatement("INSERT INTO " + nameBD + ".Usuarios VALUES (?,?,?,?,?)");
            insert.setString(1, user.getMail());
            insert.setString(2, user.getNombre());
            insert.setString(3, user.getDir());
            insert.setString(4, user.getPass());
            insert.setObject(5, user.getPermisos(), java.sql.Types.CHAR, 1);

            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1) {
                exito = true;
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error insertando usuario", ex);
        } finally {
            cerrarConexionYStatement(conexion, insert);
        }
        return exito;
    }

    @Override
    public boolean addProduct(Producto prod) {
        Connection conexion = null;
        PreparedStatement insert = null;
        boolean exito = false;
        try {
            conexion = pool.getConnection();
            insert = conexion.prepareStatement("INSERT INTO " + nameBD + ".Productos VALUES (?,?,?,?,?,?)");
            insert.setString(1, prod.getCodigo());
            insert.setString(2, prod.getNombre());
            insert.setDouble(3, prod.getPrecio());
            insert.setInt(4, prod.getStock());
            insert.setString(5, prod.getDesc());
            insert.setString(6, prod.getDetalles());

            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1) {
                exito = true;
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error insertando producto", ex);
        } finally {
            cerrarConexionYStatement(conexion, insert);
        }
        return exito;
    }

    @Override
    public boolean delUser(String mail) {
        Connection conexion = null;
        PreparedStatement delete = null;
        boolean exito = false;
        try {
            conexion = pool.getConnection();
            delete = conexion.prepareStatement("DELETE FROM " + nameBD + ".Usuarios WHERE Email=?");
            delete.setString(1, mail);

            int filasAfectadas = delete.executeUpdate();
            if (filasAfectadas == 1) {
                exito = true;
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error borrando usuario", ex.getMessage());
        } finally {
            cerrarConexionYStatement(conexion, delete);
        }
        return exito;
    }

    @Override
    public boolean delProduct(String codigo) {
        Connection conexion = null;
        PreparedStatement delete = null;
        PreparedStatement deleteComments = null;
        boolean exito = false;
        try {
            conexion = pool.getConnection();
            delete = conexion.prepareStatement("DELETE FROM " + nameBD + ".Productos WHERE Codigo=?");
            delete.setString(1, codigo);
            deleteComments = conexion.prepareStatement("DELETE FROM " + nameBD + ".Comentarios WHERE CodigoProducto=?");
            deleteComments.setString(1, codigo);

            int filasAfectadas = delete.executeUpdate();
            if (filasAfectadas == 1) {
                deleteComments.executeUpdate();
                exito = true;
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error borrando producto o borrando sus comentarios asignados", ex);
        } finally {
            cerrarConexionYStatement(conexion, delete, deleteComments);
        }
        return exito;
    }

    @Override
    public Usuario getUser(String mail) {
        Connection conexion = null;
        PreparedStatement select = null;
        ResultSet rs = null;
        Usuario user = null;

        try {
            conexion = pool.getConnection();
            select = conexion.prepareStatement("SELECT* FROM " + nameBD + ".Usuarios WHERE Email=?");
            select.setString(1, mail);
            rs = select.executeQuery();
            while (rs.next()) {
                user = new Usuario(rs.getString("Nombre"), rs.getString("Direccion"), rs.getString("Email"),
                        rs.getString("Pass"), rs.getString("Permisos").charAt(0));
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error obteniendo usuario", ex);
            user = null;
        } finally {
            cerrarConexionYStatement(conexion, select);
            cerrarResultSet(rs);
        }
        return user;
    }

    @Override
    public Producto getProduct(String codigo) {
        Connection conexion = null;
        PreparedStatement select = null;
        ResultSet rs = null;
        Producto prod = null;
        try {
            conexion = pool.getConnection();
            select = conexion.prepareStatement("SELECT* FROM " + nameBD + ".Productos WHERE Codigo=?");
            select.setString(1, codigo);
            rs = select.executeQuery();
            while (rs.next()) {
                prod = new Producto(rs.getString("Codigo"), rs.getString("Nombre"), rs.getDouble("Precio"),
                        rs.getInt("Stock"), rs.getString("Descripcion"), rs.getString("Detalles"));
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error obteniendo producto", ex);
            prod = null;
        } finally {
            cerrarConexionYStatement(conexion, select);
            cerrarResultSet(rs);
        }
        return prod;
    }

    @Override
    public boolean updateUser(String mail, Usuario user) {
        Connection conexion = null;
        PreparedStatement update = null;
        PreparedStatement updateComentarios = null;
        boolean exito = false;
        try {
            conexion = pool.getConnection();
            update = conexion.prepareStatement("UPDATE " + nameBD +
                    ".Usuarios SET Nombre=?, Direccion=?, Pass=?, Permisos=? WHERE Email=?");
            update.setString(1, user.getNombre());
            update.setString(2, user.getDir());
            update.setString(3, user.getPass());
            update.setObject(4, user.getPermisos(), java.sql.Types.CHAR, 1);
            update.setString(5, mail);

            int filasAfectadas = update.executeUpdate();
            if (filasAfectadas == 1) {
                updateComentarios = conexion.prepareStatement("UPDATE " + nameBD +
                        ".Comentarios SET Nombre=? WHERE Email=?");
                updateComentarios.setString(1, user.getNombre());
                updateComentarios.setString(2, mail);
                updateComentarios.executeUpdate();
                exito = true;
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error editando usuario o su nombre en los comentarios", ex.getMessage());
        } finally {
            cerrarConexionYStatement(conexion, update, updateComentarios);
        }
        return exito;
    }

    @Override
    public boolean updateProduct(String codigo, Producto prod) {
        Connection conexion = null;
        PreparedStatement update = null;
        boolean exito = false;
        try {
            conexion = pool.getConnection();
            update = conexion.prepareStatement("UPDATE " + nameBD +
                    ".Productos SET Nombre=?, Precio=?, Stock=?, Descripcion=?, Detalles=? WHERE Codigo=?");
            update.setString(1, prod.getNombre());
            update.setDouble(2, prod.getPrecio());
            update.setInt(3, prod.getStock());
            update.setObject(4, prod.getDesc());
            update.setString(5, prod.getDetalles());
            update.setString(6, codigo);

            int filasAfectadas = update.executeUpdate();
            if (filasAfectadas == 1) {
                exito = true;
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error actualizando producto", ex);
        } finally {
            cerrarConexionYStatement(conexion, update);
        }
        return exito;
    }

    @Override
    public boolean updateProductIfAvailable(Map<String, Integer> carro, HttpServletRequest request,
            Map<Producto, Integer> listado) {
        Connection conexion = null;
        PreparedStatement select = null;
        PreparedStatement update = null;
        ResultSet rs = null;
        boolean exito = false;
        try {
            conexion = pool.getConnection();
            conexion.setAutoCommit(false);
            select = conexion.prepareStatement("SELECT* FROM " + nameBD + ".Productos WHERE Codigo=?");
            update = conexion.prepareStatement("UPDATE " + nameBD + ".Productos SET Stock=? WHERE Codigo=?");
            String codigoProd;
            int filasAfectadas = 0;

            Iterator<String> iterador = carro.keySet().iterator();
            while (iterador.hasNext()) {
                codigoProd = iterador.next();
                select.setString(1, codigoProd);
                rs = select.executeQuery();
                if (rs.next() == false) {
                    Tools.anadirMensaje(request, "No existe el producto con codigo: " + codigoProd +
                            "(producto eliminado de la cesta)");
                    iterador.remove();
                    conexion.rollback();
                } else {
                    Producto prod = new Producto(rs.getString("Codigo"), rs.getString("Nombre"), rs.getDouble("Precio"),
                            rs.getInt("Stock"), rs.getString("Descripcion"), rs.getString("Detalles"));
                    select.clearParameters();
                    if (carro.get(codigoProd) > prod.getStock()) {
                        Tools.anadirMensaje(request, "No hay unidades suficientes de: " + prod.getNombre() +
                                "(producto eliminado de la cesta)");
                        iterador.remove();
                        conexion.rollback();
                    } else {
                        update.setInt(1, prod.getStock() - carro.get(codigoProd));
                        update.setString(2, codigoProd);
                        filasAfectadas = update.executeUpdate();
                        if (filasAfectadas != 1) {
                            Tools.anadirMensaje(request, "Ocurrio un error en el catalogo");
                            conexion.rollback();
                        }
                        update.clearParameters();
                        listado.put(prod, carro.get(codigoProd));
                    }
                }
            }
            conexion.commit();
            exito = true;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error actualizando unidades de productos en compra", ex);
            try {
                conexion.rollback();
            } catch (SQLException ex1) {
                logger.log(Level.SEVERE, "Error haciendo rolback de la transacción que ha dado error en la actualización de unidades por compra", ex1);
            }
        } finally {
            cerrarConexionYStatement(conexion, select, update);
            cerrarResultSet(rs);
        }
        return exito;
    }

    @Override
    public int anyAdmin() {
        Connection conexion = null;
        PreparedStatement select = null;
        ResultSet rs = null;
        int numAdmin = -1;
        try {
            conexion = pool.getConnection();
            select = conexion.prepareStatement("SELECT COUNT(Permisos) AS num FROM " +
                    nameBD + ".Usuarios WHERE Permisos = 'a'");
            rs = select.executeQuery();
            while (rs.next()) {
                numAdmin = rs.getInt("num");
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error obteniendo el numero de administradores", ex);
        } finally {
            cerrarConexionYStatement(conexion, select);
            cerrarResultSet(rs);
        }
        return numAdmin;
    }

    @Override
    public Map<String, Producto> getProducts() {
        Map<String, Producto> productos = new HashMap<String, Producto>();
        Connection conexion = null;
        PreparedStatement select = null;
        ResultSet rs = null;
        try {
            conexion = pool.getConnection();
            select = conexion.prepareStatement("SELECT* FROM " + nameBD + ".Productos");
            rs = select.executeQuery();
            while (rs.next()) {
                Producto prod = new Producto(rs.getString("Codigo"), rs.getString("Nombre"),
                        rs.getDouble("Precio"), rs.getInt("Stock"), rs.getString("Descripcion"), rs.getString("Detalles"));
                productos.put(prod.getCodigo(), prod);
            }
            if (productos.size() <= 0) {
                productos = null;
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error obteniendo los productos", ex);
            productos = null;
        } finally {
            cerrarConexionYStatement(conexion, select);
            cerrarResultSet(rs);
        }
        return productos;
    }

    @Override
    public Map<String, Usuario> getUsers() {
        Map<String, Usuario> usuarios = new HashMap<String, Usuario>();
        Connection conexion = null;
        PreparedStatement select = null;
        ResultSet rs = null;
        try {
            conexion = pool.getConnection();
            select = conexion.prepareStatement("SELECT* FROM " + nameBD + ".Usuarios");
            rs = select.executeQuery();
            while (rs.next()) {
                Usuario user = new Usuario(rs.getString("Nombre"), rs.getString("Direccion"),
                        rs.getString("Email"), rs.getString("Pass"), rs.getString("Permisos").charAt(0));
                usuarios.put(user.getMail(), user);
            }
            if (usuarios.size() <= 0) {
                usuarios = null;
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error obteniendo los usuarios", ex);
            usuarios = null;
        } finally {
            cerrarConexionYStatement(conexion, select);
            cerrarResultSet(rs);
        }
        return usuarios;
    }

    @Override
    public Map<String, Producto> searchProd(String campo, String term) {
        Map<String, Producto> productos = new HashMap<String, Producto>();
        Connection conexion = null;
        PreparedStatement select = null;
        ResultSet rs = null;
        try {
            conexion = pool.getConnection();
            select = conexion.prepareStatement("SELECT* FROM " + nameBD + ".Productos WHERE " + campo + " LIKE ?");
            select.setString(1, "%" + term + "%");
            rs = select.executeQuery();
            while (rs.next()) {
                Producto prod = new Producto(rs.getString("Codigo"), rs.getString("Nombre"),
                        rs.getDouble("Precio"), rs.getInt("Stock"), rs.getString("Descripcion"), rs.getString("Detalles"));
                productos.put(prod.getCodigo(), prod);
            }
            if (productos.size() <= 0) {
                productos = null;
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error buscando producto", ex);
            productos = null;
        } finally {
            cerrarConexionYStatement(conexion, select);
            cerrarResultSet(rs);
        }
        return productos;
    }

    @Override
    public boolean saveRequest(String fechaHora, String requestedURL, String remoteAddr,
            String remoteHost, String method, String param, String userAgent) {
        Connection conexion = null;
        PreparedStatement insert = null;
        boolean exito = false;
        try {
            conexion = pool.getConnection();
            insert = conexion.prepareStatement("INSERT INTO " + nameBD + ".Log VALUES (?,?,?,?,?,?,?)");
            insert.setString(1, fechaHora);
            insert.setString(2, requestedURL);
            insert.setString(3, remoteAddr);
            insert.setString(4, remoteHost);
            insert.setString(5, method);
            insert.setString(6, param);
            insert.setString(7, userAgent);

            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1) {
                exito = true;
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error guardando log de petición", ex);
        } finally {
            cerrarConexionYStatement(conexion, insert);
        }
        return exito;
    }

    @Override
    public boolean saveCart(Carrito cart, boolean completado, String date, String formPago) {
        Connection conexion = null;
        PreparedStatement insertHistorial = null;
        PreparedStatement insertCarrito = null;
        boolean exito = false;
        try {
            conexion = pool.getConnection();
            conexion.setAutoCommit(false);
            insertHistorial = conexion.prepareStatement("INSERT INTO " + nameBD + ".HistorialCarritos VALUES (?,?,?,?,?,?)");
            insertHistorial.setString(1, cart.getUser());
            insertHistorial.setString(2, cart.getCodigo());
            insertHistorial.setString(3, date);
            insertHistorial.setDouble(4, cart.getPrecio());
            insertHistorial.setString(5, formPago);
            insertHistorial.setBoolean(6, completado);

            int filasAfectadas = insertHistorial.executeUpdate();
            if (filasAfectadas != 1) {
                conexion.rollback();
            } else {
                insertCarrito = conexion.prepareStatement("INSERT INTO " + nameBD + ".Carritos VALUES (?,?,?,?,?)");
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
                        conexion.rollback();
                        break;
                    }
                    insertCarrito.clearParameters();
                }
                conexion.commit();
                exito = true;
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error añadiendo carrito al registro", ex);
            try {
                conexion.rollback();
            } catch (SQLException ex1) {
                logger.log(Level.SEVERE, "Error haciendo rollback de la transacción para insertar carrito en el registro", ex1);
            }
        } finally {
            cerrarConexionYStatement(conexion, insertCarrito, insertHistorial);
        }
        return exito;
    }

    @Override
    public Carrito requestLastIncompleteCart(String mail) {
        Connection conexion = null;
        PreparedStatement selectCarro = null;
        PreparedStatement selectProductos = null;
        ResultSet consultaDatosCarro = null;
        ResultSet rs = null;
        Carrito carro = null;
        try {
            conexion = pool.getConnection();
            conexion.setAutoCommit(false);
            ArrayList<String> carrosIncompletos = this.requestIncompleteCarts(mail);
            if (carrosIncompletos != null) {
                selectCarro = conexion.prepareStatement("SELECT * FROM " + nameBD + ".HistorialCarritos WHERE CodigoCarrito=?");
                selectCarro.setString(1, carrosIncompletos.get(0));
                consultaDatosCarro = selectCarro.executeQuery();

                while (consultaDatosCarro.next()) {
                    carro = new Carrito(consultaDatosCarro.getString("CodigoCarrito"),
                            consultaDatosCarro.getString("Email"), consultaDatosCarro.getDouble("Precio"));
                }
                if (carro == null) {
                    conexion.rollback();
                } else {
                    Map<String, Integer> productosCarro = new HashMap<String, Integer>();
                    selectProductos = conexion.prepareStatement("SELECT CodigoProducto, Cantidad FROM " +
                            nameBD + ".Carritos WHERE CodigoCarrito=?");
                    selectProductos.setString(1, carrosIncompletos.get(0));
                    rs = selectProductos.executeQuery();
                    while (rs.next()) {
                        productosCarro.put(rs.getString("CodigoProducto"), rs.getInt("Cantidad"));
                    }
                    conexion.commit();
                    carro.setArticulos(productosCarro);
                }
            } else {
                conexion.rollback();
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error obteniendo el ultimo carrito incompleto del usuario", ex);
            carro = null;
            try {
                conexion.rollback();
            } catch (SQLException ex1) {
                logger.log(Level.SEVERE, "Error haciendo rollback de la transacción para obtener ultimo carro incompleto del usuario", ex1);
            }
        } finally {
            cerrarConexionYStatement(conexion, selectCarro, selectProductos);
            cerrarResultSet(consultaDatosCarro, rs);
        }
        return carro;
    }

    @Override
    public boolean deleteImcompleteCartsClient(String mailClient) {
        Connection conexion = null;
        PreparedStatement deleteHistorialCarros = null;
        PreparedStatement deleteProdCarro = null;
        boolean exito = false;
        try {
            conexion = pool.getConnection();
            conexion.setAutoCommit(false);
            ArrayList<String> carrosIncompletos = this.requestIncompleteCarts(mailClient);
            if (carrosIncompletos != null) {
                deleteHistorialCarros = conexion.prepareStatement("DELETE FROM "
                        + nameBD + ".HistorialCarritos WHERE CodigoCarrito=?");
                deleteProdCarro = conexion.prepareStatement("DELETE FROM "
                        + nameBD + ".Carritos WHERE CodigoCarrito=?");
                for (int i = 0; i < carrosIncompletos.size(); i++) {
                    deleteHistorialCarros.setString(1, carrosIncompletos.get(i));
                    deleteHistorialCarros.execute();
                    deleteHistorialCarros.clearParameters();
                    deleteProdCarro.setString(1, carrosIncompletos.get(i));
                    deleteProdCarro.execute();
                    deleteProdCarro.clearParameters();
                }
            }
            conexion.commit();
            exito = true;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error borrando los carritos incompletos de un usuario", ex);
            try {
                conexion.rollback();
            } catch (SQLException ex1) {
                logger.log(Level.SEVERE, "Error haciendo rollback de la transacción para borrar carros incompletos de un usuario", ex1);
            }
        } finally {
            cerrarConexionYStatement(conexion, deleteHistorialCarros, deleteProdCarro);
        }
        return exito;
    }

    @Override
    public ArrayList<Carrito> requestSalesRecord(String campo, String term) {
        if (campo.equals("1") == true) {
            campo = "'1'";
        }
        Connection conexion = null;
        PreparedStatement select = null;
        ResultSet rs = null;
        ArrayList<Carrito> historial = new ArrayList<Carrito>();
        try {
            Calendar cal = Calendar.getInstance(new Locale("es", "ES"));
            conexion = pool.getConnection();
            select = conexion.prepareStatement("SELECT* FROM " + nameBD +
                    ".HistorialCarritos WHERE Completado=true AND " + campo + "=?");
            select.setString(1, term);
            rs = select.executeQuery();
            while (rs.next()) {
                Carrito carro = new Carrito(rs.getString("CodigoCarrito"), rs.getString("Email"), 
                        rs.getDouble("Precio"), rs.getDate("FechaHora", cal).toString(),
                        rs.getTime("FechaHora", cal).toString(), rs.getString("Pago"));
                historial.add(carro);
            }

            if (historial.size() <= 0) {
                historial = null;
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error obteniendo historial de ventas", ex);
            historial = null;
        } finally {
            cerrarConexionYStatement(conexion, select);
            cerrarResultSet(rs);
        }
        return historial;
    }

    @Override
    public ArrayList<Producto> getDetailsCartRecord(String codigo) {
        ArrayList<Producto> listado = new ArrayList<Producto>();
        Connection conexion = null;
        PreparedStatement select = null;
        ResultSet rs = null;
        try {
            conexion = pool.getConnection();
            select = conexion.prepareStatement("SELECT* FROM " + nameBD + ".Carritos WHERE CodigoCarrito=?");
            select.setString(1, codigo);
            rs = select.executeQuery();
            while (rs.next()) {
                Producto prod = new Producto(rs.getString("CodigoProducto"), rs.getString("Nombre"),
                        rs.getDouble("Precio"), rs.getInt("Cantidad"));
                listado.add(prod);
            }
            if (listado.size() <= 0) {
                listado = null;
            } 
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error obteniendo los productos pertenecientes a un carrito", ex);
            listado = null;
        } finally {
            cerrarConexionYStatement(conexion, select);
            cerrarResultSet(rs);
        }
        return listado;
    }

    private ArrayList<String> requestIncompleteCarts(String mail) {
        ArrayList<String> carrosIncompletos = new ArrayList<String>();
        Connection conexion = null;
        PreparedStatement selectHistorial = null;
        ResultSet rs = null;
        try {
            conexion = pool.getConnection();
            selectHistorial = conexion.prepareStatement("SELECT CodigoCarrito FROM " + nameBD +
                    ".HistorialCarritos WHERE Email=? AND Completado=false");
            selectHistorial.setString(1, mail);
            rs = selectHistorial.executeQuery();
            while (rs.next()) {
                carrosIncompletos.add(rs.getString("CodigoCarrito"));
            }
            if (carrosIncompletos.size() <= 0) {
                carrosIncompletos = null;
            } 
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error obteniendo los codigos de los ultimos carros incompletos", ex);
            carrosIncompletos = null;
        } finally {
            cerrarConexionYStatement(conexion, selectHistorial);
            cerrarResultSet(rs);
        }
        return carrosIncompletos;
    }

    @Override
    public boolean newComment(Usuario user, String codigoProducto, String codigoComentario,
            String fechaHora, String comentario) {
        Connection conexion = null;
        PreparedStatement insert = null;
        boolean exito = false;
        try {
            conexion = pool.getConnection();
            insert = conexion.prepareStatement("INSERT INTO " + nameBD + ".Comentarios VALUES (?,?,?,?,?,?)");
            insert.setString(1, codigoComentario);
            insert.setString(2, fechaHora);
            insert.setString(3, codigoProducto);
            insert.setString(4, user.getMail());
            insert.setString(5, user.getNombre());
            insert.setString(6, comentario);

            int filasAfectadas = insert.executeUpdate();
            if (filasAfectadas == 1) {
                exito = true;
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error añadiendo nuevo comentario", ex);
        } finally {
            cerrarConexionYStatement(conexion, insert);
        }
        return exito;
    }

    @Override
    public boolean deleteComment(String codigoComentario) {
        Connection conexion = null;
        PreparedStatement delete = null;
        boolean exito = false;
        try {
            conexion = pool.getConnection();
            delete = conexion.prepareStatement("DELETE FROM " + nameBD + ".Comentarios WHERE CodigoComentario=?");
            delete.setString(1, codigoComentario);

            int filasAfectadas = delete.executeUpdate();
            if (filasAfectadas == 1) {
                exito = true;
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error borrando comentario", ex);
        } finally {
            cerrarConexionYStatement(conexion, delete);
        }
        return exito;
    }

    @Override
    public boolean updateComment(String codComentario, Comentario comentario) {
        Connection conexion = null;
        PreparedStatement update = null;
        boolean exito = false;
        try {
            conexion = pool.getConnection();
            update = conexion.prepareStatement("UPDATE " + nameBD + ".Comentarios SET FechaHora=?, CodigoProducto=?, "
                    + "Email=?, Nombre=?, Comentario=? WHERE CodigoComentario=?");
            update.setString(1, comentario.getFechaHora());
            update.setString(2, comentario.getCodigoProducto());
            update.setString(3, comentario.getEmail());
            update.setString(4, comentario.getNombre());
            update.setString(5, comentario.getComentario());
            update.setString(6, comentario.getCodigoComentario());

            int filasAfectadas = update.executeUpdate();          
            conexion.close();
            if (filasAfectadas == 1) {
                exito = true;
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "Error modificando comentario", ex);
        } finally{
            cerrarConexionYStatement(conexion, update);
        }
        return exito;
    }

    @Override
    public LinkedList<Comentario> getComentarios(String campo, String valor) {
        LinkedList<Comentario> comentarios = new LinkedList<Comentario>();
        Connection conexion = null;
        PreparedStatement select = null;
        ResultSet rs = null;        
        try {  
            conexion = pool.getConnection();
            select = conexion.prepareStatement("SELECT * FROM " + nameBD + ".Comentarios WHERE " + campo + "=? ORDER BY FechaHora DESC");
            select.setString(1, valor);
            rs = select.executeQuery();
            while (rs.next()) {
                Comentario comment = new Comentario(rs.getString("CodigoComentario"), rs.getDate("FechaHora").toString(),
                        rs.getTime("FechaHora").toString(), rs.getString("CodigoProducto"), rs.getString("Email"),
                        rs.getString("Nombre"), rs.getString("Comentario"));
                comentarios.add(comment);
            }
            if (comentarios.size() <= 0) {
                comentarios = null;
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error obteniendo los comentarios", ex);
            comentarios = null;
        } finally {
            cerrarConexionYStatement(conexion, select);
            cerrarResultSet(rs);
        }
        return comentarios;
    }

    @Override
    public Comentario getComment(String codComentario) {
        Comentario comment = null;
        Connection conexion = null;
        PreparedStatement select = null;
        ResultSet rs = null;
        try {
            conexion = pool.getConnection();
            select = conexion.prepareStatement("SELECT * FROM " + nameBD + ".Comentarios WHERE CodigoComentario=?");
            select.setString(1, codComentario);
            rs = select.executeQuery();            
            while (rs.next()) {
                comment = new Comentario(rs.getString("CodigoComentario"), rs.getDate("FechaHora").toString(), 
                        rs.getTime("FechaHora").toString(), rs.getString("CodigoProducto"),
                        rs.getString("Email"), rs.getString("Nombre"), rs.getString("Comentario"));
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error obteniendo comentario", ex);
        } finally {
            cerrarConexionYStatement(conexion, select);
            cerrarResultSet(rs);
        }
        return comment;
    }


    private void cerrarConexionYStatement(Connection conexion, Statement... statements) {
        try {
            conexion.close();
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error al cerrar una conexión a la base de datos", ex);
        } finally {
            for (Statement statement : statements) {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (SQLException ex) {
                        logger.log(Level.SEVERE, "Error al cerrar un statement", ex);
                    }
                }
            }
        }
    }

    private void cerrarResultSet(ResultSet... results) {
        for (ResultSet rs : results) {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, "Error al cerrar un resultset", ex);
                }
            }
        }
    }
}