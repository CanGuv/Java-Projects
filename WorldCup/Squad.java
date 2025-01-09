import java.util.ArrayList;

public class Squad {
    private String teamName;
    private ArrayList<Player> players;
    private Manager manager;

    Squad(String teamName, Manager manager){
        this.teamName = teamName;
        this.manager = manager;
        players = new ArrayList<>();
    }

    public void addPlayer(Player p){
        players.add(p);
    }

    //get a player object by surname
    public Player getPlayer(String surname){
        for(Player p: players){
            if(p.getSurname().equals(surname)){
                return p;
            }
        }
        return null;
    }

    //get a player object by index
    public Player getPlayer(int n){
        return players.get(n);
    }

    public String getTeamName() {
        return teamName;
    }

    public Manager getManager() {
        return manager;
    }

    @Override
    public String toString() {
        StringBuilder squad = new StringBuilder();

        for(Player p : players){
            squad.append(p.getFirstName()).append(" ").append(p.getSurname())
                    .append(" ").append("(").append(p.getPosition()).append(")").append("\n");
        }
        return squad.toString();
    }
}
