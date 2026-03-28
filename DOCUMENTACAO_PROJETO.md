# Documentacao Tecnica do Projeto Contas

## 1. Visao geral

Este projeto e um aplicativo Android nativo escrito majoritariamente em Java para controle de contas pessoais. O app permite:

- cadastrar um usuario com saldo inicial
- cadastrar contas de entrada e saida
- classificar gastos como `NECESSARIO` ou `DESNECESSARIO`
- listar, editar e excluir contas
- excluir varias contas em lote
- gerar PDF com o resumo das contas
- listar, abrir e excluir os PDFs gerados
- exibir um widget com o saldo atual

O app usa Room para persistencia local, ViewModel + LiveData nas telas principais e um repositorio para executar acesso ao banco fora da UI thread.

## 2. Stack e versoes

### 2.1 Build e SDK

- Android Gradle Plugin: `8.8.2`
- Gradle Wrapper: `8.10.2`
- Java sourceCompatibility: `17`
- Java targetCompatibility: `17`
- compileSdk: `34`
- targetSdk: `34`
- minSdk: `26`
- applicationId: `br.com.contas`
- versionCode: `1`
- versionName: `1.0`

### 2.2 Dependencias principais

- Retrofit: `2.9.0`
- Retrofit Gson Converter: `2.9.0`
- AndroidX ViewPager2: `1.1.0`
- AndroidX Core: `1.9.0`
- AndroidX AppCompat: `1.6.1`
- Material Components: `1.9.0`
- Lifecycle LiveData: `2.6.2`
- Lifecycle ViewModel: `2.6.2`
- ConstraintLayout: `2.1.4`
- Room Runtime: `2.5.2`
- Room Compiler: `2.5.2`
- Gson: `2.8.8`
- iText PDF: `5.5.13.2`
- Lombok: `1.18.36`
- JUnit 4: `4.13.2`
- AndroidX Test JUnit: `1.1.5`
- Espresso Core: `3.5.1`

### 2.3 Configuracoes Gradle relevantes

- `android.useAndroidX=true`
- `android.nonTransitiveRClass=true`
- `android.suppressUnsupportedCompileSdk=34`
- exclusao explicita de:
  - `org.jetbrains.kotlin:kotlin-stdlib-jdk7`
  - `org.jetbrains.kotlin:kotlin-stdlib-jdk8`

Essas exclusoes evitam conflito de classes duplicadas do Kotlin em tempo de build.

### 2.4 Uso de Lombok

Lombok foi adicionado para reduzir boilerplate de getters e setters nas classes de modelo simples.

Configuracao usada no Gradle:

- `compileOnly 'org.projectlombok:lombok:1.18.36'`
- `annotationProcessor 'org.projectlombok:lombok:1.18.36'`
- `testCompileOnly 'org.projectlombok:lombok:1.18.36'`
- `testAnnotationProcessor 'org.projectlombok:lombok:1.18.36'`

Classes que usaram Lombok durante a tentativa de simplificacao:

- `DashboardData.java`
- `Cotacao.java`

Observacao:

- Lombok nao foi aplicado indiscriminadamente no projeto inteiro
- ele foi mantido configurado no Gradle, mas nao esta em uso nas classes atuais
- ele nao foi mantido nas entidades Room `Conta` e `Usuario`, porque o annotation processor do Room neste projeto nao reconheceu corretamente os accessors gerados em tempo de compilacao
- ele tambem foi removido de `DashboardData` e `Cotacao` para evitar erro visual na IDE quando o suporte ao Lombok nao esta totalmente configurado
- por isso, as classes do projeto permanecem com getters e setters explicitos

## 3. Estrutura de modulos

### 3.1 Modulos

- modulo raiz: configuracao de plugins e repositorios
- modulo `app`: aplicativo Android principal

### 3.2 Arquivos de configuracao

- `settings.gradle`: define o nome do projeto e inclui `:app`
- `build.gradle`: define plugins Android principais
- `app/build.gradle`: define SDKs, Java 17, viewBinding e dependencias
- `gradle.properties`: propriedades globais do Gradle
- `gradle/wrapper/gradle-wrapper.properties`: versao fixa do wrapper

