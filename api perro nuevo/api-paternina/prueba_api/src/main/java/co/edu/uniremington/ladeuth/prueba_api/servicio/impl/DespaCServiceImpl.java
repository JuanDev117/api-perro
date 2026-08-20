package co.edu.uniremington.ladeuth.prueba_api.servicio.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import co.edu.uniremington.ladeuth.prueba_api.excepcion.RecursoDuplicadoException;
import co.edu.uniremington.ladeuth.prueba_api.excepcion.RecursoNoEncontradoException;
import co.edu.uniremington.ladeuth.prueba_api.modelo.DesparacitarCachorro;
import co.edu.uniremington.ladeuth.prueba_api.repositorys.DespaCachorroRepository;
import co.edu.uniremington.ladeuth.prueba_api.servicio.DespaCService;

@Service
public class DespaCServiceImpl implements DespaCService {
    
       
 private final DespaCachorroRepository despaCachorroRepository;

 private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public DespaCServiceImpl(DespaCachorroRepository despaCachorroRepository){
        this.despaCachorroRepository = despaCachorroRepository;
    }

    // leer todo
    public List<DesparacitarCachorro> listarTodos() {
        List<DesparacitarCachorro> desparacitar = despaCachorroRepository.findAll();
        if (desparacitar.isEmpty()) {
            throw new RecursoNoEncontradoException("No hay planes de desparacitación registrados");
        }
        return desparacitar;
    }

    // buscar por nombre 
    @Override
    public DesparacitarCachorro buscarPorNombre(String nombre) {
        return despaCachorroRepository.findByNombreContainingIgnoreCase(nombre)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se ha asignado un plan de desparacitación al cachorro con nombre: " + nombre));
    }

    public List<DesparacitarCachorro> buscarPorFecha(LocalDateTime fechaCreacion){
    List<DesparacitarCachorro> desparacitar = despaCachorroRepository.findByFechaCreacion(fechaCreacion);
        if (desparacitar.isEmpty()) {
            throw new RecursoNoEncontradoException("No hay planes de desparacitación hechos en la fecha" + fechaCreacion);
        }
        return desparacitar;
      }

    public List<DesparacitarCachorro> listarActivos(Boolean activo){
    List<DesparacitarCachorro> desparacitar = despaCachorroRepository.findByActivo(activo);
        if (desparacitar.isEmpty()) {
            throw new RecursoNoEncontradoException("No hay planes de desparacitación activos registrados");
        }
        return desparacitar;
      }

      public Optional<DesparacitarCachorro> cambiarActivo(Long id, Boolean activo){
        return despaCachorroRepository.findById(id).map(planExistente -> {
        planExistente.setActivo(activo);
        DesparacitarCachorro actualizado = despaCachorroRepository.save(planExistente);
        return actualizado;
         });
      }

    // agregar
    @Override
    public DesparacitarCachorro agregar(DesparacitarCachorro vacuna, Long cantidad, ChronoUnit unidadTiempo) {
        if (despaCachorroRepository.existsById(vacuna.getId())) {
            throw new RecursoDuplicadoException("Ya existe un plan de desparacitación con el id: " + vacuna.getId());
        }
        DesparacitarCachorro nuevo = despaCachorroRepository.save(vacuna);
        programarAlerta(cantidad, unidadTiempo);
        return nuevo;
    }

    
    //Emitir una alerta cada que se pase el tiempo de control
    public void programarAlerta(long cantidad, ChronoUnit unidadTiempo) {
        long delayEnSegundos = unidadTiempo.getDuration().getSeconds() * cantidad;

        scheduler.scheduleAtFixedRate(() -> {
            boolean vencidos = despaCachorroRepository.existsByFechaAlerta(LocalDateTime.now());

            if (vencidos) {
                System.out.println("⚠️ Alerta: Es hora de la desparacitación");
            }
        }, delayEnSegundos, delayEnSegundos, TimeUnit.SECONDS);
    }

    /*private boolean verificarRegistros(ChronoUnit unidadTiempo, long cantidad) {
        LocalDateTime fechaLimite = LocalDateTime.now().minus(cantidad, unidadTiempo);
        return despaCachorroRepository.existsByFechaCreacion(fechaLimite);
    
    }*/

}
