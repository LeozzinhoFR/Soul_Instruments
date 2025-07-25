package com.roncolatoandpedro.soulinstruments.dao.impl;

import com.roncolatoandpedro.soulinstruments.dao.interfaces.InstrumentoDAO;
import com.roncolatoandpedro.soulinstruments.dto.InstrumentoDTO;
import com.roncolatoandpedro.soulinstruments.dto.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InstrumentoDAOImpl implements InstrumentoDAO {

    private final Connection conexao;

    public InstrumentoDAOImpl(Connection conexao) {
        this.conexao = conexao;
    }

    private InstrumentoDTO mapearResultSet(ResultSet rs) throws SQLException {
        Long idInstrumento = rs.getLong("idinstrumento");
        String nome = rs.getString("nome");
        Categoria categoria = Categoria.valueOf(rs.getString("categoria"));
        return new InstrumentoDTO(idInstrumento, nome, categoria);
    }

    @Override
    public InstrumentoDTO salvar(InstrumentoDTO instrumento) throws SQLException {
        String sql = "INSERT INTO Instrumento (nome, categoria) VALUES (?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, instrumento.getNome());
            stmt.setString(2, instrumento.getCategoria().name());
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    instrumento.setIdInstrumento(generatedKeys.getLong(1));
                }
            }
        }
        return instrumento;
    }


    @Override
    public void atualizar(InstrumentoDTO instrumento) throws SQLException {
        String sql = "UPDATE Instrumento SET nome = ?, categoria = ? WHERE idInstrumento = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, instrumento.getNome());
            stmt.setString(2, instrumento.getCategoria().name());
            stmt.setLong(3, instrumento.getIdInstrumento());
            stmt.executeUpdate();
        }
    }


    @Override
    public void remover(Long instrumentoId) throws SQLException {
        String sql = "DELETE FROM Instrumento WHERE idInstrumento = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, instrumentoId);
            stmt.executeUpdate();
        }
    }


    @Override
    public Optional<InstrumentoDTO> buscarPorId(Long instrumentoId) throws SQLException {
        String sql = "SELECT * FROM Instrumento WHERE idInstrumento = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, instrumentoId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearResultSet(rs));
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    @Override
    public List<InstrumentoDTO> listarTodos() throws SQLException {
        List<InstrumentoDTO> instrumentos = new ArrayList<>();
        String sql = "SELECT * FROM instrumento ORDER BY idinstrumento";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                instrumentos.add(mapearResultSet(rs));
            }
        }
        return instrumentos;
    }
}