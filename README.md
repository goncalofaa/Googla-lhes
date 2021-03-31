# Googla-lhes

A aplicação a ser implementada permite usar uma desktop grid para a procura de palavras num grupo
de noticias. Assim o utilizador deve correr um cliente que lhe permite inserir as palavras e iniciar a
procura. O cliente deve enviar esta palavra ao servidor que irá criar tarefas que são executadas pelos
workers. Cada tarefa consiste na pesquisa de uma expressão ou frase no texto de uma notícia. O
worker deve devolver ao servidor uma lista que contém todos os índices das ocorrências da palavra
no texto da notícia. Após todas as tarefas terem sido executadas, o servidor agrupa os resultados e
envia ao cliente. A informação que o servidor deve enviar ao cliente consiste numa lista com os títulos
das notícias em que a palavra ocorre bem como uma lista dos índices das ocorrências para cada uma
dessas notícias. Após recebidas os títulos das notícias onde a palavra aparece, o cliente mostra ao
utilizador estes resultados. Quando o utilizador selecicona uma das noticias da lista, o cliente deve
enviar uma mensagem ao servidor a pedir o texto da notícia.
Quando o servidor arrancar deve ler todas as notícias que fazem parte do corpus. Estas notícias
encontram-se num conjunto de ficheiros numa pasta que é passada ao servidor. O servidor ao receber
um novo pedido de pesquisa vai criar um conjunto de tarefas, uma para cada notícia, que consiste
em procurar a expressão numa das notícias. 

