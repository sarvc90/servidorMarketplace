package com.servidor.modelo;

import java.util.ArrayList;
import java.util.List;

import com.servidor.excepciones.ProductoNoEncontradoException;
import com.servidor.excepciones.ProductoYaExisteException;
import com.servidor.util.UtilVendedor;

public class Vendedor extends Persona {
    private List<Producto> publicaciones;
    private List<Vendedor> redDeContactos;
    private UtilVendedor utilVendedor;
    private List<Integer> calificaciones;
    private int contadorCalificaciones;
    private double promedioCalificaciones;
    
    


    public Vendedor() {
        super();
        this.publicaciones = new ArrayList<>(); // Inicializar como lista vacía
        this.redDeContactos = new ArrayList<>(); // Inicializar como lista vacía
        this.utilVendedor = UtilVendedor.getInstance();
        this.calificaciones = new ArrayList<>(); // Inicializar lista de calificaciones
        this.contadorCalificaciones = 0; // Inicializar contador de calificaciones
        this.promedioCalificaciones = 0.0; // Inicializar promedio de calificaciones
       
    }

    public Vendedor(String id, String nombre, String apellido, String cedula, String direccion, String contraseña,
            List<Producto> publicaciones, List<Vendedor> redDeContactos, List<Integer> calificaciones,
            int contadorCalificaciones, double promedioCalificaciones) {
        super(id, nombre, apellido, cedula, direccion, contraseña);
        this.publicaciones = (publicaciones != null) ? publicaciones : new ArrayList<>(); // Asignar lista o vacía
        this.redDeContactos = (redDeContactos != null) ? redDeContactos : new ArrayList<>(); // Asignar lista o vacía
        this.utilVendedor = UtilVendedor.getInstance();
        this.calificaciones = (calificaciones != null) ? calificaciones : new ArrayList<>(); // Inicializar lista de
                                                                                             // calificaciones
        this.contadorCalificaciones = contadorCalificaciones; // Inicializar contador de calificaciones
        this.promedioCalificaciones = promedioCalificaciones; // Inicializar promedio de calificaciones
        inicializarRedDeContactos();
    }

    public List<Producto> getPublicaciones() {
        return publicaciones;
    }

    public void setPublicaciones(List<Producto> publicaciones) {
        this.publicaciones = publicaciones;
    }

    public void setUtilVendedor(UtilVendedor utilVendedor) {
        this.utilVendedor = utilVendedor;
    }

    public UtilVendedor getUtilVendedor() {
        return utilVendedor;
    }

    public List<Vendedor> getRedDeContactos() {
        return redDeContactos;
    }

    public void setRedDeContactos(List<Vendedor> redDeContactos) {
        this.redDeContactos = redDeContactos;
    }

    @Override
    public String toString() {
        return "Vendedor [id=" + id + ", nombre=" + nombre + ", apellido=" + apellido + ", cedula=" + cedula
                + ", publicaciones=" + publicaciones + ", direccion=" + direccion + ", redDeContactos=" + redDeContactos
                + ", contraseña=" + contraseña + ", utilVendedor=" + utilVendedor + "]";
    }

    public void crearProducto(Producto producto) throws ProductoYaExisteException {
        boolean exito = utilVendedor.crearProducto(producto, this);
        if (exito) {
            publicaciones.add(producto);

        }
    }

    public void eliminarProducto(String productoId) throws ProductoNoEncontradoException {
        boolean exito = utilVendedor.eliminarProducto(productoId);
        if (exito) {
            // Buscar el producto en la lista de publicaciones y removerlo
            for (Producto producto : publicaciones) {
                if (producto.getId().equals(productoId)) {
                    publicaciones.remove(producto);
                    break;
                }
            }

        }
    }

    public void modificarProducto(Producto productoModificado) {
        boolean exito = utilVendedor.modificarProducto(productoModificado);
        // Buscar el producto en la lista de publicaciones y removerlo
        if (exito) {
            for (Producto producto : publicaciones) {
                if (producto.getId().equals(productoModificado.getId())) {
                    int posicion = publicaciones.indexOf(producto);
                    publicaciones.set(posicion, productoModificado);
                    break;
                }
            }
        }
    }

    private void inicializarRedDeContactos() {
        List<Solicitud> solicitudesAceptadas = utilVendedor.obtenerSolicitudesAceptadas(this);
        for (Solicitud solicitud : solicitudesAceptadas) {
            Vendedor receptor = solicitud.getReceptor();
            if (!redDeContactos.contains(receptor)) {
                redDeContactos.add(receptor);
            }

        }
    }

    public List<Solicitud> obtenerSolicitudesPendientes() {
        return utilVendedor.obtenerSolicitudesPendientes(this);
    }

    public List<Solicitud> obtenerSolicitudesRechazadas() {
        return utilVendedor.obtenerSolicitudesRechazadas(this);
    }

    public List<Solicitud> obtenerSolicitudesAceptadas() {
        return utilVendedor.obtenerSolicitudesAceptadas(this);
    }

    public void calificar(int calificacion) {
        if (calificacion < 1 || calificacion > 5) {
            throw new IllegalArgumentException("La calificación debe estar entre 1 y 5.");
        }
        calificaciones.add(calificacion); // Agregar la calificación a la lista
        contadorCalificaciones++; // Incrementar el contador de calificaciones
        calcularPromedio(); // Recalcular el promedio
    }

    private void calcularPromedio() {
        // Calcular promedio sin OptionalDouble
        if (!calificaciones.isEmpty()) {
            int suma = 0;
            for (int calificacion : calificaciones) {
                suma += calificacion;
            }
            promedioCalificaciones = (double) suma / calificaciones.size();
            contadorCalificaciones = calificaciones.size();
        } else {
            promedioCalificaciones = 0.0;
            contadorCalificaciones = 0;
        }
    }

    public double getPromedioCalificaciones() {
        return promedioCalificaciones; // Devuelve el promedio de calificaciones
    }

    public int getContadorCalificaciones() {
        return contadorCalificaciones; // Devuelve el número total de calificaciones
    }

    public List<Integer> getCalificaciones() {
        return new ArrayList<>(calificaciones); // Devuelve una copia de la lista de calificaciones
    }
}

