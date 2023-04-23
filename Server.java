import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Server {
    public static void main(String argv[]) throws Exception {
        
        // cria um socket que escuta por novas conexoes
        ServerSocket socket = new ServerSocket(6789); 
        
        // lista que vai armazenas as conexoes
        List<Socket> conexoes = new ArrayList<>(); 

        // controle
        int[] settings = new int[2];
        settings[0] = 0; // numero de conexoes
        settings[1] = 0; // jogadas feitas

        // armazena as jogadas
        int[] jogadas = new int[2];

        // armazena as fichadas apostadas
        int[] fichas = new int[2];

        // controle do saldo de fichas dos players
        int[] saldo = new int[2];
        saldo[0] = 1000; // player 1
        saldo[1] = 1000; // player 2
  
        while (true) {

            // socket para cada nova conexao
            Socket socketConexao = socket.accept();
            
            // adiciona o sockets na lista de conexoes
            conexoes.add(socketConexao);

            // incrementa o numero de conexoes
            synchronized(settings) {
                settings[0] = settings[0] + 1;
            }

            // cria uma nova thread para cada jogador (conexao)
            // isso vai garantir que cada conexao tenha uma thread para si propria, garantindo multiplas conexoes
            new Thread(() -> {
                try {
                    
                    // stream que le o que o cliente envia
                    BufferedReader doCliente = new BufferedReader(new InputStreamReader(socketConexao.getInputStream()));

                    // loop que fica ouvindo novas jogadas
                    while (true) { 

                        // le a jogada (fichas e numero)
                        String jogada = doCliente.readLine();
                        
                        // protocolo de apostas das fichas
                        if(jogada.contains("fichas")) {
                        
                            synchronized(jogadas) {

                                // le quantas fichas foram apostadas
                                int aposta = Integer.parseInt(jogada.replace(" fichas", ""));
                                
                                // atualiza o saldo de fichas e registra quantas fichas foram apostadas
                                if(conexoes.get(0) == socketConexao) {
                                    fichas[0] = aposta;
                                    saldo[0] = saldo[0] - aposta;
                                } else {
                                    fichas[1] = aposta;
                                    saldo[1] = saldo[1] - aposta;
                                }
                                 
                            }

                        } else { // protocolo numero apostado

                            synchronized(settings) {
                            
                                synchronized(jogadas) {
                                
                                    // salva a jogada 
                                    if(conexoes.get(0) == socketConexao) {
                                        jogadas[0] = Integer.parseInt(jogada);
                                    } else {
                                        jogadas[1] = Integer.parseInt(jogada);
                                    }
                                 
                                }
                                
                                // incrementa a quantidade de jogadas feitas
                                settings[1] = settings[1] + 1;
                                
                                // verifica se todos jogadores ja jogaram
                                if(settings[0] == settings[1]) { 
                                
                                    // gira a roleta
                                    Random random = new Random();
                                    int numeroRoleta = random.nextInt(36) + 1;
                                    //int numeroRoleta = 10;

                                    synchronized (conexoes) {
                                        
                                        // controle
                                        int aux = 0;
                                        
                                        // percorre a lista de jogadores
                                        for (Socket conexao : conexoes) { 
                                            
                                            String resultado;
                                            
                                            // verifica se o jogador atual ganhou
                                            if(numeroRoleta == jogadas[aux]) {
                                                saldo[aux] = saldo[aux] + fichas[aux]*2;
                                                resultado = "Voce ganhou "+saldo[aux]+" fichas, seu saldo atual: "+saldo[aux]+'\n';
                                            } else {
                                                resultado = "Voce perdeu :c | Numero ganhador: "+numeroRoleta+" | Voce jogou: "+jogadas[aux]+" | Voce perdeu: "+fichas[aux]+" fichas, seu saldo atual: "+saldo[aux]+"\n";
                                            }
                                            
                                            // controle 
                                            aux = aux + 1;

                                            // envia o resultado da rodada para os jogadores
                                            DataOutputStream sender = new DataOutputStream(conexao.getOutputStream());
                                            sender.writeBytes(resultado);
                                        }

                                        // reseta as jogadas para uma nova rodada
                                        settings[1] = 0;
                                        jogadas[0] = 0;
                                        jogadas[1] = 0;

                                    }
                                }

                            }

                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
