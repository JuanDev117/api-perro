package co.edu.uniremington.ladeuth.prueba_api.servicio.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import co.edu.uniremington.ladeuth.prueba_api.dto.FichaDTO;
import co.edu.uniremington.ladeuth.prueba_api.excepcion.RecursoNoEncontradoException;
import co.edu.uniremington.ladeuth.prueba_api.modelo.Ficha;
import co.edu.uniremington.ladeuth.prueba_api.repositorys.FichaRepository;
import co.edu.uniremington.ladeuth.prueba_api.servicio.FichaService;

@Service
public class FichaServiceImpl implements FichaService {
    
    private final FichaRepository fichaRepository;

    public FichaServiceImpl(FichaRepository fichaRepository){
        this.fichaRepository = fichaRepository;
    }

    @Override
    public List<FichaDTO> listarTodos() {
    return fichaRepository.findAll()
                          .stream()
                          .map(this::convertirFichaADTO)
                          .collect(Collectors.toList());
    }

    @Override
    public FichaDTO buscarPorId(Long id) {
        Ficha ficha = fichaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una ficha con ID: " + id));
        return convertirFichaADTO(ficha);
    }

    @Override
     public List<FichaDTO> buscarPorRaza(String raza){
    return fichaRepository.findByRazaContainingIgnoreCase(raza)
                          .stream()
                          .map(this::convertirFichaADTO)
                          .collect(Collectors.toList());
    }

    @Override
     public List<FichaDTO> buscarPorSexo(String sexo){
    return fichaRepository.findBySexo(sexo)
                          .stream()
                          .map(this::convertirFichaADTO)
                          .collect(Collectors.toList());
    }

    @Override
     public List<FichaDTO> buscarPorFechaNacimiento(LocalDate fechaNacimiento){
    return fichaRepository.findByFechaNacimiento(fechaNacimiento)
                          .stream()
                          .map(this::convertirFichaADTO)
                          .collect(Collectors.toList());
    }

     @Override
     public List<FichaDTO> buscarPorEsperanzaDeVida(String esperanzaDeVida){
    return fichaRepository.findByEsperanzaDeVida(esperanzaDeVida)
                          .stream()
                          .map(this::convertirFichaADTO)
                          .collect(Collectors.toList());
    }

    @Override
     public List<FichaDTO> buscarPorPeso(String peso){
    return fichaRepository.findByPeso(peso)
                          .stream()
                          .map(this::convertirFichaADTO)
                          .collect(Collectors.toList());
    }

    @Override
     public List<FichaDTO> buscarPorAltura(String altura){
    return fichaRepository.findByAltura(altura)
                          .stream()
                          .map(this::convertirFichaADTO)
                          .collect(Collectors.toList());
    }

    @Override
     public List<FichaDTO> buscarPorColores(String colores){
    return fichaRepository.findByColores(colores)
                          .stream()
                          .map(this::convertirFichaADTO)
                          .collect(Collectors.toList());
    }

    @Override
     public List<FichaDTO> buscarPorPelaje(String pelaje){
    return fichaRepository.findByColores(pelaje)
                          .stream()
                          .map(this::convertirFichaADTO)
                          .collect(Collectors.toList());
    }

//actualizar
    @Override
    public FichaDTO actualizar(Long id, FichaDTO fichaDTO) {
        Ficha fichaExistente = fichaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe ficha con ID: " + id));

        fichaExistente.setRaza(fichaDTO.getRaza());
        fichaExistente.setFechaNacimiento(fichaDTO.getFechaNacimiento());
        fichaExistente.setPeso(fichaDTO.getPeso());
        fichaExistente.setAltura(fichaDTO.getAltura());

        Ficha actualizada = fichaRepository.save(fichaExistente);
        return convertirFichaADTO(actualizada);
    }

//eliminar
    public boolean eliminar(Long id){
        return fichaRepository.deleteFichaById(id) > 0;
    }

     private FichaDTO convertirFichaADTO(Ficha ficha) {
        FichaDTO dto = new FichaDTO();
        dto.setId(ficha.getId());
        dto.setRaza(ficha.getRaza());
         dto.setSexo(ficha.getSexo());
        dto.setFechaNacimiento(ficha.getFechaNacimiento());
        dto.setEsperanzaDeVida(ficha.getEsperanzaDeVida());
        dto.setPeso(ficha.getPeso());
        dto.setAltura(ficha.getAltura());
        dto.setColores(ficha.getColores());
        dto.setPelaje(ficha.getPelaje());
        return dto;
    }

}
