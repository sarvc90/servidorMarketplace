package com.servidor.modelo;




public enum Categoria {
    VEHICULOS, 
    TECNOLOGIA,
    HOGAR,
    DEPORTES,
    BELLEZA,
    JUGUETES,
    SALUD,
    ROPA;

    public static Categoria fromString(String categoria) {
        try {
            return Categoria.valueOf(categoria.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // O lanza una excepción personalizada si prefieres
        }
    }
}
