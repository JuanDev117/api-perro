package co.edu.uniremington.ladeuth.prueba_api.servicio.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import co.edu.uniremington.ladeuth.prueba_api.excepcion.RecursoNoEncontradoException;
import co.edu.uniremington.ladeuth.prueba_api.modelo.ChequeoGeneral;
import co.edu.uniremington.ladeuth.prueba_api.repositorys.ChequeoGRepository;
import co.edu.uniremington.ladeuth.prueba_api.servicio.ChequeoGService;

@Service
public class ChequeoGServiceImpl implements ChequeoGService {
    
 private final ChequeoGRepository chequeoGRepository;

 private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public ChequeoGServiceImpl(ChequeoGRepository chequeoGRepository){
        this.chequeoGRepository = chequeoGRepository;
    }

    // leer todo
    public List<ChequeoGeneral> listarTodos() {
        List<ChequeoGeneral> chequeos = chequeoGRepository.findAll();
        if (chequeos.isEmpty()) {
            throw new RecursoNoEncontradoException("No hay chequeos registrados");
        }
        return chequeos;
    }

    // buscar por nombre 
    @Override
    public ChequeoGeneral buscarPorNombre(String nombre) {
        return chequeoGRepository.findByNombreContainingIgnoreCase(nombre)
                .orElseThrow(() -> new RecursoNoEncontradoException("No hay registro de un chequeo al perro con nombre: " + nombre));
    }

    public List<ChequeoGeneral> buscarPorFecha(LocalDateTime fechaCreacion){
    List<ChequeoGeneral> chequeos = chequeoGRepository.findByFechaCreacion(fechaCreacion);
        if (chequeos.isEmpty()) {
            throw new RecursoNoEncontradoException("No hay registros de chequeo general en esta fecha:" + fechaCreacion);
        }
        return chequeos;
      }


    public List<ChequeoGeneral> listarActivos(Boolean activo){
    List<ChequeoGeneral> chequeos = chequeoGRepository.findByActivo(activo);
        if (chequeos.isEmpty()) {
            throw new RecursoNoEncontradoException("No hay registros de chequeo general activos");
        }
        return chequeos;
      }

      public Optional<ChequeoGeneral> cambiarActivo(Long id, Boolean activo){
        return chequeoGRepository.findById(id).map(chequeoExistente -> {
        chequeoExistente.setActivo(activo);
        ChequeoGeneral actualizado = chequeoGRepository.save(chequeoExistente);
        return actualizado;
         });
      }

    // agregar
    @Override
     public ChequeoGeneral agregar(ChequeoGeneral chequeo, Long cantidad, ChronoUnit unidadTiempo) {
        ChequeoGeneral nuevo = chequeoGRepository.save(chequeo);
        programarAlerta(cantidad, unidadTiempo);

        return nuevo;
    }


    //Emitir una alerta cada que se pase el tiempo de control
    public void programarAlerta(long cantidad, ChronoUnit unidadTiempo) {
        long delayEnSegundos = unidadTiempo.getDuration().getSeconds() * cantidad;

        scheduler.scheduleAtFixedRate(() -> {
            boolean vencidos = chequeoGRepository.existsByFechaAlerta(LocalDateTime.now());

            if (vencidos) {
                System.out.println(" Alerta: Es hora de un nuevo chequeo");
            }
        }, delayEnSegundos, delayEnSegundos, TimeUnit.SECONDS);
    }

   /* private boolean verificarRegistros(ChronoUnit unidadTiempo, long cantidad) {
        LocalDateTime fechaLimite = LocalDateTime.now().minus(cantidad, unidadTiempo);
        return chequeoGRepository.existsByFechaCreacion(fechaLimite);
    
    }

    public boolean chequeosVencidos(){
        LocalDateTime ahora = LocalDateTime.now();
        return chequeoGRepository.existsByFechaAlerta(ahora);
    }*/

}
