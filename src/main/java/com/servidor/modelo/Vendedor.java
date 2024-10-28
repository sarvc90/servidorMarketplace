package com.servidor.modelo;

import java.util.List;

import com.servidor.util.UtilVendedor;

public class Vendedor extends Persona {
    private List<Producto> publicaciones;
    private List<Vendedor> redDeContactos;
    private UtilVendedor utilVendedor;

    public Vendedor() {
        super();
    }

    public Vendedor(String id, String nombre, String apellido, String cedula, String direccion, String contraseña,
            List<Producto> publicaciones, List<Vendedor> redDeContactos) {
        super(id, nombre, apellido, cedula, direccion, contraseña);
        this.publicaciones = publicaciones;
        this.redDeContactos = redDeContactos;
        this.utilVendedor = utilVendedor.getInstance();
    }

    public List<Producto> getPublicaciones() {
        return publicaciones;
    }

    public void setPublicaciones(List<Producto> publicaciones) {
        this.publicaciones = publicaciones;
    }

    public List<Vendedor> getRedDeContactos() {
        return redDeContactos;
    }

    public void setRedDeContactos(List<Vendedor> redDeContactos) {
        this.redDeContactos = redDeContactos;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Productos Propios (Ordenados por Fecha de Publicación):\n");
        List<Producto> productosPropiosOrdenados = getPublicaciones();
        for (Producto producto : productosPropiosOrdenados) {
            sb.append(producto.toString()).append("\n"); // Asumiendo que Producto tiene un método toString
        }
        return sb.toString();
    }

}
