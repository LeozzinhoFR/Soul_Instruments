package com.roncolatoandpedro.soulinstruments.ui.fornecedor;

import com.roncolatoandpedro.soulinstruments.controller.FornecedorController;
import com.roncolatoandpedro.soulinstruments.dto.FornecedorDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class PainelFornecedor extends JPanel {

    private final FornecedorController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<FornecedorDTO> listaFornecedores;

    // Componentes do Formulário
    private JTextField txtNome;
    private JTextField txtCnpj;
    private JTextArea txtDescricao;
    private JButton btnSalvar;
    private JButton btnLimpar;
    private Long idFornecedorSelecionado = null;
    private JButton btnEditar, btnExcluir;

    // Componentes de Busca
    private JTextField txtBusca;
    private JButton btnBuscar;

    public PainelFornecedor(FornecedorController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(10, 10));
        initComponents();
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                refreshTable(); // Atualiza a tabela toda vez que o painel é mostrado
            }
        });
    }

    private void initComponents() {
        // --- PAINEL DA TABELA (ESQUERDA) ---
        JPanel painelTabela = new JPanel(new BorderLayout(10, 10));
        painelTabela.setBorder(BorderFactory.createTitledBorder("Fornecedores Cadastrados"));

        // --- Área de Busca ---
        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelBusca.add(new JLabel("Buscar por Nome ou CNPJ:"));
        txtBusca = new JTextField(20);
        btnBuscar = new JButton("Buscar");
        JButton btnLimparBusca = new JButton("Limpar");
        painelBusca.add(txtBusca);
        painelBusca.add(btnBuscar);
        painelBusca.add(btnLimparBusca);
        painelTabela.add(painelBusca, BorderLayout.NORTH);

        // --- Tabela ---
        String[] colunas = {"ID", "Nome Fantasia", "CNPJ"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        painelTabela.add(new JScrollPane(table), BorderLayout.CENTER);

        // --- Botões de Ação da Tabela ---
        JPanel painelAcoesTabela = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnEditar = new JButton("Carregar para Edição");
        JButton btnExcluir = new JButton("Excluir Selecionado");
        painelAcoesTabela.add(btnEditar);
        painelAcoesTabela.add(btnExcluir);
        painelTabela.add(painelAcoesTabela, BorderLayout.SOUTH);

        //LISTENER
        table.getSelectionModel().addListSelectionListener(e -> {
            boolean isRowSelected = table.getSelectedRow() != -1;
            btnEditar.setEnabled(isRowSelected);
            btnExcluir.setEnabled(isRowSelected);
        });

        // Inicie os botões como desabilitados
        btnEditar.setEnabled(false);
        btnExcluir.setEnabled(false);

        // --- PAINEL DO FORMULÁRIO (DIREITA) ---
        JPanel painelFormulario = new JPanel(new GridBagLayout());
        painelFormulario.setBorder(BorderFactory.createTitledBorder("Adicionar / Editar Fornecedor"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; painelFormulario.add(new JLabel("Nome Fantasia:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        txtNome = new JTextField(20);
        painelFormulario.add(txtNome, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; painelFormulario.add(new JLabel("CNPJ:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; txtCnpj = new JTextField(20);
        painelFormulario.add(txtCnpj, gbc);

        gbc.gridx = 0; gbc.gridy = 2; painelFormulario.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.ipady = 40; // Altura extra
        txtDescricao = new JTextArea(3, 20);
        painelFormulario.add(new JScrollPane(txtDescricao), gbc);

        JPanel painelBotoesForm = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSalvar = new JButton("Salvar");
        btnLimpar = new JButton("Limpar / Novo");
        painelBotoesForm.add(btnSalvar);
        painelBotoesForm.add(btnLimpar);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.ipady = 0;
        painelFormulario.add(painelBotoesForm, gbc);

        // --- JSplitPane PARA DIVIDIR A TELA ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, painelTabela, painelFormulario);
        splitPane.setDividerLocation(550);
        add(splitPane, BorderLayout.CENTER);

        // --- LISTENERS ---
        btnSalvar.addActionListener(e -> salvar());
        btnLimpar.addActionListener(e -> limparFormulario());
        btnEditar.addActionListener(e -> carregarParaEdicao());
        btnExcluir.addActionListener(e -> excluir());
        btnBuscar.addActionListener(e -> buscar());
        btnLimparBusca.addActionListener(e -> refreshTable());
    }

    private void refreshTable() {
        buscar(null);
    }

    private void buscar() {
        String termo = txtBusca.getText();
        buscar(termo);
    }

    private void buscar(String termo) {
        try {
            tableModel.setRowCount(0);
            listaFornecedores = controller.buscar(termo);
            for (FornecedorDTO f : listaFornecedores) {
                tableModel.addRow(new Object[]{f.getIdFornecedor(), f.getNomeFornecedor(), f.getCnpj()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar fornecedores: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salvar() {
        String nome = txtNome.getText().trim();
        String cnpj = txtCnpj.getText().trim();
        String descricao = txtDescricao.getText().trim();

        if (nome.isEmpty() || cnpj.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome Fantasia e CNPJ são obrigatórios.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        FornecedorDTO fornecedor = new FornecedorDTO(idFornecedorSelecionado, nome, cnpj, descricao);
        try {
            controller.salvar(fornecedor);
            JOptionPane.showMessageDialog(this, "Fornecedor salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparFormulario();
            refreshTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar fornecedor: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparFormulario() {
        txtNome.setText("");
        txtCnpj.setText("");
        txtDescricao.setText("");
        idFornecedorSelecionado = null;
        table.clearSelection();
    }

    private void carregarParaEdicao() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um fornecedor na tabela para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        FornecedorDTO fornecedor = listaFornecedores.get(table.convertRowIndexToModel(selectedRow));
        idFornecedorSelecionado = fornecedor.getIdFornecedor();
        txtNome.setText(fornecedor.getNomeFornecedor());
        txtCnpj.setText(fornecedor.getCnpj());
        txtDescricao.setText(fornecedor.getDescricao());
    }

    private void excluir() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um fornecedor na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        FornecedorDTO fornecedor = listaFornecedores.get(table.convertRowIndexToModel(selectedRow));
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir o fornecedor '" + fornecedor.getNomeFornecedor() + "'?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controller.remover(fornecedor.getIdFornecedor());
                JOptionPane.showMessageDialog(this, "Fornecedor excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparFormulario();
                refreshTable();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}