package com.roncolatoandpedro.soulinstruments.controller;

import com.roncolatoandpedro.soulinstruments.dao.DAOFactory;
import com.roncolatoandpedro.soulinstruments.dao.interfaces.InstrumentoDAO;
import com.roncolatoandpedro.soulinstruments.dao.interfaces.ProdutoDAO;
import com.roncolatoandpedro.soulinstruments.dto.InstrumentoDTO;

import java.sql.SQLException;
import java.util.List;

public class InstrumentoController {

    private final InstrumentoDAO instrumentoDAO;
    private final ProdutoDAO produtoDAO;

    public InstrumentoController() {
        try {
            this.instrumentoDAO = DAOFactory.criarInstrumentoDAO();
            this.produtoDAO = DAOFactory.criarProdutoDAO();
        } catch (SQLException e) {
            // Em uma aplicação real, um sistema de log seria melhor.
            // Para Swing, podemos lançar uma RuntimeException para parar a app se a conexão falhar.
            throw new RuntimeException("Falha ao criar Instrumento.", e);
        }
    }

    public void salvarInstrumento(InstrumentoDTO instrumento) throws SQLException {
        if (instrumento.getIdInstrumento() == null) {
            instrumentoDAO.salvar(instrumento);
        } else {
            instrumentoDAO.atualizar(instrumento);
        }
    }


    public void removerInstrumento(Long id) throws SQLException {
        // Regra de Negócio: Não permitir exclusão se houver um Produto associado.
        if (produtoDAO.existeProdutoPorInstrumento(id)) {
            throw new SQLException("Não é possível remover. O instrumento está sendo usado por um ou mais produtos.");
        }
        instrumentoDAO.remover(id);
    }

    public List<InstrumentoDTO> listarInstrumentos() throws SQLException {
        return instrumentoDAO.listarTodos();
    }
}