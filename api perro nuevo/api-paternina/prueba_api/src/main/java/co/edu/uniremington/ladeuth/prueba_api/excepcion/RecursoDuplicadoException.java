package co.edu.uniremington.ladeuth.prueba_api.excepcion;

public class RecursoDuplicadoException extends RuntimeException {
    public RecursoDuplicadoException(String message){
        super(message);
    }
}
