package com.bancolombia.crediya.r2dbc.data;

import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("usuario")
public class UsuarioData {

    @Id
    @Column("id_usuario")
    BigInteger  idUsuario;

    @Column("documento_identidad")
    String  documentoIdentidad;

    @Column("nombres")
    String  nombres;

    @Column("apellidos")
    String  apellidos;

    @Column("fecha_nacimiento")
    LocalDate   fechaNacimiento;

    @Column("direccion")
    String  direccion;

    @Column("telefono") 
    String  telefono;

    @Column("correo_electronico")   
    String  correoElectronico;

    @Column("salario_base") 
    Double  salarioBase;

    @ManyToOne
    @Column("id_rol") 
    Integer  idRol;
}
