package cl.duoc.mineria.asignador.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Manejo de Errores de Validación (Ej: Falla un @NotNull o @NotBlank en el DTO)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Existen errores en los datos enviados");
        problemDetail.setTitle("Error de Validación");
        problemDetail.setType(URI.create("https://api.mineria.cl/errors/bad-request"));
        problemDetail.setProperty("timestamp", Instant.now());

        // Extraemos qué campos fallaron exactamente y por qué
        Map<String, String> errores = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.put(error.getField(), error.getDefaultMessage());
        }
        problemDetail.setProperty("errores", errores);

        return problemDetail;
    }

    // 2. Manejo de Errores de Negocio (Cuando no encontramos la ruta en la BD)
    @ExceptionHandler(RutaNotFoundException.class)
    public ProblemDetail handleRutaNotFoundException(RutaNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Ruta no encontrada");
        problemDetail.setType(URI.create("https://api.mineria.cl/errors/not-found"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    // 3. Manejo de Errores de Conversión (Ej: Intentan asignar destino "BODEGA" en vez de CHANCADO/RELAVES)
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Argumento Inválido");
        problemDetail.setType(URI.create("https://api.mineria.cl/errors/invalid-argument"));
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    // 4. Fallback Global (Para cualquier otro error inesperado del servidor)
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGlobalException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error interno en el servidor Asignador");
        problemDetail.setTitle("Error Interno");
        problemDetail.setType(URI.create("https://api.mineria.cl/errors/internal-error"));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("detalle_tecnico", ex.getMessage()); // Opcional, ayuda a debugear
        return problemDetail;
    }
}