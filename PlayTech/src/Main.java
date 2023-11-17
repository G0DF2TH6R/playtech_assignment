import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {

        SortedMap<String, String[]> match_data = getMatchData("match_data.txt");

        SortedMap[] players_data = getPlayerData("player_data.txt", match_data);

        writeResultsToFile("result.txt", players_data[0], players_data[1]);

    }


    /**
     * Gets match data from file.
     * @param file_path File path to the file with match data
     * @return A Sorted map where key is match id and value is the winning team and its winning rate.
     * @throws IOException If there are errors with the file path.
     */
    public static SortedMap<String, String[]> getMatchData(String file_path) throws IOException {
        SortedMap<String, String[]> match_data = new TreeMap<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file_path))) {
            String line = bufferedReader.readLine();
            while (line != null) {
                String[] data = line.split(",");
                String[] rate = new String[2];
                if (data[3].equals("A")) {
                    rate[0] = data[1];
                    rate[1] = "A";
                } else if (data[3].equals("B")) {
                    rate[0] = data[2];
                    rate[1] = "B";
                } else rate[1] = "DRAW";
                match_data.put(data[0], rate);

                line = bufferedReader.readLine();
            }
        } catch (IOException exception) {
            throw new IOException("Error with file path.");
        }

        return match_data;

    }

    /**
     * Gets players actions and handles them.
     * @param file_path File path to the file with players data
     * @param match_data To check if bet action results in a win or loss for player
     * @return An array where the first element is legitimate players and the second illegitimate players.
     * @throws IOException If there are errors with the file path.
     */
    public static SortedMap[] getPlayerData(String file_path, SortedMap<String, String[]> match_data) throws IOException {
        Player player = new Player("");
        String player_id = "";
        System.out.println(player);

        SortedMap<String, Player> players = new TreeMap<>();
        SortedMap<String, Player> illegal_players = new TreeMap<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file_path))) {
            String line = bufferedReader.readLine();
            while (line != null) {
                String[] action_data = line.split(",");
                player_id = action_data[0];
                if (players.get(player_id) == null && illegal_players.get(player_id) == null) {
                    player = new Player(player_id);
                    players.put(player_id, player);
                } else if (!player.isIllegitimate()) {
                    player = players.get(player_id);
                } else {
                    player = illegal_players.get(player_id);
                }

                if (!player.isIllegitimate()) {
                    switch (action_data[1]) {
                        case "DEPOSIT" -> {
                            player.handleDeposit(Integer.parseInt(action_data[3]));
                        }
                        case "WITHDRAW" -> {
                            if (!player.handleWithdraw(Long.parseLong(action_data[3]))) {
                                players.get(player_id).setIllegitimate(true);
                                String illegal_action = action_data[0] + " " + action_data[1] + " null " + action_data[3] + " null";
                                player.setInfo(illegal_action);
                                players.remove(player.getId());
                                illegal_players.put(player.getId(), player);
                            }
                        }
                        case "BET" -> {
                            String[] match_info = match_data.get(action_data[2]);
                            if (!player.handleBet(Integer.parseInt(action_data[3]), match_info, action_data[4])) {
                                players.get(player_id).setIllegitimate(true);
                                String illegal_action = action_data[0] + " " + action_data[1] + " " + action_data[2] + " " + action_data[3] + " " + action_data[4];
                                player.setInfo(illegal_action);
                                players.remove(player.getId());
                                illegal_players.put(player.getId(), player);
                            }
                        }
                    }
                }


                line = bufferedReader.readLine();
            }
            if (!player.isIllegitimate()) {
                players.put(player_id, player);
            }
        }

        SortedMap[] return_statement = {players, illegal_players};


        return return_statement;
    }


    /**
     * Writes data into file, with first block being legitimate players' data, second block with illegitimate players'
     * data and the third block being casino balance change after the analyzed actions.
     * @param file_path File path to the file where the info is to be written.
     * @param players Sorted map of legitimate players
     * @param illegal_players Sorted map of illegitimate players.
     * @throws IOException If there are errors with the file path.
     */
    public static void writeResultsToFile(String file_path, SortedMap<String, Player> players, SortedMap<String, Player> illegal_players) throws IOException {
        int casino_balance = 0;

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file_path))) {
            if (players.size() == 0) {
                bufferedWriter.write("\n");
            } else {
                for (String s : players.keySet()) {
                    Player player = players.get(s);
                    casino_balance -= player.getBalance() - player.getDeposits();
                    bufferedWriter.write(players.get(s).toString() + "\n");
                }
            }

            bufferedWriter.write("\n");

            if (illegal_players.size() == 0) {
                bufferedWriter.write("\n");
            } else {
                for (String s : illegal_players.keySet()) {
                    Player player = illegal_players.get(s);
                    bufferedWriter.write(player.toString() + "\n");
                }
            }

            bufferedWriter.write("\n");

            bufferedWriter.write(Integer.toString(casino_balance));
        } catch (IOException exception) {
            throw new IOException("Error with file path.");
        }
    }
}
