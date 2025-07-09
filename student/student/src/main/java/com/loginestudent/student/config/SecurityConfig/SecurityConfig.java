// src/main/java/com/loginestudent/student/config/SecurityConfig/SecurityConfig.java
package com.loginestudent.student.config.SecurityConfig;

import com.loginestudent.student.service.CustomUserDetailsService; // ¡NUEVO! Importa tu CustomUserDetailsService
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider; // Importar
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // ¡NUEVO! Importar BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder; // ¡NUEVO! Importar PasswordEncoder
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; // Ya lo tienes

@Configuration // Indica que esta clase contiene configuraciones de Spring
@EnableWebSecurity // Habilita la seguridad web de Spring Security
public class SecurityConfig {

    // Inyectamos nuestro CustomUserDetailsService.
    // La inyección por constructor es la forma recomendada en Spring Boot.
    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean // Define un bean para configurar la cadena de filtros de seguridad HTTP
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        // Permite acceso público a la página de login, la URL de registro y los archivos CSS
                        .requestMatchers("/login", "/process_register", "/css/**").permitAll()
                        // Permite acceso público a las rutas de guardar datos.
                        // Esto es importante si los formularios de añadir/editar se acceden
                        // antes de que el usuario esté autenticado (ej. desde el modal en login/registro,
                        // o si hay redirecciones que no pasan por /home).
                        .requestMatchers("/guardarAlumno", "/guardarAsistencia", "/guardarContacto").permitAll()
                        // Requiere autenticación para la ruta /home
                        .requestMatchers("/home").authenticated()
                        // Cualquier otra petición requiere autenticación
                        .anyRequest().authenticated()
                )
                // Configuración del formulario de login
                .formLogin(form -> form
                        .loginPage("/login") // Especifica la URL de tu página de login
                        .defaultSuccessUrl("/home", true) // Redirige a /home en caso de login exitoso
                        .failureUrl("/login?error") // Redirige a /login?error en caso de fallo de login
                        .permitAll() // Permite a todos acceder a la página de login
                )
                // Configuración del logout
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout") // Redirige a /login?logout en caso de logout exitoso
                        .permitAll() // Permite a todos realizar logout
                );

        // Deshabilitar CSRF para simplificar el desarrollo y la depuración.
        // ¡ADVERTENCIA! En un entorno de PRODUCCIÓN, DEBERÍAS habilitar CSRF
        // para protegerte contra ataques de falsificación de solicitudes entre sitios.
        // Asegúrate de enviar el token CSRF en tus formularios POST si lo habilitas.
        http.csrf(AbstractHttpConfigurer::disable);

        return http.build(); // Construye y devuelve la cadena de filtros de seguridad
    }

    @Bean // Define un bean para el codificador de contraseñas
    public PasswordEncoder passwordEncoder() {
        // BCryptPasswordEncoder es el estándar y altamente recomendado para cifrar contraseñas.
        return new BCryptPasswordEncoder();
    }

    @Bean // Define un bean para el proveedor de autenticación DAO
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        // Establece nuestro CustomUserDetailsService para que Spring Security
        // sepa cómo cargar los detalles del usuario desde la base de datos.
        authProvider.setUserDetailsService(customUserDetailsService);
        // Establece el PasswordEncoder para que Spring Security pueda verificar
        // las contraseñas encriptadas al autenticar usuarios.
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}