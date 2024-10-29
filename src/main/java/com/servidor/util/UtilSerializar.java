package com.servidor.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.servidor.modelo.MarketPlace;
import com.servidor.modelo.Producto;
import com.servidor.modelo.Solicitud;
import com.servidor.modelo.Vendedor;

public class UtilSerializar implements Serializable {
    private static UtilSerializar instancia;
    private UtilProperties utilProperties;
    private UtilLog utilLog;
    private UtilPersistencia utilPersistencia;

    private UtilSerializar() {
        this.utilProperties = utilProperties.getInstance();
        this.utilLog = utilLog.getInstance();
        this.utilPersistencia = utilPersistencia.getInstance();
    }

    // Método para guardar el modelo serializado en un único archivo
    public void guardarModeloSerializadoBin(Object modelo) {
        String rutaArchivo = utilProperties.obtenerPropiedad("rutaModeloSerializado.bin");
        new SerializarTarea(modelo, rutaArchivo, false).start();
    }

    public UtilSerializar getInstance() {
        if (instancia == null) {
            instancia = new UtilSerializar();
        }
        return instancia;
    }

    public void guardarModeloSerializadoXML(MarketPlace modelo) {
        String rutaArchivo = utilProperties.obtenerPropiedad("rutaModeloSerializado.xml");
        new SerializarTarea(modelo, rutaArchivo, true).start();
    }

    // Método para cargar el modelo desde el archivo
    public Object cargarModeloSerializadoBin() {
        String rutaArchivo = utilProperties.obtenerPropiedad("rutaModeloSerializado.bin");
        List<Object> lista = new ArrayList<>();
        new DeserializarTarea(rutaArchivo, false, lista).start();
        return lista.get(0);
    }

    public MarketPlace cargarModeloSerializadoDesdeXML() {
        String rutaArchivo = utilProperties.obtenerPropiedad("rutaModeloSerializado.xml");
        List<Object> lista = new ArrayList<>();
        new DeserializarTarea(rutaArchivo, true, lista).start();
        return (MarketPlace) lista.get(0);
    }

    public void actualizarSerializacionVendedores() {
        List<Vendedor> listaVendedores = utilPersistencia.leerVendedoresDesdeArchivo();
        serializarLista(listaVendedores, true);
        serializarLista(listaVendedores, false);
        utilLog.escribirLog("Serialización de vendedores actualizada correctamente.", Level.INFO);
    }

    public void actualizarSerializacionProductos() {
        List<Producto> listaProductos = utilPersistencia.leerProductosDesdeArchivo();
        serializarLista(listaProductos, false);
        serializarLista(listaProductos, true);
        utilLog.escribirLog("Serialización de productos actualizada correctamente.", Level.INFO);
    }

    public void actualizarSerializacionSolicitudes() {
        List<Solicitud> listaSolicitudes = utilPersistencia.leerSolicitudesDesdeArchivo();
        serializarLista(listaSolicitudes, false);
        serializarLista(listaSolicitudes, true);
        utilLog.escribirLog("Serialización de solicitudes actualizada correctamente.", Level.INFO);
    }

    public void serializarLista(List<?> lista, boolean esXML) {
        String ruta;
        if (lista.isEmpty()) {
            utilLog.escribirLog("La lista está vacía, no se puede serializar.", Level.WARNING);
            return;
        }

        Object primerElemento = lista.get(0);
        if (primerElemento instanceof Producto) {
            ruta = esXML ? utilProperties.obtenerPropiedad("rutaProductos.xml")
                    : utilProperties.obtenerPropiedad("rutaProductos.bin");
        } else if (primerElemento instanceof Vendedor) {
            ruta = esXML ? utilProperties.obtenerPropiedad("rutaVendedores.xml")
                    : utilProperties.obtenerPropiedad("rutaVendedores.bin");
        } else if (primerElemento instanceof Solicitud) {
            ruta = esXML ? utilProperties.obtenerPropiedad("rutaSolicitudes.xml")
                    : utilProperties.obtenerPropiedad("rutaSolicitudes.bin");
        } else {
            utilLog.escribirLog("Tipo de lista no reconocido para serialización.", Level.SEVERE);
            return;
        }

        new SerializarTarea(lista, ruta, esXML).start();
    }

    public void serializarObjeto(Object obj, String ruta, boolean esXML) {
        new SerializarTarea(obj, ruta, esXML).start();
    }

    public List<Producto> deserializarProductos(boolean esXML) {
        String ruta = esXML ? utilProperties.obtenerPropiedad("rutaProductos.xml")
                : utilProperties.obtenerPropiedad("rutaProductos.bin");
        List<Object> lista = new ArrayList<>();
        new DeserializarTarea(ruta, esXML, lista).start();
        List<Producto> productos = new ArrayList<>();
        for (Object obj : lista) {
            productos.add((Producto) obj);
        }
        return productos;
    }

    public List<Vendedor> deserializarVendedores(boolean esXML) {
        String ruta = esXML ? utilProperties.obtenerPropiedad("rutaVendedores.xml")
                : utilProperties.obtenerPropiedad("rutaVendedores.bin");
        List<Object> lista = new ArrayList<>();
        new DeserializarTarea(ruta, esXML, lista).start();
        List<Vendedor> vendedores = new ArrayList<>();
        for (Object obj : lista) {
            vendedores.add((Vendedor) obj);
        }
        return vendedores;
    }

    public List<Solicitud> deserializarSolicitudes(boolean esXML) {
        String ruta = esXML ? utilProperties.obtenerPropiedad("rutaSolicitudes.xml")
                : utilProperties.obtenerPropiedad("rutaSolicitudes.bin");
        List<Object> lista = new ArrayList<>();
        new DeserializarTarea(ruta, esXML, lista).start();
        List<Solicitud> solicitudes = new ArrayList<>();
        for (Object obj : lista) {
            solicitudes.add((Solicitud) obj);
        }
        return solicitudes;
    }
}
