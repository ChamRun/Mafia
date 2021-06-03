import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;

public class God {
    ArrayList<Handler> actives = new ArrayList<>();
    ArrayList<Handler> watchers = new ArrayList<>();

    public void addActive(ServerSocket server){

        try {
            Socket socket = server.accept();
            System.out.println("New Client Connected.");

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            Handler handler = new Handler(in, out, socket);
            handler.start();

        }
        catch (SocketException e){
            System.out.println("Client Disconnected.");
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }



    class Handler extends Thread {

        private String name;
        public Role role;

        DataInputStream in;
        DataOutputStream out;
        Socket socket;

        public Handler(DataInputStream in, DataOutputStream out, Socket socket) {
            this.in = in;
            this.out = out;
            this.socket = socket;
        }



        public String getUserName() {
            return name;
        }

        public void act(){
            role.act();
        }

        public void introduction() {
            String massage = "Your role is: " + this.role.getName() + "\n";

            if (role instanceof Mafia){
                for (Handler h: actives) {
                    if (h.role instanceof Mafia && !h.equals(this)){
                         massage += h.getUserName() + " is " + h.role.getName() + "\n";
                    }
                }
            }
            if (role instanceof Mayor){
                for (Handler h: actives) {
                    if (h.role instanceof CityDoctor){
                        massage += "Doctor of City is " + h.getUserName() + "\n";
                    }
                }
            }

            sendToClient(massage);
        }

        public void joinChat(){
            sendToClient("Day is Started! You Can chat for 5 minutes. Send OVER if you're done.");

        }

        public void leaveChat(){
        }

        public void sendToClient(String playerListens){
            try {
                out.writeUTF(playerListens);
            }
            catch (SocketException e){
                System.out.println("Player has been disconnected.");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }



        @Override
        public void run() {

            try {

                name = in.readUTF();

                while (nameIsUsed(name)) {
                    out.writeUTF("BadName");
                    name = in.readUTF();
                }

                out.writeUTF("GoodName");

                System.out.println(name + " registered.\n");
                notifyActives((actives.size() + 1) + " actives.");
                actives.add(this);

            }
            catch (SocketException e){
                System.out.println(name + " disconnected.");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }


        public void vote() {
        }
    }

    public boolean nameIsUsed(String name) {
        for (Handler h: actives){
            if (h.getUserName().equals(name)){
                return true;
            }
        }
        return false;
    }


    public void notifyActives(String godSays) {

        for (Handler h: actives) {

            h.sendToClient(godSays);

        }
    }

    public void setRandomRoles() {

        ArrayList<Role> roles = importantRoles();
        Collections.shuffle(roles);

        for (Handler h: actives) {

            h.role = roles.get(0);
            roles.remove(0);

        }

    }


    public ArrayList<Role> importantRoles() {
        int nRoles = actives.size();

        ArrayList<Role> roles = new ArrayList<>();

        roles.add(new GodFather());
        nRoles--;
        if (nRoles == 0){
            return roles;
        }

        roles.add(new CityDoctor());
        nRoles--;
        if (nRoles == 0){
            return roles;
        }

        roles.add(new Detective());
        nRoles--;
        if (nRoles == 0){
            return roles;
        }

        roles.add(new Sniper());
        nRoles--;
        if (nRoles == 0){
            return roles;
        }

        roles.add(new DoctorLector());
        nRoles--;
        if (nRoles == 0){
            return roles;
        }

        roles.add(new SimpleCitizen());
        nRoles--;
        if (nRoles == 0){
            return roles;
        }

        roles.add(new Mayor());
        nRoles--;
        if (nRoles == 0){
            return roles;
        }

        roles.add(new Psychic());
        nRoles--;
        if (nRoles == 0){
            return roles;
        }

        roles.add(new SimpleMafia());
        nRoles--;
        if (nRoles == 0){
            return roles;
        }

        roles.add(new Bulletproof());
        nRoles--;
        if (nRoles == 0){
            return roles;
        }

        while (nRoles != 0){
            if (nRoles % 3 == 0){
                roles.add(new SimpleMafia());
            }
            else {
                roles.add(new SimpleCitizen());
            }

            nRoles--;
        }

        return roles;
    }

    public void turnFirstNight() {
        for (Handler h: actives){
            h.introduction();
        }
    }

    public void turnDay() {
        System.out.println("Day started.");

        for (Handler h: actives) {

            h.joinChat();
        }

        try {
            Thread.sleep(1000 * 60 * 5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Handler h: actives) {

            h.leaveChat();
        }

    }

    public void election() {
        for (Handler h: actives) {

            h.vote();
        }

        for (Handler h: actives) {

            if (h.role instanceof Mayor){
                if (((Mayor) h.role).cancelElection()) {

                }
            }
        }

    }

    public void turnNight() {
        for (Handler h: actives){
            h.act();
        }
    }

    public boolean gameIsOver() {

        int nMafia = 0, nCitizen = 0;

        for (Handler h: actives) {

            if (h.role instanceof Mafia){
                nMafia++;
            }
            else {
                nCitizen++;
            }

        }

        if (nMafia == 0){
            displayFinalResult(false);
            return true;
        }

        if (nCitizen <= nMafia){

            displayFinalResult(true);
            return true;
        }


        return false;

    }

    private void displayFinalResult(boolean mafiaWon) {

        if (mafiaWon){
            System.out.println();
        }
        else {
            System.out.println();
        }

    }
}

