package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Controller {

    @FXML
    private Label question, error, category, nickError, end;
    @FXML
    private Button sendButton;
    @FXML
    private TextField letter, nick, usedLetters;
    @FXML
    private ImageView image;
    @FXML
    private TextArea topScorers, lastGames, playCounter;

    private String cat;
    private String q;
    int fails = 0;

    private void getRandomQuestion(){

        Random random = new Random();
        int categoryNumber = random.nextInt(3); //todo
        int questionNumber = random.nextInt(3); //todo

        String[] questions = new String[3];

        switch(categoryNumber){
            case 0 : {
                try {
                    File file = new File("jedzenie.txt");
                    Scanner sc = new Scanner(file);

                    int i = 0;
                    while (sc.hasNextLine()) {
                        questions[i] = sc.nextLine();
                        System.out.println(questions[i]);//todo
                        i++;
                    }
                    cat = "jedzenie";
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            }
            case 1 : {
                try {
                    File file = new File("osoby.txt");
                    Scanner sc = new Scanner(file);

                    int i = 0;
                    while (sc.hasNextLine()) {
                        questions[i] = sc.nextLine();
                        System.out.println(questions[i]);//todo
                        i++;
                    }
                    cat = "osoby";
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            }
            case 2 : {
                try {
                    File file = new File("technologie.txt");
                    Scanner sc = new Scanner(file);

                    int i = 0;
                    while (sc.hasNextLine()) {
                        questions[i] = sc.nextLine();
                        System.out.println(questions[i]);//todo
                        i++;
                    }
                    cat = "technologie";
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            }
            default: {
                System.err.println("Error while getting random category");
            }
        }

        q = questions[questionNumber];

    }

    String questionX = "";

    @FXML
    public void initialize() {
        countScoredNames(); // TO DELETE LATER
        getRandomQuestion();
        category.setText(cat);

        char space = ' ';
        for (int i = 0; i < q.length(); i++) {
            if(q.charAt(i)==space) questionX +=" ";
            else questionX += "-";
        }
        question.setText(questionX);
    }

    @FXML
    public void buttonAction(){

        String let = letter.getText().toLowerCase();
        char l = let.charAt(0);
        q = q.toLowerCase();

        if (letter.getText().length()!=1) {
            error.setVisible(true);
        } else {
            if (nick.getText().length() > 0){

                boolean check = false;
                error.setVisible(false);
                nickError.setVisible(false);
                for (int i = 0; i < q.length(); i++) {
                    if (q.charAt(i) == l) {
                        questionX = questionX.substring(0, i)+l+questionX.substring(i+1);
                        check = true;
                    }
                }

                if(!check){
                    fails++;
                    String filePath = fails + ".png";
                    File file = new File(filePath);
                    Image img = new Image(file.toURI().toString());
                    image.setImage(img);
                }

                question.setText(questionX);
                usedLetters.setText(usedLetters.getText()+","+let.charAt(0));

                if (fails > 6){
                    end.setText("Przegrałeś/aś");
                    sendButton.setDisable(true);
                    nick.setDisable(true);
                    letter.setDisable(true);
                }
            } else {
                nickError.setVisible(true);
            }

        }

    }

    // zapisywanie nicku użytkownika do pliku
    private void saveNicknameToFile(String nick, int score){
        String fileName = "wyniki.txt";
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(fileName, true));
            writer.append(nick + "," + Integer.toString(score) + '\n');
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // czytanie wszystkich linii z pliku
    private ArrayList<String> readWholeFile(String filePath){
        Scanner scanner = null;
        ArrayList<String> list = new ArrayList<String>();
        try {
            scanner = new Scanner(new File(filePath));
            while(scanner.hasNext()){
                list.add(scanner.next());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return list;
    }

    private Set<String> getUniqueNames(ArrayList<String> list){
        ArrayList<String> temp = extractNamesFromScore(list);
        Set<String> set = new HashSet<String>(temp);
        return set;
    }

    private ArrayList<String> extractNamesFromScore(ArrayList<String> list){
        ArrayList<String> temp = new ArrayList<>();
        String[] parts;
        for(String value : list){
            parts = value.split(",");
            temp.add(parts[0]);
        }
        return temp;
    }

    private Map<String, Integer> mapSort(Map<String, Integer> map){
        return map.entrySet()
                .stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    private void countScoredNames(){
        ArrayList<String> list = readWholeFile("wyniki.txt");
        Map<String, Integer> map = new LinkedHashMap<>();
        Set<String> users = getUniqueNames(list);
        for(String user : users){
            map.put(user,Collections.frequency(extractNamesFromScore(list), user));
        }
        map = mapSort(map);
        for(Map.Entry<String,Integer> entry : map.entrySet()){
            playCounter.appendText(entry.getKey() + ": " + entry.getValue() + "\n");
        }
    }

}
