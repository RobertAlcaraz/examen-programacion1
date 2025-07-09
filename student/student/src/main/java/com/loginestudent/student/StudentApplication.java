package com.loginestudent.student;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.loginestudent.student.repository")
@EntityScan(basePackages = {
		"com.loginestudent.student.model", // ¡¡¡AÑADE ESTA LÍNEA!!!
		"com.loginestudent.student.alumnos",
		"com.loginestudent.student.asistencias",
		"com.loginestudent.student.contactos"
})
public class StudentApplication {
	public static void main(String[] args) {
		SpringApplication.run(StudentApplication.class, args);
	}
}