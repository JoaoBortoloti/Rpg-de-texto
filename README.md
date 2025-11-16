🧙‍♂️ RPG de Texto – Projeto POO em Java
📖 Sobre o Projeto

Este é um RPG de texto em Java desenvolvido com o objetivo de demonstrar, na prática, os principais pilares e recursos da Programação Orientada a Objetos (POO).

O projeto utiliza:

Abstração

Encapsulamento

Herança

Polimorfismo

Composição

Interfaces

Enums

Comparabilidade (Comparable)

Clonagem (clone())

Além disso, o código foi estruturado para facilitar evolução, testes e entendimento da arquitetura.

🎮 Como Jogar
✔️ Pré-requisitos

Java 17 ou superior instalado

Terminal/Prompt de comando

Entrar na pasta raiz do projeto (onde ficam as pastas src e bin)

⚒️ Compilação e Execução
1. Compile o projeto em UTF-8

No terminal, dentro da pasta do projeto, execute:

javac -encoding UTF-8 -d bin -sourcepath src src\sistema\Main.java


Isso irá gerar os arquivos .class dentro da pasta bin/, mantendo a estrutura de pacotes.

2. Execute o jogo
java -cp bin sistema.Main

📂 Estrutura do Projeto
.
├── src/
│   ├── sistema/
│   │   └── Main.java
│   ├── entidades/
│   ├── classes/
│   ├── itens/
│   ├── combate/
│   └── ...
├── bin/           
└── README.md

🧠 Conceitos Aplicados

O projeto aborda de forma prática:

Classes e Objetos

Árvore de herança para personagens (ex: Personagem → Guerreiro/Mago/etc.)

Interfaces para comportamentos (ex: Atacável, Defensável)

Composição para inventário, armas e habilidades

Polimorfismo em ações de combate

Uso de Enums para categorias e efeitos

Implementação de Comparable para ordenação

Clonagem de personagens/itens quando necessário

📌 Objetivo Educacional

Este RPG foi criado para fins de estudo, servindo como base para:

Exercícios de POO

Treinamento de lógica de programação

Exploração de arquitetura orientada a objetos

Evolução para projetos maiores (ex: RPG com interface gráfica ou versão online)
