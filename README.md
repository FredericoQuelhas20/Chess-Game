# Chess-Game

## Descrição do Projeto
Este projeto tem como objetivo implementar um jogo de xadrez completo em Java, desenvolvido no âmbito da unidade curricular de Programação Avançada. O sistema inclui as regras completas do xadrez, interface de utilizador, e gestão de partidas.

## Equipa de Desenvolvimento
- Frederico Quelhas
- Sebastian Gon
- Ivanilson

## Tecnologias Utilizadas
- **Java** (versão 21 ou superior)
- **IntelliJ IDEA** como IDE
- **GitHub** para controle de versão e colaboração

## Funcionalidades
- Implementação completa das regras do jogo de xadrez
- Sistema de movimentação de peças (Peão, Torre, Cavalo, Bispo, Rainha, Rei)
- Validação de movimentos legais
- Deteção de xeque e xeque-mate
- Interface de utilizador para visualização do tabuleiro
- Gestão de turnos entre jogadores
- Sistema de captura de peças

## Instalação

1. Clone o repositório do GitHub:
```bash
git clone https://github.com/FredericoQuelhas20/Chess-Game.git
```

2. Certifique-se de que o Java está instalado e configurado:
```bash
java -version
```

3. Abra o projeto no IntelliJ IDEA.

4. Execute a aplicação através da classe principal.

## Estrutura do Projeto
```
/src          - Código-fonte do projeto (pt/isec/pa/chess)
/test         - Testes unitários e de integração
/reports      - Relatórios e documentação do projeto
/.idea        - Configurações do IntelliJ IDEA
/README.md    - Documentação do projeto
```

## Como Utilizar

1. Execute a aplicação principal
2. Inicie um novo jogo de xadrez
3. Alterne entre jogadores (Brancas e Pretas)
4. Selecione uma peça e escolha um movimento válido
5. Continue a jogar até alcançar xeque-mate ou empate

## Compilação e Execução

### Através do IntelliJ IDEA:
1. Abra o projeto (`PAChess.iml`)
2. Localize a classe principal em `/src/pt/isec/pa/chess/`
3. Execute a aplicação (Run → Run 'Main')

### Através da linha de comandos:
```bash
# Compilar
javac -d out src/pt/isec/pa/chess/**/*.java

# Executar
java -cp out pt.isec.pa.chess.Main
```

## Testes

Os testes estão localizados na pasta `/test`. Para executar:

1. No IntelliJ IDEA: clique com o botão direito na pasta `test` → Run 'All Tests'
2. Os relatórios de testes podem ser consultados na pasta `/reports`

## Licença

Este projeto está licenciado sob a Licença MIT - veja o ficheiro LICENSE para mais detalhes.

## Contacto

Para questões ou colaboração:
- **Frederico Quelhas** - [@FredericoQuelhas20](https://github.com/FredericoQuelhas20)

---

**Nota:** Projeto académico desenvolvido para a unidade curricular de Programação Avançada.
