package co.edu.uniremington.ladeuth.prueba_api.servicio;

import java.time.LocalDate;
import java.util.List;

import co.edu.uniremington.ladeuth.prueba_api.dto.FichaDTO;

public interface FichaService {

    List<FichaDTO> listarTodos();

    FichaDTO buscarPorId(Long id);

    List<FichaDTO> buscarPorRaza( String raza);

    List<FichaDTO> buscarPorSexo( String sexo);

    List<FichaDTO> buscarPorFechaNacimiento( LocalDate fechaNacimiento);

    List<FichaDTO> buscarPorEsperanzaDeVida(String esperanzaDeVida);

    List<FichaDTO> buscarPorPeso(String peso);

    List<FichaDTO> buscarPorAltura(String altura);

    List<FichaDTO> buscarPorColores(String colores);

    List<FichaDTO> buscarPorPelaje(String pelaje);

    FichaDTO actualizar(Long id, FichaDTO fichaDTO);

    boolean eliminar(Long id);
}