## 4. Estrutura de pastas

### 4.1 `app/src/main/java/br/com/contas`

Pasta raiz do codigo-fonte Java do aplicativo.

#### `DAO/`

Contem as interfaces Room DAO.

- `ContaDao.java`
  - faz insert, update, delete e consultas de contas
  - possui queries para:
    - listar contas por usuario
    - ordenar por data, nome e valor
    - buscar conta por id
    - somar gastos necessarios
    - somar gastos desnecessarios
    - apagar todas as contas

- `UsuarioDao.java`
  - faz insert, update, delete e consultas do usuario
  - possui queries para:
    - buscar o primeiro usuario
    - listar usuarios
    - buscar usuario por nome
    - apagar todos os usuarios

#### `activities/`

Contem as Activities principais do app.

- `ActivityTelaIncialListaConta.java`
  - tela principal
  - lista contas, exibe saldos, abre outras telas, edita, exclui e controla selecao multipla

- `ActivityTelaUsuario.java`
  - tela de cadastro ou atualizacao do usuario

- `ActivityTelaSalvarListaDeContaNoCelular.java`
  - tela que gera PDF, mostra local salvo e permite apagar todos os dados

- `ActivityTelaListaDocumentoPdf.java`
  - lista os PDFs gerados, abre PDF e exclui PDF

- `ActivityTelaSobreConta.java`
  - tela de configuracoes de ordenacao e tamanho visual da lista

- `ContasWidget.java`
  - widget Android com saldo resumido

- `MyApplication.java`
  - classe `Application`
  - observa ciclo de vida das Activities para atualizar o widget

#### `activities/telas_conta/`

Contem as telas de criacao e edicao de contas.

- `ActivityTelaNovaConta.java`
  - host com `ViewPager2` para as abas de entrada e saida
  - tambem recebe uma conta para edicao

- `ActivityTelaNovaContaFormatoLista.java`
  - host da versao rapida de cadastro em lista

- `FragmentTelaContaAdicao.java`
  - formulario completo para contas de entrada

- `FragmentTelaContaSubtracao.java`
  - formulario completo para contas de saida
  - possui toggle de `NECESSARIO` e `DESNECESSARIO`

- `FragmentTelaContaFormatoListaAdicao.java`
  - formulario simplificado para adicionar entrada rapidamente

- `FragmentTelaContaFormatoListaSubtracao.java`
  - formulario simplificado para adicionar saida rapidamente

- `ViewPagerAdapterConta.java`
  - adapter do pager da tela completa de contas

- `ViewPagerAdapterListaConta.java`
  - adapter do pager da tela simplificada

#### `adapter/`

Adapters das listas.

- `ContaAdapter.java`
  - renderiza a lista principal de contas
  - mostra `CheckBox` apenas no modo de selecao
  - pinta a linha conforme o tipo da conta

- `ListPdfAdapter.java`
  - renderiza a lista de PDFs
  - recebe `List<File>`
  - extrai titulo e data do nome do arquivo
  - faz fallback quando o nome nao segue o padrao esperado

#### `api/`

Estruturas para integracao HTTP.

- `ApiClient.java`
  - hoje esta comentado e nao esta em uso

- `ApiService.java`
  - define endpoint da cotacao

- `Cotacao.java`
  - modelo de resposta da API

- `InformacaoMoeda.java`
  - encapsula valores e formatacao da cotacao

Essa parte esta praticamente inativa no estado atual do aplicativo.

#### `custom/`

- `CustomTextView.java`
  - sobrescreve `setText`
  - tenta formatar automaticamente valores numericos usando `DecimalDigits`

#### `entities/`

Modelos principais do dominio.

- `Conta.java`
  - entidade Room
  - representa uma conta individual

- `Usuario.java`
  - entidade Room
  - representa o usuario e o saldo agregado

- `ContaTipo.java`
  - centraliza constantes de tipo:
    - `ENTRADA`
    - `SAIDA`

