package com.ozymandias.protocolozero.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "directory")
@Getter @Setter
public class Directory extends FileSystemNode {

    // Lista polimórfica: pode conter Files ou Directories misturados
    @OneToMany(mappedBy = "parent")
    private List<FileSystemNode> children = new ArrayList<>();
}