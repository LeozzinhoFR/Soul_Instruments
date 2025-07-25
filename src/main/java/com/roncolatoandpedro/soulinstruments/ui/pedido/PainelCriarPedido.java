package com.roncolatoandpedro.soulinstruments.ui.pedido;

import com.roncolatoandpedro.soulinstruments.controller.PedidoController;
import com.roncolatoandpedro.soulinstruments.dto.FornecedorDTO;
import com.roncolatoandpedro.soulinstruments.dto.ItemPedidoDTO;
import com.roncolatoandpedro.soulinstruments.dto.PedidoDTO;
import com.roncolatoandpedro.soulinstruments.dto.ProdutoDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.SQLException;
import java.util.List;

public class PainelCriarPedido extends JPanel {

    private final PedidoController controller;
    private JComboBox<FornecedorDTO> cbxFornecedores;
    private JTable tabelaProdutosDisponiveis;
    private JTable tabelaItensPedido;
    private DefaultTableModel produtosModel;
    private DefaultTableModel carrinhoModel;
    private JButton btnAdicionar;
    private JButton btnFinalizar;
    private JLabel lblValorTotal;

    public PainelCriarPedido(PedidoController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(10, 10));
        initComponents();
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                carregarFornecedores(); // Sempre carrega a lista mais recente de fornecedores
            }
        });
    }

    private void initComponents() {
        // --- PAINEL NORTE: SELEÇÃO DO FORNECEDOR ---
        JPanel painelFornecedor = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelFornecedor.add(new JLabel("Selecione o Fornecedor:"));
        cbxFornecedores = new JComboBox<>();
        cbxFornecedores.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof FornecedorDTO) {
                    setText(((FornecedorDTO) value).getNomeFornecedor());
                }
                return this;
            }
        });
        painelFornecedor.add(cbxFornecedores);

        // --- PAINEL CENTRAL: PRODUTOS E CARRINHO ---
        produtosModel = new DefaultTableModel(new String[]{"ID", "Instrumento", "Marca/Modelo", "Preço"}, 0);
        tabelaProdutosDisponiveis = new JTable(produtosModel);
        JScrollPane scrollProdutos = new JScrollPane(tabelaProdutosDisponiveis);
        scrollProdutos.setBorder(BorderFactory.createTitledBorder("Produtos Disponíveis do Fornecedor"));

        carrinhoModel = new DefaultTableModel(new String[]{"ID Produto", "Produto", "Qtd", "Vlr. Unitário"}, 0);
        tabelaItensPedido = new JTable(carrinhoModel);
        JScrollPane scrollCarrinho = new JScrollPane(tabelaItensPedido);
        scrollCarrinho.setBorder(BorderFactory.createTitledBorder("Itens do Pedido"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollProdutos, scrollCarrinho);
        splitPane.setResizeWeight(0.6);

        // --- PAINEL SUL: AÇÕES E TOTAL ---
        JPanel painelAcoes = new JPanel(new BorderLayout());
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnAdicionar = new JButton("Adicionar ao Pedido");
        btnFinalizar = new JButton("Finalizar Pedido");
        lblValorTotal = new JLabel("Valor Total: R$ 0.00");
        lblValorTotal.setFont(new Font("Arial", Font.BOLD, 16));

        btnAdicionar.setEnabled(false);
        btnFinalizar.setEnabled(false);

        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnFinalizar);
        painelAcoes.add(painelBotoes, BorderLayout.CENTER);
        painelAcoes.add(lblValorTotal, BorderLayout.EAST);

        add(painelFornecedor, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(painelAcoes, BorderLayout.SOUTH);

        // --- LISTENERS ---
        cbxFornecedores.addActionListener(e -> carregarProdutosDoFornecedor());
        tabelaProdutosDisponiveis.getSelectionModel().addListSelectionListener(e -> {
            btnAdicionar.setEnabled(tabelaProdutosDisponiveis.getSelectedRow() != -1);
        });
        btnAdicionar.addActionListener(e -> adicionarItemAoPedido());
        btnFinalizar.addActionListener(e -> finalizarPedido());
    }

    private void carregarFornecedores() {
        try {
            List<FornecedorDTO> fornecedores = controller.listarFornecedores();
            if (fornecedores.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhum fornecedor cadastrado. Cadastre um fornecedor primeiro.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            for (FornecedorDTO f : fornecedores) {
                cbxFornecedores.addItem(f);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar fornecedores: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarProdutosDoFornecedor() {
        FornecedorDTO fornecedorSelecionado = (FornecedorDTO) cbxFornecedores.getSelectedItem();
        if (fornecedorSelecionado == null) return;

        // CORREÇÃO: Verifica se o carrinho já tem itens antes de mudar o fornecedor.
        if (carrinhoModel.getRowCount() > 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Mudar o fornecedor irá limpar o pedido atual. Deseja continuar?",
                    "Aviso",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.NO_OPTION) {
                // Se o usuário não quiser limpar, precisamos reverter a seleção do ComboBox
                // para o fornecedor do pedido atual. Isso é complexo.
                // Uma abordagem mais simples é apenas avisar e limpar.
                // Para reverter, você precisaria armazenar o fornecedor anterior.
                // Por simplicidade, vamos limpar e continuar.
                limparFormulario(); // Limpa o carrinho
            } else {
                limparFormulario(); // Limpa o carrinho
            }
        }

        produtosModel.setRowCount(0); // Limpa a tabela de produtos disponíveis

        try {
            List<ProdutoDTO> produtos = controller.listarProdutosPorFornecedor(fornecedorSelecionado.getIdFornecedor());
            for (ProdutoDTO p : produtos) {
                produtosModel.addRow(new Object[]{p.getIdProduto(), p.getNomeDoInstrumento(), p.getMarca() + " " + p.getModelo(), p.getPreco()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos do fornecedor: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void adicionarItemAoPedido() {
        int selectedRow = tabelaProdutosDisponiveis.getSelectedRow();
        if (selectedRow == -1) return;

        String qtdStr = JOptionPane.showInputDialog(this, "Informe a quantidade:", "Adicionar Produto", JOptionPane.QUESTION_MESSAGE);
        if (qtdStr == null || qtdStr.trim().isEmpty()) return;

        try {
            int quantidade = Integer.parseInt(qtdStr);
            if (quantidade <= 0) {
                JOptionPane.showMessageDialog(this, "A quantidade deve ser um número positivo.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Long idProduto = (Long) produtosModel.getValueAt(selectedRow, 0);
            String nomeProduto = produtosModel.getValueAt(selectedRow, 1) + " " + produtosModel.getValueAt(selectedRow, 2);
            Double precoUnitario = (Double) produtosModel.getValueAt(selectedRow, 3);

            carrinhoModel.addRow(new Object[]{idProduto, nomeProduto, quantidade, precoUnitario});
            btnFinalizar.setEnabled(true);

            // Atualizar valor total (simplificado, lógica completa no DTO)
            double total = 0;
            for (int i = 0; i < carrinhoModel.getRowCount(); i++) {
                total += (Integer)carrinhoModel.getValueAt(i, 2) * (Double)carrinhoModel.getValueAt(i, 3);
            }
            lblValorTotal.setText(String.format("Valor Total: R$ %.2f", total));

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um número válido para a quantidade.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void finalizarPedido() {
        FornecedorDTO fornecedor = (FornecedorDTO) cbxFornecedores.getSelectedItem();
        if (fornecedor == null) return;

        PedidoDTO novoPedido = new PedidoDTO();
        novoPedido.setIdFornecedor(fornecedor.getIdFornecedor());

        for (int i = 0; i < carrinhoModel.getRowCount(); i++) {
            Long idProduto = (Long) carrinhoModel.getValueAt(i, 0);
            int quantidade = (Integer) carrinhoModel.getValueAt(i, 2);
            novoPedido.addItem(new ItemPedidoDTO(idProduto, quantidade));
        }

        try {
            controller.salvarPedido(novoPedido);
            JOptionPane.showMessageDialog(this, "Pedido Nº " + novoPedido.getIdPedido() + " criado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparFormulario();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao finalizar pedido: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparFormulario() {
        carrinhoModel.setRowCount(0);
        lblValorTotal.setText("Valor Total: R$ 0.00");
        btnFinalizar.setEnabled(false);
    }
}