- `NecessidadeGasto.java`
  - centraliza constantes de classificacao:
    - `NECESSARIO`
    - `DESNECESSARIO`
    - `ADICAO`

#### `persistence/`

- `UsuarioDatabase.java`
  - banco Room singleton
  - registra as entidades `Usuario` e `Conta`
  - expone `usuarioDao()` e `contaDao()`

#### `persistence/converters/`

- `DateConverter.java`
  - `TypeConverter` do Room para `Date <-> String`
  - tambem tem utilitarios de conversao manual para a camada de UI

#### `repository/`

- `ContasRepository.java`
  - camada central de acesso a dados
  - executa leitura e escrita em `ExecutorService`
  - retorna resultados para a main thread via `Handler`
  - encapsula transacoes para manter conta e saldo coerentes

- `DashboardData.java`
  - objeto agregado para a tela inicial
  - agrupa:
    - usuario
    - lista de contas
    - saldo necessario
    - saldo desnecessario

#### `utils/`

- `DecimalDigits.java`
  - formatacao numerica por idioma

- `Ordenar.java`
  - estado global da opcao de ordenacao e tamanho visual da lista

- `PdfGenerator.java`
  - gera PDF com tabela geral e secoes por tipo de gasto

- `UtilsDateMaskWatcher.java`
  - mascara de data nos campos da UI

- `UtilsGUI.java`
  - utilitario visual legado do projeto

- `UtilsValida.java`
  - validacao simples de nome e valor

#### `viewmodel/`

- `TelaInicialViewModel.java`
  - ViewModel da tela inicial

- `UsuarioViewModel.java`
  - ViewModel da tela de usuario

### 4.2 `app/src/main/res`

Recursos visuais e de interface.

- `layout/`: XMLs das telas, fragments, rows de lista, dialogs, toolbar e widget
- `menu/`: menus da toolbar e menus contextuais
- `drawable/`: backgrounds, botoes, imagens e seletores
- `font/`: fontes customizadas
- `values/`: strings, cores, temas, estilos, dimensoes
- `xml/`: configuracoes auxiliares, `FileProvider`, backup e widget
- `mipmap-*/`: icones do app

### 4.3 `app/src/test/java`

Testes unitarios JUnit do projeto.

### 4.4 `app/src/androidTest/java`

Espaco para testes instrumentados Android. As dependencias estao configuradas, mas hoje nao ha testes de interface implementados.

## 5. Arquitetura atual

## 5.1 Padrao geral

O projeto esta em um estado hibrido:

- UI em `Activity` e `Fragment`
- ViewModel nas telas principais
- Repository para acesso a dados
- Room para persistencia
- Adapters para listas
- Utils para formatacao, validacao e PDF

Fluxo principal:

1. A tela chama um metodo do ViewModel ou do Repository.
2. O Repository executa a operacao no banco fora da thread principal.
3. O resultado volta para a main thread.
4. O ViewModel publica no `LiveData`.
5. A tela observa o `LiveData` e renderiza a interface.

## 5.2 Pontos fortes da arquitetura atual

- acesso ao Room saiu da UI thread nas telas principais
- existe uma camada de repositorio central para regras de persistencia
- a tela inicial usa `ViewModel + LiveData`
- a tela de usuario usa `ViewModel + LiveData`
- a lista de PDFs agora usa uma unica fonte de verdade
- strings magicas principais de tipo e necessidade foram centralizadas em constantes
- Lombok esta configurado no projeto para uso futuro, mas o codigo atual ficou explicitamente declarado para evitar conflitos e erros visuais

## 5.3 Dividas tecnicas ainda existentes

- alguns fragments ainda chamam o `Repository` diretamente em vez de passar por ViewModel proprio
- `Ordenar` usa estado global estatico
- `fallbackToDestructiveMigration()` ainda existe no banco
- o widget ainda faz leitura de banco por conta propria
- a camada `api/` esta incompleta e parcialmente comentada
- ainda nao ha testes instrumentados/UI

## 6. Fluxo de funcionamento do app

## 6.1 Inicializacao

