package co.edu.uniremington.ladeuth.prueba_api.excepcion;

public class RecursoNoEncontradoException extends RuntimeException {
    
    public RecursoNoEncontradoException(String message){
        super(message);
    }
}
