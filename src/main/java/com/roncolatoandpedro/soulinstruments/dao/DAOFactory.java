package com.roncolatoandpedro.soulinstruments.dao;

import com.roncolatoandpedro.soulinstruments.dao.impl.*;
import com.roncolatoandpedro.soulinstruments.dao.interfaces.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DAOFactory {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/SoulInstruments";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "Eclesiastes1401";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            // Em uma aplicação Swing, um erro aqui é fatal e deve ser comunicado.
            // Lançar uma RuntimeException para parar a aplicação é uma abordagem válida.
            throw new ExceptionInInitializerError(new RuntimeException("Driver PostgreSQL JDBC não encontrado. Verifique o classpath.", e));
        }
    }
    private static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // --- MÉTODOS FACTORY PARA CRIAR CADA DAO ---.
    public static InstrumentoDAO criarInstrumentoDAO() throws SQLException {
        return new InstrumentoDAOImpl(getConexao());
    }
    public static ProdutoDAO criarProdutoDAO() throws SQLException {
        return new ProdutoDAOImpl(getConexao());
    }
    public static FornecedorDAO criarFornecedorDAO() throws SQLException {
        return new FornecedorDAOImpl(getConexao());
    }
    public static ItemPedidoDAO criarItemPedidoDAO() throws SQLException {
        // Se ItemPedidoDAOImpl não precisar de outras dependências DAO no construtor
        return new ItemPedidoDAOImpl(getConexao());
    }

    public static PedidoDAO criarPedidoDAO() throws SQLException {
        // Obter a conexão uma vez. Esta mesma conexão será usada por todos os DAOs.
        Connection conexao = getConexao();

        // Cria as dependências que PedidoDAOImpl precisa, passando a mesma conexão.
        ProdutoDAO produtoDAO = new ProdutoDAOImpl(conexao);
        ItemPedidoDAO itemPedidoDAO = new ItemPedidoDAOImpl(conexao);

        // Retorna a instância do PedidoDAOImpl com suas dependências.
        return new PedidoDAOImpl(conexao, itemPedidoDAO, produtoDAO);
    }
}
