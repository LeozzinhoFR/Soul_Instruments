package com.roncolatoandpedro.soulinstruments.dao.interfaces;

import com.roncolatoandpedro.soulinstruments.dto.PedidoDTO;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PedidoDAO {
    Connection getConexao();
    ProdutoDAO getProdutoDAO();
    PedidoDTO salvar(PedidoDTO pedido) throws SQLException;
    void atualizarStatus(PedidoDTO pedido) throws SQLException;
    void remover(Long idPedido) throws SQLException; // Adicionado throws SQLException
    Optional<PedidoDTO> buscarPorId(Long idPedido) throws SQLException;
    List<PedidoDTO> listarTodos() throws SQLException;
    List<PedidoDTO> listarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) throws SQLException;
    List<PedidoDTO> listarPorFornecedor(Long idFornecedor) throws SQLException; // Nome do método corrigido
    boolean existeItemComProduto(Long idProduto) throws SQLException;
}