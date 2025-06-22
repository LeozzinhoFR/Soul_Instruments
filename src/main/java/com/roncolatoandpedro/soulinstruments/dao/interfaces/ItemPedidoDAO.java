package com.roncolatoandpedro.soulinstruments.dao.interfaces;

import com.roncolatoandpedro.soulinstruments.dto.ItemPedidoDTO;
import com.roncolatoandpedro.soulinstruments.dto.ProdutoDTO; // Para buscar o preço

import java.sql.SQLException;
import java.util.List;
/*
 Esse IMPL é como se fosse o carrinho de supermercado com os itens
 por isso tem uma lista de itemPedido na linha 15
*/
public interface ItemPedidoDAO {
    // O ProdutoDAO será necessário para buscar o preço do produto, por isso referência-lo
    ItemPedidoDTO salvar(ItemPedidoDTO itemPedido) throws SQLException;
    List<ItemPedidoDTO> buscarPorPedidoId(Long idPedido) throws SQLException;
}