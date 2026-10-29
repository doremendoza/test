import java.util.LinkedHashMap;
import java.util.Map;

/** Demonstration: uses student CleanupPlanner methods after they are implemented. */
public class CleanupPlannerDemo {
    /** Utility class; no instances. */
    private CleanupPlannerDemo() { }
    /** Demo driver.
	@param args ignored
     */
    public static void main(String[] args) {
        Map<String, Integer> litter = new LinkedHashMap<>();
        Map<String, Boolean> water = new LinkedHashMap<>();
        litter.put("Creek Trail", 7); water.put("Creek Trail", true);
        litter.put("Library Plaza", 12); water.put("Library Plaza", false);
        litter.put("North Park", 4); water.put("North Park", true);
        litter.put("Station Square", 9); water.put("Station Square", false);
        litter.put("Willow Path", 11); water.put("Willow Path", true);
        litter.put("Community Garden", 5); water.put("Community Garden", false);
        System.out.println("Volunteer 17 team (4 teams): " + CleanupPlanner.teamFor(17, 4));
        System.out.println("Volunteers 8 and 20 same team? " + CleanupPlanner.sameTeam(8, 20, 4));
        System.out.println("Team 2 (15 volunteers, 4 teams): " + CleanupPlanner.teamMembers(15, 2, 4));
        System.out.println("Priority locations: " + CleanupPlanner.priorityLocations(litter, water));
        System.out.println("Priority count: " + CleanupPlanner.countPriorityLocations(litter, water));
    }
}
