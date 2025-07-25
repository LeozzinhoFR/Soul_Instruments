package com.roncolatoandpedro.soulinstruments.ui.main;

import com.roncolatoandpedro.soulinstruments.controller.*;
import com.roncolatoandpedro.soulinstruments.ui.instrumento.PainelInstrumento;
import com.roncolatoandpedro.soulinstruments.ui.pedido.*;
import com.roncolatoandpedro.soulinstruments.ui.produto.PainelProduto;
import com.roncolatoandpedro.soulinstruments.ui.fornecedor.PainelFornecedor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel containerPanel;

    public MainFrame() {
        setTitle("Soul Instruments - Sistema de Gestão");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- PAINEL DE NAVEGAÇÃO ---
        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        String[] buttonLabels = {"Instrumentos", "Fornecedores", "Produtos", "Listar Pedidos", "Novo Pedido"};
        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.addActionListener(new NavigationListener());
            navigationPanel.add(button);
        }

        // --- PAINEL CONTAINER COM CARDLAYOUT ---
        cardLayout = new CardLayout();
        containerPanel = new JPanel(cardLayout);

        // --- INICIALIZANDO CONTROLLERS E PAINÉIS ---
        // Criando instâncias dos controllers
        InstrumentoController instrumentoController = new InstrumentoController();
        FornecedorController fornecedorController = new FornecedorController();
        ProdutoController produtoController = new ProdutoController();
        PedidoController pedidoController = new PedidoController();

        // Criando e adicionando os painéis ao CardLayout
        containerPanel.add(new PainelInstrumento(instrumentoController), "Instrumentos");
        containerPanel.add(new PainelFornecedor(fornecedorController), "Fornecedores");
        containerPanel.add(new PainelProduto(produtoController), "Produtos");
        containerPanel.add(new PainelListarPedidos(pedidoController), "Listar Pedidos");
        containerPanel.add(new PainelCriarPedido(pedidoController), "Novo Pedido");
        containerPanel.add(new PainelListarPedidos(pedidoController), "Listar Pedidos");

        // Adicionando os painéis de navegação e container ao frame
        add(navigationPanel, BorderLayout.NORTH);
        add(containerPanel, BorderLayout.CENTER);
    }

    // Listener para os botões de navegação
    private class NavigationListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            cardLayout.show(containerPanel, command);
        }
    }
}