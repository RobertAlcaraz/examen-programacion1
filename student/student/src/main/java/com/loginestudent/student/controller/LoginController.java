package com.loginestudent.student.controller;

import com.loginestudent.student.alumnos.Alumno;
import com.loginestudent.student.asistencias.Asistencia;
import com.loginestudent.student.contactos.Contacto;
import com.loginestudent.student.model.User;
import com.loginestudent.student.repository.AlumnoRepository;
import com.loginestudent.student.repository.AsistenciaRepository;
import com.loginestudent.student.repository.ContactoRepository;
import com.loginestudent.student.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam; // <-- ¡Mantener este import!
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.time.format.DateTimeFormatter;

@Controller
public class LoginController {

    @Autowired
    private AlumnoRepository alumnoRepository;

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @Autowired
    private ContactoRepository contactoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- MODIFICACIÓN CLAVE AQUÍ: Método showLoginForm con @RequestParam ---
    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "showRegister", required = false, defaultValue = "false") boolean showRegister,
                                Model model,
                                @ModelAttribute(value = "user", binding = false) User user // Usa binding=false para que no intente bindear de RequestParams si no es necesario
    ) {
        // El objeto 'user' para el formulario de registro.
        // Si no existe en el modelo (ej. primera carga o redirect sin errores de validación), se crea uno nuevo.
        // Si viene de un RedirectAttributes (con errores), ya estará en el modelo y no se sobrescribirá.
        if (!model.containsAttribute("user") || user.getUsername() == null) { // Agregado || user.getUsername() == null para reiniciar si viene vacío.
            model.addAttribute("user", new User());
        }

        // Esta bandera es la que controlará la visibilidad de los formularios en login.html
        model.addAttribute("showRegisterForm", showRegister);

        return "login"; // Devuelve tu login.html (que ahora contendrá ambos formularios)
    }

    // --- Método para procesar el formulario de registro ---
    @PostMapping("/process_register")
    public String processRegister(@Valid @ModelAttribute("user") User user,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes) {

        // 1. Manejo de errores de validación (ej. @NotBlank, @Size en la clase User)
        if (bindingResult.hasErrors()) {
            // Esto es crucial para que Thymeleaf pueda mostrar los errores de validación en el HTML
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.user", bindingResult);
            redirectAttributes.addFlashAttribute("user", user); // Vuelve a enviar el objeto user para mantener los datos
            redirectAttributes.addFlashAttribute("registrationError", "Por favor, corrige los errores en el formulario de registro.");
            redirectAttributes.addFlashAttribute("showRegisterForm", true); // Bandera para que el HTML muestre el form de registro
            return "redirect:/login"; // Redirige de vuelta a la página de login
        }

        // 2. Verificar si el nombre de usuario ya existe
        if (userRepository.findByUsername(user.getUsername()) != null) {
            redirectAttributes.addFlashAttribute("registrationError", "El nombre de usuario '" + user.getUsername() + "' ya existe. Por favor, elige otro.");
            redirectAttributes.addFlashAttribute("user", user); // Vuelve a enviar el objeto user para mantener los datos
            redirectAttributes.addFlashAttribute("showRegisterForm", true); // Bandera para que el HTML muestre el form de registro
            return "redirect:/login"; // Redirige de vuelta a la página de login
        }

        // 3. Encriptar la contraseña antes de guardar en la base de datos
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // 4. Guardar el nuevo usuario en la base de datos
        try {
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("registrationSuccess", "¡Registro exitoso! Ya puedes iniciar sesión con '" + user.getUsername() + "'.");
        } catch (Exception e) {
            System.err.println("Error al registrar el usuario: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("registrationError", "Error al registrar el usuario: " + e.getMessage());
            redirectAttributes.addFlashAttribute("user", user); // Vuelve a enviar el objeto user para mantener los datos
            redirectAttributes.addFlashAttribute("showRegisterForm", true); // Bandera para que el HTML muestre el form de registro
        }
        return "redirect:/login"; // Redirige siempre a la página de login después de un intento de registro
    }


    @GetMapping("/home")
    public String home(@RequestParam(value = "searchQuery", required = false) String searchQuery, Model model) {
        // Asegúrate de que estos objetos estén disponibles para los formularios de AÑADIR
        if (!model.containsAttribute("nuevoAlumno")) {
            model.addAttribute("nuevoAlumno", new Alumno());
        }
        if (!model.containsAttribute("nuevaAsistencia")) {
            model.addAttribute("nuevaAsistencia", new Asistencia());
        }
        if (!model.containsAttribute("nuevoContacto")) {
            model.addAttribute("nuevoContacto", new Contacto());
        }

        // ***** ¡ESTO ES LO QUE FALTABA PARA LOS BOTONES DE EDITAR! *****
        // Asegúrate de que estos objetos estén disponibles para los formularios de EDITAR (modales)
        // aunque se llenen con JavaScript, Thymeleaf necesita que el objeto exista en el Model
        // al momento de renderizar el HTML con th:object.
        if (!model.containsAttribute("alumnoModificado")) {
            model.addAttribute("alumnoModificado", new Alumno());
        }
        if (!model.containsAttribute("asistenciaModificada")) {
            model.addAttribute("asistenciaModificada", new Asistencia());
        }
        if (!model.containsAttribute("contactoModificado")) {
            model.addAttribute("contactoModificado", new Contacto());
        }
        // ***************************************************************


        List<Alumno> alumnos;
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            alumnos = alumnoRepository.findByNombreContainingIgnoreCase(searchQuery.trim());
            model.addAttribute("searchQuery", searchQuery.trim());
            if (alumnos.isEmpty()) {
                model.addAttribute("mensajeInfoBusqueda", "No se encontraron alumnos con el nombre: '" + searchQuery.trim() + "'.");
            } else {
                model.addAttribute("mensajeInfoBusqueda", "Resultados para la búsqueda: '" + searchQuery.trim() + "'.");
            }
        } else {
            alumnos = alumnoRepository.findAll();
            model.addAttribute("searchQuery", "");
        }
        model.addAttribute("alumnos", alumnos);

        List<Asistencia> asistencias = asistenciaRepository.findAll();
        model.addAttribute("asistencias", asistencias);

        List<Contacto> contactos = contactoRepository.findAll();
        model.addAttribute("contactos", contactos);

        // --- LÍNEAS DE DEPURACIÓN (Opcional: puedes quitarlas en producción) ---
        System.out.println("--- Depuración de Alumnos en LoginController.home() ---");
        System.out.println("searchQuery: " + (searchQuery != null ? searchQuery : "N/A"));
        System.out.println("Número de alumnos cargados: " + alumnos.size());

        System.out.println("--- Depuración de Asistencias en LoginController.home() ---");
        System.out.println("Número de asistencias cargadas: " + asistencias.size());

        System.out.println("--- Depuración de Contactos en LoginController.home() ---");
        System.out.println("Número de contactos cargados: " + contactos.size());
        // --- FIN DE LÍNEAS DE DEPURACIÓN ---

        return "home";
    }

    // *** MÉTODOS DE GUARDAR (SIN VALIDACIÓN) ***

    @PostMapping("/guardarAlumno")
    public String guardarAlumno(@ModelAttribute("nuevoAlumno") Alumno nuevoAlumno,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            alumnoRepository.save(nuevoAlumno);
            redirectAttributes.addFlashAttribute("mensajeExito", "¡Alumno '" + nuevoAlumno.getNombre() + "' guardado correctamente!");
        } catch (Exception e) {
            System.err.println("Error al guardar alumno: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("mensajeError", "Error al guardar el alumno: " + e.getMessage());
        }
        return "redirect:/home";
    }

    @PostMapping("/guardarAsistencia")
    public String guardarAsistencia(@ModelAttribute("nuevaAsistencia") Asistencia nuevaAsistencia,
                                    BindingResult bindingResult,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {

        if (nuevaAsistencia.getAlumno() == null || nuevaAsistencia.getAlumno().getId() == null) {
            redirectAttributes.addFlashAttribute("mensajeError", "Debe seleccionar un alumno válido para la asistencia.");
            // También es buena idea devolver el objeto que se intentó guardar para que los campos no se pierdan
            // si el usuario refresca o el navegador recuerda los datos.
            redirectAttributes.addFlashAttribute("nuevaAsistencia", nuevaAsistencia);
            return "redirect:/home";
        }

        try {
            Optional<Alumno> alumnoOptional = alumnoRepository.findById(nuevaAsistencia.getAlumno().getId());
            if (alumnoOptional.isPresent()) {
                nuevaAsistencia.setAlumno(alumnoOptional.get());
                asistenciaRepository.save(nuevaAsistencia);
                redirectAttributes.addFlashAttribute("mensajeExito", "¡Asistencia para " + nuevaAsistencia.getAlumno().getNombre() + " registrada correctamente!");
            } else {
                redirectAttributes.addFlashAttribute("mensajeError", "Error: No se encontró el alumno seleccionado con ID " + nuevaAsistencia.getAlumno().getId() + " para registrar asistencia.");
                // Esto podría no ser necesario, pero es un caso de borde.
                redirectAttributes.addFlashAttribute("nuevaAsistencia", nuevaAsistencia);
                return "redirect:/home";
            }
        } catch (Exception e) {
            System.err.println("Error al guardar asistencia: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("mensajeError", "Error al guardar la asistencia: " + e.getMessage());
            // Esto podría no ser necesario, pero es un caso de borde.
            redirectAttributes.addFlashAttribute("nuevaAsistencia", nuevaAsistencia);
        }
        return "redirect:/home";
    }

    @PostMapping("/guardarContacto")
    public String guardarContacto(@ModelAttribute("nuevoContacto") Contacto nuevoContacto,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        if (nuevoContacto.getAlumno() == null || nuevoContacto.getAlumno().getId() == null) {
            redirectAttributes.addFlashAttribute("mensajeError", "Debe seleccionar un alumno válido para el contacto.");
            // Es crucial para volver a poblar el modal si hubo un error de validación manual
            redirectAttributes.addFlashAttribute("nuevoContacto", nuevoContacto);
            // También podemos enviar una bandera para que JavaScript abra el modal al cargar la página
            redirectAttributes.addFlashAttribute("showNuevoContactoModal", true);
            return "redirect:/home";
        }

        try {
            Optional<Alumno> alumnoOptional = alumnoRepository.findById(nuevoContacto.getAlumno().getId());
            if (alumnoOptional.isPresent()) {
                nuevoContacto.setAlumno(alumnoOptional.get());
                contactoRepository.save(nuevoContacto);
                redirectAttributes.addFlashAttribute("mensajeExito", "¡Contacto '" + nuevoContacto.getNombre() + "' para " + nuevoContacto.getAlumno().getNombre() + " guardado correctamente!");
            } else {
                redirectAttributes.addFlashAttribute("mensajeError", "Error: No se encontró el alumno seleccionado con ID " + nuevoContacto.getAlumno().getId() + " para asociar el contacto.");
                redirectAttributes.addFlashAttribute("nuevoContacto", nuevoContacto); // Conservar datos
                redirectAttributes.addFlashAttribute("showNuevoContactoModal", true); // Reabrir modal
                return "redirect:/home";
            }
        } catch (Exception e) {
            System.err.println("Error al guardar contacto: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("mensajeError", "Error al guardar el contacto: " + e.getMessage());
            redirectAttributes.addFlashAttribute("nuevoContacto", nuevoContacto); // Conservar datos
            redirectAttributes.addFlashAttribute("showNuevoContactoModal", true); // Reabrir modal
            return "redirect:/home";
        }
        return "redirect:/home";
    }

    // *** MÉTODOS DE ELIMINAR ***

    @PostMapping("/eliminarContacto")
    public String eliminarContacto(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Contacto> contactoOptional = contactoRepository.findById(id);
            if (contactoOptional.isPresent()) {
                contactoRepository.deleteById(id);
                redirectAttributes.addFlashAttribute("mensajeExito", "¡Contacto eliminado correctamente!");
            } else {
                String errorMsg = "Error: No se encontró el contacto con ID: " + id + " para eliminar.";
                System.err.println(errorMsg);
                redirectAttributes.addFlashAttribute("mensajeError", errorMsg);
            }
        } catch (Exception e) {
            System.err.println("Error al eliminar contacto con ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("mensajeError", "Error al eliminar el contacto: " + e.getMessage());
        }
        return "redirect:/home";
    }

    @PostMapping("/eliminarAlumno")
    public String eliminarAlumno(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Alumno> alumnoOptional = alumnoRepository.findById(id);
            if (alumnoOptional.isPresent()) {
                String nombreAlumno = alumnoOptional.get().getNombre();
                // Eliminar asistencias y contactos asociados primero
                asistenciaRepository.deleteByAlumnoId(id);
                contactoRepository.deleteByAlumnoId(id);
                alumnoRepository.deleteById(id);
                redirectAttributes.addFlashAttribute("mensajeExito", "¡Alumno '" + nombreAlumno + "' y sus datos asociados eliminados correctamente!");
            } else {
                String errorMsg = "Error: No se encontró el alumno con ID: " + id + " para eliminar.";
                System.err.println(errorMsg);
                redirectAttributes.addFlashAttribute("mensajeError", errorMsg);
            }
        } catch (Exception e) {
            System.err.println("Error al eliminar alumno con ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("mensajeError", "Error al eliminar el alumno: " + e.getMessage() + ". Asegúrese de que no haya restricciones de clave externa pendientes.");
        }
        return "redirect:/home";
    }

    @PostMapping("/eliminarAsistencia")
    public String eliminarAsistencia(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Asistencia> asistenciaOptional = asistenciaRepository.findById(id);
            if (asistenciaOptional.isPresent()) {
                Asistencia asistencia = asistenciaOptional.get();
                String nombreAlumno = (asistencia.getAlumno() != null) ? asistencia.getAlumno().getNombre() : "un alumno desconocido";
                String fechaAsistencia = (asistencia.getFecha() != null) ? asistencia.getFecha().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) : "fecha desconocida";

                asistenciaRepository.deleteById(id);
                redirectAttributes.addFlashAttribute("mensajeExito", "¡Asistencia para '" + nombreAlumno + "' del " + fechaAsistencia + " eliminada correctamente!");
            } else {
                String errorMsg = "Error: No se encontró la asistencia con ID: " + id + " para eliminar.";
                System.err.println(errorMsg);
                redirectAttributes.addFlashAttribute("mensajeError", errorMsg);
            }
        } catch (Exception e) {
            System.err.println("Error al eliminar asistencia con ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("mensajeError", "Error al eliminar la asistencia: " + e.getMessage());
        }
        return "redirect:/home";
    }

    // *** MÉTODOS DE EDITAR (SIN VALIDACIÓN) ***

    @PostMapping("/editarAlumno")
    public String editarAlumno(@ModelAttribute("alumnoModificado") Alumno alumnoModificado,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            Optional<Alumno> alumnoOptional = alumnoRepository.findById(alumnoModificado.getId());
            if (alumnoOptional.isPresent()) {
                Alumno alumnoExistente = alumnoOptional.get();
                alumnoExistente.setNombre(alumnoModificado.getNombre());
                alumnoExistente.setCurso(alumnoModificado.getCurso());
                alumnoRepository.save(alumnoExistente);
                redirectAttributes.addFlashAttribute("mensajeExito", "¡Alumno '" + alumnoExistente.getNombre() + "' actualizado correctamente!");
            } else {
                String errorMsg = "Error: No se encontró el alumno con ID: " + alumnoModificado.getId() + " para editar.";
                System.err.println(errorMsg);
                redirectAttributes.addFlashAttribute("mensajeError", errorMsg);
            }
        } catch (Exception e) {
            System.err.println("Error al editar alumno con ID " + alumnoModificado.getId() + ": " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("mensajeError", "Error al editar el alumno: " + e.getMessage());
        }
        return "redirect:/home";
    }

    @PostMapping("/editarContacto")
    public String editarContacto(@ModelAttribute("contactoModificado") Contacto contactoModificado,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        if (contactoModificado.getAlumno() == null || contactoModificado.getAlumno().getId() == null) {
            redirectAttributes.addFlashAttribute("mensajeError", "Error al editar el contacto: Debe seleccionar un alumno válido.");
            redirectAttributes.addFlashAttribute("contactoModificado", contactoModificado); // Para conservar los datos ingresados
            // Podrías añadir una bandera aquí para reabrir el modal de edición al recargar,
            // similar a 'showNuevoContactoModal', pero para el modal de edición.
            // Esto requeriría más JS. Por ahora, solo informamos el error.
            return "redirect:/home";
        }

        try {
            Optional<Contacto> contactoOptional = contactoRepository.findById(contactoModificado.getId());
            Optional<Alumno> alumnoOptional = alumnoRepository.findById(contactoModificado.getAlumno().getId());

            if (contactoOptional.isPresent() && alumnoOptional.isPresent()) {
                Contacto contactoExistente = contactoOptional.get();
                Alumno alumnoAsociado = alumnoOptional.get();

                contactoExistente.setNombre(contactoModificado.getNombre());
                contactoExistente.setRelacion(contactoModificado.getRelacion());
                contactoExistente.setTelefono(contactoModificado.getTelefono());
                contactoExistente.setEmail(contactoModificado.getEmail());
                contactoExistente.setAlumno(alumnoAsociado);

                contactoRepository.save(contactoExistente);
                redirectAttributes.addFlashAttribute("mensajeExito", "¡Contacto '" + contactoExistente.getNombre() + "' actualizado correctamente!");
            } else {
                String errorMsg = "Error: Contacto (ID: " + contactoModificado.getId() + ") o Alumno (ID: " + contactoModificado.getAlumno().getId() + ") no encontrados para la edición.";
                System.err.println(errorMsg);
                redirectAttributes.addFlashAttribute("mensajeError", errorMsg);
            }
        } catch (Exception e) {
            System.err.println("Error al editar contacto con ID " + contactoModificado.getId() + ": " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("mensajeError", "Error al editar el contacto: " + e.getMessage());
        }
        return "redirect:/home";
    }

    @PostMapping("/editarAsistencia")
    public String editarAsistencia(@ModelAttribute("asistenciaModificada") Asistencia asistenciaModificada,
                                   BindingResult bindingResult,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {

        if (asistenciaModificada.getAlumno() == null || asistenciaModificada.getAlumno().getId() == null) {
            redirectAttributes.addFlashAttribute("mensajeError", "Debe seleccionar un alumno válido para la asistencia.");
            redirectAttributes.addFlashAttribute("asistenciaModificada", asistenciaModificada); // Conservar datos
            // Podrías añadir una bandera aquí para reabrir el modal de edición
            return "redirect:/home";
        }

        try {
            Optional<Asistencia> asistenciaOptional = asistenciaRepository.findById(asistenciaModificada.getId());
            Optional<Alumno> alumnoOptional = alumnoRepository.findById(asistenciaModificada.getAlumno().getId());

            if (asistenciaOptional.isPresent() && alumnoOptional.isPresent()) {
                Asistencia asistenciaExistente = asistenciaOptional.get();
                Alumno nuevoAlumno = alumnoOptional.get();

                asistenciaExistente.setAlumno(nuevoAlumno);
                asistenciaExistente.setFecha(asistenciaModificada.getFecha());
                asistenciaExistente.setPresente(asistenciaModificada.getPresente());

                asistenciaRepository.save(asistenciaExistente);
                redirectAttributes.addFlashAttribute("mensajeExito", "¡Asistencia actualizada correctamente para " + nuevoAlumno.getNombre() + "!");
            } else {
                String errorMsg = "Error: Asistencia (ID: " + asistenciaModificada.getId() + ") o Alumno (ID: " + asistenciaModificada.getAlumno().getId() + ") no encontrados para la edición.";
                System.err.println(errorMsg);
                redirectAttributes.addFlashAttribute("mensajeError", errorMsg);
            }
        } catch (Exception e) {
            System.err.println("Error al editar asistencia con ID " + asistenciaModificada.getId() + ": " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("mensajeError", "Error al editar la asistencia: " + e.getMessage());
        }
        return "redirect:/home";
    }
}