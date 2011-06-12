package modelo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Juan Díez-Yanguas Barber
 */
public class Carrito implements Serializable{
    private String codigo;
    private String user;
    private Map <String, Integer> articulos = new HashMap <String, Integer>();
    private double precio;
    private String fecha;
    private String hora;
    private String formaPago;

    public Carrito (){
    }

    public Carrito(String codigo, String user) {
        this.codigo = codigo;
        this.user = user;
    }

    public Carrito(String codigo, String user, double precio) {
        this.codigo = codigo;
        this.user = user;
        this.precio = precio;
    }

    public Carrito(String codigo, String user, double precio, String fecha, String hora, String formaPago) {
        this.codigo = codigo;
        this.user = user;
        this.precio = precio;
        this.fecha = fecha;
        this.hora = hora;
        this.formaPago = formaPago;
    }  
    
    public void addProduct (String newProd, int cantidad, double prodPrice){
        if (this.articulos.containsKey(newProd) == true){
            int newCant = this.articulos.get(newProd) + cantidad;
            this.articulos.put(newProd, newCant);
            this.precio += (cantidad * prodPrice);
        }else{
            this.articulos.put(newProd, cantidad);
            this.precio += (prodPrice * cantidad);
        }
    }

    public boolean editCant (String prod, int cantidad, double prodPrice){
        if (this.articulos.containsKey(prod) == true){
            if (cantidad == 0){
                removeProd (prod, prodPrice);
                return true;
            }
            int cantidadActual = this.articulos.get(prod);
            this.precio += ((cantidad-cantidadActual) * prodPrice);
            this.articulos.put(prod, cantidad);
            return true;
        }else{
            return false;
        }
    }

    public void removeProd(String prod, double prodPrice){
        this.precio -= (prodPrice * this.articulos.get(prod));
        this.articulos.remove(prod);
    }

    public void delProduct (String prod){
        this.articulos.remove(prod);
    }

    public Map <String, Integer> getArticulos (){
        return articulos;
    }

    public int getLenght (){
        return articulos.size();
    }

    public double getPrecio (){
        return precio;
    }

    public String getUser() {
        return user;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setPrecio (double price){
        this.precio = price;
    }

    public void setArticulos(Map<String, Integer> articulos) {
        this.articulos = articulos;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }    
}