- `ActivityTelaIncialListaConta` e a `LAUNCHER Activity`
- `MyApplication` registra callbacks de ciclo de vida
- o widget pode ser atualizado no inicio e pausa das telas

## 6.2 Fluxo da tela principal

`ActivityTelaIncialListaConta`:

- cria toolbar
- inicializa componentes
- monta `TelaInicialViewModel`
- observa `dashboardLiveData`
- em `onResume`, chama `loadDashboard()`

`TelaInicialViewModel.loadDashboard()`:

- chama `repository.loadDashboard(Ordenar.opcaoOrdenacao, ...)`

`ContasRepository.loadDashboard()`:

- busca usuario atual
- busca lista ordenada de contas
- calcula saldo necessario e desnecessario
- devolve um `DashboardData`

`renderizarDashboard()`:

- atualiza referencia de usuario
- recria o `ContaAdapter`
- atualiza os saldos de tela
- atualiza titulo da toolbar conforme modo de selecao

## 6.3 Fluxo de selecao e exclusao multipla

Na tela inicial:

- `entrarModoSelecao()`
  - ativa o modo de selecao
  - limpa itens selecionados
  - re-renderiza a lista com `CheckBox`

- `alternarSelecao(int position)`
  - adiciona ou remove a posicao no `Set<Integer> posicaoSelecionada`

- `selecionarTodos()`
  - marca tudo ou limpa a selecao completa

- `excluirSelecionadas()`
  - valida se ha itens marcados
  - se houver um item, usa `deletarConta`
  - se houver varios, usa `deletarMaisDeUmaConta`

- `deletarConta(int posicao)`
  - confirma exclusao
  - ajusta saldo conforme tipo da conta
  - usa `TelaInicialViewModel.deleteConta`

- `deletarMaisDeUmaConta(List<Conta>)`
  - soma entradas e saidas separadamente
  - pergunta se saidas devem ou nao retornar ao saldo
  - usa `TelaInicialViewModel.deleteContas`

## 6.4 Fluxo de cadastro de usuario

`ActivityTelaUsuario`:

- `configurarViewModel()`
  - observa `usuarioLiveData`

- `onResume()`
  - chama `usuarioViewModel.loadUsuario()`

- `renderizarUsuario(Usuario usuario)`
  - se existir usuario, preenche campos e muda o botao para `Atualizar`
  - se nao existir, limpa campos e deixa o botao como `Salvar`

- `salvarUsuario(View view)`
  - valida nome
  - formata saldo
  - chama `usuarioViewModel.salvarUsuario(...)`

`UsuarioViewModel.salvarUsuario(...)`:

- delega para `ContasRepository.saveUsuario(...)`

`ContasRepository.saveUsuario(...)`:

- se ja existir usuario, atualiza
- se nao existir, insere novo

## 6.5 Fluxo de cadastro e edicao de contas

### Tela completa

`ActivityTelaNovaConta`:

- hospeda `ViewPager2`
- abre os fragments de entrada e saida
- repassa conta para edicao quando existe

`FragmentTelaContaAdicao`:

- `salvarNovaContaAdicao()`
  - cria conta de entrada
  - define `tipo = ENTRADA`
  - define `necessidadeGasto = ADICAO`
  - insere e soma valor ao saldo

- `salvarEdicaoConta()`
  - busca conta atual no banco
  - atualiza nome, valor, data e usuario
  - recalcula delta do saldo
  - salva

`FragmentTelaContaSubtracao`:

- `botaoNecessarioDesnecessario()`
  - controla o estado do toggle

- `salvarNovaContaSubtracao()`
  - cria conta de saida
  - grava `NECESSARIO` ou `DESNECESSARIO`
  - subtrai valor do saldo

- `salvarEdicaoConta()`
  - atualiza nome, valor, data e classificacao
  - recalcula delta do saldo

### Tela simplificada

`ActivityTelaNovaContaFormatoLista`:

- hospeda `ViewPager2` simplificado

`FragmentTelaContaFormatoListaAdicao`:

- cria entradas de forma rapida
- limpa os campos apos salvar

