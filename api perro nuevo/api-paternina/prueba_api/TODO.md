# TODO - Verificación y Limpieza Proyecto Perro/Ficha

## Objetivo
Vaciar la base de datos, revisar rutas, corregir documentación y verificar que todo corra bien tras el renombrado de Raza → Perro.

## Pasos
- [x] 1. Cambiar `ddl-auto` de `update` a `create` (vacía la BD regenerando el esquema)
- [x] 2. Arrancar la app para que se regenere el esquema (vacío)
- [x] 3. Volver `ddl-auto` a `update`
- [x] 4. Corregir `DOCUMENTACION.md` (flujo correcto: crear Perro con ficha anidada, formato fecha ISO, rutas `/api/perros`)
- [x] 5. Corregir `FichaRepository` (eliminar `findByCodigoInterno` que no existe en Ficha)
- [x] 6. Compilar y verificar BUILD SUCCESS
- [x] 7. Arrancar la app y confirmar que corre en el puerto 8081
- [x] 8. Verificar endpoints GET (perros, fichas, disponibles, indice) → 200
- [x] 9. Verificar creación de Perro con ficha anidada (`POST /api/perros`) → 201
- [x] 10. Confirmar base de datos vacía (tablas regeneradas)
- [x] 11. Eliminar método `crear` de Ficha (impl, interfaz y endpoint POST /api/fichas) por ser obsoleto
- [x] 12. Actualizar DOCUMENTACION.md (eliminar POST /api/fichas, aclarar que la ficha se crea con el perro)
- [x] 13. Hacer funcional la búsqueda por índice de ficha: mostrar `id` + `raza` de la ficha al listar perros (para poder usar GET /api/fichas/{id})
- [x] 14. Hacer que el id de Perro y el id de su Ficha coincidan siempre (relación 1:1 con PK compartida vía @MapsId); regenerar esquema y verificar con 2 perros (id 1↔1, id 2↔2)
</content>
