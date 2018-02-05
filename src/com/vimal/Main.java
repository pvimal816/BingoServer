package com.vimal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.print("Enter port No. for server: ");
        Scanner scanner = new Scanner(System.in);
        int portNo = scanner.nextInt();
        ServerSocket sskt = null;
        int totalPlayers;
        try {
            sskt = new ServerSocket(portNo);

            System.out.print("Enter total number of players : ");
            totalPlayers = scanner.nextInt();

            Socket[] playerSockets = new Socket[totalPlayers];
            DataOutputStream[] dos = new DataOutputStream[totalPlayers];
            DataInputStream[] dis = new DataInputStream[totalPlayers];

            for (int i = 0; i < totalPlayers; i++) {
                playerSockets[i] = sskt.accept();
                dis[i] = new DataInputStream(playerSockets[i].getInputStream());
                dos[i] = new DataOutputStream(playerSockets[i].getOutputStream());
                notifySinglePlayer(dos[i], "YOUR TURN:" + i + '\n');
            }

            int turn = 0, flag = 1;
            while (true && flag == 1) {
                for (int i = 0; i < totalPlayers; i++) {
                    notifyPlayers(dos, "TURN:" + i + '\n');
                    short selectedNumber = dis[i].readByte();
                    /////////
                    System.out.println("Slected No. : " + selectedNumber);
                    /////////
                    notifyPlayers(dos, "SELECTED NUMBER:" + selectedNumber + '\n');
                    ArrayList<Integer> winners = checkIfAnyPlayerWon(dis);
                    if (winners.size() > 0) {
                        String w = declareWinners(winners);
                        notifyPlayers(dos, "WON:" + w + '\n');
                        flag = 0;
                        break;
                    }
                    notifyPlayers(dos, "NOBODYWON:" + '\n');
                }
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private static String declareWinners(ArrayList<Integer> winners) {
        System.out.println("Match Over!");
        System.out.print("Winners are : ");
        StringBuffer sb = new StringBuffer();
        for (int i : winners)
            sb.append(i + "|");
        System.out.println(sb.toString());
        return sb.toString();
    }

    private static ArrayList<Integer> checkIfAnyPlayerWon(DataInputStream[] dis) {
        ArrayList<Integer> winners = new ArrayList<>();
        try {
            for (int i = 0; i < dis.length; i++) {
                if (dis[i].readByte() == 'Y') {
                    winners.add(i);
                }
            }
        } catch (IOException e) {
            System.err.println("[ Error While receiving message from players! ]" + e.getMessage());
        }
        return winners;
    }

    private static void notifyPlayers(DataOutputStream[] dos, String s) {
        for (DataOutputStream dout : dos)
            notifySinglePlayer(dout, s);
    }

    private static void notifySinglePlayer(DataOutputStream dout, String s) {
        try {
            dout.writeBytes(s);
        } catch (IOException e) {
            System.err.println("[ Error While Sending message to players! ]" + e.getMessage());
        }
    }
}
