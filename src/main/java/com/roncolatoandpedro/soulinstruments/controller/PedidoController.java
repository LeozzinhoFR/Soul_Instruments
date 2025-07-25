package com.roncolatoandpedro.soulinstruments.controller;

import com.roncolatoandpedro.soulinstruments.dao.DAOFactory;
import com.roncolatoandpedro.soulinstruments.dao.impl.PedidoDAOImpl;
import com.roncolatoandpedro.soulinstruments.dao.impl.ProdutoDAOImpl;
import com.roncolatoandpedro.soulinstruments.dao.interfaces.*;
import com.roncolatoandpedro.soulinstruments.dto.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class PedidoController {

    public List<FornecedorDTO> listarFornecedores() throws SQLException {
        FornecedorDAO dao = DAOFactory.criarFornecedorDAO();
        return dao.listarTodos();
    }

    public List<ProdutoDTO> listarProdutosPorFornecedor(Long idFornecedor) throws SQLException {
        ProdutoDAO dao = DAOFactory.criarProdutoDAO();
        // Você precisará adicionar este método ao seu ProdutoDAO
        return dao.listarPorFornecedor(idFornecedor);
    }

    public void salvarPedido(PedidoDTO pedido) throws SQLException {
        // Regra: Status inicial é PENDENTE
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setDataPedido(LocalDate.now());

        PedidoDAO dao = DAOFactory.criarPedidoDAO();
        dao.salvar(pedido);
    }

    public List<PedidoDTO> listarTodosPedidos() throws SQLException {
        PedidoDAO dao = DAOFactory.criarPedidoDAO();
        return dao.listarTodos();
    }

    public void confirmarEntrega(PedidoDTO pedido) throws SQLException {
        // CORREÇÃO: Usar a DAOFactory para criar um PedidoDAO que já vem com
        // uma conexão única e todas as suas dependências (ProdutoDAO, ItemPedidoDAO) injetadas.
        // Isso simplifica o controller e centraliza a lógica de criação na factory.
        PedidoDAO pedidoDAO = DAOFactory.criarPedidoDAO();
        Connection conexao = pedidoDAO.getConexao(); // Precisamos de um getter para a conexão no DAO

        try {
            conexao.setAutoCommit(false);

            // A instância 'pedidoDAO' já tem seu próprio 'produtoDAO' interno.
            ProdutoDAO produtoDAO = pedidoDAO.getProdutoDAO(); // Precisamos de um getter

            // 1. Atualizar o estoque para cada item do pedido
            for (ItemPedidoDTO item : pedido.getItens()) {
                ProdutoDTO produto = produtoDAO.buscarPorId(item.getIdProduto())
                        .orElseThrow(() -> new SQLException("Produto com ID " + item.getIdProduto() + " não encontrado."));

                int novoEstoque = produto.getQuantidadeEstoque() + item.getQuantidade();
                produto.setQuantidadeEstoque(novoEstoque);
                produtoDAO.atualizar(produto);
            }

            // 2. Atualizar o status e a data de entrega do pedido
            pedido.setStatus(StatusPedido.ENTREGUE);
            pedido.setDataEntrega(LocalDate.now());
            pedidoDAO.atualizarStatus(pedido);

            // 3. Se tudo deu certo, comitar a transação
            conexao.commit();

        } catch (SQLException e) {
            if (conexao != null) {
                conexao.rollback();
            }
            throw e;
        } finally {
            if (conexao != null) {
                conexao.setAutoCommit(true);
                conexao.close();
            }
        }
    }

    public FornecedorDTO buscarFornecedorPorId(Long id) throws SQLException {
        FornecedorDAO dao = DAOFactory.criarFornecedorDAO();
        return dao.buscarPorId(id).orElse(null);
    }
}