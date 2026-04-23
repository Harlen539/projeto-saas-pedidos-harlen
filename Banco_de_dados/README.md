# 🧾 SaaS de Gestão de Pedidos (Multi-tenant)

Projeto em desenvolvimento de um sistema SaaS para gestão de pedidos, produtos e usuários, com arquitetura voltada para aplicações reais de mercado.

---

## 🚧 Status do Projeto

⚠️ **Em andamento**

Atualmente, o projeto está na fase inicial de construção da base de dados.
As próximas etapas incluirão o desenvolvimento do backend com Spring Boot e implementação de autenticação.

---

## 🧠 Objetivo

Construir um sistema multi-tenant onde cada empresa (tenant) possui seus próprios dados isolados, simulando um cenário real de SaaS (Software as a Service).

---

## 🏗️ Tecnologias (até o momento)

* PostgreSQL
* SQL (modelagem relacional)

---

## 🗄️ Estrutura do Banco de Dados

O banco foi modelado para suportar multi-tenancy através da associação de dados à entidade `empresa`.

### Principais tabelas:

* `empresa`
* `usuario`
* `produto`
* `pedido`
* `item_pedido`

---

## 🔗 Relacionamentos

* Uma empresa possui vários usuários
* Produtos pertencem a uma empresa
* Pedidos pertencem a uma empresa
* Itens de pedido relacionam produtos e pedidos

---

## 📊 Características implementadas

* Modelagem relacional normalizada
* Uso de chaves estrangeiras (FK)
* Índices para performance
* Estrutura preparada para multi-tenant (`empresa_id`)

---

## 🚀 Próximas etapas

* [ ] Configuração do backend com Spring Boot
* [ ] Criação das entidades JPA
* [ ] Implementação de API REST
* [ ] Autenticação com JWT
* [ ] Deploy da aplicação

---

## 📌 Observações

Este projeto está sendo desenvolvido com foco em:

* Boas práticas de arquitetura
* Organização de código
* Preparação para ambiente real de produção

---

## 👨‍💻 Autor

Desenvolvido por Harlen
