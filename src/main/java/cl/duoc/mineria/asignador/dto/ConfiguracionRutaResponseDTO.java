package cl.duoc.mineria.asignador.dto;

import lombok.Data;

@Data
public class ConfiguracionRutaResponseDTO {
    
    private Long id;
    private Long palaId;
    private String clasificacionMaterial;
    private String destinoAsignado; // Lo devolvemos como String, no como Enum, para que sea un JSON limpio
    private Boolean rutaHabilitada;
    
}