package com.servidor.modelo;

import java.io.Serializable;
import java.util.List;

import com.servidor.excepciones.ReseñaExistenteException;
import com.servidor.excepciones.ReseñaNoEncontradaException;
import com.servidor.excepciones.SolicitudExistenteException;
import com.servidor.excepciones.SolicitudNoExistenteException;
import com.servidor.excepciones.UsuarioExistenteException;
import com.servidor.excepciones.UsuarioNoEncontradoException;
import com.servidor.util.UtilMarketPlace;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Comparator;


public class MarketPlace implements Serializable {
    private List<Vendedor> vendedores;
    private List<Solicitud> solicitudes;
    private List<Producto> productos;
    private Admin administrador;
    private UtilMarketPlace utilMarketPlace;
    private List<Reseña> reseñas;

    // Constructor
public MarketPlace(UtilMarketPlace utilMarketPlace) {
    this.administrador = new Admin("1", "Juana", "Arias", "123", "direccion", "contraseña");
    this.utilMarketPlace = utilMarketPlace;

    // Inicializar vendedores
    List<Vendedor> vendedoresTemp = utilMarketPlace.obtenerVendedores();
    this.vendedores = (vendedoresTemp != null && !vendedoresTemp.isEmpty()) ? vendedoresTemp : new ArrayList<>();

    // Inicializar solicitudes
    List<Solicitud> solicitudesTemp = utilMarketPlace.obtenerSolicitudes();
    this.solicitudes = (solicitudesTemp != null && !solicitudesTemp.isEmpty()) ? solicitudesTemp : new ArrayList<>();

    // Inicializar productos
    List<Producto> productosTemp = utilMarketPlace.obtenerProductos();
    this.productos = (productosTemp != null && !productosTemp.isEmpty()) ? productosTemp : new ArrayList<>();

    // Inicializar reseñas
    List<Reseña> reseñasTemp = utilMarketPlace.obtenerReseñas();
    this.reseñas = (reseñasTemp != null && !reseñasTemp.isEmpty()) ? reseñasTemp: new ArrayList<>();

    //LLAMAR METODO DE SERIALIZAR MODELO
}

    public void setUtilMarketPlace(UtilMarketPlace utilMarketPlace) {
        this.utilMarketPlace = utilMarketPlace;
        this.vendedores = utilMarketPlace.obtenerVendedores();
        this.solicitudes = utilMarketPlace.obtenerSolicitudes();
        this.productos = utilMarketPlace.obtenerProductos();
        this.reseñas = utilMarketPlace.obtenerReseñas();    
        }
    
    public List<Vendedor> getVendedores() {
        return vendedores;
    }

    public void setVendedores(List<Vendedor> vendedores) {
        this.vendedores = vendedores;
    }

    public Admin getAdministrador() {
        return administrador;
    }

    public void setAdministrador(Admin administrador) {
        this.administrador = administrador;
    }

    public void setSolicitudes(List<Solicitud> solicitudes) {
        this.solicitudes = solicitudes;
    }

