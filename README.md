# 📦 Order Management

Aplicação **Spring Boot** para gerenciamento de pedidos B2B.  
Permite cadastrar, consultar, atualizar status e cancelar pedidos, além de integrar com RabbitMQ para envio de notificações.

---

## 🚀 Funcionalidades

- Cadastro de pedidos com itens e parceiro associado
- Consulta de pedidos com filtros e paginação
- Atualização do status do pedido (APROVADO, CANCELADO, etc.)
- Cancelamento de pedidos
- Cache para relatórios diários
- Integração com RabbitMQ para envio de notificações de status

---

## ⚙️ Tecnologias

- **Java 21**
- **Spring Boot** (Web, Data JPA, Validation, Cache)
- **PostgreSQL** (armazenamento dos pedidos)
- **RabbitMQ** (mensageria para notificações)
- **Swagger/OpenAPI** (documentação da API)
- **Docker & Docker Compose** (subir todo o ambiente com um comando)

---

## ▶️ Como rodar a aplicação

> **Pré-requisitos:** Docker e Docker Compose instalados.

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/GambitEvans/order-management.git
   cd order-management
   
2. **Docker**
- **Se estiver usando o windows, precisa abrir o docker antes de rodar o seguinte comando:**
- **O comando a seguir deve ser rodado a primeira vez, da segunda vez em diante (não havendo alteração do docker-compose ou do dockerfile) não precisa do "--build"**
    ```bash
   docker-compose up -d --build

## 🧰 Teste de carga com JMeter

Após a aplicação estar rodando via Docker Compose, é possível executar um teste de carga básico com o **Apache JMeter**.

### 📌 Passos para executar:

1. Certifique-se de que a aplicação está acessível em:  
   [http://localhost:8080](http://localhost:8080)

2. Execute o comando abaixo a partir da raiz do projeto:
    ```bash
    docker run --rm ^
      --network=order-management_order-net ^
      -v "%cd%\\tests:/tests" ^
      justb4/jmeter:latest ^
      -n -t /tests/order-management-test-plan.jmx ^
      -JBASE_URL=http://order-management:8080 ^
      -l /tests/results.jtl ^
      -j /tests/jmeter.log
   
Em resumo esse comando usa o plano de tests q tá na pasta tests (order-management-test-plan.jmx), executa, e gera uns logs no arquivo jmeter.log e o resultado dos testes apareceram num arquivo result.jtl.

> ⚠️ **Observação:**  
> 💡 Se estiver usando Linux ou macOS, use / ao invés de \ nos caminhos e \ para quebras de linha.
> 
> Por limitação de tempo, não foi possível desenvolver um conjunto de testes de carga mais robusto com múltiplos cenários e simulações avançadas.

## 🧪 Como rodar os testes unitários

> Requisitos: JDK 21 e Maven (ou use o wrapper incluso)

1. Para executar os testes , utilize o seguinte comando na raiz do projeto:

    ```bash
    ./gradlew test
> ⚠️ **Observação:**
> Também por limitação de tempo método criado para exemplificar um cenário onde seria viável usar cache(OrderService::getLastDailyReport) não foi 
> testado pois estava dando um conflito entre a condição passada por SpEL no repository OrderRepository e as configurações de teste.