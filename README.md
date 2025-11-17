🌊 Chat Ambiental
Sistema de chat em tempo real desenvolvido em Java para comunicação e monitoramento ambiental do Rio Tietê, criado como trabalho de APS (Atividade Prática Supervisionada) do curso de Ciência da Computação.

📋 Sobre o Projeto
O Chat Ambiental é uma aplicação cliente-servidor que permite a comunicação em tempo real entre múltiplos usuários, facilitando o compartilhamento de informações sobre monitoramento ambiental. O sistema oferece funcionalidades de mensagens de texto e transferência de arquivos, ideal para equipes que trabalham com dados ambientais.

✨ Funcionalidades
-💬 Chat em Tempo Real: Troca de mensagens instantâneas entre usuários conectados
-📁 Transferência de Arquivos: Envio e recebimento de arquivos (até 10MB)
-👥 Lista de Usuários Online: Visualização de todos os usuários conectados
-🔐 Validação de Usuários: Sistema de autenticação com nomes únicos
-🎨 Interface Gráfica Intuitiva: Desenvolvida com Java Swing
-⏱️ Timestamps: Registro de horário em todas as mensagens
-🔔 Notificações de Sistema: Avisos de entrada/saída de usuários

🛠️ Tecnologias Utilizadas
Java SE 8+
Java Swing (Interface Gráfica)
Sockets TCP/IP (Comunicação em Rede)
Serialização de Objetos (Protocolo de Mensagens)
Threads (Processamento Concorrente)
ConcurrentHashMap (Gerenciamento Thread-Safe)
    
🚀 Como Executar
Pré-requisitos
JDK 8 ou superior instalado
Compilador Java (javac)

Compilação
bash# Compile todos os arquivos .java
javac -d bin src/com/chatambiental/**/*.java
Executando o Servidor
bash# Porta padrão (5000)
java -cp bin com.chatambiental.servidor.core.ServidorPrincipal

# Ou especifique uma porta personalizada
java -cp bin com.chatambiental.servidor.core.ServidorPrincipal 8080
Executando o Cliente
bashjava -cp bin com.chatambiental.cliente.ui.JanelaLogin

📖 Como Usar
Conectando ao Servidor

Execute o servidor primeiro
Abra a aplicação cliente
Preencha os campos:

Nome de Usuário: Apenas letras, números e underscore
Servidor: localhost (para testes locais)
Porta: 5000 (ou a porta configurada)


Clique em "Conectar"

Enviando Mensagens

Digite sua mensagem no campo de texto inferior
Pressione Enter ou clique em "Enviar"

Enviando Arquivos

Menu Arquivo → Enviar Arquivo (ou Ctrl+O)
Selecione o arquivo desejado (máximo 10MB)
O arquivo será enviado para todos os usuários conectados

Desconectando

Menu Arquivo → Desconectar (ou Ctrl+Q)
Ou feche a janela

🔧 Configurações
Porta do Servidor
Por padrão, o servidor utiliza a porta 5000. Para alterar:
bashjava -cp bin com.chatambiental.servidor.core.ServidorPrincipal [PORTA]
Limite de Tamanho de Arquivo
O tamanho máximo para transferência de arquivos está definido em 10 MB. Para alterar, modifique a validação em JanelaPrincipal.java:
javaif (arquivo.length() > 10 * 1024 * 1024) { // Altere este valor
    // ...
}
🏗️ Arquitetura
Padrão Cliente-Servidor

Servidor: Gerencia conexões e distribui mensagens
Cliente: Interface de usuário e comunicação com servidor

Protocolo de Comunicação
O sistema utiliza serialização de objetos Java para comunicação:

MensagemTexto: Mensagens de chat
MensagemArquivo: Transferência de arquivos
MensagemComando: Comandos do sistema (conectar, desconectar, etc.)

Concorrência

Cada cliente é gerenciado por uma thread separada (ThreadCliente)
ConcurrentHashMap para gerenciamento thread-safe de conexões
Sincronização nas operações de escrita de mensagens

🤝 Contribuindo
Contribuições são bem-vindas! Sinta-se à vontade para:

Fazer um Fork do projeto
Criar uma branch para sua feature (git checkout -b feature/NovaFuncionalidade)
Commit suas mudanças (git commit -m 'Adiciona nova funcionalidade')
Push para a branch (git push origin feature/NovaFuncionalidade)
Abrir um Pull Request

📝 Melhorias Futuras

 Mensagens privadas entre usuários
 Criptografia de mensagens
 Histórico de conversas
 Suporte a emojis e formatação de texto
 Notificações de área de trabalho
 Sistema de salas/canais
 Interface web
 Banco de dados para persistência

📄 Licença
Este projeto foi desenvolvido para fins educacionais como parte do curso de Ciência da Computação.
