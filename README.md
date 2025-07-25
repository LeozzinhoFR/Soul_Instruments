\# Soul Instruments - Sistema de Gestão de Estoque e Pedidos



!\[Java](https://img.shields.io/badge/Java-17-blue)

!\[Maven](https://img.shields.io/badge/Build-Maven-red)

!\[Database](https://img.shields.io/badge/Database-PostgreSQL-blue)

!\[UI](https://img.shields.io/badge/UI-Java%20Swing-orange)



Sistema de desktop para gestão de uma loja de instrumentos musicais, desenvolvido como projeto prático para a disciplina de Programação Orientada a Objetos. A aplicação permite o gerenciamento completo de instrumentos, produtos, fornecedores e o ciclo de vida de pedidos de compra.



\## Visão Geral



O Soul Instruments é uma aplicação Java desktop com interface gráfica construída em Swing, seguindo a arquitetura \*\*MVC (Model-View-Controller)\*\*. O objetivo principal do projeto foi aplicar conceitos de OOP, design patterns (como DAO, DTO e Factory), e a integração com banco de dados relacional (PostgreSQL) utilizando JDBC puro para persistência de dados.



\### Screenshots



\*(Dica: Substitua os links abaixo pelos links das suas capturas de tela após enviá-las para o repositório ou outro local)\*



| Tela de Produtos | Tela de Novo Pedido | Tela de Listagem de Pedidos |

| :---: | :---: | :---: |

| !\[Tela de Produtos](https://github.com/user-attachments/assets/93c1d67a-7ebb-4406-be64-cabc906d35c3) | !\[Tela de Novo Pedido](https://github.com/user-attachments/assets/b50d34a3-007f-46f3-bc01-8b3625af2d20) | !\[Tela de Listagem de Pedidos](https://github.com/user-attachments/assets/128d66de-97f1-4425-a71f-1e5781ece08f) |





\## Funcionalidades Principais



O sistema é dividido em quatro módulos principais:



\### 🎸 Gestão de Instrumentos

\- \*\*CRUD completo\*\* para os tipos base de instrumentos (ex: "Guitarra Elétrica", "Bateria Acústica").

\- Cadastro com \*\*Nome\*\* e \*\*Categoria\*\* (Cordas, Sopro, Percussão, Eletrônico).

\- Validação que impede a exclusão de um instrumento se ele estiver sendo utilizado por algum produto.



\### 🚚 Gestão de Fornecedores

\- \*\*CRUD completo\*\* para fornecedores.

\- Cadastro com \*\*Nome Fantasia\*\*, \*\*CNPJ\*\* (com validação de unicidade) e \*\*Descrição\*\*.

\- Funcionalidade de \*\*busca\*\* por nome ou CNPJ.

\- Validação que impede a exclusão de um fornecedor se ele estiver associado a algum produto.



\### 📦 Gestão de Produtos

\- \*\*CRUD completo\*\* para produtos, que são instâncias vendáveis de um instrumento.

\- Cadastro de produto associado a um \*\*Instrumento Base\*\* e um \*\*Fornecedor\*\*.

\- Campos para \*\*Marca\*\*, \*\*Modelo\*\*, \*\*Descrição\*\* e \*\*Preço\*\*.

\- \*\*Controle de Estoque\*\* automatizado, onde o estoque inicial é zero e só é incrementado via recebimento de pedidos.

\- Tabela de listagem com \*\*ordenação clicável\*\* por qualquer coluna.

\- Validação que impede a exclusão de um produto se ele estiver presente em um pedido.



\### 📝 Gestão de Pedidos

\- \*\*Criação de Pedidos de Compra\*\* para um único fornecedor por vez.

\- Adição de \*\*múltiplos produtos\*\* ao mesmo pedido, com especificação de quantidade.

\- \*\*Cálculo automático\*\* do valor total do pedido.

\- \*\*Listagem e visualização\*\* de todos os pedidos realizados, com seus respectivos itens.

\- Funcionalidade de \*\*Confirmar Entrega\*\*, que atualiza o status do pedido para "ENTREGUE", registra a data de entrega e \*\*incrementa o estoque\*\* dos produtos recebidos.



\## Regras de Negócio Implementadas



\- \*\*Integridade Referencial:\*\* O sistema impede a exclusão de entidades "pai" (como Fornecedores e Instrumentos) se elas possuírem entidades "filho" (Produtos) associadas.

\- \*\*Unicidade de Pedido por Fornecedor:\*\* A interface guia o usuário a criar um pedido contendo apenas produtos de um único fornecedor selecionado.

\- \*\*Ciclo de Vida do Pedido:\*\* Todo novo pedido é criado com o status `PENDENTE` e a data de entrega nula. Apenas após a confirmação de entrega o status muda para `ENTREGUE` e a data é preenchida.

\- \*\*Controle de Estoque Rígido:\*\* O estoque de um produto não pode ser editado manualmente. Ele é um reflexo direto dos pedidos de compra cujas entregas foram confirmadas.

\- \*\*Integridade de Edição:\*\* Ao editar um produto que já faz parte de um pedido, o fornecedor associado a ele não pode ser alterado.



\## Tecnologias Utilizadas



\- \*\*Linguagem:\*\* Java 17

\- \*\*Interface Gráfica:\*\* Java Swing

\- \*\*Banco de Dados:\*\* PostgreSQL

\- \*\*Conectividade com BD:\*\* JDBC (Java Database Connectivity)

\- \*\*Build e Dependências:\*\* Apache Maven

\- \*\*Tema da UI (Look and Feel):\*\* \[FlatLaf](https://www.formdev.com/flatlaf/) (para uma aparência moderna e limpa)



\## Arquitetura



O projeto foi estruturado seguindo o padrão \*\*MVC\*\* para garantir a separação de responsabilidades e a manutenibilidade do código.



\- \*\*Model:\*\* Camada responsável pelos dados e a lógica de acesso a eles.

&nbsp; - \*\*DTO (Data Transfer Object):\*\* Classes simples (`ProdutoDTO`, `FornecedorDTO`, etc.) que representam as entidades do sistema.

&nbsp; - \*\*DAO (Data Access Object):\*\* Classes que encapsulam toda a lógica de comunicação com o banco de dados via JDBC (`ProdutoDAOImpl`, etc.).

&nbsp; - \*\*DAOFactory:\*\* Classe que centraliza a criação das instâncias de DAO, gerenciando a conexão com o banco.

\- \*\*View:\*\* A camada de apresentação, responsável pela interface gráfica.

&nbsp; - Composta por `JFrame` (janela principal) e múltiplos `JPanel` (telas), organizados com `CardLayout` para a navegação.

\- \*\*Controller:\*\* A camada que faz a ponte entre a View e o Model.

&nbsp; - Recebe as ações do usuário (cliques de botão), aciona as regras de negócio e chama os DAOs para manipular os dados, atualizando a View com o resultado.



\## Como Executar o Projeto



Siga os passos abaixo para configurar e executar a aplicação localmente.



\### Pré-requisitos

\- \*\*Java JDK 17\*\* ou superior.

\- \*\*Apache Maven\*\* 3.6 ou superior.

\- \*\*PostgreSQL\*\* instalado e rodando.



\### 1. Configuração do Banco de Dados



a. Crie um novo banco de dados no PostgreSQL. Exemplo:

```sql

CREATE DATABASE soulinstruments;

```



b. Execute o script abaixo para criar todas as tabelas necessárias:



<details>

<summary><strong>Clique para ver o Script de Criação de Tabelas (schema.sql)</strong></summary>



```sql

-- Limpa o banco para um novo começo (cuidado em produção!)

TRUNCATE TABLE fornecedor, instrumento, produto, pedido, itempedido RESTART IDENTITY CASCADE;



-- Criação das Tabelas

CREATE TABLE instrumento (

&nbsp;   idinstrumento SERIAL PRIMARY KEY,

&nbsp;   nome VARCHAR(50) NOT NULL UNIQUE,

&nbsp;   categoria VARCHAR(20) NOT NULL

);



CREATE TABLE fornecedor (

&nbsp;   idfornecedor SERIAL PRIMARY KEY,

&nbsp;   nomefornecedor VARCHAR(60) NOT NULL,

&nbsp;   cnpj VARCHAR(20) NOT NULL UNIQUE,

&nbsp;   descricao VARCHAR(255)

);



CREATE TABLE produto (

&nbsp;   idproduto SERIAL PRIMARY KEY,

&nbsp;   marca VARCHAR(30) NOT NULL,

&nbsp;   modelo VARCHAR(30),

&nbsp;   descricao VARCHAR(255),

&nbsp;   preco DECIMAL(10, 2) NOT NULL,

&nbsp;   quantidadeestoque INT NOT NULL,

&nbsp;   idinstrumento INT NOT NULL,

&nbsp;   idfornecedor INT NOT NULL,

&nbsp;   FOREIGN KEY (idinstrumento) REFERENCES instrumento(idinstrumento),

&nbsp;   FOREIGN KEY (idfornecedor) REFERENCES fornecedor(idfornecedor)

);



CREATE TABLE pedido (

&nbsp;   idpedido SERIAL PRIMARY KEY,

&nbsp;   datapedido DATE NOT NULL,

&nbsp;   dataentrega DATE,

&nbsp;   valortotal DECIMAL(10, 2),

&nbsp;   idfornecedor INT NOT NULL,

&nbsp;   status VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',

&nbsp;   FOREIGN KEY (idfornecedor) REFERENCES fornecedor(idfornecedor)

);



CREATE TABLE itempedido (

&nbsp;   iditempedido SERIAL PRIMARY KEY,

&nbsp;   quantidade INT NOT NULL,

&nbsp;   valorunitario DECIMAL(10, 2) NOT NULL,

&nbsp;   valortotal DECIMAL(10, 2) NOT NULL,

&nbsp;   idproduto INT NOT NULL,

&nbsp;   idpedido INT NOT NULL,

&nbsp;   FOREIGN KEY (idproduto) REFERENCES produto(idproduto),

&nbsp;   FOREIGN KEY (idpedido) REFERENCES pedido(idpedido) ON DELETE CASCADE

);

```

</details>



c. (Opcional) Execute o script abaixo para popular o banco com dados iniciais:



<details>

<summary><strong>Clique para ver o Script de População (data.sql)</strong></summary>



```sql

-- Inserindo Fornecedores

INSERT INTO fornecedor (nomefornecedor, cnpj, descricao) VALUES

('Fender Musical Instruments', '11.222.333/0001-44', 'Fornecedor oficial de guitarras e baixos Fender e Squier.'),

('Gibson Brands, Inc.', '55.666.777/0001-88', 'Fornecedor de guitarras Gibson, Epiphone e baixos.'),

('Yamaha Musical', '99.888.777/0001-66', 'Instrumentos musicais diversos, de teclados a baterias.'),

('Roland Corporation', '22.333.444/0001-55', 'Especialista em instrumentos eletrônicos, sintetizadores e baterias eletrônicas.');



-- Inserindo Instrumentos base

INSERT INTO instrumento (nome, categoria) VALUES

('Guitarra Elétrica', 'cordas'),

('Baixo Elétrico', 'cordas'),

('Violão Acústico', 'cordas'),

('Bateria Acústica', 'percussao'),

('Teclado Arranjador', 'eletronico'),

('Sintetizador', 'eletronico');



-- Inserindo Produtos (IDs assumem que as inserções anteriores geraram IDs de 1 em diante)

INSERT INTO produto (marca, modelo, descricao, preco, quantidadeestoque, idinstrumento, idfornecedor) VALUES

('Fender', 'Player Stratocaster', 'Corpo em amieiro, braço em maple, trio de captadores single-coil.', 4500.00, 10, 1, 1),

('Fender', 'American Professional II Telecaster', 'O padrão da indústria para guitarras versáteis. Cor Butterscotch Blonde.', 11500.00, 5, 1, 1),

('Fender', 'Player Jazz Bass', 'Baixo de 4 cordas, versátil e confortável. Acabamento 3-Color Sunburst.', 4800.00, 8, 2, 1),

('Gibson', 'Les Paul Standard 60s', 'Corpo em mogno, tampo em maple, captadores BurstBucker. Cor Iced Tea.', 17000.00, 3, 1, 2),

('Gibson', 'SG Standard', 'Corpo leve em mogno, braço fino e acesso fácil às casas superiores. Cor Cherry Red.', 9800.00, 4, 1, 2),

('Gibson', 'Hummingbird', 'Violão eletro-acústico icônico com timbre rico e quente.', 22000.00, 2, 3, 2),

('Yamaha', 'PSR-E373', 'Teclado arranjador portátil com 61 teclas sensitivas e vasta biblioteca de sons.', 1800.00, 15, 5, 3),

('Yamaha', 'Stage Custom Birch', 'Kit de bateria acústica em madeira Birch. Som focado e profissional. Bumbo de 22".', 5500.00, 6, 4, 3),

('Roland', 'Juno-DS61', 'Sintetizador de 61 teclas com sons profissionais, performance aprimorada e operação a pilhas.', 5200.00, 7, 6, 4);

```

</details>



\### 2. Configuração da Aplicação



a. Clone o repositório:

```bash

git clone \[URL\_DO\_SEU\_REPOSITORIO]

cd Soul\_Instruments\_V1

```



b. Configure a conexão com o banco de dados no arquivo `DAOFactory.java`:

&nbsp;  - Abra: `src/main/java/com/roncolatoandpedro/soulinstruments/dao/DAOFactory.java`

&nbsp;  - Altere as constantes `DB\_URL`, `DB\_USER` e `DB\_PASSWORD` com as suas credenciais.



\### 3. Executando



Você pode executar o projeto diretamente pela sua IDE (como o IntelliJ IDEA, clicando com o botão direito no arquivo `Main.java` e selecionando "Run") ou via Maven no terminal:



```bash

mvn clean compile exec:java

```



---

\*\*Autores:\*\* Leonardo F. e Pedro.

