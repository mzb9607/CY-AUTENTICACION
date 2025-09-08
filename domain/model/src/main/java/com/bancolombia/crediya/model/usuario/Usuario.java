package com.bancolombia.crediya.model.usuario;
import lombok.Builder;
import lombok.EqualsAndHashCode;

import java.math.BigInteger;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder(toBuilder = true)
public class Usuario {
    BigInteger  idUsuario;
    String  documentoIdentidad;
    String  nombres;
    String  apellidos;
    LocalDate   fechaNacimiento;
    String  direccion;
    String  telefono;
    String  correoElectronico;
    Double  salarioBase;
    Integer idRol;
}