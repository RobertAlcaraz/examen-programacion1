package com.loginestudent.student.repository;

import com.loginestudent.student.asistencias.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional; // Importar para @Transactional
import org.springframework.data.jpa.repository.Modifying; // Importar para @Modifying
import org.springframework.data.jpa.repository.Query; // Importar para @Query

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
    // Métodos personalizados pueden ir aquí si los necesitas, por ejemplo:
    // List<Asistencia> findByAlumnoId(Long alumnoId);

    // Nuevo método para eliminar asistencias por ID de alumno
    @Modifying
    @Transactional
    @Query("DELETE FROM Asistencia a WHERE a.alumno.id = :alumnoId")
    void deleteByAlumnoId(Long alumnoId);
}