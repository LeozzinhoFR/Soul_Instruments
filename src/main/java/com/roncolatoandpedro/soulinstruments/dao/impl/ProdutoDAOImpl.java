package com.roncolatoandpedro.soulinstruments.dao.impl;

import com.roncolatoandpedro.soulinstruments.dao.interfaces.ProdutoDAO;
import com.roncolatoandpedro.soulinstruments.dto.ProdutoDTO;
import com.roncolatoandpedro.soulinstruments.dto.Categoria;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class ProdutoDAOImpl implements ProdutoDAO {
    private final Connection conexao;

    public ProdutoDAOImpl(Connection conexao) {
        this.conexao = conexao;
    }


    @Override
    public ProdutoDTO salvar(ProdutoDTO produto) throws SQLException{
        String sql = "INSERT INTO Produto (marca, modelo, descricao, preco, quantidadeEstoque, idInstrumento, idFornecedor)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexao.prepareStatement (sql, new String[]{"idproduto"})) {
            stmt.setString(1, produto.getMarca());
            stmt.setString(2, produto.getModelo());
            stmt.setString(3, produto.getDescricao());
            stmt.setDouble(4, produto.getPreco());
            stmt.setInt(5, produto.getQuantidadeEstoque());
            stmt.setLong(6, produto.getIdInstrumento());
            if (produto.getIdFornecedor() != null) {
                stmt.setLong(7, produto.getIdFornecedor());
            } else {
                stmt.setNull(7, java.sql.Types.BIGINT);
            }


            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()){
                        produto.setIdProduto(generatedKeys.getLong(1)); //define o ID gerado para o produto
                    } else {
                        throw new SQLException("Falha ao obter ID gerado para o produto");
                    }
                }
            } else {
                throw new SQLException("Falha ao salvar Produto, nenhuma linha afetada");
            }
            return produto;
        }
    }

    @Override
    public void atualizar(ProdutoDTO produto) throws SQLException{
        String sql = "UPDATE Produto SET marca = ?, modelo = ?, descricao = ?, preco = ?, quantidadeEstoque = ?, idInstrumento = ?, idFornecedor = ? WHERE idProduto = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, produto.getMarca());
            stmt.setString(2, produto.getModelo());
            stmt.setString(3, produto.getDescricao());
            stmt.setDouble(4, produto.getPreco());
            stmt.setInt(5, produto.getQuantidadeEstoque());
            stmt.setLong(6, produto.getIdInstrumento());
            if (produto.getIdFornecedor() != null) {
                stmt.setLong(7, produto.getIdFornecedor());
            } else {
                stmt.setNull(7, java.sql.Types.BIGINT);
            }
            stmt.setLong(8, produto.getIdProduto());

            stmt.executeUpdate();
        }
    }


    @Override
    public void remover(Long idProduto) throws SQLException{
        String sql = "DELETE FROM Produto WHERE idProduto = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, idProduto);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            throw new SQLException("Falha ao excluir produto do produto");
        }
    }





    private ProdutoDTO mapearResultSetParaProdutoDTO(ResultSet rs) throws SQLException {
        ProdutoDTO produto = new ProdutoDTO(
                rs.getLong("idproduto"),
                rs.getString("marca"),
                rs.getString("modelo"),
                rs.getString("descricao"),
                rs.getDouble("preco"),
                rs.getInt("quantidadeestoque"),
                rs.getLong("idinstrumento"),
                rs.getLong("idfornecedor")
        );
        // Verifica se as colunas do JOIN existem no ResultSet antes de tentar lê-las
        // Isso torna o método de mapeamento mais flexível
        if (hasColumn(rs, "nome_instrumento")) {
            produto.setNomeDoInstrumento(rs.getString("nome_instrumento"));
        }
        if (hasColumn(rs, "nome_fornecedor")) {
            produto.setNomeFornecedor(rs.getString("nome_fornecedor"));
        }
        return produto;
    }

    // Método auxiliar para verificar se uma coluna existe no ResultSet
    private boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columns = rsmd.getColumnCount();
        for (int x = 1; x <= columns; x++) {
            if (columnName.equals(rsmd.getColumnName(x))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Optional<ProdutoDTO> buscarPorId(Long idProduto) throws SQLException{
        String sql = "SELECT * FROM Produto WHERE idProduto = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            stmt.setLong(1, idProduto);
            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()){
                    return Optional.of(mapearResultSetParaProdutoDTO(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<ProdutoDTO> listarTodos() throws SQLException {
        List<ProdutoDTO> produtos = new ArrayList<>();
        String sql = "SELECT p.*, i.nome as nome_instrumento, f.nomefornecedor as nome_fornecedor " +
                "FROM produto p " +
                "JOIN instrumento i ON p.idinstrumento = i.idinstrumento " +
                "JOIN fornecedor f ON p.idfornecedor = f.idfornecedor " +
                "ORDER BY i.nome, p.marca";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ProdutoDTO produto = mapearResultSetParaProdutoDTO(rs); // Usando seu método de mapeamento existente
                // Preenche os campos extras com os dados do JOIN
                produto.setNomeDoInstrumento(rs.getString("nome_instrumento"));
                produto.setNomeFornecedor(rs.getString("nome_fornecedor"));
                produtos.add(produto);
            }
        }
        return produtos;
        }

    @Override
    public boolean existeProdutoPorFornecedor(Long idFornecedor) throws SQLException {
        String sql = "SELECT COUNT(*) FROM produto WHERE idfornecedor = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, idFornecedor);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    @Override
    public List<ProdutoDTO> listarPorFornecedor(Long idFornecedor) throws SQLException {
        List<ProdutoDTO> produtos = new ArrayList<>();
        String sql = "SELECT p.*, i.nome as nome_instrumento " +
                "FROM produto p " +
                "JOIN instrumento i ON p.idinstrumento = i.idinstrumento " +
                "WHERE p.idfornecedor = ? ORDER BY i.nome, p.marca";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, idFornecedor);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    produtos.add(mapearResultSetParaProdutoDTO(rs));
                }
            }
        }
        return produtos;
    }

    @Override
    public boolean existeProdutoPorInstrumento(Long idInstrumento) throws SQLException {
        String sql = "SELECT COUNT(*) FROM produto WHERE idinstrumento = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, idInstrumento);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }


}