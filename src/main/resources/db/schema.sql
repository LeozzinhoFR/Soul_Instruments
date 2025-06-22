CREATE TABLE Instrumento (
                             idInstrumento SERIAL PRIMARY KEY,
                             nome VARCHAR(50) NOT NULL,
                             categoria VARCHAR(10) NOT NULL,
                             CHECK (categoria IN ('cordas', 'percussao', 'sopro','eletronico'))
);

CREATE TABLE Fornecedor (
                            idFornecedor SERIAL PRIMARY KEY,
                            nomeFornecedor VARCHAR(60) NOT NULL,
                            cnpj VARCHAR(15) NOT NULL,
                            descricao VARCHAR(255) NOT NULL
);

CREATE TABLE Produto (
                         idProduto SERIAL PRIMARY KEY,
                         marca VARCHAR(30) NOT NULL,
                         modelo VARCHAR(30) NULL,
                         descricao VARCHAR(255) NOT NULL,
                         preco DECIMAL NOT NULL,
                         quantidadeEstoque INT NOT NULL,
                         idInstrumento INT NOT NULL,
                         idFornecedor INT NOT NULL,
                         FOREIGN KEY (idInstrumento) REFERENCES Instrumento(idInstrumento),
                         FOREIGN KEY (idFornecedor) REFERENCES Fornecedor(idFornecedor)
);

CREATE TABLE Pedido (
                        idPedido SERIAL PRIMARY KEY,
                        dataPedido DATE NOT NULL,
                        dataEntrega DATE NOT NULL,
                        valorTotal DECIMAL,
                        idFornecedor INTEGER,
                        FOREIGN KEY (idFornecedor) REFERENCES Fornecedor(idFornecedor)
);

CREATE TABLE ItemPedido (
                            idItemPedido SERIAL PRIMARY KEY,
                            quantidade INTEGER,
                            valorTotal  DECIMAL,
                            valorUnitario DECIMAL NOT NULL,
                            idProduto INTEGER,
                            idPedido INTEGER,
                            FOREIGN KEY (idProduto) REFERENCES Produto(idProduto),
                            FOREIGN KEY (idPedido) REFERENCES Pedido(idPedido)
);

SELECT * FROM Fornecedor;

SELECT * FROM Instrumento;

SELECT * FROM Produto;

SELECT * FROM Pedido;

SELECT * FROM ItemPedido;