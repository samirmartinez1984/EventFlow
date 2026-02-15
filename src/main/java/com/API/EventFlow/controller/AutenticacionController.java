package com.API.EventFlow.controller;

import com.API.EventFlow.seguridadDTO.RespuestaAutenticacion;
import com.API.EventFlow.seguridadDTO.SolicitudAutenticacion;
import com.API.EventFlow.seguridadDTO.SolicitudRegistro;
import com.API.EventFlow.service.AutenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/autenticación")
@RequiredArgsConstructor
public class AutenticacionController {

    private final AutenticationService serviceAutentication;

    @PostMapping("/registro")
    public ResponseEntity<RespuestaAutenticacion> registrar(@RequestBody SolicitudRegistro solicitudRegistro){
        return ResponseEntity.ok(serviceAutentication.registrar(solicitudRegistro));
    }


    @PostMapping("/registro-admin")
    public ResponseEntity<RespuestaAutenticacion> registrarAdmin(@RequestBody SolicitudRegistro solicitudRegistro){
        return ResponseEntity.ok(serviceAutentication.registrarAdmin(solicitudRegistro));
    }


    @PostMapping("/login")
    public ResponseEntity<RespuestaAutenticacion> autenticar(@RequestBody SolicitudAutenticacion solicitudAutenticacion){
            return ResponseEntity.ok(serviceAutentication.autenticar(solicitudAutenticacion));
    }


    @DeleteMapping("/{correo}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable String correo){
        serviceAutentication.eliminarUsuarioPorCorreo(correo);
        return ResponseEntity.noContent().build();
    }
}
