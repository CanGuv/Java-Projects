import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    public static Squad[] squads = new Squad[32];
    public static HashMap<Squad, Integer> squadPoints = new HashMap<>();

    public static void main(String[] args){

        File playersFile = new File("Players.csv");
        File managersFile = new File("Managers.csv");

        ArrayList<Player> playersList = new ArrayList<>();
        ArrayList<Manager> managerList = new ArrayList<>();

        try{

            Scanner playerScanner = new Scanner(playersFile);
            Scanner managerScanner = new Scanner(managersFile);

            if (playerScanner.hasNextLine() && managerScanner.hasNextLine()) {
                playerScanner.nextLine();
                managerScanner.nextLine();
            }

            while(playerScanner.hasNextLine()){
                String line = playerScanner.nextLine();
                String[] values = line.split(",");

                Player player = new Player(values[0],values[1],values[2],values[3],Double.parseDouble(values[4]),Double.parseDouble(values[5]),Double.parseDouble(values[6]),Double.parseDouble(values[7]),Double.parseDouble(values[8]),Double.parseDouble(values[9]),Double.parseDouble(values[10]),Double.parseDouble(values[11]),Double.parseDouble(values[12]),Double.parseDouble(values[13]));
                playersList.add(player);
            }

            while(managerScanner.hasNextLine()){
                String line = managerScanner.nextLine();
                String values[] = line.split(",");

                Manager manager = new Manager(values[0],values[1],values[2],values[3],Double.parseDouble(values[4]),Double.parseDouble(values[5]),Double.parseDouble(values[6]),Double.parseDouble(values[7]));
                managerList.add(manager);
            }

            for(int i = 0; i < managerList.size(); i++){
                Squad squad = new Squad(managerList.get(i).getTeam(),managerList.get(i));
                squads[i] = squad;
                for (Player player : playersList) {
                    if (managerList.get(i).getTeam().equals(player.getTeam())) {
                        squad.addPlayer(player);
                    }
                }
            }

        }catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        for(Squad s : squads){
            System.out.println(getTeam(s));
        }

        runTournament();
    }

    public static Team getTeam(Squad s){
        Team t = new Team(s.getTeamName(), s.getManager());
        String[] formation = s.getManager().getFavouredFormation().split("-");

        HashMap<Player, Double> playerRating = new HashMap<>();
        for(int i = 0; i < 26; i++){
            Player p = s.getPlayer(i);
            double[] attributes = {s.getPlayer(i).getChanceCreation(),s.getPlayer(i).getAggression(),s.getPlayer(i).getDefensiveness(),s.getPlayer(i).getDribbling(),s.getPlayer(i).getFitness(),s.getPlayer(i).getOffsideAdherence(),s.getPlayer(i).getPassingAccuracy(),s.getPlayer(i).getPositioning(),s.getPlayer(i).getShotAccuracy(),s.getPlayer(i).getShotFrequency()};
            double average = Arrays.stream(attributes).average().orElse(0);
            playerRating.put(p,average);
        }

        List<Map.Entry<Player, Double>> squadPlayers = new ArrayList<>(playerRating.entrySet());
        squadPlayers.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        int keeperCount = 0;
        int defenderCount = 0;
        int midfielderCount = 0;
        int attackerCount = 0;

        for(Map.Entry<Player, Double> entry : squadPlayers){
            Player p = entry.getKey();

            if("Goal Keeper".equalsIgnoreCase(p.getPosition().trim()) && keeperCount < 1){
                t.addPlayer(p);
                keeperCount++;
            }

            if("Defender".equalsIgnoreCase(p.getPosition().trim()) && defenderCount < Integer.parseInt(formation[0])){
                t.addPlayer(p);
                defenderCount++;
            }

            if("Midfielder".equalsIgnoreCase(p.getPosition().trim()) && midfielderCount < Integer.parseInt(formation[1])){
                t.addPlayer(p);
                midfielderCount++;
            }

            if("Forward".equalsIgnoreCase(p.getPosition().trim()) && attackerCount < Integer.parseInt(formation[2])){
                t.addPlayer(p);
                attackerCount++;
            }
        }
        return t;
    }

    public static void runTournament(){
        theWinner(semiFinal(quarterFinal(roundOf16(groupStageMatches(getGroups())))));
    }

    public static Squad[][] getGroups(){
        Squad[][] groups = new Squad[8][4];
        List<Squad> squadList = Arrays.asList(squads);
        Collections.shuffle(squadList);
        int index = 0;

        for(int i = 0; i < groups.length; i++){
            for(int j = 0; j < groups[0].length; j++){
                groups[i][j] = squadList.get(index);
                index++;
            }
        }
        return groups;
    }

    public static Squad[] groupStageMatches(Squad[][] groupTeams){
        Squad[] roundOf16 = new Squad[16];

        int index = 0;
        for (Squad[] groupTeam : groupTeams) {
            matchResult(groupTeam[0], groupTeam[1]);
            matchResult(groupTeam[0], groupTeam[2]);
            matchResult(groupTeam[0], groupTeam[3]);
            matchResult(groupTeam[1], groupTeam[2]);
            matchResult(groupTeam[1], groupTeam[3]);
            matchResult(groupTeam[2], groupTeam[3]);
            Arrays.sort(groupTeam, Comparator.comparingInt(team -> squadPoints.getOrDefault(team, 0)).reversed());

            roundOf16[index++] = groupTeam[0];
            roundOf16[index++] = groupTeam[1];
        }

        printGroupResults(groupTeams);
        return roundOf16;
    }

    public static Squad[] roundOf16(Squad[] squad){
        List<Squad> squadAsList = Arrays.asList(squad);
        Collections.shuffle(squadAsList);
        Squad[] quarterFinal = new Squad[8];

        int index = 0;

        System.out.println("Round of 16");

        for(int i = 0; i < squad.length; i+=2){
            quarterFinal[index] = knockoutResult(squadAsList.get(i),squadAsList.get(i + 1));
            index++;
        }

        return quarterFinal;
    }

    public static Squad[] quarterFinal(Squad[] squad){
        List<Squad> squadAsList = Arrays.asList(squad);
        Collections.shuffle(squadAsList);
        Squad[] semiFinal = new Squad[4];

        int index = 0;

        System.out.println("Quarter Final");

        for(int i = 0; i < squad.length; i+=2){
            semiFinal[index] = knockoutResult(squadAsList.get(i),squadAsList.get(i + 1));
            index++;
        }

        return semiFinal;
    }

    public static Squad[] semiFinal(Squad[] squad){
        List<Squad> squadAsList = Arrays.asList(squad);
        Collections.shuffle(squadAsList);
        Squad[] lastTwo = new Squad[2];

        int index = 0;

        System.out.println("Semi Final");

        for(int i = 0; i < squad.length; i+=2){
            lastTwo[index] = knockoutResult(squadAsList.get(i),squadAsList.get(i + 1));
            index++;
        }

        return lastTwo;
    }

    public static void theWinner(Squad[] squad){
        System.out.println("Final");

        System.out.println("World Cup Winner:"+ knockoutResult(squad[0],squad[1]).getTeamName());
    }

    public static Squad knockoutResult(Squad a, Squad b){
        Random random = new Random();
        boolean teamAWins = random.nextBoolean();

        System.out.println(a.getTeamName().trim() + " vs " + b.getTeamName().trim());
        if(teamAWins){
            System.out.println("Winner: " + a.getTeamName().trim());
            System.out.println();
            return a;
        }else {
            System.out.println("Winner: " + b.getTeamName().trim());
            System.out.println();
            return b;
        }
    }

    public static void matchResult(Squad a, Squad b){
        Random random = new Random();
        boolean teamAWins = random.nextBoolean();
        boolean resultIsDraw = random.nextBoolean();

        if(resultIsDraw){
            squadPoints.put(a,squadPoints.getOrDefault(a,0) + 1);
            squadPoints.put(b,squadPoints.getOrDefault(b,0) + 1);
        } else if (teamAWins) {
            squadPoints.put(a,squadPoints.getOrDefault(a,0) + 3);
        } else {
            squadPoints.put(b,squadPoints.getOrDefault(b,0) + 3);
        }
    }

    public static void printGroupResults(Squad[][] groupTeams) {
        int groupName = 65;

        for (Squad[] groupTeam : groupTeams) {
            System.out.println("Group " + (char) groupName + ":");
            for (Squad squad : groupTeam) {
                System.out.println(squad.getTeamName() + " - " + squadPoints.getOrDefault(squad, 0));
            }
            groupName++;
            System.out.println();
        }
    }
}