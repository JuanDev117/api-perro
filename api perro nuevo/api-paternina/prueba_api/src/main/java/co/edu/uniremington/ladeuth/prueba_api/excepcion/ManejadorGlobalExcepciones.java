package co.edu.uniremington.ladeuth.prueba_api.excepcion;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ManejadorGlobalExcepciones {
   
    //Método manejador de RecursoNoEncontrado (error 404)
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleRecursoNoEncontrado(RecursoNoEncontradoException ex){
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(),
               "Recurso No Encontrado",
                     ex.getMessage()
                );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);

    }

    //método manejador de RecursoDupicado (error 409)
    @ExceptionHandler(RecursoDuplicadoException.class)
    public ResponseEntity<ErrorResponse> handleRecursoDuplicadodo(RecursoDuplicadoException ex){
        ErrorResponse error = new ErrorResponse(HttpStatus.CONFLICT.value(),
               "Recurso Ya Existente",
                     ex.getMessage()
                );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);

    }

    //método manejador de Error no controlado (error 500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleManejadorGlobalExcepciones(Exception ex){
        String mensaje = ex.getMessage() != null ? ex.getMessage() : "Error inesperado en el servidor";
        ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
              "Error Interno Del Servidor",
              mensaje);

        ex.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
    
}
