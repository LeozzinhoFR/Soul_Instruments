package com.roncolatoandpedro.soulinstruments.controller;

import com.roncolatoandpedro.soulinstruments.dao.DAOFactory;
import com.roncolatoandpedro.soulinstruments.dao.interfaces.FornecedorDAO;
import com.roncolatoandpedro.soulinstruments.dao.interfaces.ProdutoDAO;
import com.roncolatoandpedro.soulinstruments.dto.FornecedorDTO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class FornecedorController {

    private final FornecedorDAO fornecedorDAO;
    private final ProdutoDAO produtoDAO;

    public FornecedorController() {
        try {
            // Obtém as instâncias dos DAOs através da fábrica
            this.fornecedorDAO = DAOFactory.criarFornecedorDAO();
            this.produtoDAO = DAOFactory.criarProdutoDAO();
        } catch (SQLException e) {
            // Lança uma exceção em tempo de execução se a conexão com o banco falhar na inicialização
            throw new RuntimeException("Erro ao inicializar os DAOs no FornecedorController.", e);
        }
    }

    /**
     * Salva ou atualiza um fornecedor.
     * Se o ID for nulo, salva um novo. Se não, atualiza o existente.
     */
    public void salvar(FornecedorDTO fornecedor) throws SQLException {
        // Validação de negócio: Verificar se CNPJ já existe ao tentar salvar um NOVO fornecedor
        if (fornecedor.getIdFornecedor() == null) {
            Optional<FornecedorDTO> existente = fornecedorDAO.buscarPorCnpj(fornecedor.getCnpj());
            if (existente.isPresent()) {
                throw new SQLException("O CNPJ informado já está cadastrado para outro fornecedor.");
            }
            fornecedorDAO.salvar(fornecedor);
        } else {
            fornecedorDAO.atualizar(fornecedor);
        }
    }

    /**
     * Remove um fornecedor, aplicando a regra de negócio.
     */
    public void remover(Long id) throws SQLException {
        // Regra de Negócio: Não permitir exclusão de Fornecedor se houver um Produto associado.
        if (produtoDAO.existeProdutoPorFornecedor(id)) {
            throw new SQLException("Não é possível remover. Este fornecedor está associado a um ou mais produtos.");
        }
        fornecedorDAO.remover(id);
    }

    /**
     * Lista todos os fornecedores cadastrados.
     */
    public List<FornecedorDTO> listarTodos() throws SQLException {
        return fornecedorDAO.listarTodos();
    }

    /**
     * Busca fornecedores por nome ou CNPJ.
     */
    public List<FornecedorDTO> buscar(String termo) throws SQLException {
        if (termo == null || termo.trim().isEmpty()) {
            return listarTodos();
        }
        return fornecedorDAO.buscarPorNomeOuCnpj(termo);
    }
}