package com.roncolatoandpedro.soulinstruments.dto;

public class FornecedorDTO {
    private Long idFornecedor;
    private String nomeFornecedor;
    private String cnpj;
    private String descricao;
    
    public FornecedorDTO() {}
    
    public FornecedorDTO(Long idFornecedor, String nomeFornecedor, String cnpj, String descricao) {
        this.idFornecedor = idFornecedor;
        this.nomeFornecedor = nomeFornecedor;
        this.cnpj = cnpj;
        this.descricao = descricao;
    }

    public Long getIdFornecedor() {
        return idFornecedor;
    }

    public void setIdFornecedor(Long idFornecedor) {
        this.idFornecedor = idFornecedor;
    }

    public String getNomeFornecedor() {
        return nomeFornecedor;
    }

    public void setNomeFornecedor(String nomeFornecedor) {
        this.nomeFornecedor = nomeFornecedor;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
