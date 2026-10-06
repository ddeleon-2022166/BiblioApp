# BiblioApp - Sistema de Gestión de Biblioteca

Descripción:  
BiblioApp es una API REST backend desarrollada con Spring Boot destinada a modernizar el control de un catálogo de libros, la administración de usuarios y la gestión automatizada de préstamos y devoluciones universitarias. Aplica reglas estrictas de negocio y se encuentra asegurada mediante JWT (JSON Web Tokens).

## Características principales
* Control completo de catálogo de libros (CRUD con eliminación lógica).
* Gestión de préstamos y devoluciones automatizadas con validación de stock.
* Control de reglas de negocio: límite de préstamos por usuario, plazos de devolución y sanciones automáticas.
* Arquitectura monolítica organizada en capas, escalable a microservicios en el futuro.
* Seguridad Stateless mediante Spring Security y tokens JWT.

## Tecnologías Utilizadas
* **Java 21**
* **Spring Boot 3.3.4**
* **Maven** (Gestor de dependencias)
* **MySQL** (Sistema gestor de base de datos)
* **Spring Security & JWT (jjwt)**
* **Lombok & Bean Validation**

## Requisitos Previos
* JDK 17 o superior (Recomendado 21)
* Maven
* MySQL 8.0+
* Postman (para pruebas de la API)
* IntelliJ IDEA (IDE recomendado)

## Configuración de MySQL y Variables de Entorno
En el archivo `src/main/resources/application.properties` se utilizan variables de entorno para evitar incrustar secretos. Configure las siguientes variables de entorno en su sistema o directamente en la Run Configuration de IntelliJ IDEA:
* `DB_USER` (Usuario de MySQL, por defecto: `root`)
* `DB_PASSWORD` (Contraseña de MySQL, por defecto: `root`)
* `JWT_SECRET` (Clave HMAC-SHA de al menos 256 bits)

La base de datos `db_biblio_app` será creada automáticamente.

## Instalación y Ejecución

1. Clonar este repositorio.
2. Abrir el proyecto en IntelliJ IDEA. Maven descargará las dependencias de forma automática.
3. Asegurarse de que el servicio de MySQL se encuentre ejecutándose.
4. Ejecutar la clase principal `BiblioAppApplication.java`.

**Ejecución desde Maven (Terminal):**
```bash
mvn clean install
mvn spring-boot:run