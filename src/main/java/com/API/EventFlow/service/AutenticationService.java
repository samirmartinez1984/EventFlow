package com.API.EventFlow.service;


import com.API.EventFlow.exceptiones.RecursoNoEncontradoException;
import com.API.EventFlow.config.JwtService;
import com.API.EventFlow.model.Rol;
import com.API.EventFlow.model.Usuario;
import com.API.EventFlow.repository.UsuarioRepository;
import com.API.EventFlow.seguridadDTO.RespuestaAutenticacion;
import com.API.EventFlow.seguridadDTO.SolicitudAutenticacion;
import com.API.EventFlow.seguridadDTO.SolicitudRegistro;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio de autenticación y autorización de usuarios.
 *
 * <p>
 * La clase {@code AutenticationService} encapsula la lógica de seguridad del sistema,
 * incluyendo registro de usuarios, autenticación y generación de tokens JWT.
 * </p>
 *
 * @see Usuario
 * @see Rol
 * @see JwtService
 */
@Service
@RequiredArgsConstructor
public class AutenticationService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Registra un nuevo usuario CLIENTE en el sistema.
     *
     * <p>
     * <strong>Flujo de operaciones:</strong>
     * </p>
     * <ol>
     *   <li>Crea nuevo Usuario con rol CLIENTE</li>
     *   <li>Encripta contraseña usando PasswordEncoder</li>
     *   <li>Guarda usuario en BD</li>
     *   <li>Auténtico usuario recién registrado</li>
     *   <li>Genera token JWT para sesión inmediata</li>
     *   <li>Retorna token en RespuestaAutenticacion</li>
     * </ol>
     *
     * <p>
     * <strong>Seguridad:</strong>
     * </p>
     * <ul>
     *   <li>Contraseña se encripta con bcrypt antes de guardar</li>
     *   <li>Usuario se autentica automáticamente después de registro</li>
     *   <li>Token JWT se genera con duración limitada</li>
     * </ul>
     *
     * @param solicitudRegistro DTO con nombre, apellido, correo y contraseña
     * @return RespuestaAutenticación con token JWT válido
     *
     * @example
     * SolicitudRegistro solicitud = new SolicitudRegistro("Juan", "Pérez", "juan@example.com", "password123");
     * RespuestaAutenticación respuesta = autenticationService.registrar(solicitud);
     * System.out.println("Token: " + respuesta.token());
     * // Usuario puede usar token inmediatamente sin hacer login adicional
     */
    public RespuestaAutenticacion registrar(SolicitudRegistro solicitudRegistro) {
        Usuario usuario = Usuario.builder()
                .nombre(solicitudRegistro.nombres())
                .primerApellido(solicitudRegistro.apellidos())
                .correo(solicitudRegistro.correo())
                .clave(passwordEncoder.encode(solicitudRegistro.clave()))
                .rol(Rol.CLIENTE)
                .build();

        usuarioRepository.save(usuario);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        solicitudRegistro.correo(),
                        solicitudRegistro.clave()
                )
        );

        String token = jwtService.generarToken(usuario);
        return new RespuestaAutenticacion(token);
    }

    /**
     * Registra un nuevo usuario ADMIN en el sistema.
     *
     * <p>
     * <strong>Diferencia con registrar():</strong>
     * Este método asigna rol ADMIN en lugar de CLIENTE.
     * Requiere permisos de administrador para ejecutar.
     * </p>
     *
     * <p>
     * <strong>Flujo de operaciones:</strong>
     * </p>
     * <ol>
     *   <li>Crea nuevo Usuario con rol ADMIN</li>
     *   <li>Encripta contraseña usando PasswordEncoder</li>
     *   <li>Guarda usuario en BD</li>
     *   <li>Auténtico usuario recién registrado</li>
     *   <li>Genera token JWT con permisos de administrador</li>
     *   <li>Retorna token en RespuestaAutenticacion</li>
     * </ol>
     *
     * @param solicitudRegistro DTO con nombre, apellido, correo y contraseña
     * @return RespuestaAutenticación con token JWT válido para ADMIN
     *
     * @example
     * SolicitudRegistro solicitud = new SolicitudRegistro("Admin", "Sistema", "admin@example.com", "securePass123");
     * RespuestaAutenticación respuesta = autenticationService.registrarAdmin(solicitud);
     * // Usuario ahora tiene acceso a endpoints administrativos
     */
    public RespuestaAutenticacion registrarAdmin(SolicitudRegistro solicitudRegistro) {
        Usuario usuario = Usuario.builder()
                .nombre(solicitudRegistro.nombres())
                .primerApellido(solicitudRegistro.apellidos())
                .correo(solicitudRegistro.correo())
                .clave(passwordEncoder.encode(solicitudRegistro.clave()))
                .rol(Rol.ADMIN)
                .build();

        usuarioRepository.save(usuario);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        solicitudRegistro.correo(),
                        solicitudRegistro.clave()));

        String token = jwtService.generarToken(usuario);
        return new RespuestaAutenticacion(token);
    }

    /**
     * Autentica un usuario existente validando credenciales.
     *
     * <p>
     * <strong>Flujo de operaciones:</strong>
     * </p>
     * <ol>
     *   <li>Valida credenciales (correo + contraseña) con AuthenticationManager</li>
     *   <li>Si válido, busca usuario en BD</li>
     *   <li>Genera token JWT para la sesión</li>
     *   <li>Retorna token en RespuestaAutenticacion</li>
     * </ol>
     *
     * <p>
     * <strong>Validaciones:</strong>
     * </p>
     * <ul>
     *   <li>AuthenticationManager valida que contraseña coincida (bcrypt)</li>
     *   <li>Usuario debe existir en BD</li>
     * </ul>
     *
     * @param solicitudAutenticacion DTO con correo y contraseña del usuario
     * @return RespuestaAutenticación con token JWT válido
     *
     * @throws UsernameNotFoundException si usuario no existe
     * @throws BadCredentialsException si contraseña es incorrecta (desde AuthenticationManager)
     *
     * @example
     * SolicitudAutenticación solicitud = new SolicitudAutenticacion("usuario@example.com", "password123");
     * RespuestaAutenticación respuesta = autenticationService.autenticar(solicitud);
     * String token = respuesta.token();
     * // Cliente usa este token en peticiones subsecuentes
     */
    public RespuestaAutenticacion autenticar(SolicitudAutenticacion solicitudAutenticacion) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        solicitudAutenticacion.correo(),
                        solicitudAutenticacion.clave()
                )
        );

        Usuario usuario = usuarioRepository.findByCorreo(solicitudAutenticacion.correo())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        String token = jwtService.generarToken(usuario);
        return new RespuestaAutenticacion(token);
    }

    /**
     * Elimina un usuario del sistema por correo.
     *
     * <p>
     * <strong>Nota importante:</strong>
     * Debido a las cascadas configuradas en JPA (CascadeType. ALL),
     * eliminar un usuario también elimina automáticamente:
     * </p>
     * <ul>
     *   <li>Todos los eventos que creó (si es ADMIN)</li>
     *   <li>Todos los tipos de boleto que creó (si es ADMIN)</li>
     *   <li>Todas las compras que realizó</li>
     * </ul>
     *
     * <p>
     * <strong>Riesgo:</strong> La eliminación es destructiva. Considerar
     * implementar soft delete (marcar como eliminado sin borrar datos).
     * </p>
     *
     * @param correo Correo del usuario a eliminar
     * @throws RecursoNoEncontradoException si usuario no existe
     *
     * @example
     * autenticationService.eliminarUsuarioPorCorreo("usuario@example.com");
     * System.out.println("Usuario y todos sus datos eliminados");
     */
    public void eliminarUsuarioPorCorreo(String correo){
        Usuario usuarioEncontrado = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        usuarioRepository.delete(usuarioEncontrado);
        System.out.println("Usuario eliminado " + correo);
    }
}
