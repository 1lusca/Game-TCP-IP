# Game TCP/IP - Roleta

#### Integrantes do grupo: Christian Fagundes,  Erick Oliveira Schneider, Lucas Schneider

-----------------

#### Funcionamento

- Servidor online fica aguardando conexões dos clientes (players);
- Player conecta com o servidor; 
- Servidor aguarda todos os player conectados fazerem a sua jogada; 
- O jogador envia para o servidor a sua aposta (número de 1 - 36);
- Após todos players que estão conectados jogarem, o servidor roda a roleta;
- O Servidor envia para todos os players se eles ganharam ou perderam;

-----------------

#### Servidor

- O servidor cria uma thread para cada player que conectou (permitindo múltiplas conexões simultãneas) e armazena esse socket em uma lista.
- Após todos os players jogarem, o servidores varre a lista e envia para os players se ganharam ou perderam.

-----------------

#### Cliente

- Após se conectar com o servidor, o cliente cria uma thread para ouvir a resposta do servidor.
- A thread principal fica responsável por enviar as apostas do cliente.



