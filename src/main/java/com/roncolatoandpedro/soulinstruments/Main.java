package com.roncolatoandpedro.soulinstruments;

import com.formdev.flatlaf.FlatDarkLaf; // Usaremos o tema escuro como base
import com.roncolatoandpedro.soulinstruments.ui.main.MainFrame;
import javax.swing.*;
import java.awt.Color;

public class Main {

    public static void main(String[] args) {

        // --- CONFIGURAÇÃO DO TEMA E CORES CUSTOMIZADAS ---
        try {
            // 1. Instala o Look and Feel base (tema escuro)
            FlatDarkLaf.setup();

            // 2. Define as cores da sua paleta
            Color night = new Color(0x15161B);
            Color gunmetal = new Color(0x2E343B);
            Color frenchGray = new Color(0xCACFD6);
            Color darkCyan = new Color(0x048A81);
            // Color chamoisee = new Color(0x91785D); // Cor não usada por enquanto

            // 3. Sobrescreve as propriedades do UIManager com suas cores
            // Fundo principal da janela e painéis
            UIManager.put("Panel.background", night);
            UIManager.put("Frame.background", night);
            UIManager.put("SplitPane.background", night);
            UIManager.put("TabbedPane.background", night);
            UIManager.put("TitledBorder.border", BorderFactory.createLineBorder(gunmetal.brighter()));
            UIManager.put("Separator.background", gunmetal);


            // Fundo de componentes de texto e tabelas
            UIManager.put("TextField.background", gunmetal);
            UIManager.put("TextArea.background", gunmetal);
            UIManager.put("Table.background", gunmetal);
            UIManager.put("ScrollPane.background", night);
            UIManager.put("ScrollPane.viewport.background", gunmetal);
            UIManager.put("List.background", gunmetal);
            UIManager.put("ComboBox.background", gunmetal);

            // Cor do texto principal
            UIManager.put("Label.foreground", frenchGray);
            UIManager.put("TitledBorder.titleColor", frenchGray);
            UIManager.put("TextField.foreground", frenchGray);
            UIManager.put("TextArea.foreground", frenchGray);
            UIManager.put("Table.foreground", frenchGray);
            UIManager.put("ComboBox.foreground", frenchGray);

            // Cor dos botões
            UIManager.put("Button.background", darkCyan);
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.hoverBackground", darkCyan.brighter());
            UIManager.put("Button.pressedBackground", darkCyan.darker());

            // Cor de seleção em tabelas e textos
            UIManager.put("Table.selectionBackground", darkCyan);
            UIManager.put("Table.selectionForeground", Color.WHITE);
            UIManager.put("TextField.selectionBackground", darkCyan);
            UIManager.put("TextField.selectionForeground", Color.WHITE);
            UIManager.put("TextArea.selectionBackground", darkCyan);
            UIManager.put("TextArea.selectionForeground", Color.WHITE);

            // Cor da borda quando um componente está em foco
            UIManager.put("Component.focusColor", darkCyan);
            UIManager.put("Button.default.focusColor", darkCyan);
            UIManager.put("TextComponent.arc", 5); // Bordas arredondadas nos campos de texto
            UIManager.put("Button.arc", 5); // Bordas arredondadas nos botões

        } catch (Exception ex) {
            System.err.println("Falha ao inicializar o tema FlatLaf.");
        }
        // --- FIM DA CONFIGURAÇÃO DO TEMA ---


        // Inicia a interface gráfica na thread correta
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}