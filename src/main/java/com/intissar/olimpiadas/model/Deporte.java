package com.intissar.olimpiadas.model;

import java.util.Objects;

/**
 * Clase Deporte
 */
public class Deporte {
    private int id_deporte;
    private String nombre;

    /**
     * Constructor con parámetros de deporte
     *
     * @param id_deporte del deporte
     * @param nombre del deporte
     */
    public Deporte(int id_deporte, String nombre) {
        this.id_deporte = id_deporte;
        this.nombre = nombre;
    }

    /**
     * Constructor vacío de deporte
     */
    public Deporte() {}

    /**
     * ToString de deporte
     *
     * @return descripción de deporte
     */
    @Override
    public String toString() {
        return nombre;
    }

    /**
     * Getter para el id del deporte
     *
     * @return id del deporte
     */
    public int getId_deporte() {
        return id_deporte;
    }

    /**
     * Setter para el id del deporte
     *
     * @param id_deporte nuevo id del deporte
     */
    public void setId_deporte(int id_deporte) {
        this.id_deporte = id_deporte;
    }

    /**
     * Getter para el nombre del deporte
     *
     * @return nombre del deporte
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Setter para el nombre del deporte
     *
     * @param nombre nuevo nombre del deporte
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Deporte deporte = (Deporte) o;
        return id_deporte == deporte.id_deporte;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id_deporte);
    }

}
