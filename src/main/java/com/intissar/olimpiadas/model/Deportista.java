package com.intissar.olimpiadas.model;

import java.sql.Blob;
import java.util.Objects;

/**
 * Clase Deportista
 */
public class Deportista {
    private int id_deportista;
    private String nombre;
    private SexCategory sexo; // Enum
    private int peso;
    private int altura;
    private Blob foto;

    /**
     * Constructor con parámetros deportista
     *
     * @param id_deportista del deportista
     * @param nombre del deportista
     * @param sexo sexo del deportista
     * @param peso del deportista
     * @param altura del deportista
     * @param foto del deportista
     */
    public Deportista(int id_deportista, String nombre, char sexo, int peso, int altura, Blob foto) {
        this.id_deportista = id_deportista;
        this.nombre = nombre;
        this.sexo = getSexCategory(sexo); // Usa el método para convertir el char a SexCategory
        this.peso = peso;
        this.altura = altura;
        this.foto = foto;
    }

    /**
     * Constructor vacío de deportista
     */
    public Deportista() {}

    /**
     * ToString de deportista
     *
     * @return descripción de deportista
     */
    @Override
    public String toString() {
        return nombre;
    }

    /**
     * Enum de Sexo
     */
    public enum SexCategory {
        MALE, FEMALE;
    }

    /**
     * Función que devuelve el sexo del deportista
     *
     * @param sex sexo del deportista
     * @return SexCategory o null
     */
    public SexCategory getSexCategory(char sex) {
        if (sex == 'F') {
            return SexCategory.FEMALE;
        } else if (sex == 'M') {
            return SexCategory.MALE;
        }
        return null;
    }

    /**
     * Getter para el id del deportista
     *
     * @return id del deportista
     */
    public int getId_deportista() {
        return id_deportista;
    }

    /**
     * Setter para el id del deportista
     *
     * @param id_deportista nuevo id del deportista
     */
    public void setId_deportista(int id_deportista) {
        this.id_deportista = id_deportista;
    }

    /**
     * Getter para el nombre del deportista
     *
     * @return nombre del deportista
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Setter para el nombre del deportista
     *
     * @param nombre nuevo nombre del deportista
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Getter para el sexo del deportista
     *
     * @return sexo del deportista
     */
    public char getSexo() {
        if (sexo == SexCategory.FEMALE) {
            return 'F';
        }
        return 'M';
    }

    /**
     * Setter para el sexo del deportista
     *
     * @param sexo nuevo sexo del deportista
     */
    public void setSexo(SexCategory sexo) {
        this.sexo = sexo;
    }

    /**
     * Getter para el peso del deportista
     *
     * @return peso del deportista
     */
    public int getPeso() {
        return peso;
    }

    /**
     * Setter para el peso del deportista
     *
     * @param peso nuevo peso del deportista
     */
    public void setPeso(int peso) {
        this.peso = peso;
    }

    /**
     * Getter para la altura del deportista
     *
     * @return altura del deportista
     */
    public int getAltura() {
        return altura;
    }

    /**
     * Setter para la altura del deportista
     *
     * @param altura nueva altura del deportista
     */
    public void setAltura(int altura) {
        this.altura = altura;
    }

    /**
     * Getter para la foto del deportista
     *
     * @return foto del deportista
     */
    public Blob getFoto() {
        return foto;
    }

    /**
     * Setter para la foto del deportista
     *
     * @param foto nueva foto del deportista
     */
    public void setFoto(Blob foto) {
        this.foto = foto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Deportista that = (Deportista) o;
        return id_deportista == that.id_deportista;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id_deportista);
    }

}
