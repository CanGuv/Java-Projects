public class Team extends Squad{

    Team(String teamName, Manager manager) {
        super(teamName, manager);
    }

    @Override
    public String toString() {
        int a = 0;
        StringBuilder result = new StringBuilder();
        result.append("Team:").append(getTeamName()).append("\n")
                .append("Manager: ").append(getManager().getFirstName()).append(getManager().getSurname()).append("\n")
                .append("Formation: ").append(getManager().getFavouredFormation()).append("\n")
                .append("Players:\n");

        while(a < 11){
                result.append(getPlayer(a)).append("\n");
                a++;
        }

        return result.toString();
    }
}
