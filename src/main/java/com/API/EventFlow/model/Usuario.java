package com.API.EventFlow.model;

import jakarta.persistence.*;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "usuarios")
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nombres")
    private String nombre;
    @Column(name = "apellidos")
    private String primerApellido;
    @Column(name = "correo", unique = true)
    private String correo;
    private String clave;
    @Enumerated(EnumType.STRING)
    @Column(name = "rol", columnDefinition = "ENUM('ADMIN','CLIENTE')")
    private Rol rol;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL,orphanRemoval = true)
    @Builder.Default
    private List<Evento> eventos = new ArrayList<>();

    // mappedBy indica que la propiedad cliente en compra es la dueña de la relación.
    // Cascade = CascadeTypeALL, orphanRemoval = true, indica que si eliminas el cliente
    // también eliminas sus compras.
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Compra> compras = new ArrayList<>();


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(rol.name()));
    }

    @Override
    public String getPassword() {
        return clave;
    }

    @Override
    public String getUsername() {
        return correo;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
