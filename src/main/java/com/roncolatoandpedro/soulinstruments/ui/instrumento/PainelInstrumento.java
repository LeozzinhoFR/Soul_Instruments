package com.roncolatoandpedro.soulinstruments.ui.instrumento;

import com.roncolatoandpedro.soulinstruments.controller.InstrumentoController;
import com.roncolatoandpedro.soulinstruments.dto.Categoria;
import com.roncolatoandpedro.soulinstruments.dto.InstrumentoDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class PainelInstrumento extends JPanel {

    private final InstrumentoController controller;
    private JTable table;
    private DefaultTableModel tableModel;

    // Campos do formulário
    private JTextField txtNome;
    private JComboBox<Categoria> cbxCategoria;
    private JTextArea txtDescricao;
    private JButton btnSalvar;
    private JButton btnLimpar;
    private Long idInstrumentoSelecionado = null;

    public PainelInstrumento(InstrumentoController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(10, 10));
        initComponents();
        refreshTable();
    }

    private void initComponents() {
        // --- PAINEL DO FORMULÁRIO (DIREITA) ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Adicionar / Editar Instrumento"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nome
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        txtNome = new JTextField(20);
        formPanel.add(txtNome, gbc);

        // Categoria
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Categoria:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        cbxCategoria = new JComboBox<>(Categoria.values());
        formPanel.add(cbxCategoria, gbc);

        // Descrição (não existe no modelo de dados final, removido)

        // Botões Salvar e Limpar
        JPanel buttonPanelForm = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSalvar = new JButton("Salvar");
        btnLimpar = new JButton("Limpar");
        buttonPanelForm.add(btnSalvar);
        buttonPanelForm.add(btnLimpar);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        formPanel.add(buttonPanelForm, gbc);

        // --- PAINEL DA TABELA (ESQUERDA) ---
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Instrumentos Cadastrados"));

        String[] columnNames = {"ID", "Nome", "Categoria"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        // --- Botões de Ação da Tabela ---
        JPanel painelAcoesTabela = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnEditar = new JButton("Carregar para Edição");
        JButton btnExcluir = new JButton("Excluir Selecionado");
        painelAcoesTabela.add(btnEditar);
        painelAcoesTabela.add(btnExcluir);
        tablePanel.add(painelAcoesTabela, BorderLayout.SOUTH);

        //LISTENER
        table.getSelectionModel().addListSelectionListener(e -> {
            boolean isRowSelected = table.getSelectedRow() != -1;
            btnEditar.setEnabled(isRowSelected);
            btnExcluir.setEnabled(isRowSelected);
        });

        // Inicie os botões como desabilitados
        btnEditar.setEnabled(false);
        btnExcluir.setEnabled(false);




        // --- ADICIONANDO PAINÉIS AO PAINEL PRINCIPAL ---
        // Usando JSplitPane para dividir formulário e tabela
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tablePanel, formPanel);
        splitPane.setDividerLocation(500);
        add(splitPane, BorderLayout.CENTER);

        // --- LISTENERS (AÇÕES) ---
        btnSalvar.addActionListener(e -> salvarInstrumento());
        btnLimpar.addActionListener(e -> limparFormulario());
        btnEditar.addActionListener(e -> carregarInstrumentoParaEdicao());
        btnExcluir.addActionListener(e -> excluirInstrumento());
    }

    private void refreshTable() {
        tableModel.setRowCount(0); // Limpa a tabela
        try {
            List<InstrumentoDTO> instrumentos = controller.listarInstrumentos();
            for (InstrumentoDTO instrumento : instrumentos) {
                tableModel.addRow(new Object[]{
                        instrumento.getIdInstrumento(),
                        instrumento.getNome(),
                        instrumento.getCategoria()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar instrumentos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salvarInstrumento() {
        String nome = txtNome.getText().trim();
        Categoria categoria = (Categoria) cbxCategoria.getSelectedItem();

        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O campo 'Nome' é obrigatório.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        InstrumentoDTO instrumento = new InstrumentoDTO(idInstrumentoSelecionado, nome, categoria);

        try {
            if (idInstrumentoSelecionado == null) {
                controller.salvarInstrumento(instrumento);
                JOptionPane.showMessageDialog(this, "Instrumento adicionado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                controller.salvarInstrumento(instrumento);
                JOptionPane.showMessageDialog(this, "Instrumento atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            limparFormulario();
            refreshTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar instrumento: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparFormulario() {
        txtNome.setText("");
        cbxCategoria.setSelectedIndex(0);
        idInstrumentoSelecionado = null;
        table.clearSelection();
    }

    private void carregarInstrumentoParaEdicao() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um instrumento na tabela para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        idInstrumentoSelecionado = (Long) tableModel.getValueAt(selectedRow, 0);
        String nome = (String) tableModel.getValueAt(selectedRow, 1);
        Categoria categoria = (Categoria) tableModel.getValueAt(selectedRow, 2);

        txtNome.setText(nome);
        cbxCategoria.setSelectedItem(categoria);
    }

    private void excluirInstrumento() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um instrumento na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long id = (Long) tableModel.getValueAt(selectedRow, 0);
        String nome = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir o instrumento '" + nome + "'?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controller.removerInstrumento(id);
                JOptionPane.showMessageDialog(this, "Instrumento excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparFormulario();
                refreshTable();
            } catch (SQLException e) {
                // Captura a exceção de violação de chave estrangeira
                JOptionPane.showMessageDialog(this, "Erro ao excluir instrumento: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}