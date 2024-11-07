package com.intissar.olimpiadas.model;

import java.util.Objects;

/**
 * Clase Olimpiada
 */
public class Olimpiada {
    private int id_olimpiada;
    private String nombre;
    private int anio;
    private SeasonCategory temporada; // Enum
    private String ciudad;

    /**
     * Constructor con parámetros olimpiada
     *
     * @param id_olimpiada de la olimpiada
     * @param nombre de la olimpiada
     * @param anio de la olimpiada
     * @param temporada de la olimpiada
     * @param ciudad de la olimpiada
     */
    public Olimpiada(int id_olimpiada, String nombre, int anio, String temporada, String ciudad) {
        this.id_olimpiada = id_olimpiada;
        this.nombre = nombre;
        this.anio = anio;
        this.temporada = getSeasonCategory(temporada);
        this.ciudad = ciudad;
    }

    /**
     * Constructor vacío de olimpiada
     */
    public Olimpiada() {}

    /**
     * ToString de olimpiada
     *
     * @return descripción de olimpiada
     */
    @Override
    public String toString() {
        return nombre;
    }

    /**
     * Enum de Temporada
     */
    public enum SeasonCategory {
        WINTER, SUMMER;
    }

    /**
     * Función que devuelve la temporada de las olimpiadas
     *
     * @param season temporada de las olimpiadas
     * @return SeasonCategory o null
     */
    public SeasonCategory getSeasonCategory(String season) {
        if (season.equals("Winter")) {
            return SeasonCategory.WINTER;
        } else if (season.equals("Summer")) {
            return SeasonCategory.SUMMER;
        }
        return null;
    }

    /**
     * Getter para el id de la olimpiada
     *
     * @return id de la olimpiada
     */
    public int getId_olimpiada() {
        return id_olimpiada;
    }

    /**
     * Setter para el id de la olimpiada
     *
     * @param id_olimpiada nuevo id de la olimpiada
     */
    public void setId_olimpiada(int id_olimpiada) {
        this.id_olimpiada = id_olimpiada;
    }

    /**
     * Getter para el nombre de la olimpiada
     *
     * @return nombre de la olimpiada
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Setter para el nombre de la olimpiada
     *
     * @param nombre nuevo nombre de la olimpiada
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Getter para el año de la olimpiada
     *
     * @return año de la olimpiada
     */
    public int getAnio() {
        return anio;
    }

    /**
     * Setter para el año de la olimpiada
     *
     * @param anio nuevo año de la olimpiada
     */
    public void setAnio(int anio) {
        this.anio = anio;
    }

    /**
     * Getter para la temporada de la olimpiada
     *
     * @return temporada de la olimpiada
     */
    public String getTemporada() {
        if (temporada.equals(SeasonCategory.WINTER)) {
            return "Winter";
        }
        return "Summer";
    }

    /**
     * Setter para la temporada de la olimpiada
     *
     * @param temporada nueva temporada de la olimpiada
     */
    public void setTemporada(SeasonCategory temporada) {
        this.temporada = temporada;
    }

    /**
     * Getter para la ciudad de la olimpiada
     *
     * @return ciudad de la olimpiada
     */
    public String getCiudad() {
        return ciudad;
    }

    /**
     * Setter para la ciudad de la olimpiada
     *
     * @param ciudad nueva ciudad de la olimpiada
     */
    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Olimpiada olimpiada = (Olimpiada) o;
        return id_olimpiada == olimpiada.id_olimpiada;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id_olimpiada);
    }

}
