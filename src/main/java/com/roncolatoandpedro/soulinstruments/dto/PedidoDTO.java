package com.roncolatoandpedro.soulinstruments.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PedidoDTO {
    private Long idPedido;
    private LocalDate dataPedido;
    private LocalDate dataEntrega;
    private Double valorTotal;
    private StatusPedido status;
    private Long idFornecedor;
    private List<ItemPedidoDTO> itens;

    public PedidoDTO() {
        this.itens = new ArrayList<>();
        this.valorTotal = 0.0;
    }

    public void calcularValorTotalPedido() {
        this.valorTotal = 0.0;
        if (this.itens != null) {
            for (ItemPedidoDTO item : this.itens) {
                // Garante que o total do item esteja calculado antes de somar
                item.calcularValorTotal();
                this.valorTotal += item.getValorTotal();
            }
        }
    }

    // Getters e Setters
    public Long getIdPedido() {
        return idPedido;
    }
    public void setIdPedido(Long idPedido) {
        this.idPedido = idPedido;
    }
    public LocalDate getDataPedido() {
        return dataPedido;
    }
    public void setDataPedido(LocalDate dataPedido) {
        this.dataPedido = dataPedido;
    }
    public LocalDate getDataEntrega() {
        return dataEntrega;
    }
    public void setDataEntrega(LocalDate dataEntrega) {
        this.dataEntrega = dataEntrega;
    }
    public Double getValorTotal() {
        return valorTotal;
    }
    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public StatusPedido getStatus() {
        return status;
    }
    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public Long getIdFornecedor() {
        return idFornecedor;
    }
    public void setIdFornecedor(Long idFornecedor) {
        this.idFornecedor = idFornecedor;
    }
    public List<ItemPedidoDTO> getItens() {
        return itens;
    }
    public void setItens(List<ItemPedidoDTO> itens) {
        this.itens = itens; calcularValorTotalPedido();
    }
    public void addItem(ItemPedidoDTO item) {
        this.itens.add(item); calcularValorTotalPedido();
    }
}