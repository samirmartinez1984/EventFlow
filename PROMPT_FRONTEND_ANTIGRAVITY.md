# PROMPT PARA GENERAR FRONTEND CON ANTIGRAVITY IDE

## 📋 ANÁLISIS COMPLETO DEL BACKEND - EVENTFLOW

### 1. DESCRIPCIÓN GENERAL DEL PROYECTO

**EventFlow** es una plataforma de gestión y venta de boletos para eventos. Es un sistema web que permite:
- **Administradores**: Crear eventos, gestionar tipos de boletos con diferentes precios y disponibilidad
- **Clientes**: Visualizar eventos, comprar boletos, ver historial de compras

**Stack Tecnológico Backend**:
- Java 25
- Spring Boot 3.5.6
- Spring Security con JWT
- JPA/Hibernate para persistencia
- MySQL como base de datos
- Flyway para migraciones
- Lombok para reducir boilerplate

---

## 🏗️ ARQUITECTURA DEL SISTEMA

### Modelos de Datos (Entidades):

#### 1. **Usuario**
```
- id (Long) - PK
- nombre (String)
- primerApellido (String)
- correo (String) - UNIQUE, usado como username
- clave (String) - encriptada con bcrypt
- rol (Enum: ADMIN, CLIENTE)
- eventos (List<Evento>) - relación 1:N
- compras (List<Compra>) - relación 1:N
```
**Roles**:
- **ADMIN**: Crear eventos, gestionar boletos
- **CLIENTE**: Comprar boletos, ver eventos

#### 2. **Evento**
```
- id (Long) - PK
- nombreEvento (String) - UNIQUE
- fechaEvento (LocalDateTime) - MUST BE FUTURE
- capacidadMaxima (Integer) - 1 a 10,000
- usuario (Usuario) - FK, creador del evento
- tiposBoleto (List<TipoBoleto>) - relación 1:N
```

#### 3. **TipoBoleto** (Categorías de boletos para cada evento)
```
- id (Long) - PK
- nombreTipo (String) - ej: "VIP", "General", "Estudiante"
- precio (BigDecimal) - precio unitario
- boletosDisponibles (Integer) - stock
- evento (Evento) - FK
- creadoPor (Usuario) - FK
- compras (List<Compra>) - relación 1:N
```

#### 4. **Compra**
```
- id (Long) - PK
- cantidad (Integer) - boletos comprados
- fechaDeCompra (LocalDateTime) - timestamp de compra
- compraTotal (BigDecimal) - cantidad × precio
- tipoBoleto (TipoBoleto) - FK
- cliente (Usuario) - FK
```

---

## 📡 ENDPOINTS API - COMPLETO

### **AUTENTICACIÓN** (`/api/autenticación`)

| Método | Endpoint | Autenticación | Payload | Respuesta |
|--------|----------|---------------|---------|-----------|
| POST | `/registro` | NO | `{nombres, apellidos, correo, clave}` | `{token}` |
| POST | `/registro-admin` | ADMIN | `{nombres, apellidos, correo, clave}` | `{token}` |
| POST | `/login` | NO | `{correo, clave}` | `{token}` |
| DELETE | `/{correo}` | ADMIN | - | 204 No Content |

**Token JWT**: Válido por 24 horas, se incluye en header `Authorization: Bearer <token>`

---

### **EVENTOS** (`/api/eventos`) - Solo ADMIN puede crear/editar/eliminar

| Método | Endpoint | Autenticación | Payload | Respuesta |
|--------|----------|---------------|---------|-----------|
| GET | `/` | CLIENTE/ADMIN | - | `[{id, nombreEvento, fechaEvento, capacidadMaxima}]` |
| GET | `/{id}` | CLIENTE/ADMIN | - | `{id, nombreEvento, fechaEvento, capacidadMaxima}` |
| POST | `/` | ADMIN | `{nombreEvento, fechaEvento, capacidadMaxima}` | `{id, nombreEvento, fechaEvento, capacidadMaxima}` |
| PUT | `/{id}` | ADMIN | `{nombreEvento, fechaEvento, capacidadMaxima}` | `{id, nombreEvento, fechaEvento, capacidadMaxima}` |
| DELETE | `/{id}` | ADMIN | - | 204 No Content |

**Validaciones**:
- `nombreEvento`: No puede estar vacío, debe ser ÚNICO
- `fechaEvento`: Debe ser FUTURA (posterior a hoy)
- `capacidadMaxima`: Mínimo 1, máximo 10,000

---

### **TIPOS DE BOLETOS** (`/api/tipoboletos`) - Solo ADMIN puede crear/editar/eliminar