`FragmentTelaContaFormatoListaSubtracao`:

- cria saidas de forma rapida
- limpa os campos apos salvar

## 6.6 Fluxo de geracao de PDF

`ActivityTelaSalvarListaDeContaNoCelular`:

- `criarPdf(View view)`
  - busca usuario
  - busca contas ordenadas do usuario atual
  - chama `gerarPdf(...)`

- `gerarPdf(...)`
  - instancia `PdfGenerator`
  - gera o arquivo
  - mostra mensagem de sucesso ou erro
  - mostra caminho salvo em `textViewLocalSalvoPdf`

`PdfGenerator`:

- cria o arquivo em `getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)`
- gera:
  - primeira pagina com todas as contas
  - segunda pagina com secoes de gastos desnecessarios e necessarios
- usa `ContaTipo` e `NecessidadeGasto` para montar totais

## 6.7 Fluxo da lista de PDFs

`ActivityTelaListaDocumentoPdf`:

- `atualizarListaDePdfs()`
  - carrega arquivos PDF do diretorio do app
  - ordena por `lastModified` desc

- `abrirDocumentoComUmClickNaLista()`
  - abre o `File` da mesma lista que esta sendo exibida

- `confirmarExclusaoPdf(int posicao)`
  - mostra dialogo e deleta o `File` correto

`ListPdfAdapter`:

- usa regex para interpretar nomes no formato:
  - `titulo_dd-MM-yyyy-HH:mm:ss.pdf`
- se o nome nao seguir esse padrao:
  - titulo = nome do arquivo sem extensao
  - data = string vazia

## 6.8 Fluxo do widget

`ContasWidget`:

- `onUpdate()`
  - atualiza cada widget ativo

- `updateAppWidget(...)`
  - busca saldo
  - formata valor
  - escreve em `RemoteViews`

- `abrirAplicativoAoClicarNoWidget(...)`
  - cria `PendingIntent` para abrir a tela inicial

`MyApplication`:

- chama `updateWidgets()` no `onActivityStarted` e `onActivityPaused`

## 7. Explicacao dos arquivos principais e metodos

## 7.1 `ContasRepository.java`

Classe central da camada de dados.

Metodos principais:

- `loadDashboard(String ordenacao, DataCallback<DashboardData> callback)`
  - carrega usuario, lista de contas e totais agregados

- `getUsuario(DataCallback<Usuario> callback)`
  - busca o usuario atual

- `getContaById(Long contaId, DataCallback<Conta> callback)`
  - busca uma conta especifica

- `saveUsuario(String nome, Double saldo, boolean editar, SaveUsuarioCallback callback)`
  - insere ou atualiza usuario
  - hoje o parametro `editar` nao decide a regra sozinho; a existencia real no banco tambem e verificada

- `insertConta(Conta conta, double saldoDelta, ActionCallback callback)`
  - insere conta e atualiza saldo na mesma transacao

- `updateConta(Conta conta, double saldoDelta, ActionCallback callback)`
  - atualiza conta e atualiza saldo na mesma transacao

- `deleteConta(Conta conta, double saldoDelta, ActionCallback callback)`
  - remove conta e ajusta saldo

- `deleteContas(List<Conta> contas, double saldoDelta, ActionCallback callback)`
  - remove varias contas e ajusta saldo total

- `deleteAllData(ActionCallback callback)`
  - apaga contas e usuario

- `getOrderedContasForUsuarioAtual(String ordenacao, DataCallback<List<Conta>> callback)`
  - carrega lista ordenada do usuario atual

- `updateSaldoInterno(double saldoDelta)`
  - regra interna para atualizar saldo agregado do usuario

## 7.2 `TelaInicialViewModel.java`

- `getDashboardLiveData()`
  - expoe os dados observaveis da tela inicial

- `loadDashboard()`
  - carrega o `DashboardData`

- `deleteConta(...)`
  - remove uma conta e recarrega dashboard

- `deleteContas(...)`
  - remove varias contas e recarrega dashboard

## 7.3 `UsuarioViewModel.java`

- `getUsuarioLiveData()`
  - expoe o usuario observavel

