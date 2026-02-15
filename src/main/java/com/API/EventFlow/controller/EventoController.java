package com.API.EventFlow.controller;

import com.API.EventFlow.dto.EventoDTO;
import com.API.EventFlow.service.EventoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/eventos")
@RequiredArgsConstructor
public class EventoController {

    // Inyecccion de dependencia por constructor.
    private final EventoService eventoService;

    // Controlador para crear un evento.POST
    @PostMapping
    public ResponseEntity<EventoDTO> crearEvento(@Valid @RequestBody EventoDTO eventoDTO, Principal principal){

        // Obtenemos el email del usuario autenticado.
        String correo = principal.getName();

        // 2. Llamamos al servicio con el DTO y el email
        EventoDTO eventoCreado = eventoService.crearEvento(eventoDTO, correo);

        // Retornamos el DTO creado.
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoCreado);
    }


    // Controlador para buscar un evento por ID. GET
    @GetMapping("/{id}")
    public ResponseEntity<EventoDTO> buscarEventoPorId(@PathVariable Long id){
        EventoDTO eventoEncontrado = eventoService.obtenerEventoPorId(id);
        return ResponseEntity.status(HttpStatus.OK).body(eventoEncontrado);
    }


    // Controlador para listar todos los eventos. GET
    @GetMapping
    public ResponseEntity<List<EventoDTO>> listarTodosLosEventos(){
        List<EventoDTO> eventoDTOList = eventoService.listarTodosLosEventos();
        return ResponseEntity.status(HttpStatus.OK).body(eventoDTOList);
    }


    // Controlador para actualizar un evento. PUT
    @PutMapping("/{id}")
    public ResponseEntity<EventoDTO> actualizarEvento(@Valid @RequestBody EventoDTO eventoDTO, @PathVariable Long id){
        EventoDTO eventoActualizado = eventoService.actualizarEvento(eventoDTO, id);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoActualizado);
    }


    // Controlador para eliminar un evento por ID. DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEvento(@PathVariable Long id){
        eventoService.eliminarEvento(id);
        return ResponseEntity.noContent().build();
    }
}
