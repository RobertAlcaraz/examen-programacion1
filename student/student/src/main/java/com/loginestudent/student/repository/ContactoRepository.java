package com.loginestudent.student.repository;

import com.loginestudent.student.contactos.Contacto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional; // Importar para @Transactional
import org.springframework.data.jpa.repository.Modifying; // Importar para @Modifying
import org.springframework.data.jpa.repository.Query; // Importar para @Query

@Repository
public interface ContactoRepository extends JpaRepository<Contacto, Long> {
    // Spring Data JPA ya proporciona métodos CRUD básicos.
    // Si necesitas métodos de búsqueda específicos (ej. findByNombreContaining), los añadiríamos aquí más adelante.

    // Nuevo método para eliminar contactos por ID de alumno
    @Modifying
    @Transactional
    @Query("DELETE FROM Contacto c WHERE c.alumno.id = :alumnoId")
    void deleteByAlumnoId(Long alumnoId);
}