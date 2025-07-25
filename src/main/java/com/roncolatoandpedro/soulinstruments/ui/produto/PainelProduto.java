package com.roncolatoandpedro.soulinstruments.ui.produto;

import com.roncolatoandpedro.soulinstruments.controller.ProdutoController;
import com.roncolatoandpedro.soulinstruments.dto.FornecedorDTO;
import com.roncolatoandpedro.soulinstruments.dto.InstrumentoDTO;
import com.roncolatoandpedro.soulinstruments.dto.ProdutoDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Comparator;
import java.sql.SQLException;
import java.util.List;

public class PainelProduto extends JPanel {

    private final ProdutoController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<ProdutoDTO> listaProdutos;

    // Componentes do Formulário
    private JComboBox<InstrumentoDTO> cbxInstrumento;
    private JComboBox<FornecedorDTO> cbxFornecedor;
    private JTextField txtMarca, txtModelo, txtPreco;
    private JTextArea txtDescricao;
    private JTextField txtEstoque; // Não editável
    private JButton btnSalvar, btnLimpar;
    private Long idProdutoSelecionado = null;

    public PainelProduto(ProdutoController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(10, 10));
        initComponents();
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                System.out.println("Painel de Produtos visível, atualizando dados...");
                refreshTable();
                carregarComboBoxes();
            }
        });
    }

    private void initComponents() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, criarPainelTabela(), criarPainelFormulario());
        splitPane.setDividerLocation(600);
        add(splitPane, BorderLayout.CENTER);

        setupListeners();
    }



    private JPanel criarPainelTabela() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createTitledBorder("Produtos Cadastrados"));

        String[] colunas = {"ID", "Instrumento", "Marca", "Modelo", "Preço", "Estoque", "Fornecedor"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true); //Habilita a ordenação automática ao clicar nos cabeçalhos das colunas
        DefaultRowSorter sorter = (DefaultRowSorter) table.getRowSorter();
        sorter.setComparator(0, (Comparator<Long>) (id1, id2) -> id1.compareTo(id2)); // Coluna 0 (ID)
        painel.add(new JScrollPane(table), BorderLayout.CENTER);



        JPanel painelAcoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnEditar = new JButton("Carregar para Edição");
        JButton btnExcluir = new JButton("Excluir Selecionado");
        painelAcoes.add(btnEditar);
        painelAcoes.add(btnExcluir);
        painel.add(painelAcoes, BorderLayout.SOUTH);

        btnEditar.addActionListener(e -> carregarParaEdicao());
        btnExcluir.addActionListener(e -> excluir());

        //LISTENER
        table.getSelectionModel().addListSelectionListener(e -> {
            boolean isRowSelected = table.getSelectedRow() != -1;
            btnEditar.setEnabled(isRowSelected);
            btnExcluir.setEnabled(isRowSelected);
        });

        // Inicie os botões como desabilitados
        btnEditar.setEnabled(false);
        btnExcluir.setEnabled(false);

        return painel;
    }

    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Adicionar / Editar Produto"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Linha 0: Instrumento
        gbc.gridx = 0; gbc.gridy = 0; painel.add(new JLabel("Instrumento Base:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        cbxInstrumento = new JComboBox<>();
        painel.add(cbxInstrumento, gbc);

        // Linha 1: Marca
        gbc.gridx = 0; gbc.gridy = 1; painel.add(new JLabel("Marca:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        txtMarca = new JTextField();
        painel.add(txtMarca, gbc);

        // Linha 2: Modelo
        gbc.gridx = 0; gbc.gridy = 2; painel.add(new JLabel("Modelo:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        txtModelo = new JTextField();
        painel.add(txtModelo, gbc);

        // Linha 3: Descrição
        gbc.gridx = 0; gbc.gridy = 3; painel.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.ipady = 40;
        txtDescricao = new JTextArea(3, 20);
        painel.add(new JScrollPane(txtDescricao), gbc);
        gbc.ipady = 0;

        // Linha 4: Preço
        gbc.gridx = 0; gbc.gridy = 4; painel.add(new JLabel("Preço (R$):"), gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        txtPreco = new JTextField();
        painel.add(txtPreco, gbc);

        // Linha 5: Estoque
        gbc.gridx = 0; gbc.gridy = 5; painel.add(new JLabel("Estoque Atual:"), gbc);
        gbc.gridx = 1; gbc.gridy = 5;
        txtEstoque = new JTextField("0");
        txtEstoque.setEditable(false);
        txtEstoque.setFocusable(false);
        painel.add(txtEstoque, gbc);

        // Linha 6: Fornecedor
        gbc.gridx = 0; gbc.gridy = 6; painel.add(new JLabel("Fornecedor:"), gbc);
        gbc.gridx = 1; gbc.gridy = 6;
        cbxFornecedor = new JComboBox<>();
        painel.add(cbxFornecedor, gbc);

        // Linha 7: Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSalvar = new JButton("Salvar");
        btnLimpar = new JButton("Limpar / Novo");
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnLimpar);
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        painel.add(painelBotoes, gbc);

        return painel;
    }

    private void setupListeners() {
        btnSalvar.addActionListener(e -> salvar());
        btnLimpar.addActionListener(e -> limparFormulario());
    }

    private void refreshTable() {
        try {
            tableModel.setRowCount(0);
            listaProdutos = controller.listarTodos(); // Este método já faz o JOIN

            if (listaProdutos == null || listaProdutos.isEmpty()) {
                System.out.println("Nenhum produto encontrado para exibir na tabela.");
                return;
            }

            for (ProdutoDTO p : listaProdutos) {
                // CORREÇÃO: Usar os getters dos nomes que vêm do JOIN
                tableModel.addRow(new Object[]{
                        p.getIdProduto(),
                        p.getNomeDoInstrumento(), // Usar o campo transient
                        p.getMarca(),
                        p.getModelo(),
                        String.format("%.2f", p.getPreco()),
                        p.getQuantidadeEstoque(),
                        p.getNomeFornecedor() // Usar o campo transient
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void carregarComboBoxes() {
        try {
            // Carregar Instrumentos
            cbxInstrumento.removeAllItems();
            List<InstrumentoDTO> instrumentos = controller.listarTodosInstrumentos();
            for (InstrumentoDTO i : instrumentos) {
                cbxInstrumento.addItem(i);
            }
            cbxInstrumento.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof InstrumentoDTO) {
                        setText(((InstrumentoDTO) value).getNome());
                    }
                    return this;
                }
            });

            // Carregar Fornecedores
            cbxFornecedor.removeAllItems();
            List<FornecedorDTO> fornecedores = controller.listarTodosFornecedores();
            for (FornecedorDTO f : fornecedores) {
                cbxFornecedor.addItem(f);
            }
            cbxFornecedor.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof FornecedorDTO) {
                        setText(((FornecedorDTO) value).getNomeFornecedor());
                    }
                    return this;
                }
            });

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar dados de apoio: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salvar() {
        // Obter dados do formulário
        InstrumentoDTO instrumento = (InstrumentoDTO) cbxInstrumento.getSelectedItem();
        FornecedorDTO fornecedor = (FornecedorDTO) cbxFornecedor.getSelectedItem();
        String marca = txtMarca.getText().trim();
        String modelo = txtModelo.getText().trim();
        String descricao = txtDescricao.getText().trim();
        String precoStr = txtPreco.getText().trim().replace(",", ".");

        // Validações
        if (instrumento == null || fornecedor == null || marca.isEmpty() || precoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Instrumento, Fornecedor, Marca e Preço são obrigatórios.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double preco = Double.parseDouble(precoStr);
            if (preco < 0) {
                JOptionPane.showMessageDialog(this, "O preço não pode ser negativo.", "Erro de Validação", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ProdutoDTO produto = new ProdutoDTO();
            produto.setIdProduto(idProdutoSelecionado);
            produto.setMarca(marca);
            produto.setModelo(modelo);
            produto.setDescricao(descricao);
            produto.setPreco(preco);
            produto.setIdInstrumento(instrumento.getIdInstrumento());
            produto.setIdFornecedor(fornecedor.getIdFornecedor());
            // O estoque não é setado aqui, é controlado pelo controller

            controller.salvar(produto);
            JOptionPane.showMessageDialog(this, "Produto salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparFormulario();
            refreshTable();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "O preço deve ser um número válido.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar produto: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparFormulario() {
        idProdutoSelecionado = null;
        table.clearSelection();
        cbxInstrumento.setSelectedIndex(-1);
        cbxFornecedor.setSelectedIndex(-1);
        txtMarca.setText("");
        txtModelo.setText("");
        txtDescricao.setText("");
        txtPreco.setText("");
        txtEstoque.setText("0");
        cbxFornecedor.setEnabled(true); // Habilita o ComboBox para um novo cadastro
    }

    private void carregarParaEdicao() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na tabela para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ProdutoDTO produto = listaProdutos.get(table.convertRowIndexToModel(selectedRow));
        idProdutoSelecionado = produto.getIdProduto();

        // Selecionar itens nos ComboBoxes
        selecionarItemNoComboBox(cbxInstrumento, produto.getIdInstrumento());
        selecionarItemNoComboBox(cbxFornecedor, produto.getIdFornecedor());

        txtMarca.setText(produto.getMarca());
        txtModelo.setText(produto.getModelo());
        txtDescricao.setText(produto.getDescricao());
        txtPreco.setText(String.format("%.2f", produto.getPreco()));
        txtEstoque.setText(String.valueOf(produto.getQuantidadeEstoque()));

        // Regra de Negócio: desabilitar ComboBox de Fornecedor se o produto estiver em uso
        try {
            if (controller.isProdutoEmUso(idProdutoSelecionado)) {
                cbxFornecedor.setEnabled(false);
                JOptionPane.showMessageDialog(this, "Este produto já está em um pedido. O fornecedor não pode ser alterado.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                cbxFornecedor.setEnabled(true);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao verificar uso do produto: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            cbxFornecedor.setEnabled(false); // Desabilita por segurança
        }
    }

    // Método auxiliar para selecionar item no ComboBox por ID
    private void selecionarItemNoComboBox(JComboBox comboBox, Long id) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            Object item = comboBox.getItemAt(i);
            if (item instanceof InstrumentoDTO && ((InstrumentoDTO) item).getIdInstrumento().equals(id)) {
                comboBox.setSelectedIndex(i);
                return;
            }
            if (item instanceof FornecedorDTO && ((FornecedorDTO) item).getIdFornecedor().equals(id)) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private void excluir() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ProdutoDTO produto = listaProdutos.get(table.convertRowIndexToModel(selectedRow));
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir o produto '" + produto.getMarca() + " " + produto.getModelo() + "'?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controller.remover(produto.getIdProduto());
                JOptionPane.showMessageDialog(this, "Produto excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparFormulario();
                refreshTable();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}