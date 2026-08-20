package co.edu.uniremington.ladeuth.prueba_api.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.edu.uniremington.ladeuth.prueba_api.modelo.Perro;
import java.util.List;
import java.util.Optional;


public interface PerroRepository extends JpaRepository<Perro, Long> {

    Optional<Perro> findByCodigoInterno(String codigoInterno);
    Optional<Perro> findById(Long id);
    Optional<Perro> findByNombreContainingIgnoreCase(String nombre);
    List<Perro> findByEdad(Long edad);
    List<Perro> findByDisponible(Boolean disponible);
    List<Perro> findByFichaId(Long fichaId);

    @Modifying
    @Query("delete from Perro r where r.id = :id")
    int deletePerroById(@Param("id") Long id);

    boolean existsById(Long id);

    
    
}
 