package cl.duoc.mineria.asignador.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "configuracion_ruta")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfiguracionRuta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Vinculamos la regla a una pala específica. Así, si Carlos reporta que la Pala 2 falló, podemos buscar todas las rutas con palaId = 2 y deshabilitarlas.
    @Column(name = "pala_id", nullable = false)
    private Long palaId; 

    // Ej: "Sulfuros", "Óxidos", "Estéril"
    @Column(name = "clasificacion_material", nullable = false, length = 100)
    private String clasificacionMaterial;

    // Ej: "CHANCADO" o "RELAVES"
    @Enumerated(EnumType.STRING)
    @Column(name = "destino_asignado", nullable = false, length = 50)
    private Destino destinoAsignado;

    // Si la pala falla, esto pasa a false para bloquear la asignación 
    // y obligar al sistema a buscar la siguiente pala vecina disponible.
    @Column(name = "ruta_habilitada", nullable = false)
    private Boolean rutaHabilitada;

}