| Método | Endpoint | Autenticación | Payload | Respuesta |
|--------|----------|---------------|---------|-----------|
| GET | `/` | CLIENTE/ADMIN | - | `[{id, nombreTipo, precio, boletosDisponibles, eventoId}]` |
| GET | `/{id}` | CLIENTE/ADMIN | - | `{id, nombreTipo, precio, boletosDisponibles, eventoId}` |
| POST | `/` | ADMIN | `{nombreTipo, precio, boletosDisponibles, eventoId}` | `{id, nombreTipo, precio, boletosDisponibles, eventoId}` |
| PUT | `/{id}` | ADMIN | `{nombreTipo, precio, boletosDisponibles, eventoId}` | `{id, nombreTipo, precio, boletosDisponibles, eventoId}` |
| DELETE | `/{id}` | ADMIN | - | 204 No Content |

**Validaciones**:
- `nombreTipo`: No puede estar vacío
- `precio`: Mayor a 0, máximo 999,999.99
- `boletosDisponibles`: Mínimo 1
- `eventoId`: El evento debe existir

---

### **COMPRAS** (`/api/compras`) - CLIENTE/ADMIN pueden comprar, ADMIN gestiona todas

| Método | Endpoint | Autenticación | Payload | Respuesta |
|--------|----------|---------------|---------|-----------|
| GET | `/` | CLIENTE/ADMIN | - | `[{id, cantidad, fechaDeCompra, compraTotal, tipoBoletoId}]` |
| GET | `/{id}` | CLIENTE/ADMIN | - | `{id, cantidad, fechaDeCompra, compraTotal, tipoBoletoId}` |
| POST | `/` | CLIENTE/ADMIN | `{cantidad, tipoBoletoId}` | `{id, cantidad, fechaDeCompra, compraTotal, tipoBoletoId}` |
| PUT | `/{id}` | CLIENTE/ADMIN | `{cantidad, tipoBoletoId}` | `{id, cantidad, fechaDeCompra, compraTotal, tipoBoletoId}` |
| DELETE | `/{id}` | CLIENTE/ADMIN | - | 204 No Content |

**Validaciones**:
- `cantidad`: Mínimo 1 boleto
- `tipoBoletoId`: El tipo de boleto debe existir
- **Stock**: No puede comprar más boletos de los disponibles
- El `compraTotal` se calcula automáticamente: `cantidad × precioUnitario`

---

## 🎯 FLUJOS DE NEGOCIO PRINCIPALES

### Flujo 1: ADMINISTRADOR crea evento y define boletos
```
1. Admin se registra o hace login
2. Admin recibe token JWT
3. Admin crea evento (POST /api/eventos)
   - Nombre único
   - Fecha futura
   - Capacidad máxima
4. Admin crea tipos de boletos para el evento (POST /api/tipoboletos)
   - VIP: $150, 50 disponibles
   - General: $50, 200 disponibles
   - Estudiante: $30, 100 disponibles
5. Sistema disponibiliza evento para clientes
```

### Flujo 2: CLIENTE busca, compra y gestiona boletos
```
1. Cliente se registra o hace login
2. Cliente recibe token JWT
3. Cliente lista eventos (GET /api/eventos)
4. Cliente selecciona evento y ve tipos de boletos
5. Cliente compra boletos (POST /api/compras)
   - Selecciona tipo de boleto (ej: General)
   - Especifica cantidad (ej: 3 boletos)
   - Sistema calcula total: 3 × $50 = $150
   - Sistema decrementa stock: 200 - 3 = 197
6. Cliente ve historial de compras (GET /api/compras)
7. Cliente puede modificar compra (PUT /api/compras/{id})
8. Cliente puede cancelar compra (DELETE /api/compras/{id})
```

---

## 🔐 SEGURIDAD

**Autenticación**: JWT (JSON Web Tokens)
- Token válido por 24 horas
- Se incluye en header: `Authorization: Bearer eyJhbGc...`
- Generado con clave secreta basada en configuración

**Autorización**: Control de acceso por rol
- **CLIENTE**: Puede leer eventos/boletos, crear/editar/eliminar sus propias compras
- **ADMIN**: Acceso total - gestiona eventos, boletos, puede ver todas las compras

**Validaciones**:
- Contraseña encriptada con bcrypt
- Correo único por usuario
- Email extraído del token JWT para operaciones de usuario

---

## 📊 ESTRUCTURA DE CARPETAS DEL BACKEND

