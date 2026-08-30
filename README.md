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

A pasta `arquivos` contém os arquivos disponibilizados pelo servidor.

A pasta `downloads` é criada automaticamente pelo cliente quando necessário e armazena os arquivos recebidos.

## Compilação

No terminal, dentro da pasta do projeto:

```bash
javac Server.java Client.java
```

## Execução do servidor

```bash
java Server <IP> <porta>
```

Exemplo:

```bash
java Server 127.0.0.1 5000
```

## Execução do cliente

Em outro terminal:

```bash
java Client <IP> <porta> <nome-do-arquivo>
```

Exemplo:

```bash
java Client 127.0.0.1 5000 teste.txt
```

O arquivo recebido será salvo na pasta `downloads`.

## Protocolo

O cliente envia uma requisição textual no formato:

```text
GET nome-do-arquivo
```

Quando o arquivo existe, o servidor responde com:

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

## Testes

Podem ser realizados os seguintes testes:

1. Solicitar um arquivo existente e comparar o arquivo original com o recebido.
2. Solicitar um arquivo inexistente e verificar a mensagem de erro.
3. Transferir um arquivo grande para verificar o recebimento em múltiplas leituras.
4. Tentar iniciar dois servidores no mesmo endereço IP e porta para observar o erro de bind.

## Observação

O servidor permanece ativo após cada transferência e pode atender novas conexões sucessivamente.

Nesta implementação, cada conexão é processada por vez. Não foram utilizadas threads para atendimento concorrente de clientes.
