package com.loginestudent.student.contactos;

import com.loginestudent.student.alumnos.Alumno;
import jakarta.persistence.*;
// Se eliminaron las importaciones de validación como:
// import jakarta.validation.constraints.Email;
// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.NotNull;
// import jakarta.validation.constraints.Pattern;
// import jakarta.validation.constraints.Size;

@Entity
@Table(name = "contactos")
public class Contacto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Se eliminaron las anotaciones de validación como @NotBlank y @Size
    private String nombre;

    // Se eliminaron las anotaciones de validación como @NotBlank y @Size
    private String relacion;

    // Se eliminaron las anotaciones de validación como @NotBlank, @Size y @Pattern
    private String telefono;

    // Se eliminaron las anotaciones de validación como @NotBlank, @Email y @Size
    private String email;

    // Se eliminó la anotación @NotNull
    @ManyToOne(fetch = FetchType.EAGER) // Carga el alumno junto con el contacto
    @JoinColumn(name = "alumno_id", nullable = false) // Mantener nullable=false para la DB, no es una validación de Bean Validation.
    private Alumno alumno;

    // Constructor vacío (necesario para JPA)
    public Contacto() {
    }

    // Constructor para crear nuevas instancias de Contacto
    public Contacto(String nombre, String relacion, String telefono, String email, Alumno alumno) {
        this.nombre = nombre;
        this.relacion = relacion;
        this.telefono = telefono;
        this.email = email;
        this.alumno = alumno;
    }

    // Getters y Setters
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

    public String getRelacion() {
        return relacion;
    }

    public void setRelacion(String relacion) {
        this.relacion = relacion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }
}