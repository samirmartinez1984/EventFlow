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
     * @param solicitudRegistro DTO con nombre, apellido, cédula, correo y contraseña
     * @return RespuestaAutenticación con token JWT válido
     */
    public RespuestaAutenticacion registrar(SolicitudRegistro solicitudRegistro) {
        Usuario usuario = Usuario.builder()
                .nombre(solicitudRegistro.nombres())
                .primerApellido(solicitudRegistro.apellidos())
                .cedula(solicitudRegistro.cedula())
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
     * @param solicitudRegistro DTO con nombre, apellido, cédula, correo y contraseña
     * @return RespuestaAutenticación con token JWT válido para ADMIN
     */
    public RespuestaAutenticacion registrarAdmin(SolicitudRegistro solicitudRegistro) {
        Usuario usuario = Usuario.builder()
                .nombre(solicitudRegistro.nombres())
                .primerApellido(solicitudRegistro.apellidos())
                .cedula(solicitudRegistro.cedula())
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
     * @param solicitudAutenticacion DTO con correo y contraseña del usuario
     * @return RespuestaAutenticación con token JWT válido
     * @throws UsernameNotFoundException si usuario no existe
     * @throws BadCredentialsException si contraseña es incorrecta
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
     * @param correo Correo del usuario a eliminar
     * @throws RecursoNoEncontradoException si usuario no existe
     */
    public void eliminarUsuarioPorCorreo(String correo){
        Usuario usuarioEncontrado = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        usuarioRepository.delete(usuarioEncontrado);
        System.out.println("Usuario eliminado " + correo);
    }
}
