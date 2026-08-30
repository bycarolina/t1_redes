# T1 - Fundamentos de Redes

## Cliente e servidor de arquivos TCP em Java

O projeto implementa um servidor de arquivos e um cliente utilizando sockets TCP e apenas bibliotecas padrão do Java.

## Requisitos

- JDK instalado
- `java` e `javac` disponíveis no PATH

Para verificar:

```bash
java -version
javac -version
```

## Estrutura

```text
t1_redes/
|-- Server.java
|-- Client.java
|-- arquivos/
|   `-- teste.txt
`-- downloads/
```

A pasta `arquivos` contém os arquivos disponibilizados pelo servidor. Para disponibilizar um arquivo, ele deve ser colocado dentro dessa pasta antes da requisição do cliente.

A pasta `downloads` é criada automaticamente pelo cliente quando necessário e armazena os arquivos recebidos.

## Compilação

No terminal, dentro da pasta do projeto, execute:

```bash
javac Server.java Client.java
```

Esse comando compila os dois arquivos-fonte e gera `Server.class` e `Client.class`.

Sempre que o código-fonte for alterado, é necessário compilá-lo novamente antes da execução.

## Execução do servidor

O servidor recebe o endereço IP e a porta como argumentos:

```bash
java Server <IP> <porta>
```

Para executar localmente utilizando a porta 5000:

```bash
java Server 127.0.0.1 5000
```

Após ser iniciado, o servidor permanece aguardando conexões.

Os arquivos que poderão ser solicitados pelos clientes devem estar na pasta `arquivos`.

## Execução do cliente

Com o servidor em execução, abra outro terminal na pasta do projeto e execute:

```bash
java Client <IP> <porta> <nome-do-arquivo>
```

Por exemplo, para solicitar `teste.txt` ao servidor local:

```bash
java Client 127.0.0.1 5000 teste.txt
```

O cliente envia a requisição ao servidor e, se o arquivo existir, salva o conteúdo recebido na pasta `downloads`.

Por exemplo:

```text
arquivos/teste.txt     -> arquivo disponibilizado pelo servidor
downloads/teste.txt    -> arquivo recebido pelo cliente
```

## Execução entre duas máquinas

Para executar o projeto entre duas máquinas na mesma rede, o servidor deve ser iniciado utilizando um endereço IP da máquina em que está sendo executado.

Por exemplo:

```bash
java Server 192.168.0.10 5000
```

Na outra máquina, o cliente deve utilizar esse mesmo endereço para se conectar:

```bash
java Client 192.168.0.10 5000 teste.txt
```

O endereço IP utilizado no exemplo é apenas ilustrativo. Deve ser substituído pelo endereço real da máquina que executa o servidor.

A conexão entre máquinas também depende das configurações da rede e do firewall permitirem conexões TCP na porta utilizada.

Para testes em uma única máquina, pode ser utilizado `127.0.0.1`.

## Protocolo

O cliente envia uma requisição textual no formato:

```text
GET nome-do-arquivo
```

Quando o arquivo existe, o servidor responde primeiro com:

```text
OK <tamanho-em-bytes>
```

e em seguida envia exatamente a quantidade informada de bytes do arquivo.

Quando o arquivo não existe, o servidor responde:

```text
ERROR FILE_NOT_FOUND
```

Uma requisição inválida recebe:

```text
ERROR INVALID_REQUEST
```

A indicação `OK` ou `ERROR` permite que o cliente diferencie uma resposta de controle do conteúdo do próprio arquivo.

## Testes realizados

A implementação foi testada utilizando o endereço `127.0.0.1` e a porta `5000`.

Foram realizados os seguintes testes:

1. Transferência de um arquivo existente.
2. Solicitação de um arquivo inexistente.
3. Transferência de um arquivo vazio.
4. Transferência de um arquivo de 54.977 bytes, recebido em 7 leituras.
5. Transferência de um arquivo de 10 MB (10.485.760 bytes), recebido em 1280 leituras.
6. Tentativa de iniciar duas instâncias do servidor em `127.0.0.1:5000`, resultando em `Address already in use: bind`.
7. Transferência de um arquivo cujo conteúdo era literalmente `ERROR FILE_NOT_FOUND`, confirmando que o conteúdo não é confundido com a resposta de erro do protocolo.

## Observações

O servidor permanece ativo após cada transferência e pode atender novas conexões sucessivamente.

Nesta implementação, cada conexão é processada por vez. Não foram utilizadas threads para atendimento concorrente de clientes.

Os arquivos são transferidos em blocos utilizando um buffer de 8192 bytes, permitindo a transferência de arquivos maiores sem a necessidade de carregar todo o conteúdo do arquivo na memória.
