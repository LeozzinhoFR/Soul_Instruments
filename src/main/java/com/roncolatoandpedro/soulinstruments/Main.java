package com.roncolatoandpedro.soulinstruments;

import com.roncolatoandpedro.soulinstruments.dao.DAOFactory;
import com.roncolatoandpedro.soulinstruments.dao.interfaces.*;
import com.roncolatoandpedro.soulinstruments.dto.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Main {

    public static void main(String[] args) {
        System.out.println("--- INICIANDO TESTES DA CAMADA DAO ---");

        try {
            // Executa os testes para cada entidade
            testarFornecedorDAO();
            testarInstrumentoDAO();
            testarProdutoDAO();
            testarPedidoDAO();

        } catch (SQLException e) {
            System.err.println("!!! UM ERRO GERAL OCORREU DURANTE OS TESTES !!!");
            e.printStackTrace();
        }

        System.out.println("\n--- TESTES FINALIZADOS ---");
    }

    private static void testarFornecedorDAO() throws SQLException {
        System.out.println("\n--- TESTANDO FornecedorDAO ---");
        FornecedorDAO fornecedorDAO = DAOFactory.criarFornecedorDAO();

        // 1. SALVAR
        System.out.println("1. Salvando novo fornecedor...");
        FornecedorDTO novoFornecedor = new FornecedorDTO(null, "Fender Musical Instruments", "11222333000144", "Fornecedor oficial de guitarras Fender.");
        novoFornecedor = fornecedorDAO.salvar(novoFornecedor);
        System.out.println("Salvo com sucesso! ID: " + novoFornecedor.getIdFornecedor());

        // 2. BUSCAR POR ID
        System.out.println("\n2. Buscando fornecedor pelo ID: " + novoFornecedor.getIdFornecedor());
        Optional<FornecedorDTO> fornecedorBuscado = fornecedorDAO.buscarPorId(novoFornecedor.getIdFornecedor());
        System.out.println("Encontrado: " + fornecedorBuscado.isPresent());
        fornecedorBuscado.ifPresent(f -> System.out.println("  -> Nome: " + f.getNomeFornecedor()));

        // 3. ATUALIZAR
        System.out.println("\n3. Atualizando descrição do fornecedor...");
        novoFornecedor.setDescricao("Fornecedor oficial de guitarras e baixos Fender.");
        fornecedorDAO.atualizar(novoFornecedor);
        FornecedorDTO fornecedorAtualizado = fornecedorDAO.buscarPorId(novoFornecedor.getIdFornecedor()).get();
        System.out.println("Descrição atualizada: " + fornecedorAtualizado.getDescricao());

        // 4. LISTAR TODOS
        System.out.println("\n4. Listando todos os fornecedores...");
        List<FornecedorDTO> todosFornecedores = fornecedorDAO.listarTodos();
        System.out.println("Total de fornecedores encontrados: " + todosFornecedores.size());
        todosFornecedores.forEach(f -> System.out.println("  -> " + f.getNomeFornecedor()));

        // 5. REMOVER
        System.out.println("\n5. Removendo fornecedor ID: " + novoFornecedor.getIdFornecedor());
        fornecedorDAO.remover(novoFornecedor.getIdFornecedor());
        Optional<FornecedorDTO> fornecedorRemovido = fornecedorDAO.buscarPorId(novoFornecedor.getIdFornecedor());
        System.out.println("Fornecedor removido com sucesso? " + fornecedorRemovido.isEmpty());
    }

    private static void testarInstrumentoDAO() throws SQLException {
        System.out.println("\n--- TESTANDO InstrumentoDAO ---");
        InstrumentoDAO instrumentoDAO = DAOFactory.criarInstrumentoDAO();

        // 1. SALVAR
        System.out.println("1. Salvando novo instrumento...");
        InstrumentoDTO novoInstrumento = new InstrumentoDTO(null, "Guitarra Elétrica", Categoria.cordas);
        novoInstrumento = instrumentoDAO.salvar(novoInstrumento);
        System.out.println("Salvo com sucesso! ID: " + novoInstrumento.getIdInstrumento());

        InstrumentoDTO novoInstrumento2 = new InstrumentoDTO(null, "Guitarra Acústica", Categoria.cordas);
        novoInstrumento2 = instrumentoDAO.salvar(novoInstrumento2);
        System.out.println("Salvo com sucesso! ID: " + novoInstrumento2.getIdInstrumento());

        // 2. LISTAR TODOS
        System.out.println("\n2. Listando todos os instrumentos...");
        List<InstrumentoDTO> todosInstrumentos = instrumentoDAO.listarTodos();
        System.out.println("Total de instrumentos encontrados: " + todosInstrumentos.size());
        todosInstrumentos.forEach(f -> System.out.println("  -> " + f.getNome() + " ID: " + f.getIdInstrumento()));

        // 3. REMOVER (será removido no teste do Produto)
        //System.out.println("\n3. Instrumento será removido ao final do teste de Produto (ON DELETE CASCADE).");
    }

    private static void testarProdutoDAO() throws SQLException {
        System.out.println("\n--- TESTANDO ProdutoDAO ---");
        ProdutoDAO produtoDAO = DAOFactory.criarProdutoDAO();
        FornecedorDAO fornecedorDAO = DAOFactory.criarFornecedorDAO(); // Dependência
        InstrumentoDAO instrumentoDAO = DAOFactory.criarInstrumentoDAO(); // Dependência

        // Pré-requisitos: Criar um Fornecedor e um Instrumento para associar
        FornecedorDTO fornecedor = fornecedorDAO.salvar(new FornecedorDTO(null, "Gibson Brands", "55666777000188", "Fornecedor de guitarras Gibson."));
        InstrumentoDTO instrumento = instrumentoDAO.salvar(new InstrumentoDTO(null, "Guitarra de 12 Cordas", Categoria.cordas));
        System.out.println("Dependências criadas: Fornecedor ID " + fornecedor.getIdFornecedor() + ", Instrumento ID " + instrumento.getIdInstrumento());

        // 1. SALVAR PRODUTO
        System.out.println("1. Salvando novo produto...");
        ProdutoDTO novoProduto = new ProdutoDTO(null, "Gibson", "Les Paul Standard", "Corpo em mogno, tampo em maple.", 15000.00, 10, instrumento.getIdInstrumento(), fornecedor.getIdFornecedor());
        novoProduto = produtoDAO.salvar(novoProduto);
        System.out.println("Salvo com sucesso! ID do Produto/Instrumento: " + novoProduto.getIdProduto());

        // 2. BUSCAR POR ID
        System.out.println("\n2. Buscando produto pelo ID: " + novoProduto.getIdProduto());
        ProdutoDTO produtoBuscado = produtoDAO.buscarPorId(novoProduto.getIdProduto()).get();
        System.out.println("Encontrado: " + produtoBuscado.getMarca() + " " + produtoBuscado.getModelo());

        // 3. ATUALIZAR ESTOQUE
        System.out.println("\n3. Atualizando estoque...");
        produtoBuscado.setQuantidadeEstoque(8);
        produtoDAO.atualizar(produtoBuscado);
        ProdutoDTO produtoAtualizado = produtoDAO.buscarPorId(novoProduto.getIdProduto()).get();
        System.out.println("Novo estoque: " + produtoAtualizado.getQuantidadeEstoque());

        // 4. REMOVER (testando o ON DELETE CASCADE)
        System.out.println("\n4. Removendo o produto e suas dependências...");
        produtoDAO.remover(novoProduto.getIdProduto());
        instrumentoDAO.remover(instrumento.getIdInstrumento()); // Remove o instrumento associado
        fornecedorDAO.remover(fornecedor.getIdFornecedor()); // Remove o fornecedor associado

        Optional<ProdutoDTO> produtoRemovido = produtoDAO.buscarPorId(novoProduto.getIdProduto());
        System.out.println("Produto removido com sucesso? " + produtoRemovido.isEmpty());

        // Limpeza
        fornecedorDAO.remover(fornecedor.getIdFornecedor());
        instrumentoDAO.remover(instrumento.getIdInstrumento());
    }

    private static void testarPedidoDAO() throws SQLException {
        System.out.println("\n--- TESTANDO PedidoDAO ---");
        PedidoDAO pedidoDAO = DAOFactory.criarPedidoDAO();
        FornecedorDAO fornecedorDAO = DAOFactory.criarFornecedorDAO();
        ProdutoDAO produtoDAO = DAOFactory.criarProdutoDAO();
        InstrumentoDAO instrumentoDAO = DAOFactory.criarInstrumentoDAO();

        // Pré-requisitos: Criar Fornecedor, Instrumentos e Produtos
        FornecedorDTO fornecedor = fornecedorDAO.salvar(new FornecedorDTO(null, "Yamaha Musical", "99888777000166", "Instrumentos diversos."));
        InstrumentoDTO inst1 = instrumentoDAO.salvar(new InstrumentoDTO(null, "Teclado Arranjador", Categoria.eletronico));
        InstrumentoDTO inst2 = instrumentoDAO.salvar(new InstrumentoDTO(null, "Bateria Acústica", Categoria.percussao));

        ProdutoDTO prod1 = produtoDAO.salvar(new ProdutoDTO(null, "Yamaha", "PSR-E373", "Teclado com 61 teclas sensitivas.", 1800.62, 15, inst1.getIdInstrumento(), fornecedor.getIdFornecedor()));
        ProdutoDTO prod2 = produtoDAO.salvar(new ProdutoDTO(null, "Yamaha", "Stage Custom", "Kit de bateria completo.", 4500.00, 5, inst2.getIdInstrumento(), fornecedor.getIdFornecedor()));


        // 1. MONTAR E SALVAR PEDIDO
        System.out.println("1. Montando e salvando um novo pedido com 2 itens...");
        PedidoDTO novoPedido = new PedidoDTO();
        novoPedido.setDataPedido(LocalDate.now());
        novoPedido.setDataEntrega(LocalDate.now().plusDays(10));
        novoPedido.setIdFornecedor(fornecedor.getIdFornecedor());

        // Adicionando itens ao pedido
        novoPedido.addItem(new ItemPedidoDTO(prod1.getIdProduto(), 2)); // 2 teclados
        novoPedido.addItem(new ItemPedidoDTO(prod2.getIdProduto(), 1)); // 1 bateria

        novoPedido = pedidoDAO.salvar(novoPedido);
        System.out.println("Pedido salvo com sucesso! ID: " + novoPedido.getIdPedido());
        System.out.println("Valor Total Calculado: R$ " + novoPedido.getValorTotal());

        // 2. BUSCAR PEDIDO POR ID
        System.out.println("\n2. Buscando o pedido recém-criado...");
        PedidoDTO pedidoBuscado = pedidoDAO.buscarPorId(novoPedido.getIdPedido()).get();
        System.out.println("Pedido encontrado: ID " + pedidoBuscado.getIdPedido());
        System.out.println("Itens no pedido buscado: " + pedidoBuscado.getItens().size());
        for (ItemPedidoDTO item : pedidoBuscado.getItens()) {
            System.out.println(String.format("  -> Item ID: %d, Produto ID: %d, Qtd: %d, Preço Unit.: R$ %.2f, Total Item: R$ %.2f",
                    item.getIdItemPedido(), item.getIdProduto(), item.getQuantidade(), item.getValorUnitario(), item.getValorTotal()));
        }

        // 3. REMOVER PEDIDO
        System.out.println("\n3. Removendo o pedido...");
        pedidoDAO.remover(novoPedido.getIdPedido());
        Optional<PedidoDTO> pedidoRemovido = pedidoDAO.buscarPorId(novoPedido.getIdPedido());
        System.out.println("Pedido removido com sucesso? " + pedidoRemovido.isEmpty());

        // Limpeza final
        produtoDAO.remover(prod1.getIdProduto());
        produtoDAO.remover(prod2.getIdProduto());
        instrumentoDAO.remover(inst1.getIdInstrumento());
        instrumentoDAO.remover(inst2.getIdInstrumento());
        fornecedorDAO.remover(fornecedor.getIdFornecedor());
    }
}