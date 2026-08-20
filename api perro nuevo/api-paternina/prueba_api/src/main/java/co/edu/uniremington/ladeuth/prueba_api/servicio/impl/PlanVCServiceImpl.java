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
import co.edu.uniremington.ladeuth.prueba_api.modelo.PlanVacunaCachorro;
import co.edu.uniremington.ladeuth.prueba_api.repositorys.PlanVCachorroRepository;
import co.edu.uniremington.ladeuth.prueba_api.servicio.PlanVCService;

@Service
public class PlanVCServiceImpl implements PlanVCService {
    
       
 private final PlanVCachorroRepository planVCachorroRepository;

 private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public PlanVCServiceImpl(PlanVCachorroRepository planVCachorroRepository){
        this.planVCachorroRepository = planVCachorroRepository;
    }

    // leer todo
    public List<PlanVacunaCachorro> listarTodos() {
        List<PlanVacunaCachorro> vacunas = planVCachorroRepository.findAll();
        if (vacunas.isEmpty()) {
            throw new RecursoNoEncontradoException("No hay planes de vacuna registrados");
        }
        return vacunas;
    }

    // buscar por nombre exacto
    @Override
    public PlanVacunaCachorro buscarPorNombre(String nombre) {
        return planVCachorroRepository.findByNombreContainingIgnoreCase(nombre)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se ha asignado un plan de vacunas al cachorro con nombre: " + nombre));
    }

    public List<PlanVacunaCachorro> buscarPorFecha(LocalDateTime fechaCreacion){
    List<PlanVacunaCachorro> vacunas = planVCachorroRepository.findByFechaCreacion(fechaCreacion);
        if (vacunas.isEmpty()) {
            throw new RecursoNoEncontradoException("No hay planes de vacunas hechos en la fecha:" + fechaCreacion);
        }
        return vacunas;
      }

    public List<PlanVacunaCachorro> listarActivos(Boolean activo){
    List<PlanVacunaCachorro> vacunas = planVCachorroRepository.findByActivo(activo);
        if (vacunas.isEmpty()) {
            throw new RecursoNoEncontradoException("No hay planes de vacunas activos registrados");
        }
        return vacunas;
      }

      public Optional<PlanVacunaCachorro> cambiarActivo(Long id, Boolean activo){
        return planVCachorroRepository.findById(id).map(planExistente -> {
        planExistente.setActivo(activo);
        PlanVacunaCachorro actualizado = planVCachorroRepository.save(planExistente);
        return actualizado;
         });
      }

    // agregar
    @Override
    public PlanVacunaCachorro agregar(PlanVacunaCachorro vacuna, Long cantidad, ChronoUnit unidadTiempo) {
        if (planVCachorroRepository.existsById(vacuna.getId())) {
            throw new RecursoDuplicadoException("Ya existe un plan de vacunas con el id: " + vacuna.getId());
        }
        PlanVacunaCachorro nuevo = planVCachorroRepository.save(vacuna);
        programarAlerta(cantidad, unidadTiempo);
        return nuevo;
    }

    //Emitir una alerta cada que se pase el tiempo de control
    public void programarAlerta(long cantidad, ChronoUnit unidadTiempo) {
        long delayEnSegundos = unidadTiempo.getDuration().getSeconds() * cantidad;

        scheduler.scheduleAtFixedRate(() -> {
            boolean vencidos = planVCachorroRepository.existsByFechaAlerta(LocalDateTime.now());

            if (vencidos) {
                System.out.println("⚠️ Alerta: Es hora de aplicar la siguiente vacuna");
            }
        }, delayEnSegundos, delayEnSegundos, TimeUnit.SECONDS);
    }

    /*private boolean verificarRegistros(ChronoUnit unidadTiempo, long cantidad) {
        LocalDateTime fechaLimite = LocalDateTime.now().minus(cantidad, unidadTiempo);
        return planVCachorroRepository.existsByFechaCreacion(fechaLimite);
    
    }*/

}
