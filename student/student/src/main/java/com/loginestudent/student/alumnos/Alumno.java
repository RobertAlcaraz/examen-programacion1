package com.loginestudent.student.alumnos;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
// Se eliminaron las importaciones de validación como:
// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.Size;

@Entity
@Table(name = "alumnos")
public class Alumno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Se eliminaron las anotaciones de validación como @NotBlank y @Size
    private String nombre;

    // Se eliminaron las anotaciones de validación como @NotBlank y @Size
    private String curso;

    // 1. Constructor vacío (sin argumentos) - Generalmente requerido por JPA
    public Alumno() {
    }

    // 2. Constructor con 'nombre' y 'curso' - Necesario para 'new Alumno(nombre, curso)'
    public Alumno(String nombre, String curso) {
        this.nombre = nombre;
        this.curso = curso;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    @Override
    public String toString() {
        return "Alumno{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", curso='" + curso + '\'' +
                '}';
    }
}