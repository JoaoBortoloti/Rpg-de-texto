# Text RPG – Java OOP Showcase

## About

A text-based RPG built in Java to demonstrate core Object-Oriented Programming principles in practice. The project covers abstraction, encapsulation, inheritance, polymorphism, composition, interfaces, enums, `Comparable`, and copy constructors.

## Requirements

- Java 17+
- Maven 3.8+

## Build & Run

```bash
# Compile
mvn compile

# Run the game
mvn exec:java -Dexec.mainClass="sistema.Main"

# Run tests
mvn test
```

## Project Structure

```
src/
├── main/java/
│   ├── interfaces/   Atacavel — combat contract
│   ├── itens/        Item, Inventario, Efeito
│   ├── personagens/  Personagem (abstract), Guerreiro, Mago, Arqueiro, Inimigo
│   └── sistema/      Dado, GameConfig, CombateService, ExploracaoService, SaveSystem, Jogo, Main
└── test/java/        Unit test suites mirroring the main package structure
```

## OOP Concepts Applied

| Concept | Where |
|---|---|
| Abstraction | `Personagem` forces subclasses to implement `usarHabilidadeEspecial()` |
| Encapsulation | Private fields with validated getters/setters |
| Inheritance | `Guerreiro`, `Mago`, `Arqueiro`, `Inimigo` extend `Personagem` |
| Polymorphism | `calcularDano()` behaves differently in each subclass |
| Composition | `Personagem` owns an `Inventario` which owns `Item` objects |
| Interface | `Atacavel` defines the combat contract (`calcularDano`, `receberDano`, `estaVivo`) |
| Enum | `Efeito` types the possible effects an item can carry |
| Comparable | `Item` implements `compareTo` for natural ordering by name and effect |
| Copy constructor | `Personagem`, `Item`, and `Inventario` support deep copying |
| Factory Method | `Inimigo.criarInimigoAleatorio()` scales enemy stats to the player's level |
