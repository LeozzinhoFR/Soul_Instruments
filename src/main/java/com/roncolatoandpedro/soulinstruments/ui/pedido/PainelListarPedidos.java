package com.roncolatoandpedro.soulinstruments.ui.pedido;

import com.roncolatoandpedro.soulinstruments.controller.PedidoController;
import com.roncolatoandpedro.soulinstruments.dto.FornecedorDTO;
import com.roncolatoandpedro.soulinstruments.dto.ItemPedidoDTO;
import com.roncolatoandpedro.soulinstruments.dto.PedidoDTO;
import com.roncolatoandpedro.soulinstruments.dto.StatusPedido;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PainelListarPedidos extends JPanel {

    private final PedidoController controller;
    private JTable tabelaPedidos;
    private JTable tabelaItens;
    private DefaultTableModel pedidosModel;
    private DefaultTableModel itensModel;
    private JButton btnConfirmarEntrega;
    private List<PedidoDTO> listaDePedidos; // Para manter os objetos DTO completos
    private Map<Long, String> cacheNomesFornecedores = new HashMap<>();

    public PainelListarPedidos(PedidoController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(10, 10));
        initComponents();
        refreshTabelaPedidos();
    }

    private void initComponents() {
        // --- Tabela Principal de Pedidos ---
        String[] colunasPedidos = {"ID", "Data Pedido", "Data Entrega", "Fornecedor", "Valor Total", "Status"};
        pedidosModel = new DefaultTableModel(colunasPedidos, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaPedidos = new JTable(pedidosModel);
        JScrollPane scrollPedidos = new JScrollPane(tabelaPedidos);
        scrollPedidos.setBorder(BorderFactory.createTitledBorder("Histórico de Pedidos"));

        // --- Painel de Detalhes e Ações (Sul) ---
        JPanel painelSul = new JPanel(new BorderLayout(10, 10));

        // --- Tabela de Itens do Pedido Selecionado ---
        String[] colunasItens = {"ID Produto", "Quantidade", "Valor Unitário", "Valor Total"};
        itensModel = new DefaultTableModel(colunasItens, 0);
        tabelaItens = new JTable(itensModel);
        JScrollPane scrollItens = new JScrollPane(tabelaItens);
        scrollItens.setBorder(BorderFactory.createTitledBorder("Detalhes do Pedido Selecionado"));
        scrollItens.setPreferredSize(new Dimension(0, 150)); // Altura inicial do painel de detalhes

        // --- Painel de Botões ---
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnConfirmarEntrega = new JButton("Confirmar Entrega");
        JButton btnAtualizarLista = new JButton("Atualizar Lista");
        btnConfirmarEntrega.setEnabled(false);
        painelBotoes.add(btnAtualizarLista);
        painelBotoes.add(btnConfirmarEntrega);

        painelSul.add(scrollItens, BorderLayout.CENTER);
        painelSul.add(painelBotoes, BorderLayout.SOUTH);

        // --- Adicionando componentes ao painel principal ---
        add(scrollPedidos, BorderLayout.CENTER);
        add(painelSul, BorderLayout.SOUTH);

        // --- Adicionando Listeners ---
        tabelaPedidos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                exibirDetalhesDoPedidoSelecionado();
            }
        });

        btnAtualizarLista.addActionListener(e -> refreshTabelaPedidos());
        btnConfirmarEntrega.addActionListener(e -> confirmarEntregaAction());
    }

    private void refreshTabelaPedidos() {
        try {
            // Guarda a seleção atual para tentar restaurar depois
            int selectedRow = tabelaPedidos.getSelectedRow();
            Long selectedId = null;
            if (selectedRow != -1) {
                selectedId = (Long) pedidosModel.getValueAt(selectedRow, 0);
            }

            pedidosModel.setRowCount(0); // Limpa a tabela
            itensModel.setRowCount(0);   // Limpa a tabela de detalhes
            btnConfirmarEntrega.setEnabled(false);

            listaDePedidos = controller.listarTodosPedidos();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (PedidoDTO pedido : listaDePedidos) {
                String nomeFornecedor = getNomeFornecedor(pedido.getIdFornecedor());
                pedidosModel.addRow(new Object[]{
                        pedido.getIdPedido(),
                        pedido.getDataPedido().format(formatter),
                        pedido.getDataEntrega() != null ? pedido.getDataEntrega().format(formatter) : "PENDENTE",
                        nomeFornecedor,
                        String.format("R$ %.2f", pedido.getValorTotal()),
                        pedido.getStatus()
                });
            }

            // Tenta restaurar a seleção
            if(selectedId != null) {
                for(int i = 0; i < pedidosModel.getRowCount(); i++) {
                    if(selectedId.equals(pedidosModel.getValueAt(i, 0))) {
                        tabelaPedidos.setRowSelectionInterval(i, i);
                        break;
                    }
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar pedidos: " + e.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getNomeFornecedor(Long id) throws SQLException {
        if (cacheNomesFornecedores.containsKey(id)) {
            return cacheNomesFornecedores.get(id);
        }
        FornecedorDTO fornecedor = controller.buscarFornecedorPorId(id);
        if (fornecedor != null) {
            cacheNomesFornecedores.put(id, fornecedor.getNomeFornecedor());
            return fornecedor.getNomeFornecedor();
        }
        return "Desconhecido";
    }

    private void exibirDetalhesDoPedidoSelecionado() {
        itensModel.setRowCount(0); // Limpa a tabela de itens
        int selectedRow = tabelaPedidos.getSelectedRow();

        if (selectedRow == -1) {
            btnConfirmarEntrega.setEnabled(false);
            return;
        }

        // Pega o DTO completo da lista, correspondente à linha selecionada
        PedidoDTO pedidoSelecionado = listaDePedidos.get(selectedRow);

        // Preenche a tabela de itens com os detalhes do pedido
        for (ItemPedidoDTO item : pedidoSelecionado.getItens()) {
            itensModel.addRow(new Object[]{
                    item.getIdProduto(),
                    item.getQuantidade(),
                    String.format("%.2f", item.getValorUnitario()),
                    String.format("%.2f", item.getValorTotal())
            });
        }

        // Habilita ou desabilita o botão de confirmar entrega baseado no status
        btnConfirmarEntrega.setEnabled(pedidoSelecionado.getStatus() == StatusPedido.PENDENTE);
    }

    private void confirmarEntregaAction() {
        int selectedRow = tabelaPedidos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Nenhum pedido selecionado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        PedidoDTO pedidoSelecionado = listaDePedidos.get(selectedRow);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja confirmar a entrega deste pedido?\nEsta ação atualizará o estoque e não poderá ser desfeita.",
                "Confirmar Entrega",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controller.confirmarEntrega(pedidoSelecionado);
                JOptionPane.showMessageDialog(this, "Entrega do pedido Nº " + pedidoSelecionado.getIdPedido() + " confirmada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                refreshTabelaPedidos(); // Atualiza a lista para mostrar o novo status
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erro ao confirmar a entrega: " + e.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}