    public List<Solicitud> getSolicitudes() {
        return solicitudes;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public List<Reseña> getReseñas() {
        return this.reseñas; 
    }

    public void setReseñas(List<Reseña> reseñas) {
        this.reseñas = reseñas;
    }

    //CRUD DE VENDEDOR, SOLITCITUD Y RESEÑA

    public void crearReseña(Reseña reseña) throws ReseñaExistenteException {
    boolean exito = utilMarketPlace.crearReseña(reseña);
    if (exito) {
        reseñas.add(reseña);}
    }

    public void eliminarReseña(String idReseña) throws ReseñaNoEncontradaException {
        boolean exito = utilMarketPlace.eliminarReseña(idReseña);
        if (exito) {
            for (Reseña reseña : reseñas) {
                if (reseña.getId().equals(idReseña)) {
                    reseñas.remove(reseña);
                    break;
                }
            }
        }
    }

    public void modificarReseña(Reseña resenaModificada) throws ReseñaNoEncontradaException {
        utilMarketPlace.modificarReseña(resenaModificada);
        for (Reseña reseña : reseñas) {
            if (reseña.getId().equals(resenaModificada.getId())) {
                int posicion = reseñas.indexOf(reseña);
                reseñas.set(posicion, resenaModificada);
                break;
            }
        }
    }
    
    public void crearVendedor(Vendedor vendedor) throws UsuarioExistenteException {
        boolean exito = utilMarketPlace.crearVendedor(vendedor);
        if (exito) {
            vendedores.add(vendedor);
        }
    }

    public void eliminarVendedor(String cedulaVendedor) throws UsuarioNoEncontradoException {
        boolean exito = utilMarketPlace.eliminarVendedor(cedulaVendedor);
        if (exito) {
            for (Vendedor vendedor : vendedores) {
                if (vendedor.getCedula().equals(cedulaVendedor)) {
                    vendedores.remove(vendedor);
                    break;
                }
            }
        }
    }

    public void modificarVendedor(Vendedor vendedorModificado) throws UsuarioNoEncontradoException {
        utilMarketPlace.modificarVendedor(vendedorModificado);
        for (Vendedor vendedor : vendedores) {
            if (vendedor.getCedula().equals(vendedorModificado.getCedula())) {
                int posicion = vendedores.indexOf(vendedor);
                vendedores.set(posicion, vendedorModificado);
                break;
            }
        }
    }

    public List<Solicitud> obtenerSolicitudes() {
        return utilMarketPlace.obtenerSolicitudes();
    }

    public List<Vendedor> obtenerVendedores() {
        return utilMarketPlace.obtenerVendedores();
    }

    public List<Producto> obtenerProductos() {
        return utilMarketPlace.obtenerProductos();
    }

    public List<Reseña> obtenerReseñas(){
        return utilMarketPlace.obtenerReseñas();
    }

    public void crearSolicitud(Solicitud solicitud) throws SolicitudExistenteException {
        boolean exito = utilMarketPlace.crearSolicitud(solicitud);
        if (exito) {
            solicitudes.add(solicitud);
        }
    }

    public void eliminarSolicitud(Vendedor emisor, Vendedor receptor) throws SolicitudNoExistenteException {
        boolean exito = utilMarketPlace.eliminarSolicitud(emisor, receptor);
        if (exito) {
            for (Solicitud solicitud : solicitudes) {
                if (solicitud.getEmisor().equals(emisor) && solicitud.getReceptor().equals(receptor)) {
                    solicitudes.remove(solicitud);
                    break;
                }
            }
        }
    }

    public void cambiarEstadoSolicitud(Solicitud solicitud1, EstadoSolicitud nuevoEstado, Vendedor vendedor) {
        utilMarketPlace.cambiarEstadoSolicitud(solicitud1, nuevoEstado, vendedor);

    }

    public void exportarEstadisticas(String ruta, String nombreUsuario, String fechaInicio, String fechaFin,
            String idVendedor) {
        utilMarketPlace.exportarEstadisticas(ruta, nombreUsuario, fechaInicio, fechaFin, idVendedor);
    }

    public List<Vendedor> sugerirContactos(Vendedor vendedor) {
        List<Vendedor> sugerencias = new ArrayList<>();
        List<Vendedor> contactosDirectos = vendedor.getRedDeContactos();
        // Usar un Set para evitar duplicados
        Set<Vendedor> contactosSugeridos = new HashSet<>();
        // Explorar la red de contactos de los contactos directos
        for (Vendedor contacto : contactosDirectos) {
            for (Vendedor contactoDeContacto : contacto.getRedDeContactos()) {
                // Agregar solo si no es el vendedor original y no está en su lista de contactos
                if (!contactoDeContacto.equals(vendedor) && !contactosDirectos.contains(contactoDeContacto)) {
                    contactosSugeridos.add(contactoDeContacto);
                }
            }
        }
        // Convertir el Set a una lista
        sugerencias.addAll(contactosSugeridos);
        return sugerencias;
    }

    public List<Comentario> obtenerComentariosDeProducto(Producto producto) {

        return utilMarketPlace.obtenerComentariosDeProducto(producto);
    }

    public List<Comentario> filtrarComentariosPorContactos(List<Comentario> comentarios, Vendedor vendedor) {
        List<Comentario> comentariosFiltrados = new ArrayList<>();
        List<Vendedor> contactosDirectos = vendedor.getRedDeContactos();

        for (Comentario comentario : comentarios) {
            // Suponiendo que el comentario tiene un método getAutor() que devuelve el
            // vendedor que hizo el comentario
            if (contactosDirectos.contains(comentario.getAutor())) {
                comentariosFiltrados.add(comentario);
            }
        }

        return comentariosFiltrados;
    }

    // COMENTARIOS DE CONTACTOS

    public List<Comentario> verComentariosDeContactos(Vendedor vendedor, Producto producto) {
        List<Comentario> comentarios = obtenerComentariosDeProducto(producto);
        return filtrarComentariosPorContactos(comentarios, vendedor);
    }

    public List<Producto> organizarProductosPorFecha() {
        List<Producto> productosOrdenados = new ArrayList<>(productos); // Crear una copia de la lista de productos
        productosOrdenados.sort(Comparator.comparing(Producto::getFechaPublicacion)); // Ordenar por fecha de
                                                                                      // publicación
        return productosOrdenados; // Retornar la lista ordenada
    }

    public List<Vendedor> verMeGusta(Producto producto, Vendedor vendedor) {
        List<Vendedor> listaMeGusta = new ArrayList<>();
        List<Vendedor> contactosDirectos = vendedor.getRedDeContactos();
        Vendedor vendedorDelProducto = utilMarketPlace.obtenerAutorDeProducto(producto.getId());

        // Verificar si el vendedor original está en la red de contactos
        if (contactosDirectos.contains(vendedorDelProducto) ||
                producto.getVendedoresQueDieronLike().contains(vendedor.getId()) ||
                contactosDirectos.stream()
                        .anyMatch(contacto -> producto.getVendedoresQueDieronLike().contains(contacto.getId()))) {
            listaMeGusta = obtenerMeGusta(producto);
        }

        return listaMeGusta; // Retornar la lista de "me gusta" según las reglas de visibilidad
    }

    public List<Vendedor> obtenerMeGusta(Producto producto) {
        List<Vendedor> vendedores = new ArrayList<>();
        Set<String> vendedoresIds = producto.getVendedoresQueDieronLike();

        for (String id : vendedoresIds) {
            // Obtener el vendedor por su ID y agregarlo a la lista
            Vendedor vendedor = utilMarketPlace.obtenerVendedorPorId(id); // Asumiendo que existe un método para obtener
                                                                          // un vendedor por ID
            if (vendedor != null) { // Verificar que el vendedor no sea nulo
                vendedores.add(vendedor);
            }
        }

        return vendedores; // Retornar la lista de vendedores que dieron "me gusta"
    }

    public void darLike(Vendedor vendedor, Producto producto) {
        // Llamar al método darMeGusta de la clase Producto
        producto.darLike(vendedor.getId());
    }

    public String iniciarSesion(String cedula, String contraseña){
        try {
			return utilMarketPlace.iniciarSesion(cedula,contraseña);
		} catch (UsuarioNoEncontradoException e) {
			e.printStackTrace();
            return null;
		}
    }

    public UtilMarketPlace getUtilMarketPlace() {
        return utilMarketPlace;
    }
}