```
src/main/java/com/API/EventFlow/
├── EventFlowApplication.java          # Punto de entrada
├── controller/                         # Controladores REST
│   ├── AutenticacionController
│   ├── EventoController
│   ├── CompraController
│   └── TipoBoletoController
├── service/                            # Lógica de negocio
│   ├── AutenticationService
│   ├── EventoService
│   ├── CompraService
│   └── TipoBoletoService
├── model/                              # Entidades JPA
│   ├── Usuario
│   ├── Evento
│   ├── TipoBoleto
│   ├── Compra
│   └── Rol (Enum)
├── dto/                                # Data Transfer Objects
│   ├── EventoDTO
│   ├── CompraDTO
│   └── TipoBoletoDTO
├── repository/                         # Acceso a datos
│   ├── UsuarioRepository
│   ├── EventoRepository
│   ├── CompraRepository
│   └── TipoBoletoRepository
├── mapper/                             # Transformadores DTO↔Entity
│   ├── EventoMapper
│   ├── CompraMapper
│   └── TipoBoletoMapper
├── config/                             # Configuración
│   ├── SecurityConfig
│   ├── JwtService
│   ├── JwtFilter
│   └── AppConfig
├── configCors/                         # CORS
│   └── WebConfig
├── exceptiones/                        # Manejo de excepciones
│   ├── GlobalExceptionHandler
│   ├── RecursoNoEncontradoException
│   ├── DatosInvalidosException
│   └── StockInsuficienteException
└── seguridadDTO/                       # DTOs de seguridad
    ├── SolicitudRegistro
    ├── SolicitudAutenticacion
    └── RespuestaAutenticacion
```

---

## 🚀 SERVIDOR Y CONFIGURACIÓN

- **URL Base**: `http://localhost:8080`
- **BD**: MySQL - `eventos` database
- **Puerto**: 8080
- **Usuario BD**: root
- **Contraseña BD**: admin

---

## 💾 ESTADO DE LA BASE DE DATOS

**Migraciones activas** (Flyway):
1. `V1__create_initial_schema.sql` - Tablas iniciales
2. `V2__corregir_nombre_columna_evento.sql` - Corrección de nombres
3. `V4__crear_tabla_usuarios.sql` - Tabla de usuarios
4. `V5__Add_usuario_id_to_evento.sql` - Relación usuario-evento
5. `V6__crea_columna_creado_por_id_en_la_tabla_tipo_boletos.sql` - Usuario creador boleto
6. `V7__crea_columna_usuario_id_en_la_tabla_compra.sql` - Usuario cliente compra
7. `V8__agrega_columna_compra_total_en_la_tabla_compra.sql` - Total de compra

---

# 🎨 PROMPT PARA ANTIGRAVITY IDE

## PROMPT PRINCIPAL:

