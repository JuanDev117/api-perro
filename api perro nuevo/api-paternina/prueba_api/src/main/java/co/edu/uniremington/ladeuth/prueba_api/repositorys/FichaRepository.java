package co.edu.uniremington.ladeuth.prueba_api.repositorys;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.edu.uniremington.ladeuth.prueba_api.modelo.Ficha;

public interface FichaRepository extends JpaRepository<Ficha, Long> {

    Optional<Ficha> findById(Long id);
    List<Ficha> findByRazaContainingIgnoreCase(String raza);
    List<Ficha> findBySexo(String sexo);
    List<Ficha> findByFechaNacimiento(LocalDate fechaNacimiento);
    List<Ficha> findByEsperanzaDeVida(String esperanzaDeVida);
    List<Ficha> findByPeso(String peso);
    List<Ficha> findByAltura(String altura);
    List<Ficha> findByColores(String colores);
    List<Ficha> findByPelaje(String pelaje);

    @Modifying
    @Query("delete from Ficha f where f.id = :id")
    int deleteFichaById(@Param("id") Long id);

    boolean existsById(Long id);
}
