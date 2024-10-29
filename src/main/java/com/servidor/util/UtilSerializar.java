package com.servidor.util;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.servidor.modelo.MarketPlace;
import com.servidor.modelo.Producto;
import com.servidor.modelo.Solicitud;
import com.servidor.modelo.Vendedor;

public class UtilSerializar implements Serializable{
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
        String rutaArchivo = utilProperties.obtenerPropiedad("rutaModeloSerializado.bin"); // Define el nombre del archivo

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(rutaArchivo))) {
            oos.writeObject(modelo);
            oos.close();
            utilLog.escribirLog("Modelo guardado exitosamente en: " + rutaArchivo, Level.INFO);
        } catch (IOException e) {
            utilLog.escribirLog("Error al guardar el modelo: " + e.getMessage(), Level.SEVERE);
        }
    }

    public UtilSerializar getInstance() {
        if (instancia == null) {
            instancia = new UtilSerializar();
        }
        return instancia;
    }

    public void guardarModeloSerializadoXML(MarketPlace modelo) {
        String rutaArchivo = utilProperties.obtenerPropiedad("rutaModeloSerializado.xml");
        try {
            JAXBContext contexto = JAXBContext.newInstance(MarketPlace.class);
            Marshaller marshaller = contexto.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Guardar el modelo en un archivo XML
            marshaller.marshal(modelo, new File(rutaArchivo));

            utilLog.logInfo("Modelo serializado a XML y guardado en: " + rutaArchivo);
        } catch (JAXBException e) {
            utilLog.logSevere("Error al serializar el modelo a XML: " + e.getMessage());
        }
    }

    // Método para cargar el modelo desde el archivo
    public Object cargarModeloSerializadoBin() {
        String rutaArchivo = utilProperties.obtenerPropiedad("rutaModeloSerializado.bin"); // Define el nombre del archivo
        Object modelo = null;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(rutaArchivo))) {
            modelo = ois.readObject();
            utilLog.escribirLog("Modelo cargado exitosamente desde: " + rutaArchivo, Level.INFO);
        } catch (IOException | ClassNotFoundException e) {
            utilLog.escribirLog("Error al cargar el modelo: " + e.getMessage(), Level.SEVERE);
        }

        return modelo;
    }

    public MarketPlace cargarModeloSerializadoDesdeXML() {
        String rutaArchivo = utilProperties.obtenerPropiedad("rutaModeloSerializado.xml");
        MarketPlace modelo = null;
        try {
            JAXBContext contexto = JAXBContext.newInstance(MarketPlace.class);
            Unmarshaller unmarshaller = contexto.createUnmarshaller();

            // Cargar el modelo desde el archivo XML
            modelo = (MarketPlace) unmarshaller.unmarshal(new File(rutaArchivo));

            utilLog.logInfo("Modelo cargado desde XML: " + rutaArchivo);
        } catch (JAXBException e) {
            utilLog.logSevere("Error al cargar el modelo desde XML: " + e.getMessage());
        }
        return modelo;
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
            ruta = esXML ? utilProperties.obtenerPropiedad("rutaProductos.xml") : utilProperties.obtenerPropiedad("rutaProductos.bin");
        } else if (primerElemento instanceof Vendedor) {
            ruta = esXML ? utilProperties.obtenerPropiedad("rutaVendedores.xml")
                    : utilProperties.obtenerPropiedad("rutaVendedores.bin");
        } else {
            utilLog.escribirLog("Tipo de lista no reconocido para serialización.", Level.SEVERE);
            return;
        }

        try (FileOutputStream fos = new FileOutputStream(ruta)) {
            if (esXML) {
                XMLEncoder encoder = new XMLEncoder(fos);
                for (Object obj : lista) {
                    encoder.writeObject(obj);
                }
                encoder.close();
            } else {
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                for (Object obj : lista) {
                    oos.writeObject(obj);
                }
                oos.close();
            }
            utilLog.escribirLog("Lista serializada exitosamente a " + ruta, Level.INFO);
        } catch (IOException e) {
            utilLog.escribirLog("Error al serializar la lista: " + ruta, Level.SEVERE);
        }
    }
    public void serializarObjeto(Object obj, String ruta, boolean esXML) {
        try (FileOutputStream fos = new FileOutputStream(ruta)) {
            if (esXML) {
                XMLEncoder encoder = new XMLEncoder(fos);
                encoder.writeObject(obj);
                encoder.close();
            } else {
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(obj);
                oos.close();
            }
            utilLog.escribirLog("Objeto serializado exitosamente a " + ruta, Level.INFO);
        } catch (IOException e) {
            utilLog.escribirLog("Error al serializar el objeto: " + ruta, Level.SEVERE);
        }
    }
    public List<Producto> deserializarProductos(boolean esXML) {
        List<Producto> productos = new ArrayList<>();
        String ruta = esXML ? utilProperties.obtenerPropiedad("ruta.productos.xml")
                : utilProperties.obtenerPropiedad("ruta.productos.bin");

        try (FileInputStream fis = new FileInputStream(ruta)) {
            if (esXML) {
                XMLDecoder decoder = new XMLDecoder(fis);
                while (true) {
                    try {
                        Object obj = decoder.readObject();
                        if (obj instanceof Producto) {
                            productos.add((Producto) obj);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        break; // Fin del archivo
                    }
                }
                decoder.close();
            } else {
                ObjectInputStream ois = new ObjectInputStream(fis);
                while (true) {
                    try {
                        Object obj = ois.readObject();
                        if (obj instanceof Producto) {
                            productos.add((Producto) obj);
                        }
                    } catch (EOFException e) {
                        break; // Fin del archivo
                    }
                }
                ois.close();
            }
            utilLog.escribirLog("Productos deserializados exitosamente desde " + ruta, Level.INFO);
        } catch (IOException | ClassNotFoundException e) {
            utilLog.escribirLog("Error al deserializar productos desde: " + ruta, Level.SEVERE);
        }
        return productos;
    }
    public List<Vendedor> deserializarVendedores(boolean esXML) {
        List<Vendedor> vendedores = new ArrayList<>();
        String ruta = esXML ? utilProperties.obtenerPropiedad("ruta.vendedores.xml")
                : utilProperties.obtenerPropiedad("ruta.vendedores.bin");

        try (FileInputStream fis = new FileInputStream(ruta)) {
            if (esXML) {
                XMLDecoder decoder = new XMLDecoder(fis);
                while (true) {
                    try {
                        Object obj = decoder.readObject();
                        if (obj instanceof Vendedor) {
                            vendedores.add((Vendedor) obj);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        break; // Fin del archivo
                    }
                }
                decoder.close();
            } else {
                ObjectInputStream ois = new ObjectInputStream(fis);
                while (true) {
                    try {
                        Object obj = ois.readObject();
                        if (obj instanceof Vendedor) {
                            vendedores.add((Vendedor) obj);
                        }
                    } catch (EOFException e) {
                        break; // Fin del archivo
                    }
                }
                ois.close();
            }
            utilLog.escribirLog("Vendedores deserializados exitosamente desde " + ruta, Level.INFO);
        } catch (IOException | ClassNotFoundException e) {
            utilLog.escribirLog("Error al deserializar vendedores desde: " + ruta, Level.SEVERE);
        }
        return vendedores;
    }
}
