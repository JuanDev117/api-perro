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
import co.edu.uniremington.ladeuth.prueba_api.modelo.DesparacitarAdulto;
import co.edu.uniremington.ladeuth.prueba_api.repositorys.DespaAdultoRepository;
import co.edu.uniremington.ladeuth.prueba_api.servicio.DespaAService;

@Service
public class DespaAServiceImpl implements DespaAService {
    
       
 private final DespaAdultoRepository despaAdultoRepository;

 private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public DespaAServiceImpl(DespaAdultoRepository despaAdultoRepository){
        this.despaAdultoRepository = despaAdultoRepository;
    }

    // leer todo
    public List<DesparacitarAdulto> listarTodos() {
        List<DesparacitarAdulto> desparacitar = despaAdultoRepository.findAll();
        if (desparacitar.isEmpty()) {
            throw new RecursoNoEncontradoException("No hay planes de desparacitación registrados");
        }
        return desparacitar;
    }

    // buscar por nombre 
    @Override
    public DesparacitarAdulto buscarPorNombre(String nombre) {
        return despaAdultoRepository.findByNombreContainingIgnoreCase(nombre)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se ha asignado un plan de desparacitación al perro con nombre: " + nombre));
    }

    public List<DesparacitarAdulto> buscarPorFecha(LocalDateTime fechaCreacion){
    List<DesparacitarAdulto> desparacitar = despaAdultoRepository.findByFechaCreacion(fechaCreacion);
        if (desparacitar.isEmpty()) {
            throw new RecursoNoEncontradoException("No hay planes de desparacitación hechos en la fecha" + fechaCreacion);
        }
        return desparacitar;
      }

    public List<DesparacitarAdulto> listarActivos(Boolean activo){
    List<DesparacitarAdulto> desparacitar = despaAdultoRepository.findByActivo(activo);
        if (desparacitar.isEmpty()) {
            throw new RecursoNoEncontradoException("No hay planes de desparacitación activos registrados");
        }
        return desparacitar;
      }

      public Optional<DesparacitarAdulto> cambiarActivo(Long id, Boolean activo){
        return despaAdultoRepository.findById(id).map(planExistente -> {
        planExistente.setActivo(activo);
        DesparacitarAdulto actualizado = despaAdultoRepository.save(planExistente);
        return actualizado;
         });
      }

    // agregar
    @Override
    public DesparacitarAdulto agregar(DesparacitarAdulto vacuna, Long cantidad, ChronoUnit unidadTiempo) {
        if (despaAdultoRepository.existsById(vacuna.getId())) {
            throw new RecursoDuplicadoException("Ya existe un plan de desparacitación con el id: " + vacuna.getId());
        }
        DesparacitarAdulto nuevo = despaAdultoRepository.save(vacuna);
        programarAlerta(cantidad, unidadTiempo);
        return nuevo;
    }

    //Emitir una alerta cada que se pase el tiempo de control
    public void programarAlerta(long cantidad, ChronoUnit unidadTiempo) {
        long delayEnSegundos = unidadTiempo.getDuration().getSeconds() * cantidad;

        scheduler.scheduleAtFixedRate(() -> {
            boolean vencidos = despaAdultoRepository.existsByFechaAlerta(LocalDateTime.now());

            if (vencidos) {
                System.out.println("⚠️ Alerta: Es hora de la desparacitación");
            }
        }, delayEnSegundos, delayEnSegundos, TimeUnit.SECONDS);
    }

    /*private boolean verificarRegistros(ChronoUnit unidadTiempo, long cantidad) {
        LocalDateTime fechaLimite = LocalDateTime.now().minus(cantidad, unidadTiempo);
        return despaAdultoRepository.existsByFechaCreacion(fechaLimite);
    
    }*/

}
