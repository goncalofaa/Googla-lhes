import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Client {

    //Client
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String string_search;
    private static ArrayList<News> filtered_news;
    private String clientID;
    private Socket socket;
    private InetAddress inetAddress_serverAdress;
    //GUI
    private static final String PATH = (System.getProperty("user.dir")+System.getProperty("file.separator")+"news");
    private JFrame frame;
    private JPanel panel_search, panel_listNews, panel_displayNews;
    private JList<News> list_news;
    private JScrollPane scrollPane_displayNews;
    private DefaultListModel defaultListModel_news;
    private JTextPane textPane_news;

    public Client() throws UnknownHostException {
        setupGUI();
        this.inetAddress_serverAdress=InetAddress.getByName("localhost");;;
    }

    public Client(InetAddress inetAddress){
        setupGUI();
        this.inetAddress_serverAdress=inetAddress;
    }

    public void runClient()  {
        frame.setVisible(true);
        connectToServer();
        //Quando se fecha a janela in e out são fechados
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    in.close();
                    out.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                System.out.println("DONE");

            }
        });
    }

    public void connectToServer()  {
        try {
            this.socket = new Socket(inetAddress_serverAdress, Server.PORT);
            System.out.println("Endereço: " + inetAddress_serverAdress + " |  Socket: " + this.socket);
            out = new ObjectOutputStream(this.socket.getOutputStream());
            in = new ObjectInputStream(this.socket.getInputStream());
            //Envia para o Server, o inteiro 1,pois o server espera recerber um inteiro 1 ou 2, 1 = Client e 2 = Worker
            out.writeObject(1);
            int clientNum = (int) in.readObject();
            clientID = "Client"+ String.valueOf(clientNum);
            System.out.println("ClientID: "+clientID);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getNewsWithString(String string_search) throws IOException, ClassNotFoundException {
        try{
            out.writeObject(string_search);
            filtered_news = new ArrayList<>();
            filtered_news = (ArrayList<News>) in.readObject();
        }catch (Exception e){
            //Se o server tiver off tenta restabelecer a connecção sempre que se carrega no botaao
            e.printStackTrace();
            runClient();
        }
    }

    public void setupGUI(){
        this.string_search=null;
        frame = new JFrame("ISCTE Searcher");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setSize(960,540);
        frame.setResizable(true);
        addFrameContent();
    }

    public void addFrameContent(){
        //Painel de Procura
        panel_search = new JPanel();
        panel_search.setLayout(new FlowLayout());

        //TextField e Botao
        JTextField textField_search = new JTextField(string_search);
        textField_search.setPreferredSize(new Dimension(100,25));
        JButton button_search = new JButton("Search");

        button_search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                string_search = textField_search.getText();
                try {
                    new Thread(){
                        public void run(){
                            //Se a String for "" não faz nada
                            if (string_search.length()<1){
                                JOptionPane.showMessageDialog(null, "Please insert a valid string ", "INVALID STRING", JOptionPane.INFORMATION_MESSAGE);
                                return;
                            }
                            //Tenta enviar a string para o ClientHandler
                            System.out.println(clientID+": String Dispatched: "+string_search);
                            try{
                                out.writeObject(string_search);
                                filtered_news = new ArrayList<>();
                                filtered_news = (ArrayList<News>) in.readObject();
                            }catch (Exception e){
                                //Se o server tiver off tenta restabelecer a connecção sempre que se carrega no botao, Apos conexao é preciso carregar novamente para enviar a string
                                e.printStackTrace();
                                connectToServer();
                                return;
                            }
                            //Atualiza a Lista de Noticias com o numero de ocorrencias da string e do titulo
                            defaultListModel_news = new DefaultListModel();
                            for (News news: filtered_news){
                                Runnable doUpdateList = new Runnable() {
                                    @Override
                                    public void run() {
                                            defaultListModel_news.addElement(news.getResultsNumber()+"-"+news.getTitle());
                                    }
                                };SwingUtilities.invokeLater(doUpdateList);
                                list_news.setModel(defaultListModel_news);

                            //Reativa o Botao para ser clicavel
                            Runnable doUpdateButtonStatus = () -> button_search.setEnabled(true);
                            SwingUtilities.invokeLater(doUpdateButtonStatus);}
                        }
                    }.start();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });

        panel_search.add(textField_search);
        panel_search.add(button_search);

        frame.add(panel_search, BorderLayout.NORTH);

        //Painel da Lista de Noticias
        panel_listNews = new JPanel();
        panel_listNews.setLayout(new FlowLayout());


        list_news = new JList();

        //Se carregar numa posição da lista atualiza a noticia em display
        list_news.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount()> 1) {
                    System.out.println(list_news.getSelectedIndex());
                    changeNewInDisplay(list_news.getSelectedIndex());
                }
            }
        });

        JScrollPane scrollPane_listNews = new JScrollPane(list_news);
        scrollPane_listNews.setPreferredSize(new Dimension(425,500));

        frame.add(scrollPane_listNews, BorderLayout.WEST);

        //Painel de Display das News
        panel_displayNews = new JPanel(new BorderLayout());
        textPane_news = new JTextPane();
        textPane_news.setEditable(false);
        panel_displayNews.add(textPane_news);
        panel_displayNews.setPreferredSize(new Dimension(500,500));
        scrollPane_displayNews = new JScrollPane(panel_displayNews);
        scrollPane_displayNews.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        frame.add(scrollPane_displayNews,BorderLayout.CENTER);
    }

    //Altera a noticia em display
    public void changeNewInDisplay(int index){
        try{
            News n=filtered_news.get(index);
            this.textPane_news.setText(n.getTitle()+"\n\n"+n.getBody());
            //Meter o titulo a bold
            String title = n.getTitle();
            Style style= textPane_news.addStyle("Bold", null);
            StyleConstants.setBold(style, true);
            textPane_news.getStyledDocument().setCharacterAttributes(0,title.length(),style,false);
            //Meter Highlights em todas as ocurrencias da String Search
            Highlighter highlighter = textPane_news.getHighlighter();
            DefaultHighlighter.DefaultHighlightPainter painter =new DefaultHighlighter.DefaultHighlightPainter(Color.PINK);
            highlighter.removeAllHighlights();
            for (int i=0; i<n.getArrayList().size();i++){
                   highlighter.addHighlight(n.getArrayList().get(i)+2+n.getTitle().length(),n.getArrayList().get(i)+string_search.length()+2+n.getTitle().length(), painter);
            }
            System.out.println("Troquei");
        }  catch (Exception e) {
            System.out.println("Não troquei");
            e.printStackTrace();
        }
    }

}

