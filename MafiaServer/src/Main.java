import java.io.IOException;
import java.net.ServerSocket;
import java.util.Scanner;

public class Main {

    private static final God god = new God();



    public static void main(String[] args) throws IOException, InterruptedException {

        Scanner sc = new Scanner(System.in);

        /*
        System.out.println("How many Players? (at least 5)");
        int nOfAllPlayers = sc.nextInt();

        while (nOfAllPlayers < 5){
            System.out.println("at least 5");
            nOfAllPlayers = sc.nextInt();
        }
         */

        int nOfAllPlayers = 3;

        ServerSocket server = new ServerSocket(5056);
        System.out.println("Now Clients Should connect to:\n" + server + "\n");

        for (int i = 0; i < nOfAllPlayers; i++) {

            god.addActive(server);

        }

        Thread.sleep(500);
        System.out.println("\nAll Players are connected.\n");

        int nUnready = nOfAllPlayers - god.nActives();

        while (nUnready != 0){
            System.out.println(nUnready + " Player(s) is (are) not registered yet.");
            Thread.sleep(5000);
            nUnready = nOfAllPlayers - god.nActives();
        }

        god.setRandomRoles();
        System.out.println("Roles Are Set.");// Press Enter to start Game!");

        god.turnFirstNight();

        while (god.gameIsNotOver()) {

            System.out.println("Day...");
            god.startChatroom();

            System.out.println("Election...");
            god.election();

            if (god.gameIsNotOver()) {
                System.out.println("Night...");

                god.turnNight();
            }
            else {
                break;
            }

        }

        System.out.println("... The end.");

    }

}
