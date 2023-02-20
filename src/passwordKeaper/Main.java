package passwordKeaper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    final static String PATH = "/home/kiryl/java_projects/PassKeaper/nodeList.txt"; // put here your path to file, were you want to keep passwords
    final static String sudo_password = "1205mydr"; // put here your master password
    static boolean workingFlag = true;
    static boolean inProgramFlag = true;
    static List<Node> manifest = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (workingFlag) {
            System.out.print("Enter the master password to get in, or '-q' for exit: ");
//            char[] inputArr = System.console().readPassword(); // get input once and store in variable
//            String input = new String(inputArr);
            String input = scanner.next();
            if (input.equals(sudo_password)) {
                readNodesFromManifest();
                while (inProgramFlag) {
                    showStartScreen();
                    String command = scanner.next(); // get input again
                    switch (command) {
                        case "quit", "-q" -> closeEverything();
                        case "help", "-h" -> showHelpScreen();
                        case "getMasterPass", "-gmp" -> System.out.println("Your master password is: " + sudo_password);
                        case "genPass", "-gp" -> genPass();
                        case "listNode", "-ln" -> listNode();
                        case "makeNode", "-mkn" -> makeNode();
                        case "deleteNode", "-dn" -> deleteNode();
                        case "getInfo", "-gi" -> getInfo();
                        default -> unknownCommend();
                    }
                }
            } else if (input.equals("-q") || input.equals("quit")) {
                closeEverything();
            }
        }
    }

    public static void showStartScreen() {
        System.out.print("Enter your commend: ");
    }

    public static void showHelpScreen() {
        System.out.println("\nhelp, -h              help");
        System.out.println("quit, -q              exit");
        System.out.println("getMasterPass, -gmp   show master password");
        System.out.println("genPass, -gp          generate a strong password that you must copy");
        System.out.println("makeNode, -mkn        create a new node with website, login and password");
        System.out.println("listNode, -ln         show list of all nodes");
        System.out.println("deleteNode, -dn       delete one node");
        System.out.println("getInfo, -gi          get all information about node\n");
    }

    public static void unknownCommend() {
        System.out.println("\nUnknown Commend");
        System.out.println("Please check 'help'\n");
    }

    public static void closeEverything() {
        workingFlag = false;
        inProgramFlag = false;
    }

    public static void readNodesFromManifest() {
        try {
            FileReader fileReader = new FileReader(PATH);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            manifest.clear();
            while ((line = bufferedReader.readLine()) != null) {
                String[] tmp = line.split(" ");
                manifest.add(new Node(decode(tmp[2]), decode(tmp[1]), decode(tmp[0])));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void listNode() {
        readNodesFromManifest();
        for (int i = 0; i < manifest.size(); i++) {
            System.out.println(i + 1 + ") " + manifest.get(i).getWebSite());
        }
    }

    public static void makeNode() {

        boolean passwordMatches = false;
        System.out.print("\nEnter website: ");
        String website = scanner.next();
        System.out.print("Enter login: ");
        String login = scanner.next();
        System.out.print("Enter password: ");
        String password = scanner.next();

        while (!passwordMatches) {
            System.out.print("Repeat password: ");
            if (scanner.next().equals(password)) {
                passwordMatches = true;
                String str = encode(website) + " " + encode(login) + " " + encode(password) + "\r\n";
                try {
                    FileWriter fileWriter = new FileWriter(PATH, true);
                    fileWriter.write(str);
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                readNodesFromManifest();
                System.out.println("Node was created successfully");
            } else {
                System.err.println("Passwords does not match");
            }
        }
    }

    public static void genPass() {
        int length = (int) (Math.random() * 11 + 10);
        StringBuilder pass = new StringBuilder();
        for (int i = 0; i < length; i++) {
            pass.append((char) (Math.random() * 93 + 33));
        }
        System.out.println("\nYour new generated password: " + pass + "\n");
    }

    public static String encode(String password) {
        int pass_index = 0;
        int sudo_index = 0;

        char[] pass = password.toCharArray();
        char[] sudo_pass = sudo_password.toCharArray();

        while (pass_index < pass.length) {
            if (sudo_index >= sudo_pass.length)
                sudo_index = 0;
            pass[pass_index++] ^= ((int) sudo_pass[sudo_index++]) / sudo_pass.length;
        }

        return new String(pass);
    }

    public static String decode(String password) {
        return encode(password);
    }

    public static void deleteNode() {
        System.out.print("type number of node you want to delete: ");
        int index = scanner.nextInt();
        if (index > 0 && index <= manifest.size()) {
            manifest.remove(index - 1);
            try {
                FileWriter fileWriter = new FileWriter(PATH);
                fileWriter.write("");
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (Node node : manifest) {
                String str = encode(node.getWebSite()) + " "
                        + encode(node.getLogin()) + " "
                        + encode(node.getPassword()) + "\r\n";
                try {
                    FileWriter fileWriter = new FileWriter(PATH, true);
                    fileWriter.write(str);
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Node was deleted successfully");
        } else
            System.err.println("Incorrect number");
    }

    public static void getInfo() {
        System.out.print("type number of node you want to get information: ");
        int index = scanner.nextInt();
        if (index > 0 && index <= manifest.size()) {
            System.out.println("website - " + manifest.get(index - 1).getWebSite() +
                    ", login - " + manifest.get(index - 1).getLogin() +
                    ", password - " + manifest.get(index - 1).getPassword());
        } else
            System.err.println("Incorrect number");
    }
}
