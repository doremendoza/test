import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

/**
 * Tests for CleanupPlanner. No testing framework needed.
 *
 * <p>Each check runs on its own: if one method throws an exception or is not
 * implemented yet, the remaining checks still run. A check that runs longer
 * than 3 seconds is stopped and reported, so a loop that never ends
 * cannot hang the tests.</p>
 *
 * <p>Usage: {@code java CleanupPlannerTest}</p>
 */
public class CleanupPlannerTest {

    private static final long TIMEOUT_SECONDS = 3;
    private static final ExecutorService POOL = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    private static int checks = 0;
    private static int failures = 0;
    private static final List<String> notImplemented = new ArrayList<>();

    private CleanupPlannerTest() { }

    /**
     * Runs one check: calls {@code actual}, compares its result with {@code expected},
     * and reports a failure, an exception, or a timeout without stopping the other checks.
     */
    private static void check(String label, Object expected, Supplier<Object> actual) {
        checks++;
        Future<Object> future = POOL.submit(actual::get);
        try {
            Object result = future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (expected.equals(result)) {
                System.out.println("PASS  " + label);
            } else {
                failures++;
                System.out.println("FAIL  " + label + ": expected " + expected + ", got " + result);
            }
        } catch (TimeoutException e) {
            future.cancel(true);
            failures++;
            System.out.println("FAIL  " + label + ": did not finish within " + TIMEOUT_SECONDS
                    + " seconds. Does a loop never end?");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            failures++;
            System.out.println("FAIL  " + label + ": interrupted");
        } catch (ExecutionException e) {
            failures++;
            Throwable cause = e.getCause();
            String msg = String.valueOf(cause.getMessage());
            if (cause instanceof UnsupportedOperationException && msg.endsWith("not implemented")) {
                String method = msg.substring(0, msg.indexOf(' '));
                if (!notImplemented.contains(method)) {
                    notImplemented.add(method);
                }
                System.out.println("----  " + label + "  (" + method + " not implemented yet)");
            } else {
                System.out.println("ERROR " + label + ": " + cause + where(cause));
            }
        }
    }

    /** The line in CleanupPlanner.java where an exception happened, if known. */
    private static String where(Throwable t) {
        for (StackTraceElement e : t.getStackTrace()) {
            if (e.getClassName().equals("CleanupPlanner")) {
                return " (at CleanupPlanner.java line " + e.getLineNumber() + ")";
            }
        }
        return "";
    }

    private static Set<Integer> ints(Integer... values) { return new TreeSet<>(List.of(values)); }

    private static Set<String> names(String... values) { return new TreeSet<>(List.of(values)); }

    /**
     * Runs every check and prints a summary.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        check("teamFor 17,4", 1, () -> CleanupPlanner.teamFor(17,4));
        check("teamFor 0,5", 0, () -> CleanupPlanner.teamFor(0,5));
        check("teamFor 12,5", 2, () -> CleanupPlanner.teamFor(12,5));
        check("sameTeam true", true, () -> CleanupPlanner.sameTeam(8,20,4));
        check("sameTeam false", false, () -> CleanupPlanner.sameTeam(8,19,4));
        check("roster", ints(2,6,10,14), () -> CleanupPlanner.teamMembers(15,2,4));
        check("empty roster", ints(), () -> CleanupPlanner.teamMembers(2,3,4));
        check("no volunteers", ints(), () -> CleanupPlanner.teamMembers(0,0,4));
        check("priority 10", true, () -> CleanupPlanner.priorityCleanup(10,false));
        check("priority water 5", true, () -> CleanupPlanner.priorityCleanup(5,true));
        check("priority water 4", false, () -> CleanupPlanner.priorityCleanup(4,true));
        check("priority no water 9", false, () -> CleanupPlanner.priorityCleanup(9,false));
        Map<String,Integer> litter = Map.of("Creek Trail",7,"Library Plaza",12,"North Park",4,"Station Square",9,"Willow Path",11,"Community Garden",5);
        Map<String,Boolean> water = Map.of("Creek Trail",true,"Library Plaza",false,"North Park",true,"Station Square",false,"Willow Path",true,"Community Garden",false);
        check("priority set", names("Creek Trail","Library Plaza","Willow Path"), () -> CleanupPlanner.priorityLocations(litter,water));
        check("priority count", 3, () -> CleanupPlanner.countPriorityLocations(litter,water));
        check("empty survey", names(), () -> CleanupPlanner.priorityLocations(Map.of(),Map.of()));
        check("empty count", 0, () -> CleanupPlanner.countPriorityLocations(Map.of(),Map.of()));
        // Examples from the Javadoc comments in CleanupPlanner.java
        check("doc teamFor 7,3", 1, () -> CleanupPlanner.teamFor(7,3));
        check("doc teamFor 9,3", 0, () -> CleanupPlanner.teamFor(9,3));
        check("doc teamFor 2,3", 2, () -> CleanupPlanner.teamFor(2,3));
        check("doc teamFor 5,1", 0, () -> CleanupPlanner.teamFor(5,1));
        check("doc sameTeam 1,7,3", true, () -> CleanupPlanner.sameTeam(1,7,3));
        check("doc sameTeam 1,8,3", false, () -> CleanupPlanner.sameTeam(1,8,3));
        check("doc sameTeam 5,5,3", true, () -> CleanupPlanner.sameTeam(5,5,3));
        check("doc teamMembers 7,1,3", ints(1,4), () -> CleanupPlanner.teamMembers(7,1,3));
        check("doc teamMembers 10,0,3", ints(0,3,6,9), () -> CleanupPlanner.teamMembers(10,0,3));
        check("doc teamMembers 2,2,3", ints(), () -> CleanupPlanner.teamMembers(2,2,3));
        check("doc teamMembers 0,0,3", ints(), () -> CleanupPlanner.teamMembers(0,0,3));
        check("doc priority 15,false", true, () -> CleanupPlanner.priorityCleanup(15,false));
        check("doc priority 6,true", true, () -> CleanupPlanner.priorityCleanup(6,true));
        check("doc priority 6,false", false, () -> CleanupPlanner.priorityCleanup(6,false));
        check("doc priority 3,true", false, () -> CleanupPlanner.priorityCleanup(3,true));
        Map<String,Integer> docLitter = Map.of("Bus Stop",10,"Pond Edge",5,"Parking Lot",8,"Riverbank",2);
        Map<String,Boolean> docWater = Map.of("Bus Stop",false,"Pond Edge",true,"Parking Lot",false,"Riverbank",true);
        check("doc priority set", names("Bus Stop","Pond Edge"), () -> CleanupPlanner.priorityLocations(docLitter,docWater));
        check("doc priority count", 2, () -> CleanupPlanner.countPriorityLocations(docLitter,docWater));
        if (!notImplemented.isEmpty()) {
            System.out.println("Not implemented yet: " + String.join(", ", notImplemented)
                    + " (their checks are counted as failures)");
        }
        System.out.println("Checks: " + checks + "; failures: " + failures);
        if (failures != 0) {
            System.exit(1);
        }
    }
}
