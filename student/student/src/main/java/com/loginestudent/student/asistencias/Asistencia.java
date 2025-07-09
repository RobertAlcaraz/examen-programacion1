package com.loginestudent.student.asistencias;

import com.loginestudent.student.alumnos.Alumno;
import jakarta.persistence.*;
// Se eliminaron las importaciones de validación como:
// import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "asistencias")
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Se eliminó la anotación @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "alumno_id", nullable = false) // Mantener nullable=false para la DB, no es una validación de Bean Validation.
    private Alumno alumno;

    // Se eliminó la anotación @NotNull
    private LocalDate fecha;

    // Se eliminó la anotación @NotNull
    private Boolean presente;

    // Constructores
    public Asistencia() {
    }

    public Asistencia(Alumno alumno, LocalDate fecha, Boolean presente) {
        this.alumno = alumno;
        this.fecha = fecha;
        this.presente = presente;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Boolean getPresente() {
        return presente;
    }

    public void setPresente(Boolean presente) {
        this.presente = presente;
    }

    // Método de utilidad para el HTML
    public String getEstadoAsistencia() {
        return presente != null ? (presente ? "Presente" : "Ausente") : "Desconocido";
    }
}