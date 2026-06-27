package cl.duoc.mineria.asignador.dto;

import cl.duoc.mineria.asignador.model.ClasificacionMaterial;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegistrarRutaDTO {

    @NotNull(message = "El ID de la pala es obligatorio")
    private Long palaId;

    @NotNull(message = "La clasificación del material es obligatoria")
    private ClasificacionMaterial clasificacionMaterial;

    @NotBlank(message = "El destino asignado es obligatorio (CHANCADO o RELAVES)")
    private String destinoAsignado;
    
}