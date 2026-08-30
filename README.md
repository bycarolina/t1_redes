# T1 - Fundamentos de Redes

## Cliente e servidor de arquivos TCP em Java

O projeto implementa um servidor de arquivos e um cliente utilizando sockets TCP e apenas bibliotecas padrão do Java.

O cliente envia ao servidor uma requisição textual para obter um arquivo. O servidor verifica se o arquivo solicitado existe e, em caso positivo, envia seu conteúdo através da conexão TCP.

## Requisitos

- JDK instalado
- `java` e `javac` disponíveis no PATH

Para verificar a instalação:

```bash
java -version
javac -version
```

Não são necessárias bibliotecas ou dependências externas.

## Arquivos entregues

Os códigos-fonte da implementação são:

```text
Server.java
Client.java
```

As pastas utilizadas durante a execução não precisam estar presentes previamente na entrega.

## Preparação

Antes de executar o servidor, crie uma pasta chamada `arquivos` no mesmo diretório em que estão `Server.java` e `Client.java`.

A estrutura deve ficar semelhante a:

```text
t1_redes/
|-- Server.java
|-- Client.java
`-- arquivos/
```

Coloque dentro da pasta `arquivos` os arquivos que deseja disponibilizar para o cliente.

Por exemplo, para disponibilizar um arquivo chamado `teste.txt`:

```text
t1_redes/
|-- Server.java
|-- Client.java
`-- arquivos/
    `-- teste.txt
```

Não é necessário criar a pasta `downloads`. Ela será criada automaticamente pelo cliente quando necessário e armazenará os arquivos recebidos.

## Compilação

No terminal, dentro do diretório em que estão os arquivos do projeto, execute:

```bash
javac Server.java Client.java
```

O comando gera os arquivos compilados:

```text
Server.class
Client.class
```

Caso os códigos-fonte sejam alterados, eles devem ser compilados novamente antes da execução.

## Execução do servidor

O servidor recebe dois argumentos:

```text
IP porta
```

Para iniciá-lo:

```bash
java Server <IP> <porta>
```

Exemplo para execução local utilizando a porta 5000:

```bash
java Server 127.0.0.1 5000
```

Após a inicialização, o servidor permanece aguardando conexões.

Os arquivos que poderão ser solicitados pelos clientes devem estar dentro da pasta `arquivos`.

## Execução do cliente

Com o servidor em execução, abra outro terminal no mesmo diretório.

O cliente recebe três argumentos:

```text
IP porta nome-do-arquivo
```

Para executá-lo:

```bash
java Client <IP> <porta> <nome-do-arquivo>
```

Por exemplo, se existir o arquivo:

```text
arquivos/teste.txt
```

execute:

```bash
java Client 127.0.0.1 5000 teste.txt
```

Se a transferência for concluída com sucesso, o cliente criará automaticamente a pasta `downloads` e salvará o arquivo recebido nela:

```text
downloads/teste.txt
```

## Execução entre duas máquinas

Para executar o cliente e o servidor em máquinas diferentes, deve ser utilizado o endereço IP da máquina em que o servidor está sendo executado.

Por exemplo, supondo que o endereço da máquina do servidor seja `192.168.0.10`, o servidor pode ser iniciado com:

```bash
java Server 192.168.0.10 5000
```

Na máquina do cliente:

```bash
java Client 192.168.0.10 5000 teste.txt
```

O endereço `192.168.0.10` acima é apenas um exemplo e deve ser substituído pelo endereço IP real da máquina que executa o servidor.

A comunicação entre máquinas depende também das configurações da rede e do firewall permitirem conexões TCP na porta escolhida.

Para executar cliente e servidor na mesma máquina, pode ser utilizado o endereço de loopback:

```text
127.0.0.1
```

## Protocolo

O cliente envia ao servidor uma requisição textual no formato:

```text
GET nome-do-arquivo
```

Quando o arquivo existe, o servidor responde primeiro com:

```text
OK <tamanho-em-bytes>
```

e, em seguida, envia exatamente a quantidade informada de bytes do arquivo.

Quando o arquivo solicitado não existe, o servidor responde:

```text
ERROR FILE_NOT_FOUND
```

Uma requisição inválida recebe:

```text
ERROR INVALID_REQUEST
```

A separação entre a resposta de controle e os dados permite que o cliente diferencie uma mensagem de erro do conteúdo do próprio arquivo.

## Transferência de arquivos

Os arquivos são lidos e enviados em blocos utilizando um buffer de 8192 bytes. Dessa forma, arquivos grandes podem ser transferidos sem a necessidade de carregar todo o seu conteúdo na memória.

O cliente também realiza sucessivas leituras da conexão até receber a quantidade de bytes informada pelo servidor.

## Testes realizados

A implementação foi testada localmente utilizando o endereço `127.0.0.1` e a porta `5000`.

Foram realizados os seguintes testes:

1. Transferência de um arquivo existente.
2. Solicitação de um arquivo inexistente.
3. Transferência de um arquivo vazio.
4. Transferência de um arquivo de 54.977 bytes, recebido em 7 leituras.
5. Transferência de um arquivo de 10 MB (10.485.760 bytes), recebido em 1280 leituras.
6. Tentativa de iniciar duas instâncias do servidor em `127.0.0.1:5000`, resultando em `Address already in use: bind`.
7. Transferência de um arquivo cujo conteúdo era literalmente `ERROR FILE_NOT_FOUND`, confirmando que o conteúdo do arquivo não é confundido com uma resposta de erro do protocolo.

## Observações

O servidor permanece ativo após cada transferência e pode atender novas conexões sucessivamente.

Nesta implementação, cada conexão é processada por vez. Não foram utilizadas threads para atendimento concorrente de clientes.
