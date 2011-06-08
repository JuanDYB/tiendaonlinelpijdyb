package modelo;

import java.io.Serializable;

/**
 * @author Juan Díez-Yanguas Barber
 */
public class Producto implements Serializable{
    private String codigo;
    private String nombre;
    private double precio;
    private int stock;
    private String desc;
    private String detalles;

    public Producto (){        
    }

    public Producto(String codigo, String nombre, double precio, int stock) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }
    
    public Producto(String codigo, String nombre, double precio, int stock, String desc, String detalles) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.desc = desc;
        this.detalles = detalles;
    }

    public String getDesc() {
        return desc;
    }

    public String getDetalles() {
        return detalles;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public int getStock() {
        return stock;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDisponibilidad (){
        if (stock > 0){
            return "Stock disponible";
        }else{
            return "Agotado";
        }
    }
}