- `loadUsuario()`
  - carrega usuario atual

- `salvarUsuario(...)`
  - delega a persistencia ao repositorio

## 7.4 `ActivityTelaIncialListaConta.java`

Metodos principais:

- `configurarViewModel()`
  - conecta a Activity ao `TelaInicialViewModel`

- `renderizarDashboard(DashboardData dashboardData)`
  - atualiza lista e saldos

- `entrarModoSelecao()`
  - ativa selecao multipla

- `sairModoSelecao()`
  - desativa selecao multipla

- `excluirSelecionadas()`
  - executa exclusao em lote

- `selecionarTodos()`
  - marca todas as contas ou limpa a selecao

- `atualizarTituloToolbar()`
  - alterna entre `Contas`, `Selecione as contas` e `N selecionadas`

- `marcarLinhaSelecionada()`
  - controla clique simples e clique longo da lista

- `alternarSelecao(int position)`
  - marca ou desmarca um item

- `deletarConta(int posicao)`
  - fluxo de exclusao de um item

- `deletarMaisDeUmaConta(List<Conta> listaConta)`
  - fluxo de exclusao de varios itens

- `showCustomDialog(int position)`
  - dialogo com opcoes de editar ou deletar

## 7.5 `ActivityTelaUsuario.java`

Metodos principais:

- `configurarViewModel()`
- `renderizarUsuario(Usuario usuario)`
- `iniciaEditTextValorConta()`
  - aplica mascara monetaria

- `salvarUsuario(View view)`
  - valida e persiste o usuario

- `getNumeroParaString()`
  - converte texto monetario em string parseavel para `Double`

## 7.6 `ActivityTelaSalvarListaDeContaNoCelular.java`

Metodos principais:

- `deletarTodasContas(View view)`
  - confirma e apaga todos os dados do banco

- `criarPdf(View view)`
  - prepara usuario e lista para geracao

- `gerarPdf(View view, Usuario usuario, List<Conta> contas)`
  - chama `PdfGenerator`

- `abrirTelaDoc(View view)`
  - abre a tela de documentos PDF

## 7.7 `ActivityTelaListaDocumentoPdf.java`

Metodos principais:

- `atualizarListaDePdfs()`
- `listarPdfs()`
- `abrirPdf(File file)`
- `deletarArquivo(Context context, File file)`
- `confirmarExclusaoPdf(int posicao)`

Ponto importante:

- a Activity usa uma unica lista `listaDeArquivos` para renderizar, abrir e excluir
- isso corrigiu o bug de clicar em um PDF e abrir outro

## 7.8 `ContaAdapter.java`

Metodos principais:

- `getView(...)`
  - monta a linha da lista principal
  - pinta entrada e saida com backgrounds diferentes
  - mostra `CheckBox` so em modo de selecao

- `defineTamanhoDoLayoutDaLinhaNaLista(View convertView)`
  - ajusta a altura da row conforme configuracao salva em `Ordenar`

## 7.9 `ListPdfAdapter.java`

Metodos principais:

- `getView(...)`
  - renderiza linha de PDF

- `retornarTituloDocumento(String fileName)`
  - extrai titulo do nome do arquivo

- `retornarDataDocumento(String fileName)`
  - extrai data do nome do arquivo

- `removerExtensaoPdf(String fileName)`
  - fallback para nomes fora do padrao

## 7.10 `Conta.java`

Metodos principais:

- getters e setters da entidade
- `setUsuarioId(Long usuario)`
  - permanece explicito
  - o bug antigo de nao atualizar corretamente o campo foi corrigido

## 7.10.1 `Usuario.java`

Metodos principais:

- getters e setters da entidade
  - permanecem explicitos por compatibilidade com Room

## 7.10.2 `DashboardData.java`

Metodos principais:

- getters agregados do dashboard
  - mantidos explicitamente

## 7.10.3 `Cotacao.java`

Metodos principais:

- getter de `eurToBrl`
  - mantido explicitamente

## 7.11 `DateConverter.java`

Metodos principais:

