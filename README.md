# Game TCP/IP - Roleta

#### Integrantes do grupo: Christian Fagundes,  Erick Oliveira Schneider, Lucas Schneider

-----------------

#### Funcionamento Geral

- Servidor online fica aguardando conexões dos clientes (players);
- Player conecta com o servidor; 
- Servidor aguarda todos os player conectados fazerem a sua jogada; 
- Após todos os players fazem a sua jogada, o servidores retorna o resultado para todos os players.

- O jogador envia para o servidor a sua aposta (número de 1 - 36);
- Após todos players que estão conectados jogarem, o servidor roda a roleta;
- O Servidor envia para todos os players se eles ganharam ou perderam;

-----------------

#### Servidor

- O servidor cria uma thread para cada player que conectou e armazena esse socket em uma lista;
- Fica aguardando todos os players conectados jogarem (quantidade de fichas e número apostado);
- O controle de fichas dos players é feito no servidore;
- Após todos os players apostarem, o servidor percorre a lista de players e envia o resultado para cada um.

-----------------

#### Cliente

- Após se conectar com o servidor, o cliente cria uma thread para ouvir a resposta do servidor;
- A thread principal fica responsável por enviar as apostas;
- Cada aposta é composta pelo número de fichas e pelo número apostado;
- Primeiro, o cliente envia o número de fichas apostadas na rodade;
- Depois da quantidade de fichas, o player envia em qual número ele aposta;
- Fica aguardando o servidos enviar o resultado da rodada.



