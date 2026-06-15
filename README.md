# RPG de Texto – Projeto POO em Java

## Sobre o Projeto

RPG de texto em Java desenvolvido para demonstrar, na prática, os principais pilares da Programação Orientada a Objetos. O projeto cobre abstração, encapsulamento, herança, polimorfismo, composição, interfaces, enums, `Comparable` e construtores de cópia.

## Pré-requisitos

- Java 17+
- Maven 3.8+

## Build e Execução

```bash
# Compilar
mvn compile

# Rodar o jogo
mvn exec:java -Dexec.mainClass="sistema.Main"

# Rodar os testes
mvn test
```

## Estrutura do Projeto

```
src/
├── main/java/
│   ├── interfaces/   Atacavel — contrato de combate
│   ├── itens/        Item, Inventario, Efeito
│   ├── personagens/  Personagem (abstract), Guerreiro, Mago, Arqueiro, Inimigo
│   └── sistema/      Dado, Jogo, Main
└── test/java/        Suites de testes unitários por pacote
```

## Conceitos Aplicados

| Conceito | Onde |
|---|---|
| Abstração | `Personagem` força subclasses a implementar `usarHabilidadeEspecial()` |
| Encapsulamento | Atributos privados com getters/setters validados |
| Herança | `Guerreiro`, `Mago`, `Arqueiro`, `Inimigo` estendem `Personagem` |
| Polimorfismo | `calcularDano()` tem comportamento distinto em cada subclasse |
| Composição | `Personagem` contém `Inventario` que contém `Item` |
| Interface | `Atacavel` define o contrato `calcularDano`, `receberDano`, `estaVivo` |
| Enum | `Efeito` tipifica os efeitos possíveis de um item |
| Comparable | `Item` implementa `compareTo` para ordenação natural |
| Construtor de cópia | `Personagem`, `Item` e `Inventario` suportam cópia profunda |
| Factory Method | `Inimigo.criarInimigoAleatorio()` escala com o nível do jogador |
