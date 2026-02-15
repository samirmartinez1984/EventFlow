package com.API.EventFlow.controller;

import com.API.EventFlow.exceptiones.DatosInvalidosException;
import com.API.EventFlow.dto.CompraDTO;
import com.API.EventFlow.service.CompraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
public class CompraController {

    // Inyeccion de dependencia por contructor.
    public final CompraService compraService;

    // Controlador para obtener todas las compras.
    @GetMapping
    public ResponseEntity<List<CompraDTO>> obtenerTodasLasCompras(){
        List<CompraDTO> compras = compraService.listarTodosLasCompras();
        return ResponseEntity.status(HttpStatus.OK).body(compras);
    }


    // Controlador para obtener una compra por ID.
    @GetMapping("/{id}")
    public ResponseEntity<CompraDTO> obtenerCompraPorId(@PathVariable Long id){
        CompraDTO compraEncontrada = compraService.listarCompraPorId(id);
        return ResponseEntity.status(HttpStatus.OK).body(compraEncontrada);
    }


    // Controlador para crear una compra.
    @PostMapping
    public ResponseEntity<CompraDTO> crearCompra(@RequestBody CompraDTO compraDTO, Principal principal){

        if (principal == null || principal.getName() == null) {
            throw new DatosInvalidosException("No se pudo identificar al usuario autenticado");
        }
        String correo = principal.getName();
        CompraDTO compraCreada = compraService.crearCompra(compraDTO, correo);
        return ResponseEntity.status(HttpStatus.CREATED).body(compraCreada);
    }


    // Controlador para eliminar una compra.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCompra(@PathVariable Long id){
        compraService.eliminarCompraId(id);
        return ResponseEntity.noContent().build();
    }


    // Controlador para actualizar una compra.
    @PutMapping("/{id}")
    public ResponseEntity<CompraDTO> actualizarCompra(@RequestBody CompraDTO compraDTO,
                                                      @PathVariable Long id, Principal principal){

        if (principal == null || principal.getName() == null) {
            throw new DatosInvalidosException("No se pudo identificar al usuario autenticado");
        }
        String correo = principal.getName();

        CompraDTO compraActualizada = compraService.actualizarCompra(compraDTO, id, correo);
        return ResponseEntity.status(HttpStatus.CREATED).body(compraActualizada);
    }
}
