package com.roncolatoandpedro.soulinstruments.dto;

import com.roncolatoandpedro.soulinstruments.dto.Categoria;

public class ProdutoDTO {
    private Long idProduto;
    private String marca;
    private String modelo;
    private String descricao;
    private Double preco;
    private int quantidadeEstoque;
    private Long idInstrumento;
    private Long idFornecedor;
    private transient String nomeDoInstrumento;
    private transient String nomeFornecedor;

    // Construtor padrão
    public ProdutoDTO() {
    }

    // Construtor completo
    public ProdutoDTO(Long idProduto, String marca, String modelo, String descricao, Double preco, int quantidadeEstoque, Long idInstrumento, Long idFornecedor) {
        this.idProduto = idProduto;
        this.marca = marca;
        this.modelo = modelo;
        this.descricao = descricao;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
        this.idInstrumento = idInstrumento;
        this.idFornecedor = idFornecedor;
    }

    public Long getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(Long idProduto) {
        this.idProduto = idProduto;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setQuantidadeEstoque(int quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public Long getIdInstrumento() {
        return idInstrumento;
    }

    public void setIdInstrumento(Long idInstrumento) {
        this.idInstrumento = idInstrumento;
    }

    public String getNomeDoInstrumento() {
        return nomeDoInstrumento;
    }
    public void setNomeDoInstrumento(String nomeDoInstrumento) {
        this.nomeDoInstrumento = nomeDoInstrumento;
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


}