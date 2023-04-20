import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Server {
    public static void main(String argv[]) throws Exception {
        
        ServerSocket socket = new ServerSocket(6789); // cria um socket que escuta por novas conexoes
        
        List<Socket> conexoes = new ArrayList<>(); // lista que vai armazenas as conexoes

        int[] settings = new int[2];
        settings[0] = 0; //numero de conexoes
        settings[1] = 0; //jogadas feitas

        int[] jogadas = new int[2]; // armazena as jogadas 
        int[] fichas = new int[2]; // fichas apostadas na rodada

        int[] saldo = new int[2];
        saldo[0] = 1000;
        saldo[1] = 1000;
  
        while (true) {

            Socket socketConexao = socket.accept(); // cria um socket toda vez que uma nova conexoes e feita
            
            conexoes.add(socketConexao); // adiciona esse socket de uma nova conexao em uma lista

            synchronized(settings) {
                settings[0] = settings[0] + 1; // incrementa o numero de conexoes
            }
            // cria uma nova thread para cada jogador
            // isso vai garantir que cada conexao tenha uma thread para si propria, garantindo multiplas conexoes
            new Thread(() -> {
                try {
                    
                    BufferedReader doCliente = new BufferedReader(new InputStreamReader(socketConexao.getInputStream())); // stream que le a jogada recebida

                    while (true) { // loop que fica ouvindo novas jogadas

                        String jogada = doCliente.readLine(); // le a jogada do jogador
                        


                        if(jogada.contains("fichas")) {
                        
                            synchronized(jogadas) {

                                int aposta = Integer.parseInt(jogada.replace(" fichas", ""));
                                
                                // salva a jogada 
                                if(conexoes.get(0) == socketConexao) {
                                    fichas[0] = aposta;
                                    saldo[0] = saldo[0] - aposta;
                                } else {
                                    fichas[1] = aposta;
                                    saldo[1] = saldo[1] - aposta;
                                }
                                 
                            }















                        } else {




                        synchronized(settings) { // garante o controle de concorrencia entre threads (todo synchronized faz isso com variaves globais) 
                            
                            synchronized(jogadas) {
                                
                                // salva a jogada 
                                if(conexoes.get(0) == socketConexao) {
                                    jogadas[0] = Integer.parseInt(jogada);
                                } else {
                                    jogadas[1] = Integer.parseInt(jogada);
                                }
                                 
                            }

                            settings[1] = settings[1] + 1; // incrementa o numero de jogadas feitas
                           
                            if(settings[0] == settings[1]) { // verifica se todos os jogadores ja jogaram
                                
                                // gira a roleta
                                Random random = new Random();
                                int numeroRoleta = random.nextInt(36) + 1;
                                //int numeroRoleta = 10;

                                synchronized (conexoes) {
                                    
                                    int aux = 0;
                                    
                                    for (Socket conexao : conexoes) { // varre a lista de conexoes para verificar se os jogadores ganharam e enviar o resultado
                                        
                                        String resultado;
                                        
                                        // verifica se o jogador atual ganhou
                                        if(numeroRoleta == jogadas[aux]) {
                                            saldo[aux] = saldo[aux] + fichas[aux]*2;
                                            resultado = "Voce ganhou "+saldo[aux]+" fichas, seu saldo atual: "+saldo[aux]+'\n';
                                        } else {
                                            resultado = "Voce perdeu :c | Numero ganhador: "+numeroRoleta+" | Voce jogou: "+jogadas[aux]+" | Voce perdeu: "+fichas[aux]+" fichas, seu saldo atual: "+saldo[aux]+"\n";
                                        }
                                        
                                        System.out.println(jogadas[0]);
                                        System.out.println(jogadas[1]);
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
