# Contas

![Android](https://img.shields.io/badge/platform-Android-3DDC84)
![Java](https://img.shields.io/badge/language-Java-17-orange)
![Gradle](https://img.shields.io/badge/Gradle-8.10.2-02303A)
![AGP](https://img.shields.io/badge/AGP-8.8.2-blue)
![Status](https://img.shields.io/badge/build-tested-success)

Aplicativo Android nativo em Java para controle de contas pessoais.

O app permite:

- cadastrar usuario e saldo inicial
- criar contas de entrada e saida
- classificar gastos como necessarios ou desnecessarios
- editar e excluir contas
- selecionar varias contas para exclusao
- gerar PDF com a lista de contas
- listar, abrir e excluir PDFs gerados
- exibir widget com o saldo atual

## Funcionalidades

- [x] Cadastro de usuario
- [x] Atualizacao de saldo do usuario
- [x] Cadastro de contas de entrada
- [x] Cadastro de contas de saida
- [x] Edicao de contas
- [x] Exclusao individual de contas
- [x] Exclusao multipla com selecao por `CheckBox`
- [x] Ordenacao da lista principal
- [x] Geracao de PDF
- [x] Listagem de PDFs gerados
- [x] Abertura de PDF no app externo
- [x] Exclusao de PDF
- [x] Widget com saldo
- [x] Testes unitarios basicos
- [ ] Testes instrumentados/UI
- [ ] Migrations reais do Room

## Stack

- Android Gradle Plugin `8.8.2`
- Gradle `8.10.2`
- Java `17`
- compileSdk `34`
- minSdk `26`
- targetSdk `34`
- Room `2.5.2`
- Lifecycle ViewModel/LiveData `2.6.2`
- Retrofit `2.9.0`
- Material `1.9.0`
- iText PDF `5.5.13.2`
- Lombok `1.18.36`

## Como executar

Na raiz do projeto:

```bash
GRADLE_USER_HOME="$PWD/.gradle-user-home" ./gradlew clean installDebug
```

Para validar com testes antes:

```bash
GRADLE_USER_HOME="$PWD/.gradle-user-home" ./gradlew clean test installDebug
```

Para apenas rodar os testes:

```bash
GRADLE_USER_HOME="$PWD/.gradle-user-home" ./gradlew test
```

## Screenshots

Voce pode adicionar capturas reais aqui depois. Sugestao de estrutura:

```md
![Tela inicial](docs/screenshots/tela-inicial.png)
![Modo de exclusao](docs/screenshots/modo-exclusao.png)
![Tela de usuario](docs/screenshots/tela-usuario.png)
![Lista de PDFs](docs/screenshots/lista-pdfs.png)
```

Sugestao de telas para registrar:

- tela inicial com saldos e lista de contas
- modo de selecao para exclusao
- tela de cadastro de usuario
- tela de nova conta
- tela de PDFs
- widget

## Android Studio

1. Abra a pasta do projeto.
2. Aguarde o Gradle Sync.
3. Confirme Java 17 na IDE.
4. Selecione um emulador ou dispositivo.
5. Rode o módulo `app`.

Se a IDE ficar com cache antigo:

1. `Sync Project with Gradle Files`
2. `Build > Clean Project`
3. `Build > Rebuild Project`
4. `File > Invalidate Caches / Restart`

## Estrutura rapida

- `app/src/main/java/br/com/contas/activities`
  - telas principais

- `app/src/main/java/br/com/contas/activities/telas_conta`
  - telas e fragments de criacao/edicao de contas

- `app/src/main/java/br/com/contas/repository`
  - acesso assíncrono a dados

- `app/src/main/java/br/com/contas/viewmodel`
  - estado das telas principais

- `app/src/main/java/br/com/contas/DAO`
  - consultas Room

- `app/src/main/java/br/com/contas/entities`
  - modelos do dominio

- `app/src/main/java/br/com/contas/utils`
  - formatacao, validacao, ordenacao e PDF

- `app/src/test/java`
  - testes unitarios

## Arquitetura atual

O projeto usa uma arquitetura em camadas simples:

- UI com `Activity` e `Fragment`
- `ViewModel` nas telas principais
- `Repository` para tirar Room da UI thread
- Room para persistencia local
- adapters para renderizacao das listas

Fluxo principal:

1. A tela chama ViewModel ou Repository.
2. O Repository acessa o Room fora da thread principal.
3. O resultado volta para a UI.
4. A tela renderiza lista, saldos e estados visuais.

## Estado atual

- build validado com `./gradlew test`
- conflito de Kotlin antigo resolvido no Gradle
- lista principal com selecao multipla para exclusao
- bug da lista de PDFs corrigido
- documentacao tecnica completa incluida no repositorio

## Lombok

Lombok esta habilitado no projeto, mas o codigo atual ficou com getters e setters explicitos.

Motivo:

- Room teve incompatibilidade com accessors gerados nas entidades
- a IDE pode apontar erro visual quando o suporte ao Lombok nao esta completamente configurado

Entao a decisao final foi:

- manter Lombok configurado no Gradle para uso futuro
- manter as classes atuais explicitas para evitar ruido e instabilidade

## Testes

Testes atuais:

- `ContaTest`
  - valida atualizacao correta de `usuarioId`

- `DateConverterTest`
  - valida conversao de datas

- `DecimalDigitsTest`
  - valida formatacao numerica por idioma

- `UtilsValidaTest`
  - valida regras basicas de formulario

## Documentacao completa

Documentacao tecnica detalhada:

- [DOCUMENTACAO_PROJETO.md](/home/eduardo/Documentos/Aplicativo/Aplicativo_conta/DOCUMENTACAO_PROJETO.md)
