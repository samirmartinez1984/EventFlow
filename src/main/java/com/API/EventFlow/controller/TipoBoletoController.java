package com.API.EventFlow.controller;

import com.API.EventFlow.dto.TipoBoletoDTO;
import com.API.EventFlow.service.TipoBoletoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/tipoboletos")
@RequiredArgsConstructor
public class TipoBoletoController {

    // Inyeccion de dependencias por contructor.
    private final TipoBoletoService tipoBoletoService;

    // Controlador para listar todos los boletos.
    @GetMapping
    public ResponseEntity<List<TipoBoletoDTO>> listarTodosLosBoletos(){
        List<TipoBoletoDTO>tipoBoletos = tipoBoletoService.listarTodosLosBoletos();
        return ResponseEntity.status(HttpStatus.OK).body(tipoBoletos);
    }


    //Controlador para obtener un tipo de boleto.
    @GetMapping("/{id}")
    public ResponseEntity<TipoBoletoDTO> obtenerTipoBoletoId(@PathVariable Long id){
        TipoBoletoDTO tipoBoletoEncontrado = tipoBoletoService.obtenerTipoBoletoPorId(id);
        return ResponseEntity.status(HttpStatus.OK).body(tipoBoletoEncontrado);
    }


    // Controlador para crear un tipo boleto.
    @PostMapping
    public ResponseEntity<TipoBoletoDTO>crearBolero(@RequestBody TipoBoletoDTO tipoBoletoDTO, Principal principal){
        String correo = principal.getName();
        TipoBoletoDTO boletoCreado = tipoBoletoService.crearTipoBoleto(tipoBoletoDTO, correo);
        return ResponseEntity.status(HttpStatus.CREATED).body(boletoCreado);
    }


    // Controlador para actualizar un boleto.
    @PutMapping("/{id}")
    public ResponseEntity<TipoBoletoDTO>actualizarTipoBoleto(@RequestBody TipoBoletoDTO tipoBoletoDTO, @PathVariable Long id){
        TipoBoletoDTO boletoActualizado = tipoBoletoService.actualizarTipoBoleto(tipoBoletoDTO, id);
        return ResponseEntity.status(HttpStatus.CREATED).body(boletoActualizado);
    }


    // Controlador para eliminar un boleto.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTipoBoletoPorId(@PathVariable Long id){
         tipoBoletoService.eliminarBoleto(id);
        return ResponseEntity.noContent().build();
    }
}
