package com.roncolatoandpedro.soulinstruments.controller;

import com.roncolatoandpedro.soulinstruments.dao.DAOFactory;
import com.roncolatoandpedro.soulinstruments.dao.interfaces.*;
import com.roncolatoandpedro.soulinstruments.dto.FornecedorDTO;
import com.roncolatoandpedro.soulinstruments.dto.InstrumentoDTO;
import com.roncolatoandpedro.soulinstruments.dto.ProdutoDTO;

import java.sql.SQLException;
import java.util.List;

public class ProdutoController {
    private final ProdutoDAO produtoDAO;
    private final PedidoDAO pedidoDAO;
    private final InstrumentoDAO instrumentoDAO;
    private final FornecedorDAO fornecedorDAO;

    public ProdutoController() {
        try {
            this.produtoDAO = DAOFactory.criarProdutoDAO();
            this.pedidoDAO = DAOFactory.criarPedidoDAO();
            this.instrumentoDAO = DAOFactory.criarInstrumentoDAO();
            this.fornecedorDAO = DAOFactory.criarFornecedorDAO();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inicializar controller de produto.", e);
        }
    }

    public void salvar(ProdutoDTO produto) throws SQLException {
        if (produto.getIdProduto() == null) {
            // Regra: Estoque inicial é definido como 0. Só aumenta via Pedidos.
            produto.setQuantidadeEstoque(0);
            produtoDAO.salvar(produto);
        } else {
            produtoDAO.atualizar(produto);
        }
    }

    public void remover(Long id) throws SQLException {
        // Regra: Não permitir exclusão se o produto estiver em um itemPedido.
        if (pedidoDAO.existeItemComProduto(id)) {
            throw new SQLException("Não é possível remover. O produto está incluído em um ou mais pedidos.");
        }
        produtoDAO.remover(id);
    }

    public boolean isProdutoEmUso(Long idProduto) throws SQLException {
        return pedidoDAO.existeItemComProduto(idProduto);
    }

    public List<ProdutoDTO> listarTodos() throws SQLException {
        return produtoDAO.listarTodos();
    }

    public List<InstrumentoDTO> listarTodosInstrumentos() throws SQLException {
        return instrumentoDAO.listarTodos();
    }

    public List<FornecedorDTO> listarTodosFornecedores() throws SQLException {
        return fornecedorDAO.listarTodos();
    }
}