```
Necesito generar un frontend completo para una plataforma de gestión y venta de boletos para eventos 
llamada "EventFlow". 

El backend está construido en Java Spring Boot y expone los siguientes endpoints en http://localhost:8080:

ENDPOINTS DISPONIBLES:

AUTENTICACIÓN:
- POST /api/autenticación/registro - Registrar cliente (JSON: nombres, apellidos, correo, clave)
- POST /api/autenticación/registro-admin - Registrar admin (JSON: nombres, apellidos, correo, clave)
- POST /api/autenticación/login - Login (JSON: correo, clave) → Retorna token JWT
- DELETE /api/autenticación/{correo} - Eliminar usuario

EVENTOS (Solo Admin puede crear/editar/eliminar):
- GET /api/eventos - Listar todos los eventos
- GET /api/eventos/{id} - Obtener evento por ID
- POST /api/eventos - Crear evento (Admin) (JSON: nombreEvento, fechaEvento, capacidadMaxima)
- PUT /api/eventos/{id} - Actualizar evento (Admin) (JSON: nombreEvento, fechaEvento, capacidadMaxima)
- DELETE /api/eventos/{id} - Eliminar evento (Admin)

TIPOS DE BOLETOS (Solo Admin puede crear/editar/eliminar):
- GET /api/tipoboletos - Listar todos los tipos de boletos
- GET /api/tipoboletos/{id} - Obtener tipo de boleto por ID
- POST /api/tipoboletos - Crear tipo de boleto (Admin) (JSON: nombreTipo, precio, boletosDisponibles, eventoId)
- PUT /api/tipoboletos/{id} - Actualizar tipo de boleto (Admin) (JSON: nombreTipo, precio, boletosDisponibles)
- DELETE /api/tipoboletos/{id} - Eliminar tipo de boleto (Admin)

COMPRAS (Cliente/Admin pueden comprar, Admin gestiona todas):
- GET /api/compras - Listar todas las compras
- GET /api/compras/{id} - Obtener compra por ID
- POST /api/compras - Crear compra (JSON: cantidad, tipoBoletoId) - se calcula compraTotal automáticamente
- PUT /api/compras/{id} - Actualizar compra (JSON: cantidad, tipoBoletoId)
- DELETE /api/compras/{id} - Eliminar compra

AUTENTICACIÓN: 
- Todo endpoint excepto autenticación requiere token JWT en header: "Authorization: Bearer <token>"
- El token se obtiene al hacer login/registro

ROLES Y PERMISOS:
- ADMIN: Puede crear/editar/eliminar eventos, crear/editar/eliminar tipos de boletos, ver todas las compras
- CLIENTE: Puede ver eventos, ver tipos de boletos, comprar boletos, ver/editar/eliminar sus propias compras

MODELO DE DATOS:

USUARIO:
- id: número
- nombre: string
- primerApellido: string
- correo: string (único)
- rol: "ADMIN" o "CLIENTE"

EVENTO:
- id: número
- nombreEvento: string (único)
- fechaEvento: timestamp (debe ser futura)
- capacidadMaxima: número (1-10000)

TIPO_BOLETO:
- id: número
- nombreTipo: string (ej: "VIP", "General", "Estudiante")
- precio: decimal con 2 decimales
- boletosDisponibles: número
- eventoId: número (referencia a evento)

COMPRA:
- id: número
- cantidad: número
- fechaDeCompra: timestamp
- compraTotal: decimal (cantidad × precio)
- tipoBoletoId: número (referencia a tipo boleto)

FUNCIONALIDADES REQUERIDAS:

PÁGINA DE INICIO/AUTENTICACIÓN:
1. Página de login con campos: correo, contraseña
2. Página de registro con campos: nombres, apellidos, correo, contraseña, seleccionar rol (Cliente/Admin)
3. Validación de formularios
4. Manejo y almacenamiento de token JWT en localStorage
5. Redirección tras login exitoso

PANEL DE ADMINISTRADOR (Solo si rol=ADMIN):
1. Dashboard con estadísticas:
   - Total de eventos creados
   - Total de tipos de boletos
   - Total de compras realizadas
   - Ingresos totales (suma de compraTotal)
2. Gestión de Eventos:
   - Tabla con todos los eventos
   - Botón para crear nuevo evento (form modal)
   - Botón para editar evento (form modal)
   - Botón para eliminar evento (confirmación)
   - Validar que nombreEvento sea único
   - Validar que fechaEvento sea futura
   - Validar que capacidadMaxima esté entre 1 y 10000
3. Gestión de Tipos de Boletos:
   - Tabla con todos los tipos de boletos (filterable por evento)
   - Botón para crear nuevo tipo de boleto (form modal)
   - Botón para editar tipo de boleto (form modal)
   - Botón para eliminar tipo de boleto (confirmación)
   - Mostrar disponibilidad actual
   - Validar que nombreTipo no esté vacío
   - Validar que precio > 0
   - Validar que boletosDisponibles >= 1
4. Gestión de Compras (Vista):
   - Tabla con todas las compras realizadas
   - Mostrar info del cliente, tipo de boleto, cantidad, total, fecha
   - Poder filtrar/buscar

PANEL DE CLIENTE (Si rol=CLIENTE):
1. Vista de Eventos Disponibles:
   - Listar todos los eventos próximos (con fecha futura)
   - Tarjetas o tabla con: nombre evento, fecha, capacidad
   - Botón "Ver Detalles" → muestra tipos de boletos disponibles
2. Página de Detalle de Evento:
   - Nombre, fecha, capacidad
   - Tabla de tipos de boletos con:
     - Nombre tipo
     - Precio unitario
     - Boletos disponibles
     - Campo de cantidad a comprar
     - Botón "Comprar" que suma al carrito
3. Carrito de Compras:
   - Mostrar items seleccionados (tipo boleto, cantidad, precio unitario, subtotal)
   - Botón "Proceder a comprar" que ejecuta el POST /api/compras
   - Validar que cantidad >= 1
   - Validar que cantidad <= boletosDisponibles
   - Mostrar total de compra
4. Historial de Compras:
   - Tabla con mis compras: evento, tipo boleto, cantidad, total, fecha
   - Botón para modificar cantidad (PUT /api/compras/{id})
   - Botón para cancelar compra (DELETE /api/compras/{id})
   - Filtros por rango de fechas, evento

FUNCIONALIDADES TRANSVERSALES:
1. Barra de navegación con:
   - Logo de EventFlow
   - Usuario autenticado (nombre, correo, rol)
   - Botón Logout (limpiar token de localStorage, redirigir a login)
   - Links a secciones según rol
2. Manejo de errores:
   - Mostrar mensajes de error del backend en interfaz amigable
   - Validación de formularios en cliente (antes de enviar)
   - Mostrar spinner/loader durante requests
3. Validaciones en tiempo real:
   - Email válido (formato)
   - Campos requeridos
   - Rango de valores (capacidad, precios, etc.)
4. Manejo de sesión:
   - Verificar token válido en cada carga
   - Mantener sesión activa por 24 horas (duración del token JWT)
   - Logout automático si token vence
5. Responsive Design:
   - Interfaz adaptable a móvil, tablet, desktop
   - Usar componentes de UI consistentes

TEMAS Y ESTILOS:
- Usar una paleta moderna (ej: azules, grises, blancos)
- Fuentes legibles y profesionales
- Espaciado consistente
- Iconos descriptivos donde sea apropiado
- Botones con estados hover, active, disabled
- Formularios claros con validación visual

PREFERENCIAS TÉCNICAS:
- HTML5 semántico
- CSS moderno o framework (Tailwind, Bootstrap, etc.)
- JavaScript vanilla o framework ligero (si Antigravity lo permite)
- Almacenamiento de token JWT en localStorage
- Hacer requests HTTP a http://localhost:8080

Genera un frontend profesional, funcional y amigable para EventFlow que integre con todos estos endpoints.
```

