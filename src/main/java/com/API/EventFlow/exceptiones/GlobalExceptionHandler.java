package com.API.EventFlow.exceptiones;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Manejado global de excepciones para la aplicación.
 *
 * <p>
 * Esta clase centraliza el manejo de excepciones y convierte excepciones Java
 * en respuestas HTTP adecuadas con códigos y mensajes legibles por el cliente.
 * </p>
 *
 * <p>
 * Captura errores de validación de DTO, errores de negocio personalizados
 * y excepciones generales, retornando un JSON con detalles.
 * </p>
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    // Validaciones fallidas en DTO
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarValidaciones(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errores, HttpStatus.BAD_REQUEST);
    }

    //  Datos inválidos personalizados
    @ExceptionHandler(DatosInvalidosException.class)
    public ResponseEntity<Map<String, String>> manejarDatosInvalidos(DatosInvalidosException ex) {
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("error", ex.getMessage());
        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }

    //  Recurso no encontrado
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> manejarRecursoNoEncontrado(RecursoNoEncontradoException ex) {
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("error", ex.getMessage());
        return new ResponseEntity<>(respuesta, HttpStatus.NOT_FOUND);
    }

    //  Stock insuficiente
    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<Map<String, String>> manejarStockInsuficiente(StockInsuficienteException ex) {
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("error", ex.getMessage());
        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }

    //  Excepción general
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> manejarExcepcionGeneral(Exception ex) {
        Map<String, String> respuesta = new HashMap<>();
        respuesta.put("error", "Error interno del servidor");
        respuesta.put("detalle", ex.getMessage());
        return new ResponseEntity<>(respuesta, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}