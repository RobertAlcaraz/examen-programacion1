package com.loginestudent.student.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank; // Para validación de campos no vacíos
import jakarta.validation.constraints.Size;   // Para validación de tamaño de campos
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections; // Para Collections.singletonList

@Entity // Indica que esta clase es una entidad JPA (se mapea a una tabla de BD)
@Table(name = "users") // Define el nombre de la tabla en la base de datos
public class User implements UserDetails { // Implementa UserDetails para la integración con Spring Security

    @Id // Marca esta propiedad como la clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Configura la estrategia de generación de ID (auto-incremento)
    private Long id; // Nuevo campo para el ID de la tabla (clave primaria)

    @NotBlank(message = "El nombre de usuario no puede estar vacío.") // Validación: no nulo ni cadena vacía
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres.") // Validación de tamaño
    @Column(nullable = false, unique = true) // No puede ser nulo y debe ser único en la BD
    private String username;

    @NotBlank(message = "La contraseña no puede estar vacía.") // Validación: no nulo ni cadena vacía
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres.") // Validación de tamaño mínimo
    @Column(nullable = false) // No puede ser nulo
    private String password;

    // Constructor vacío requerido por JPA
    public User() {}

    // Constructor para facilitar la creación de objetos User
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters y Setters para todas las propiedades
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override // Sobreescritura del método getUsername() de UserDetails
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override // Sobreescritura del método getPassword() de UserDetails
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // --- Métodos de UserDetails (obligatorios para Spring Security) ---
    // Estos métodos controlan el estado de la cuenta del usuario.
    // Para una aplicación simple, los devolvemos como 'true'.

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // En una aplicación real, los roles se cargarían desde la base de datos
        // o se asignarían dinámicamente. Para este ejemplo, asignamos un rol fijo "ROLE_USER".
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Indica si la cuenta del usuario no ha caducado
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Indica si la cuenta del usuario no está bloqueada
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Indica si las credenciales (contraseña) del usuario no han caducado
    }

    @Override
    public boolean isEnabled() {
        return true; // Indica si el usuario está habilitado (activo)
    }
}