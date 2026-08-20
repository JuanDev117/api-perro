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
import co.edu.uniremington.ladeuth.prueba_api.modelo.PlanVacunaAdulto;
import co.edu.uniremington.ladeuth.prueba_api.repositorys.PlanVAdultoRepository;
import co.edu.uniremington.ladeuth.prueba_api.servicio.PlanVAService;

@Service
public class PlanVAServiceImpl implements PlanVAService {
       
 private final PlanVAdultoRepository planVAdultoRepository;

 private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public PlanVAServiceImpl(PlanVAdultoRepository planVAdultoRepository){
        this.planVAdultoRepository = planVAdultoRepository;
    }

    // leer todo
    public List<PlanVacunaAdulto> listarTodos() {
        List<PlanVacunaAdulto> vacunas = planVAdultoRepository.findAll();
        if (vacunas.isEmpty()) {
            throw new RecursoNoEncontradoException("No hay planes de vacuna registrados");
        }
        return vacunas;
    }

    // buscar por nombre
    @Override
    public PlanVacunaAdulto buscarPorNombre(String nombre) {
        return planVAdultoRepository.findByNombreContainingIgnoreCase(nombre)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se ha asignado un plan de vacunas al perro con nombre: " + nombre));
    }

    public List<PlanVacunaAdulto> buscarPorFecha(LocalDateTime fechaCreacion){
    List<PlanVacunaAdulto> vacunas = planVAdultoRepository.findByFechaCreacion(fechaCreacion);
        if (vacunas.isEmpty()) {
            throw new RecursoNoEncontradoException("No hay planes de vacunas hechos en la fecha:" + fechaCreacion);
        }
        return vacunas;
      }

    public List<PlanVacunaAdulto> listarActivos(Boolean activo){
    List<PlanVacunaAdulto> vacunas = planVAdultoRepository.findByActivo(activo);
        if (vacunas.isEmpty()) {
            throw new RecursoNoEncontradoException("No hay planes de vacunas activos registrados");
        }
        return vacunas;
      }

      public Optional<PlanVacunaAdulto> cambiarActivo(Long id, Boolean activo){
        return planVAdultoRepository.findById(id).map(planExistente -> {
        planExistente.setActivo(activo);
        PlanVacunaAdulto actualizado = planVAdultoRepository.save(planExistente);
        return actualizado;
         });
      }


    // agregar
    @Override
    public PlanVacunaAdulto agregar(PlanVacunaAdulto vacuna, Long cantidad, ChronoUnit unidadTiempo) {
        if (planVAdultoRepository.existsById(vacuna.getId())) {
            throw new RecursoDuplicadoException("Ya existe un plan de vacunas con el id: " + vacuna.getId());
        }
        PlanVacunaAdulto nuevo = planVAdultoRepository.save(vacuna);
        programarAlerta(cantidad, unidadTiempo);
        return nuevo;
    }

    
    //Emitir una alerta cada que se pase el tiempo de control
    public void programarAlerta(long cantidad, ChronoUnit unidadTiempo) {
        long delayEnSegundos = unidadTiempo.getDuration().getSeconds() * cantidad;

        scheduler.scheduleAtFixedRate(() -> {
            boolean vencidos = planVAdultoRepository.existsByFechaAlerta(LocalDateTime.now());

            if (vencidos) {
                System.out.println("⚠️ Alerta: Es hora de aplicar la siguiente vacuna");
            }
        }, delayEnSegundos, delayEnSegundos, TimeUnit.SECONDS);
    }

    /*private boolean verificarRegistros(ChronoUnit unidadTiempo, long cantidad) {
        LocalDateTime fechaLimite = LocalDateTime.now().minus(cantidad, unidadTiempo);
        return planVAdultoRepository.existsByFechaCreacion(fechaLimite);
    
    }*/

}
