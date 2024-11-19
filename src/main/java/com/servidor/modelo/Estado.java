package com.servidor.modelo;



public enum Estado {
    VENDIDO,
    PUBLICADO,
    CANCELADO;
    public static Estado fromString(String estado) {
        try {
            return Estado.valueOf(estado.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // O lanza una excepción personalizada si prefieres
        }
    }
}
