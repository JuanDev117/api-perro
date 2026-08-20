package co.edu.uniremington.ladeuth.prueba_api.servicio;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import co.edu.uniremington.ladeuth.prueba_api.modelo.ChequeoGeneral;

public interface ChequeoGService {
    
    List<ChequeoGeneral> listarTodos();
    ChequeoGeneral buscarPorNombre(String nombre);
    List<ChequeoGeneral> buscarPorFecha(LocalDateTime fechaCreacion);
    List<ChequeoGeneral> listarActivos(Boolean activo);
    Optional<ChequeoGeneral> cambiarActivo(Long id, Boolean activo);
    ChequeoGeneral agregar(ChequeoGeneral chequeo, Long cantidad, ChronoUnit unidadTiempo);
    void programarAlerta(long cantidad, ChronoUnit unidadTiempo);

}
