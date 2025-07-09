// src/main/java/com/loginestudent/student/repository/UserRepository.java
package com.loginestudent.student.repository;

import com.loginestudent.student.model.User; // Asegúrate de que la ruta sea correcta a tu clase User
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Indica que esta interfaz es un componente de repositorio de Spring
public interface UserRepository extends JpaRepository<User, Long> {
    // Este método es crucial para Spring Security. Spring Data JPA lo implementará
    // automáticamente basándose en el nombre del método para buscar un usuario por su 'username'.
    User findByUsername(String username);
}