---

## NOTAS IMPORTANTES PARA EL DESARROLLADOR FRONTEND

1. **Token JWT**: Se almacena en `localStorage` con clave `token` después de login/registro
2. **Headers de autenticación**: Incluir en toda petición autenticada: `Authorization: Bearer <token>`
3. **Base URL**: `http://localhost:8080`
4. **Errores comunes**:
   - 401: Token inválido o expirado
   - 403: Permisos insuficientes (no es ADMIN)
   - 404: Recurso no encontrado
   - 400: Datos inválidos (validaciones)
5. **Flujo de compra**: 
   - Seleccionar tipo boleto → Especificar cantidad → Comprar
   - El `compraTotal` se calcula automáticamente: `cantidad × precio`
   - El stock se decrementa automáticamente
6. **Fechas**: Usar formato ISO 8601 en JSON: `2026-12-31T20:30:00`
7. **CORS**: El backend está configurado para permitir CORS desde el frontend

---

## GENERACIÓN RECOMENDADA EN ANTIGRAVITY

1. **Arquitectura**: MVC o similar adaptado a web
2. **Componentes principales**:
   - Componente de autenticación
   - Componente de navegación/layout
   - Componente de dashboard (admin)
   - Componente de eventos
   - Componente de carrito/compras
   - Componente de tablas (reutilizable)
   - Componente de modal (reutilizable)
3. **Gestión de estado**: Usar localStorage para token, puede usarse sessionStorage para datos temporales
4. **Librería HTTP**: Fetch API nativa o axios si está disponible
5. **Validaciones**: Implementar en cliente y confiar en validaciones del servidor

---

Este prompt proporciona toda la información necesaria para que Antigravity IDE genere un frontend completo y funcional para EventFlow.
```

---

## 📝 RESUMEN EJECUTIVO

| Aspecto | Detalle |
|--------|--------|
| **Nombre** | EventFlow - Plataforma de Gestión de Boletos |
| **Tipo** | Sistema de venta de entradas para eventos |
| **Backend** | Java Spring Boot 3.5.6, MySQL, JWT |
| **Base URL** | http://localhost:8080 |
| **Roles** | ADMIN (gestión completa), CLIENTE (comprador) |
| **Endpoints** | 18 endpoints REST documentados |
| **Autenticación** | JWT (24 horas) |
| **Entidades Principales** | Usuario, Evento, TipoBoleto, Compra |
| **Funcionalidades Admin** | Crear eventos, gestionar boletos, ver reportes |
| **Funcionalidades Cliente** | Ver eventos, comprar boletos, gestionar compras |

---

## 🎯 FUNCIONALIDADES CLAVE DEL FRONTEND

✅ **Autenticación & Autorización**
- Login/Registro de usuarios
- Diferenciación de roles (Admin/Cliente)
- Token JWT seguro

✅ **Panel Admin**
- Dashboard con estadísticas
- CRUD de eventos
- CRUD de tipos de boletos
- Visualización de compras

✅ **Panel Cliente**
- Catálogo de eventos
- Detalles de evento con tipos de boletos
- Carrito de compras
- Historial de compras con opción de modificar/cancelar

✅ **Funcionalidades Transversales**
- Navegación consistente
- Manejo de errores
- Validaciones en tiempo real
- Diseño responsive
- Interfaz moderna y profesional

