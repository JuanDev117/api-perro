package co.edu.uniremington.ladeuth.prueba_api.repositorys;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.uniremington.ladeuth.prueba_api.modelo.ChequeoGeneral;

public interface ChequeoGRepository extends JpaRepository<ChequeoGeneral, Long> {
    
    Optional<ChequeoGeneral> findById(Long id);
    Optional<ChequeoGeneral> findByNombreContainingIgnoreCase(String nombre);
    List<ChequeoGeneral> findByActivo(Boolean activo);
    List<ChequeoGeneral> findByFechaCreacion(LocalDateTime fechaCreacion);
    boolean existsByFechaAlerta(LocalDateTime fecha);
    boolean existsById(Long id);

}
