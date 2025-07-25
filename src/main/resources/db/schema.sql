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
                        dataEntrega DATE NULL,
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

-- =================================================================
-- SCRIPT DE POPULAÇÃO INICIAL - SOUL INSTRUMENTS
-- =================================================================

-- 1. Inserindo Fornecedores
-- -----------------------------------------------------------------
INSERT INTO fornecedor (nomefornecedor, cnpj, descricao) VALUES
                                                             ('Fender Musical Instruments', '11222333000144', 'Fornecedor oficial de guitarras e baixos Fender e Squier.'),
                                                             ('Gibson Brands, Inc.', '55666777000188', 'Fornecedor de guitarras Gibson, Epiphone e baixos.'),
                                                             ('Yamaha Musical', '99888777000166', 'Instrumentos musicais diversos, de teclados a baterias.'),
                                                             ('Roland Corporation', '22333444000155', 'Especialista em instrumentos eletrônicos, sintetizadores e baterias eletrônicas.');

select * from fornecedor;

-- 2. Inserindo Instrumentos base
-- -----------------------------------------------------------------
INSERT INTO instrumento (nome, categoria) VALUES
                                              ('Guitarra Elétrica', 'cordas'),
                                              ('Baixo Elétrico', 'cordas'),
                                              ('Violão Acústico', 'cordas'),
                                              ('Bateria Acústica', 'percussao'),
                                              ('Teclado Arranjador', 'eletronico'),
                                              ('Sintetizador', 'eletronico');

select * from instrumento;

-- 3. Inserindo Produtos
-- -----------------------------------------------------------------
-- OBS: Os IDs de instrumento e fornecedor aqui correspondem à ordem em que
-- foram inseridos acima (ex: Fender=1, Gibson=2, Guitarra Elétrica=1, etc.)

-- Produtos da Fender (idFornecedor = 1)
INSERT INTO produto (marca, modelo, descricao, preco, quantidadeestoque, idinstrumento, idfornecedor) VALUES
                                                                                                          ('Fender', 'Player Stratocaster', 'Corpo em amieiro, braço em maple, trio de captadores single-coil.', 4514.09, 10, 1, 1),
                                                                                                          ('Fender', 'American Profess Telecaster', 'O padrão da indústria para guitarras versáteis. Cor Butterscotch Blonde.', 11502.50, 5, 1, 1),
                                                                                                          ('Fender', 'Player Jazz Bass', 'Baixo de 4 cordas, versátil e confortável. Acabamento 3-Color Sunburst.', 4806.01, 8, 2, 1);

-- Produtos da Gibson (idFornecedor = 2)
INSERT INTO produto (marca, modelo, descricao, preco, quantidadeestoque, idinstrumento, idfornecedor) VALUES
                                                                                                          ('Gibson', 'Les Paul Standard 60s', 'Corpo em mogno, tampo em maple, captadores BurstBucker. Cor Iced Tea.', 17000.00, 3, 1, 2),
                                                                                                          ('Gibson', 'SG Standard', 'Corpo leve em mogno, braço fino e acesso fácil às casas superiores. Cor Cherry Red.', 9800.00, 4, 1, 2),
                                                                                                          ('Gibson', 'Hummingbird', 'Violão eletro-acústico icônico com timbre rico e quente.', 22000.00, 2, 3, 2);

-- Produtos da Yamaha (idFornecedor = 3)
INSERT INTO produto (marca, modelo, descricao, preco, quantidadeestoque, idinstrumento, idfornecedor) VALUES
                                                                                                          ('Yamaha', 'PSR-E373', 'Teclado arranjador portátil com 61 teclas sensitivas e vasta biblioteca de sons.', 1800.00, 15, 5, 3),
                                                                                                          ('Yamaha', 'Stage Custom Birch', 'Kit de bateria acústica em madeira Birch. Som focado e profissional. Bumbo de 22".', 5500.00, 6, 4, 3);

-- Produtos da Roland (idFornecedor = 4)
INSERT INTO produto (marca, modelo, descricao, preco, quantidadeestoque, idinstrumento, idfornecedor) VALUES
    ('Roland', 'Juno-DS61', 'Sintetizador de 61 teclas com sons profissionais, performance aprimorada e operação a pilhas.', 5200.00, 7, 6, 4);

-- Fim da Inserção