- `fromString(String value)`
  - usado como `TypeConverter`

- `toString(Date date)`
  - usado como `TypeConverter`

- `stringToDate(String dateString)`
  - utilitario manual para a UI

- `dateToString(Date date)`
  - utilitario manual para a UI

## 7.12 `DecimalDigits.java`

Metodos principais:

- `formatarNumero(Double numero)`
  - formata valor monetario conforme idioma

- `formatPattern(String idioma)`
  - troca configuracao global de idioma/mascara

## 7.13 `UtilsValida.java`

- `validaCampoPreenchido(String nome, Double valor)`
  - retorna `true` apenas quando:
    - nome nao e nulo
    - nome nao e vazio
    - nome nao e so espacos
    - valor nao e nulo
    - valor e maior que zero

## 8. Banco de dados e persistencia

## 8.1 Banco Room

Nome do banco:

- `usuarios.db`

Entidades:

- `Usuario`
- `Conta`

Versao atual:

- `4`

Observacao importante:

- o banco usa `fallbackToDestructiveMigration()`
- isso significa que, se a estrutura mudar sem migration explicita, os dados podem ser apagados

## 8.2 Regras de saldo

O saldo final do usuario e mantido de forma agregada em `Usuario.saldo`.

Regras aplicadas no repositorio:

- inserir entrada:
  - saldo aumenta

- inserir saida:
  - saldo diminui

- editar entrada:
  - aplica apenas o delta entre valor novo e antigo

- editar saida:
  - aplica o delta inverso

- excluir entrada:
  - saldo pode reduzir

- excluir saida:
  - usuario pode escolher se o valor volta ou nao para o saldo

## 9. Menus e comportamento de UI

### 9.1 Menu principal da tela inicial

Arquivo:

- `app/src/main/res/menu/principal_opcoes.xml`

Comportamento atual:

- `Selecionar para excluir`
  - aparece apenas se houver contas

- `Selecionar todos`
  - aparece apenas no modo de selecao
  - alterna dinamicamente para `Limpar selecao`

- `Excluir selecionadas`
  - aparece apenas no modo de selecao

- `Cancelar selecao`
  - aparece apenas no modo de selecao

### 9.2 Lista principal de contas

Arquivo da row:

- `app/src/main/res/layout/activity_linha_lista_conta.xml`

Comportamento:

- `CheckBox` aparece apenas no modo de exclusao
- toque na linha seleciona ou desseleciona
- FAB `+` desaparece enquanto o modo de exclusao esta ativo

## 10. Scripts para executar o projeto

Todos os comandos abaixo devem ser executados na raiz do projeto:

`/home/eduardo/Documentos/Aplicativo/Aplicativo_conta`

### 10.1 Rodar testes unitarios

```bash
GRADLE_USER_HOME="$PWD/.gradle-user-home" ./gradlew test
```

### 10.2 Limpar e instalar debug no aparelho/emulador

```bash
GRADLE_USER_HOME="$PWD/.gradle-user-home" ./gradlew clean installDebug
```

### 10.3 Validacao mais completa antes de instalar

```bash
GRADLE_USER_HOME="$PWD/.gradle-user-home" ./gradlew clean build installDebug
```

### 10.4 Apenas compilar o projeto

```bash
GRADLE_USER_HOME="$PWD/.gradle-user-home" ./gradlew build
```

### 10.5 Parar daemons do Gradle

Use apenas se o ambiente estiver com cache estranho ou travado:

```bash
GRADLE_USER_HOME="$PWD/.gradle-user-home" ./gradlew --stop
```

### 10.6 Sequencia recomendada para aplicar mudancas locais

```bash
GRADLE_USER_HOME="$PWD/.gradle-user-home" ./gradlew clean test installDebug
```

## 11. Como abrir e rodar pelo Android Studio

1. Abrir a pasta raiz do projeto.
2. Aguardar o `Gradle Sync`.
3. Verificar se o JDK configurado da IDE esta em Java 17.
4. Selecionar um emulador ou dispositivo fisico.
5. Rodar a configuracao `app`.

Se a IDE nao reconhecer Lombok corretamente:

