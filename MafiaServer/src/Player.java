import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ConcurrentModificationException;


public class Player extends Thread {

    private static final String PURPLE = "\033[0;35m";
    private static final String RESET = "\033[0m";

    private final God god;

    private String name;
    public Role role;

    public boolean isBusy = false;
    private boolean isSilent = false;
    private int answerOfWho = -1;
    private int nVotes = 0;

    DataInputStream in;
    DataOutputStream out;
    Socket socket;

    ChatHandler chatHandler;
    AskingHandler askingWhoHandler;

    private boolean isInactive = false;
    private int inactiveTurns = 0;

    public Player(God god, DataInputStream in, DataOutputStream out, Socket socket) {
        this.god = god;
        this.in = in;
        this.out = out;
        this.socket = socket;
    }

    public void addVote(){
        nVotes++;
    }

    public void resetVote(){
        nVotes = 0;
    }

    public int getNVote(){
        return nVotes;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public int getAnswerOfWho() {
        isBusy = false;
        int temp = answerOfWho;
        answerOfWho = -1;
        return temp;
    }

    @Override
    public void run() {

        try {

            name = in.readUTF();

            while (god.nameIsUsed(name)) {
                out.writeUTF("BadName");
                name = in.readUTF();
            }

            out.writeUTF("GoodName");

            System.out.println(getUserName() + " registered.\n");
            god.notifyEverybody((god.nActives() + 1) + " actives.");
            god.addPlayer(this);

        }
        catch (SocketException e){
            System.out.println(getUserName() + " disconnected.");
            god.removePlayer(this);
        }
        catch (ConcurrentModificationException e){
            System.out.println("Player couldn't register successfully.");
        }
        catch (IOException e /*| InterruptedException e*/) {
            e.printStackTrace();
        }
    }

    public String getUserName() {
        return PURPLE + name + RESET;
    }

    public String compareNames(){
        return name;
    }

    public String getRoleNAme(){
        return role.getName();
    }

    public void introduction() {
        String massage = "Your role is: " + PURPLE + this.role.getName() + RESET + "\n";

        if (role instanceof Mayor) {
            massage += god.whoIsCityDoctor(this);
            sendToClient(massage);
            return;
        }

        if (role instanceof Citizen) {
            sendToClient(massage);
            return;
        }

        sendToClient(massage + god.getMafiaList(this));

    }

    public void joinChat() {

        if (isSilent) {
            isSilent = false;
        }
        else {
            isBusy = true;
            System.out.println(getUserName() + " Joined Chat.");

            chatHandler = new ChatHandler(god, this);
            chatHandler.start();
        }
    }


    public void vote() throws IOException {

        isBusy = true;
        askingWhoHandler = new AskingHandler(god, this, "Vote");
        askingWhoHandler.start();

    }


    public void setAnswerOfWho(int answer) {
        if (role instanceof SimpleMafia){
            god.suggestGodFather(answer);
        }

        answerOfWho = answer;
        sendToClient("Got it.");
        isBusy = false;

        if (god.nobodyIsBusy()){
            god.stopWaiting();
        }


    }

    public boolean askYesOrNo(String question){
        sendToClient(question + "[yes, no]");

        isBusy = true;

        AskingHandler askingHandler = new AskingHandler(god, this, "YesOrNo");
        askingHandler.start();


        synchronized(this) {
            while(isBusy) {
                System.out.println("Waiting for " + getUserName() + " to YesOrNo...");
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


        System.out.println(getUserName() + " says: " + answerIsYes);
        return answerIsYes;

    }

    boolean answerIsYes;

    public void endYesOrNo(boolean answerIsYes) {

        synchronized(Player.this) {
            isBusy = false;
            Player.this.notify();
        }

        this.answerIsYes = answerIsYes;
        isBusy = false;

    }



    public void nightAct() {

        if (this.role.actQuestion() == null){
            sendToClient("Just wait and try to hold on :D");
            return;
        }

        isBusy = true;

        if (role instanceof Bulletproof) {
            answerOfWho = -2;
            AskingHandler askingHandler = new AskingHandler(god, this, "Bulletproof");
            askingHandler.start();
            return;
        }

        sendToClient(role.actQuestion() + "\n-1: Nobody");

        AskingHandler askingHandler = new AskingHandler(god, this, "Night");
        askingHandler.start();

    }

    public void notifyOthers(String massage){
        god.notifyEverybody(massage, this);
    }


    public void sendToClient(String playerListens){

        try {
            out.writeUTF(playerListens);
        }
        catch (SocketException e){
            System.out.println(getUserName() + " disconnected.");
            god.removePlayer(this);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void mute() {
        isSilent = true;
    }

    public void suggestWatching() {

        sendToClient("You're Dead!\nDo You Wanna Watch Game? [yes, no]");

        isBusy = true;

        AskingHandler askingHandler = new AskingHandler(god, this, "Watch");
        askingHandler.start();

    }

    public void end() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void activated(){

        if(inactiveTurns == 3){
            isInactive = true;
        }
    }
}