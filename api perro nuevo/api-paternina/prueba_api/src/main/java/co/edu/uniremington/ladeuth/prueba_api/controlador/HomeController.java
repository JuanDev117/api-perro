package co.edu.uniremington.ladeuth.prueba_api.controlador;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class HomeController {
    
    @GetMapping("/home")
    public ResponseEntity <Map<String, Object>> home() {
        Map <String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("api", "Perros API");
        respuesta.put("version", "1.0.0");
        respuesta.put("curso", "Lenguaje de programación 3 - IF0122");
        respuesta.put("estado", "en línea");
        respuesta.put("documentacion", "/swagger-ui.html");
        return ResponseEntity.ok(respuesta);
    }
    
@GetMapping("/indice")
    public ResponseEntity <Map<String, Object>> indice() {
        Map <String, Object> recursos = new LinkedHashMap<>();
        recursos.put("perros", "/api/perros");
        recursos.put("perros_disponibles", "/api/perros/disponibles");
        recursos.put("buscar_perro", "/api/perros/buscar?nombre={texto}");
        recursos.put("buscar_perro_id", "/api/perros/{id}");
        recursos.put("buscar_perro_ficha_id", "/api/perros/fichas/{id}");
        recursos.put("fichas", "/api/fichas"); 
        recursos.put("buscar_ficha_id", "/api/fichas/{id}");
        recursos.put("buscar_ficha_raza", "/api/fichas/buscar/raza"); 
        recursos.put("buscar_ficha_nacimiento", "/api/fichas/buscar/fechaNacimiento"); 
        recursos.put("buscar_ficha_esperanza", "/api/fichas/buscar/esperanzaDeVida");
        recursos.put("buscar_ficha_peso", "/api/fichas/buscar/peso");
        recursos.put("buscar_ficha_altura", "/api/fichas/buscar/altura");
        recursos.put("buscar_ficha_colores", "/api/fichas/buscar/colores");
        recursos.put("buscar_ficha_pelaje", "/api/fichas/buscar/pelaje");      

        Map <String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("api","Perros API"); 
        respuesta.put("recursos",recursos);
        return ResponseEntity.ok(respuesta); 
    }

    @GetMapping("/estado")
     public ResponseEntity<Map<String,Object>>estado(){
         Map<String,Object> respuesta = new LinkedHashMap<>(); 
         respuesta.put("estado","UP"); 
         respuesta.put("marcaTiempo",LocalDateTime.now());
          return ResponseEntity.ok(respuesta);
         }
    
}
