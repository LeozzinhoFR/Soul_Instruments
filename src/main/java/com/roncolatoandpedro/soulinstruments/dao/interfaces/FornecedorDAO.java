package com.roncolatoandpedro.soulinstruments.dao.interfaces;

import com.roncolatoandpedro.soulinstruments.dto.FornecedorDTO;

import java.util.List;
import java.util.Optional;
import java.sql.SQLException;

public interface FornecedorDAO {
    FornecedorDTO salvar(FornecedorDTO fornecedor) throws SQLException; //Retornar o DTO com ID permite que retore o objeto por completo após a persistência
    void atualizar(FornecedorDTO fornecedor) throws SQLException;
    void remover(Long id) ;
    Optional<FornecedorDTO> buscarPorId(Long id) throws SQLException;
    Optional<FornecedorDTO> buscarPorCnpj(String cnpj) throws SQLException;
    List<FornecedorDTO> listarTodos() throws SQLException;
}
