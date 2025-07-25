package com.roncolatoandpedro.soulinstruments.dao.impl;

import com.roncolatoandpedro.soulinstruments.dao.interfaces.ItemPedidoDAO;
import com.roncolatoandpedro.soulinstruments.dao.interfaces.PedidoDAO;
import com.roncolatoandpedro.soulinstruments.dao.interfaces.ProdutoDAO;
import com.roncolatoandpedro.soulinstruments.dto.ItemPedidoDTO;
import com.roncolatoandpedro.soulinstruments.dto.PedidoDTO;
import com.roncolatoandpedro.soulinstruments.dto.ProdutoDTO;



import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PedidoDAOImpl implements PedidoDAO {

    private final Connection conexao;
    private final ItemPedidoDAO itemPedidoDAO;
    private final ProdutoDAO produtoDAO; //buscar os precos

    public PedidoDAOImpl(Connection conexao, ItemPedidoDAO itemPedidoDAO, ProdutoDAO produtoDAO) {
        this.conexao = conexao;
        this.itemPedidoDAO = itemPedidoDAO;
        this.produtoDAO = produtoDAO;
    }

    @Override
    public Connection getConexao() {
        return this.conexao;
    }

    @Override
    public ProdutoDAO getProdutoDAO() {
        return this.produtoDAO;
    }

    @Override
    public PedidoDTO salvar(PedidoDTO pedido) throws SQLException {
        String sqlPedido = "INSERT INTO Pedido (dataPedido, dataEntrega, valorTotal, idFornecedor) VALUES (?, ?, ?, ?)";

        boolean originalAutoCommit = conexao.getAutoCommit();
        try {
            conexao.setAutoCommit(false); // Inicia a transação

            // 1. Itera sobre os itens para buscar preços e calcular subtotais
            if (pedido.getItens() != null && !pedido.getItens().isEmpty()) {
                for (ItemPedidoDTO item : pedido.getItens()) {
                    // Busca o produto correspondente para obter o preço atual
                    ProdutoDTO produto = produtoDAO.buscarPorId(item.getIdProduto())
                            .orElseThrow(() -> new SQLException("Produto com ID " + item.getIdProduto() + " não encontrado."));

                    // Define o preço unitário no item
                    item.setValorUnitario(produto.getPreco());
                    // Calcula o valor total deste item (quantidade * preço)
                    item.calcularValorTotal();
                }
            }

            // 2. Com todos os itens já calculados, calcula o valor total do pedido
            pedido.calcularValorTotalPedido();
            // --- FIM DA CORREÇÃO DE LÓGICA ---

            // 3. Salvar o registro mestre do Pedido
            try (PreparedStatement stmtPedido = conexao.prepareStatement(sqlPedido, new String[]{"idpedido"})) {
                stmtPedido.setDate(1, Date.valueOf(pedido.getDataPedido()));
                if (pedido.getDataEntrega() != null) {
                    stmtPedido.setDate(2, Date.valueOf(pedido.getDataEntrega()));
                } else {
                    stmtPedido.setNull(2, java.sql.Types.DATE);
                }
                stmtPedido.setDouble(3, pedido.getValorTotal()); // Agora este valor NÃO é nulo
                stmtPedido.setLong(4, pedido.getIdFornecedor());

                int affectedRows = stmtPedido.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = stmtPedido.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            pedido.setIdPedido(generatedKeys.getLong(1));
                        }
                    }
                } else {
                    throw new SQLException("Falha ao salvar o pedido, nenhuma linha afetada.");
                }
            }

            // 4. Salvar os Itens do Pedido (agora com todos os dados preenchidos)
            if (pedido.getItens() != null && !pedido.getItens().isEmpty()) {
                for (ItemPedidoDTO item : pedido.getItens()) {
                    item.setIdPedido(pedido.getIdPedido()); // Garante que o ID do pedido está no item
                    itemPedidoDAO.salvar(item); // Chama o novo método salvar que só precisa do item
                }
            }

            conexao.commit(); // Efetiva a transação
            return pedido;

        } catch (SQLException e) {
            conexao.rollback(); // Desfaz tudo em caso de erro
            throw e;
        } finally {
            conexao.setAutoCommit(originalAutoCommit); // Restaura o estado da conexão
        }
    }

    @Override
    public void atualizarStatus(PedidoDTO pedido) throws SQLException {
        // Recalcula o valor total caso os itens tenham sido modificados externamente
        // (embora o ideal seja que o DTO gerencie isso ao modificar a lista de itens)
        pedido.calcularValorTotalPedido();

        String sql = "UPDATE Pedido SET dataEntrega = ?, valorTotal = ? WHERE idPedido = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (pedido.getDataEntrega() != null) {
                stmt.setDate(1, Date.valueOf(pedido.getDataEntrega()));
            } else {
                stmt.setNull(1, java.sql.Types.DATE);
            }
            stmt.setDouble(2, pedido.getValorTotal());
            stmt.setLong(3, pedido.getIdPedido());
            stmt.executeUpdate();
        }
    }

    @Override
    public void remover(Long idPedido) throws SQLException {
        // CORREÇÃO 1: A query agora deleta na tabela ItemPedido ONDE a coluna idpedido corresponde ao parâmetro.
        String sqlItens = "DELETE FROM ItemPedido WHERE idpedido = ?";
        String sqlPedido = "DELETE FROM Pedido WHERE idpedido = ?";

        boolean originalAutoCommit = conexao.getAutoCommit();
        try {
            conexao.setAutoCommit(false); // Inicia a transação

            // Passo 1: Primeiro, remove todos os itens filhos da tabela ItemPedido.
            try (PreparedStatement stmtItens = conexao.prepareStatement(sqlItens)) {
                stmtItens.setLong(1, idPedido); // Usa o ID do pedido que queremos apagar
                stmtItens.executeUpdate();
            }

            // Passo 2: Agora que os filhos foram removidos, remove o pedido pai.
            try (PreparedStatement stmtPedido = conexao.prepareStatement(sqlPedido)) {
                stmtPedido.setLong(1, idPedido); // Usa o ID do pedido que queremos apagar
                stmtPedido.executeUpdate();
            }

            conexao.commit(); // Se tudo deu certo, efetiva as remoções.

        } catch (SQLException e) {
            conexao.rollback(); // Se algo deu errado, desfaz tudo.
            throw new SQLException("Erro ao remover pedido e seus itens: " + e.getMessage(), e);
        } finally {
            conexao.setAutoCommit(originalAutoCommit); // Restaura o estado da conexão.
        }
    }


    private PedidoDTO mapearResultSetParaPedidoDTO(ResultSet rs) throws SQLException {
        PedidoDTO pedido = new PedidoDTO();
        pedido.setIdPedido(rs.getLong("idPedido"));
        pedido.setDataPedido(rs.getDate("dataPedido").toLocalDate());
        if (rs.getDate("dataEntrega") != null) {
            pedido.setDataEntrega(rs.getDate("dataEntrega").toLocalDate());
        }
        pedido.setIdFornecedor(rs.getLong("idFornecedor"));
        pedido.setValorTotal(rs.getDouble("valorTotal"));
        String statusDoBanco = rs.getString("status");
        return pedido;
    }

    @Override
    public Optional<PedidoDTO> buscarPorId(Long idPedido) throws SQLException {
        String sql = "SELECT * FROM Pedido WHERE idPedido = ?";
        PedidoDTO pedido = null;
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, idPedido);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    pedido = mapearResultSetParaPedidoDTO(rs);
                }
                if (pedido != null) {
                    List<ItemPedidoDTO> itens = this.itemPedidoDAO.buscarPorPedidoId(idPedido); // Usa o DAO injetado
                    pedido.setItens(itens); // Isso recalculará o valorTotal do pedido no DTO
                    return Optional.of(pedido);
                }
            }
        }

        if (pedido != null) {
            // Carregar os itens do pedido usando ItemPedidoDAO
            List<ItemPedidoDTO> itens = itemPedidoDAO.buscarPorPedidoId(idPedido);
            pedido.setItens(itens); // Isso também recalculará o valorTotal do pedido
            // Se o valorTotal no banco for o "mestre", você pode carregá-lo
            // e não recalcular, mas geralmente é bom recalcular a partir dos itens para consistência.
            // Se você confia no valor_total da tabela pedido:
            //  // (já feito no mapearResultSetParaPedidoDTO se a coluna existir e for usada)
            // Senão, certifique-se que calcularValorTotalPedido() seja chamado após setItens()
            //  (o que já acontece no PedidoDTO.setItens)
            return Optional.of(pedido);
        }
        return Optional.empty();
    }

    @Override
    public List<PedidoDTO> listarTodos() throws SQLException {
        List<PedidoDTO> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM Pedido ORDER BY dataPedido DESC";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                PedidoDTO pedido = mapearResultSetParaPedidoDTO(rs);
                List<ItemPedidoDTO> itens = itemPedidoDAO.buscarPorPedidoId(pedido.getIdPedido());
                pedido.setItens(itens);
                pedidos.add(pedido);
            }
        }
        return pedidos;
    }

    @Override
    public List<PedidoDTO> listarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) throws SQLException {
        List<PedidoDTO> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM Pedido WHERE dataPedido BETWEEN ? AND ? ORDER BY dataPedido DESC";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(dataInicio));
            stmt.setDate(2, Date.valueOf(dataFim));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PedidoDTO pedido = mapearResultSetParaPedidoDTO(rs);
                    List<ItemPedidoDTO> itens = itemPedidoDAO.buscarPorPedidoId(pedido.getIdPedido());
                    pedido.setItens(itens);
                    pedidos.add(pedido);
                }
            }
        }
        return pedidos;
    }

    @Override
    public List<PedidoDTO> listarPorFornecedor(Long idFornecedor) throws SQLException {
        List<PedidoDTO> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM Pedido WHERE idFornecedor = ? ORDER BY dataPedido DESC";
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, idFornecedor);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PedidoDTO pedido = mapearResultSetParaPedidoDTO(rs);
                    List<ItemPedidoDTO> itens = itemPedidoDAO.buscarPorPedidoId(pedido.getIdPedido());
                    pedido.setItens(itens);
                    pedidos.add(pedido);
                }
            }
        }
        return pedidos;
    }


    @Override
    public boolean existeItemComProduto(Long idProduto) throws SQLException {
        String sql = "SELECT COUNT(*) FROM itempedido WHERE idproduto = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, idProduto);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }


}