package com.intissar.olimpiadas.model;

import java.util.Objects;

/**
 * Clase Evento
 */
public class Evento {
    private int id_evento;
    private String nombre;
    private Olimpiada olimpiada;
    private Deporte deporte;

    /**
     * Constructor con parámetros evento
     *
     * @param id_evento del evento
     * @param nombre del evento
     * @param olimpiada del evento
     * @param deporte del evento
     */
    public Evento(int id_evento, String nombre, Olimpiada olimpiada, Deporte deporte) {
        this.id_evento = id_evento;
        this.nombre = nombre;
        this.olimpiada = olimpiada;
        this.deporte = deporte;
    }

    /**
     * Constructor vacío de evento
     */
    public Evento() {}

    /**
     * ToString de evento
     *
     * @return descripción de evento
     */
    @Override
    public String toString() {
        return nombre;
    }

    /**
     * Getter para el id del evento
     *
     * @return id del evento
     */
    public int getId_evento() {
        return id_evento;
    }

    /**
     * Setter para el id del evento
     *
     * @param id_evento nuevo id del evento
     */
    public void setId_evento(int id_evento) {
        this.id_evento = id_evento;
    }

    /**
     * Getter para el nombre del evento
     *
     * @return nombre del evento
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Setter para el nombre del evento
     *
     * @param nombre nuevo nombre del evento
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Getter para la olimpiada del evento
     *
     * @return olimpiada del evento
     */
    public Olimpiada getOlimpiada() {
        return olimpiada;
    }

    /**
     * Setter para la olimpiada del evento
     *
     * @param olimpiada nueva olimpiada del evento
     */
    public void setOlimpiada(Olimpiada olimpiada) {
        this.olimpiada = olimpiada;
    }

    /**
     * Getter para el deporte del evento
     *
     * @return deporte del evento
     */
    public Deporte getDeporte() {
        return deporte;
    }

    /**
     * Setter para el deporte del evento
     *
     * @param deporte nuevo deporte del evento
     */
    public void setDeporte(Deporte deporte) {
        this.deporte = deporte;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Evento evento = (Evento) o;
        return id_evento == evento.id_evento;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id_evento);
    }

}
