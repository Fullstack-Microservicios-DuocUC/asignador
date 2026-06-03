package cl.duoc.mineria.asignador.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegistrarRutaDTO {

    @NotNull(message = "El ID de la pala es obligatorio")
    private Long palaId;

    @NotBlank(message = "La clasificación del material no puede estar vacía")
    private String clasificacionMaterial;

    @NotBlank(message = "El destino asignado es obligatorio (CHANCADO o RELAVES)")
    private String destinoAsignado;
    
}