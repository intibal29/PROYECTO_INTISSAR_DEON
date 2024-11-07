package com.intissar.olimpiadas.model;

import java.util.Objects;

/**
 * Clase Equipo
 */
public class Equipo {
    private int id_equipo;
    private String nombre;
    private String iniciales;

    /**
     * Constructor con parámetros equipo
     *
     * @param id_equipo del equipo
     * @param nombre del equipo
     * @param iniciales del equipo
     */
    public Equipo(int id_equipo, String nombre, String iniciales) {
        this.id_equipo = id_equipo;
        this.nombre = nombre;
        this.iniciales = iniciales;
    }

    /**
     * Constructor vacío de equipo
     */
    public Equipo() {}

    /**
     * ToString de equipo
     *
     * @return descripción de equipo
     */
    @Override
    public String toString() {
        return nombre;
    }

    /**
     * Getter para el id del equipo
     *
     * @return id del equipo
     */
    public int getId_equipo() {
        return id_equipo;
    }

    /**
     * Setter para el id del equipo
     *
     * @param id_equipo nuevo id del equipo
     */
    public void setId_equipo(int id_equipo) {
        this.id_equipo = id_equipo;
    }

    /**
     * Getter para el nombre del equipo
     *
     * @return nombre del equipo
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Setter para el nombre del equipo
     *
     * @param nombre nuevo nombre del equipo
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Getter para las iniciales del equipo
     *
     * @return iniciales del equipo
     */
    public String getIniciales() {
        return iniciales;
    }

    /**
     * Setter para las iniciales del equipo
     *
     * @param iniciales nuevas iniciales del equipo
     */
    public void setIniciales(String iniciales) {
        this.iniciales = iniciales;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Equipo equipo = (Equipo) o;
        return id_equipo == equipo.id_equipo;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id_equipo);
    }

}
