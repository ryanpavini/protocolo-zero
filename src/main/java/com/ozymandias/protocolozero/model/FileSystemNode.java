package com.ozymandias.protocolozero.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "filesystem_node")
@Getter @Setter
public abstract class FileSystemNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Directory parent;

    // Permissões estilo Linux
    private String permissions = "rwxr-xr-x";

    private String owner = "root";

    // Se true, o comando 'ls' normal não mostra
    private boolean isHidden = false;
}