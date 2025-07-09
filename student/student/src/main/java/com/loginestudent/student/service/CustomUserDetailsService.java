// src/main/java/com/loginestudent/student/service/CustomUserDetailsService.java
package com.loginestudent.student.service;

import com.loginestudent.student.model.User; // Asegúrate de que la ruta sea correcta a tu clase User
import com.loginestudent.student.repository.UserRepository; // Asegúrate de que la ruta sea correcta a tu UserRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service // Indica que esta clase es un componente de servicio de Spring
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired // Inyecta el UserRepository para acceder a los datos de usuario
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Busca el usuario en la base de datos por su nombre de usuario
        User user = userRepository.findByUsername(username);
        if (user == null) {
            // Si el usuario no se encuentra, lanza una excepción que Spring Security capturará
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }
        // Devuelve el objeto User. Como tu clase User implementa UserDetails, puede ser devuelto directamente.
        return user;
    }
}