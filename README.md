## EventFlow

EventFlow es una aplicación desarrollada en **Spring Boot** con **MySQL** para la gestión de eventos, boletos y compras.  
Incluye autenticación con **JWT**, control de stock y un flujo seguro de transacciones.

---

## 🚀 Características principales
- Registro y autenticación de usuarios con **Spring Security + JWT**.
- Gestión de eventos: creación, edición y consulta.
- Administración de boletos y tipos de entradas.
- Control de compras con validación de stock.
- Migraciones de base de datos con **Flyway**.
  - Arquitectura en capas (controllers, services, repositories, models).

---

## 🛠️ Tecnologías utilizadas
- **Java 25**
- **Spring Boot**
- **Javadoc**
- **Spring Security**
- **JWT**
- **MySQL**
- **Flyway**
- **Maven**

---

## ⚙️ Configuración del proyecto

1. Clonar el repositorio:
   git clone https://github.com/samirmartinez1984/EventFlow.git
   cd EventFlow

2. 	Configurar la base de datos en application.properties:
    pring.datasource.url=jdbc:mysql://localhost:3306/eventflow
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.jpa.hibernate.ddl-auto=update

3. 	Ejecutar migraciones de Flyway automáticamente al iniciar la aplicación.
4.	Compilar y ejecutar con Maven:
   mvn spring-boot:run
  	
5	📂 Estructura del proyecto
• 	 → Controladores REST.
• 	 → Lógica de negocio.
• 	 → Interfaces de acceso a datos.
• 	 → Entidades JPA.
• 	 → Objetos de transferencia de datos.
• 	 → Configuración de seguridad y filtros JWT.
• 	 → Migraciones de base de datos con Flyway.

6 🔒 Seguridad
• 	Autenticación con JWT.
• 	Filtros personalizados para proteger endpoints.
• 	Roles y permisos configurados en la base de datos

7 📖 Próximos pasos
• 	Implementar pruebas unitarias y de integración.
• 	Documentar API con Swagger/OpenAPI.
• 	Desplegar en un servidor en la nube (Heroku, AWS, etc.).

8 👨‍💻 Autor
Samir Martínez
Backend Developer | Spring Boot & MySQL | Seguridad y Arquitectura
