package modelo;

import java.io.Serializable;

/**
 * @author Juan Díez-Yanguas Barber
 */
public class Comentario implements Serializable{
    String codigoComentario;
    String fecha;
    String hora;
    String codigoProducto;
    String Email;
    String nombre;
    String comentario;

    public Comentario(String codigoComentario, String fecha, String hora, String codigoProducto, String Email, String nombre, String comentario) {
        this.codigoComentario = codigoComentario;
        this.fecha = fecha;
        this.hora = hora;
        this.codigoProducto = codigoProducto;
        this.Email = Email;
        this.nombre = nombre;
        this.comentario = comentario;
    }

    public String getEmail() {
        return Email;
    }

    public String getCodigoComentario() {
        return codigoComentario;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public String getComentario() {
        return comentario;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }

    public String getNombre() {
        return nombre;
    }
    
    public String getFechaHora (){
        return fecha + " " + hora;
    }
}
