package com.servidor.modelo;

import java.io.Serializable;
import java.util.List;

import com.servidor.util.UtilMarketPlace;

public class MarketPlace implements Serializable{
    private List<Vendedor> vendedores;
    private List<Solicitud> solicitudes;
    private List<Producto> productos;
    private Admin administrador;
    private UtilMarketPlace utilMarketPlace;

    // Constructor
    public MarketPlace() {
        this.administrador = new Admin("1", "Juana", "Arias", "123", "direccion", "contraseña");
        this.utilMarketPlace = utilMarketPlace.getInstance();
        this.vendedores = utilMarketPlace.obtenerVendedores();
        this.solicitudes = utilMarketPlace.obtenerSolicitudes();
        this.productos = utilMarketPlace.obtenerProductos();
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

    public void setSolicitudes(List<Solicitud> solicitudes){
        this.solicitudes = solicitudes;
    }

    public List<Solicitud> getSolicitudes(){
        return solicitudes;
    }

    
    public void setProductos(List<Producto> productos){
        this.productos = productos;
    }

    public List<Producto> getProductos(){
        return productos;
    }

//Chat?
}
