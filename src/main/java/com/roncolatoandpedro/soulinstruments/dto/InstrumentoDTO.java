package com.roncolatoandpedro.soulinstruments.dto;

import com.roncolatoandpedro.soulinstruments.dto.Categoria;

public class InstrumentoDTO {
    private Long idInstrumento;
    private String nome;
    private Categoria categoria;

    public InstrumentoDTO() {
    }

    public InstrumentoDTO(Long idInstrumento, String nome, Categoria categoria) {
        this.idInstrumento = idInstrumento;
        this.nome = nome;
        this.categoria = categoria;
    }

    // Getters e Setters
    public Long getIdInstrumento() {
        return idInstrumento;
    }

    public void setIdInstrumento(Long idInstrumento) {
        this.idInstrumento = idInstrumento;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
}