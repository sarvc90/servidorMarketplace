package com.servidor.util;
import java.io.IOException;

import com.servidor.excepciones.UsuarioExistenteException;
import com.servidor.excepciones.UsuarioNoEncontradoException;
import com.servidor.modelo.Vendedor;

public class UtilMarketPlace {
    private static UtilMarketPlace instancia;
    private UtilLog utilLog;
    private UtilPersistencia utilPersistencia;
    private UtilProperties utilProperties;
    private UtilRespaldo utilRespaldo;
    private UtilSerializar utilSerializar;
    private UtilHilos utilHilos;
    

    private UtilMarketPlace(){
        this.utilLog = utilLog.getInstance();
        this.utilProperties = utilProperties.getInstance();
        this.utilPersistencia = utilPersistencia.getInstance();
        this.utilRespaldo = utilRespaldo.getInstance();
        this.utilSerializar = utilSerializar.getInstance();
        this.utilHilos = utilHilos.getInstance();
    }

    public UtilMarketPlace getInstance() {
        if (instancia == null) {
            instancia = new UtilMarketPlace();
        }
        return instancia;
    }

    public void crearVendedor(Vendedor vendedor) throws UsuarioExistenteException{

            if (utilPersistencia.buscarVendedorPorCedula(vendedor.getCedula())==null){
                utilPersistencia.guardarVendedorEnArchivo(vendedor);
                utilSerializar.actualizarSerializacionVendedores();
                utilLog.registrarAccion("Vendedor nuevo ", "Registro exitoso.", "Registro.");
            } else {
                //Excepcion de usuario no encontrado
                utilLog.registrarAccion("Desconocido ", "Registro fallido. ", "Registro");
                throw new UsuarioExistenteException();
            }
        
    }

    public void eliminarVendedor(String cedulaVendedor){
        utilPersistencia.eliminarVendedor(cedulaVendedor);
        utilSerializar.actualizarSerializacionVendedores();
        utilLog.registrarAccion("Vendedor eliminado", " El vendedor con cédula " + cedulaVendedor + " ha sido eliminado. ", " Eliminación."); 
    }

    public void modificarVendedor(Vendedor vendedorModificado){
        utilPersistencia.modificarVendedor(vendedorModificado);
        utilSerializar.actualizarSerializacionVendedores();
        utilLog.registrarAccion(("Vendedor " + vendedorModificado.getNombre() + " modificado. ")," Se modifica el vendedor "," Modificar.");
    }

    


    //Mirar logica de marketplace para ver como se va a obtener la lusta
    //si es un metodo de aqui mismo o como
}
