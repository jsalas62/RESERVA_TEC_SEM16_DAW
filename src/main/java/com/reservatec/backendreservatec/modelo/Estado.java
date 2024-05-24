package com.reservatec.backendreservatec.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "estado")
@Getter @Setter
public class Estado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_id_estado")
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;
}
