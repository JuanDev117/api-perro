package co.edu.uniremington.ladeuth.prueba_api.controlador;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uniremington.ladeuth.prueba_api.dto.FichaDTO;
import co.edu.uniremington.ladeuth.prueba_api.excepcion.RecursoNoEncontradoException;
import co.edu.uniremington.ladeuth.prueba_api.servicio.FichaService;
import io.swagger.v3.oas.annotations.Operation;


@RestController
@RequestMapping("/api/fichas")
public class FichaController {
     private final FichaService fichaService;

    public FichaController(FichaService fichaService){
        this.fichaService = fichaService;
    }

    @GetMapping
    public ResponseEntity<List<FichaDTO>> listarTodos() {
      return ResponseEntity.ok(fichaService.listarTodos());
    }

    @Operation(summary = "Buscar ficha por ID")
    @GetMapping("/{id}")
    public ResponseEntity<FichaDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(fichaService.buscarPorId(id));
    }

    @Operation(summary = "Buscar ficha por raza")
    @GetMapping("/buscar/raza")
    public ResponseEntity<List<FichaDTO>> buscarPorRaza(@RequestParam String raza) {
        return ResponseEntity.ok(fichaService.buscarPorRaza(raza));
    }

    @Operation(summary = "Buscar ficha por sexo")
    @GetMapping("/buscar/sexo")
    public ResponseEntity<List<FichaDTO>> buscarPorSexo(@RequestParam String sexo) {
        return ResponseEntity.ok(fichaService.buscarPorSexo(sexo));
    }

    @Operation(summary = "Buscar ficha por fecha de nacimiento")
    @GetMapping("/buscar/fechaNacimiento")
    public ResponseEntity<List<FichaDTO>> buscarPorFechaNacimiento(@RequestParam LocalDate fechaNacimiento) {
        return ResponseEntity.ok(fichaService.buscarPorFechaNacimiento(fechaNacimiento));
    }

    @Operation(summary = "Buscar ficha por esperanza de vida")
    @GetMapping("/buscar/esperanzaDeVida")
    public ResponseEntity<List<FichaDTO>> buscarPorEsperanzaDeVida(@RequestParam String esperanzaDeVida) {
        return ResponseEntity.ok(fichaService.buscarPorEsperanzaDeVida(esperanzaDeVida));
    }

    @Operation(summary = "Buscar ficha por peso")
    @GetMapping("/buscar/peso")
    public ResponseEntity<List<FichaDTO>> buscarPorPeso(@RequestParam String peso) {
        return ResponseEntity.ok(fichaService.buscarPorPeso(peso));
    }

    @Operation(summary = "Buscar ficha por altura")
    @GetMapping("/buscar/altura")
    public ResponseEntity<List<FichaDTO>> buscarPorAltura(@RequestParam String altura) {
        return ResponseEntity.ok(fichaService.buscarPorAltura(altura));
    }

    @Operation(summary = "Buscar ficha por colores")
    @GetMapping("/buscar/colores")
    public ResponseEntity<List<FichaDTO>> buscarPorColores(@RequestParam String colores) {
        return ResponseEntity.ok(fichaService.buscarPorColores(colores));
    }

    @Operation(summary = "Buscar ficha por pelaje")
    @GetMapping("/buscar/pelaje")
    public ResponseEntity<List<FichaDTO>> buscarPorPelaje(@RequestParam String pelaje) {
        return ResponseEntity.ok(fichaService.buscarPorPelaje(pelaje));
    }

    @Operation(summary = "Actualizar ficha por ID")
    @PutMapping("/fichas/{id}")
    public ResponseEntity<FichaDTO> actualizarFicha(@PathVariable Long id, @RequestBody FichaDTO fichaDTO) {
         FichaDTO actualizada = fichaService.actualizar(id, fichaDTO);
      return ResponseEntity.ok(actualizada);
    }

    @Operation(summary = "Eliminar ficha por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        boolean eliminado = fichaService.eliminar(id);
        if (eliminado) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            throw new RecursoNoEncontradoException("No existe ficha con ID: " + id);
        }
    }
}
