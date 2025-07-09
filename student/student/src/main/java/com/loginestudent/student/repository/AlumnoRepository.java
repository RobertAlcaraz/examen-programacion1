package com.loginestudent.student.repository;

import com.loginestudent.student.alumnos.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // Importar List

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {

    // Método para buscar alumnos por nombre, ignorando mayúsculas/minúsculas
    // y buscando coincidencias parciales (Containing)
    List<Alumno> findByNombreContainingIgnoreCase(String nombre);
}