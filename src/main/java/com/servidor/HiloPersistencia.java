package com.servidor;

import java.util.List;

class HiloPersistencia extends Thread {

    private List<Object> objetos;

    public HiloPersistencia(List<Object> objetos) {
        this.objetos = objetos;
    }


}