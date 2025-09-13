package com.bancolombia.crediya.r2dbc.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("rol")
public class RolData {

    @Id
    @Column("id_rol")
    private Integer idRol;

    @Column("nombre")
    private String nombre;

    @Column("descripcion")
    private String descripcion;
}