1. confirmar que o processamento de anotacoes esta habilitado
2. fazer `Sync Project with Gradle Files`
3. reinstalar ou habilitar o plugin Lombok na IDE, se necessario

Observacao importante:

- mesmo com Lombok habilitado no projeto, as classes atuais continuam com accessors manuais
- isso e intencional para evitar erro de compilacao do Room e erro visual na IDE

Se aparecer cache antigo:

1. `Sync Project with Gradle Files`
2. `Build > Clean Project`
3. `Build > Rebuild Project`
4. `File > Invalidate Caches / Restart`

## 12. Testes existentes

Atualmente existem 4 testes unitarios.

### 12.1 `ContaTest.java`

Objetivo:

- garantir a correcao do setter `setUsuarioId`

Cenario coberto:

- cria uma conta
- altera o `usuarioId` para `42`
- verifica se `getUsuarioId()` devolve `42`

Por que esse teste existe:

- havia um bug real em que `setUsuarioId()` nao atualizava o campo corretamente

### 12.2 `DateConverterTest.java`

Objetivo:

- validar a conversao de datas no formato do projeto

Cenarios cobertos:

- `toString_deveFormatarDataNoPadraoEsperado`
  - garante o formato `dd/MM/yyyy`

- `fromString_deveConverterTextoEmData`
  - garante que uma data valida vira `Date`

- `fromString_deveRetornarNullQuandoEntradaForInvalida`
  - garante tratamento seguro para texto invalido

Por que esse teste existe:

- o app depende de data formatada tanto na UI quanto no Room

### 12.3 `DecimalDigitsTest.java`

Objetivo:

- validar a formatacao monetaria por idioma

Cenarios cobertos:

- `formatarNumero_deveUsarPadraoBrasileiroPorPadrao`
  - espera `1.234,56`

- `formatPattern_deveTrocarPadraoQuandoIdiomaForIngles`
  - espera `1,234.56`

Por que esse teste existe:

- o app usa formatacao numerica em saldo, lista, usuario e campos monetarios

### 12.4 `UtilsValidaTest.java`

Objetivo:

- validar a regra minima de preenchimento dos campos

Cenarios cobertos:

- nome e valor validos retornam `true`
- nome `null`, vazio ou so com espacos retornam `false`
- valor `null`, zero ou negativo retornam `false`

Por que esse teste existe:

- evita regressao na validacao basica usada pelos formularios

## 13. Resultado de validacao atual

Ultima validacao executada:

```bash
GRADLE_USER_HOME=/home/eduardo/Documentos/Aplicativo/Aplicativo_conta/.gradle-user-home ./gradlew test
```

Status:

- `BUILD SUCCESSFUL`

## 14. Melhorias futuras recomendadas

### 14.1 Alta prioridade

- criar migrations reais do Room e remover `fallbackToDestructiveMigration()`
- adicionar testes instrumentados para os fluxos principais da UI
- mover os fragments de conta para `ViewModel` proprio
- revisar o widget para desacoplar mais do banco

### 14.2 Media prioridade

- trocar `ListView` por `RecyclerView`
- reduzir estado global em `Ordenar`
- limpar codigo morto ou comentado na camada `api/`
- centralizar strings de chave como `orderDataDesc`, `orderNomeAsc` etc

### 14.3 Baixa prioridade

- melhorar nomes de classes como `ActivityTelaIncialListaConta`
- revisar internacionalizacao completa do app
- padronizar melhor mascaras de moeda em todos os fragments

## 15. Resumo executivo

Hoje o projeto esta funcional, compila, roda testes unitarios e tem uma estrutura significativamente melhor do que o estado original:

- build alinhado e modernizado
- Java 17
- conflito de Kotlin resolvido
- Room fora da UI thread nas telas principais
- ViewModel nas telas mais importantes
- selecao multipla da lista principal mais clara
- bug da lista de PDFs corrigido
- documentacao centralizada neste arquivo

O proximo salto de qualidade seria atacar migrations, testes de UI e padronizacao da camada de apresentacao dos fragments.
