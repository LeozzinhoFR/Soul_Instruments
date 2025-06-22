package com.roncolatoandpedro.soulinstruments.dao.impl;

import com.roncolatoandpedro.soulinstruments.dao.interfaces.ItemPedidoDAO;
import com.roncolatoandpedro.soulinstruments.dto.ItemPedidoDTO;
import com.roncolatoandpedro.soulinstruments.dto.ProdutoDTO;
import com.roncolatoandpedro.soulinstruments.dao.interfaces.ProdutoDAO;

import java.sql.*;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

public class ItemPedidoDAOImpl implements ItemPedidoDAO {

    private final Connection conexao;

    public ItemPedidoDAOImpl(Connection conexao) {
        this.conexao = conexao;
    }

    @Override
    public ItemPedidoDTO salvar(ItemPedidoDTO itemPedido) throws SQLException {
        // Colunas: idItemPedido Chave Prim, idPedido (FK), idProduto (FK), quantidade, valorUnitario, valorTotal
        // Assumindo que itemPedido.getIdProduto() se refere ao ID da tabela produto
        String sql = "INSERT INTO ItemPedido (idPedido, idProduto, quantidade, valorUnitario, valorTotal) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, itemPedido.getIdPedido());
            stmt.setLong(2, itemPedido.getIdProduto()); // Usando o id numérico do produto
            stmt.setInt(3, itemPedido.getQuantidade());
            stmt.setDouble(4, itemPedido.getValorUnitario());
            stmt.setDouble(5, itemPedido.getValorTotal());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        itemPedido.setIdItemPedido(generatedKeys.getLong(1));
                    } else {
                        throw new SQLException("Falha ao obter o ID gerado para o item do pedido.");
                    }
                }
            } else {
                throw new SQLException("Falha ao salvar o item do pedido, nenhuma linha afetada.");
            }
            return itemPedido;
        }
    }

    private ItemPedidoDTO mapearResultSetParaItemPedidoDTO(ResultSet rs) throws SQLException {
        ItemPedidoDTO item = new ItemPedidoDTO();
        item.setIdItemPedido(rs.getLong("idItemPedido"));
        item.setIdPedido(rs.getLong("idPedido"));
        item.setIdProduto(rs.getLong("idProduto"));
        item.setQuantidade(rs.getInt("quantidade"));
        item.setValorUnitario(rs.getDouble("valorUnitario"));
        item.calcularValorTotal();
        item.setValorTotal(rs.getDouble("valorTotal"));
        return item;
    }

    @Override
    public List<ItemPedidoDTO> buscarPorPedidoId(Long idPedido) throws SQLException {
        List<ItemPedidoDTO> itens = new ArrayList<>();
        String sql = "SELECT * FROM ItemPedido WHERE idPedido = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, idPedido);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    itens.add(mapearResultSetParaItemPedidoDTO(rs));
                }
            }
        }
        return itens;
    }
}