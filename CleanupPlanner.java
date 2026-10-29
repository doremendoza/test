import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * CS2240 Week 11: a cleanup planner for a neighborhood organization.
 * @author Dore Mendoza
 *
 * <p>The organization assigns volunteers to teams and decides which cleanup
 * locations need priority attention. Complete the six methods marked TODO.</p>
 *
 * <h2>Conventions</h2>
 * <ul>
 *   <li><b>Volunteers</b> are numbered 0, 1, 2, and so on. With
 *       {@code volunteerCount} volunteers, the volunteers are 0 through
 *       {@code volunteerCount - 1}.</li>
 *   <li><b>Teams</b> are also numbered starting at 0. With {@code teamCount} teams,
 *       the teams are 0 through {@code teamCount - 1}.</li>
 *   <li>Volunteers are assigned to teams <b>in turn</b>: volunteer 0 to team 0,
 *       volunteer 1 to team 1, and so on up to the last team; then the pattern
 *       starts again at team 0. For example, with four teams, volunteers 0, 4,
 *       and 8 all belong to team 0.</li>
 *   <li>A <b>location</b> is identified by its name, such as {@code "Creek Trail"}.
 *       Names are case-sensitive.</li>
 * </ul>
 *
 * <h2>Preconditions</h2>
 * Each method lists the conditions its inputs satisfy. You may assume they hold;
 * you do not need to check them or handle {@code null} or invalid input.
 *
 * <h2>Returned sets</h2>
 * Methods that return a {@code SortedSet} return a new set each time, for example a
 * {@code TreeSet}, never {@code null}. A {@code SortedSet} displays its elements in
 * natural order (numbers in increasing order, names alphabetically) regardless of
 * the order in which you added them.
 */
public class CleanupPlanner {

    /** Not instantiated: every method is static. */
    private CleanupPlanner() { }

    /**
     * Finds the team a volunteer is assigned to, when volunteers are assigned to
     * {@code teamCount} teams in turn.
     *
     * <p>Examples with three teams: volunteers 0, 3, 6, 9, ... are on team 0;
     * volunteers 1, 4, 7, ... are on team 1; volunteers 2, 5, 8, ... are on team 2.</p>
     * <ul>
     *   <li>{@code teamFor(7, 3)} returns {@code 1}.</li>
     *   <li>{@code teamFor(9, 3)} returns {@code 0}.</li>
     *   <li>{@code teamFor(2, 3)} returns {@code 2}: a volunteer numbered lower than
     *       the number of teams is on the team with the same number.</li>
     *   <li>{@code teamFor(5, 1)} returns {@code 0}: with one team, everyone is on team 0.</li>
     * </ul>
     *
     * @param volunteer the volunteer's number; {@code volunteer >= 0}
     * @param teamCount how many teams there are; {@code teamCount > 0}
     * @return the volunteer's team number, always from 0 through {@code teamCount - 1}
     */
    public static int teamFor(int volunteer, int teamCount) {
        // Remainder after division by teamCount is always 0 through teamCount - 1.
        return volunteer % teamCount;
    }

    /**
     * Decides whether two volunteers are assigned to the same team.
     *
     * <p>Examples with three teams:</p>
     * <ul>
     *   <li>{@code sameTeam(1, 7, 3)} returns {@code true}: both are on team 1.</li>
     *   <li>{@code sameTeam(1, 8, 3)} returns {@code false}: team 1 and team 2.</li>
     *   <li>{@code sameTeam(5, 5, 3)} returns {@code true}.</li>
     * </ul>
     *
     * @param volunteerA one volunteer's number; {@code volunteerA >= 0}
     * @param volunteerB another volunteer's number; {@code volunteerB >= 0}
     * @param teamCount  how many teams there are; {@code teamCount > 0}
     * @return {@code true} if {@link #teamFor teamFor} assigns both volunteers to the
     *         same team, {@code false} otherwise
     */
    public static boolean sameTeam(int volunteerA, int volunteerB, int teamCount) {
        return teamFor(volunteerA, teamCount) == teamFor(volunteerB, teamCount);
    }

    /**
     * Builds the roster of one team: every volunteer, among volunteers 0 through
     * {@code volunteerCount - 1}, whose team is {@code team}. In set-builder notation
     * the result is
     * <pre>
     *   { v : 0 &lt;= v &lt; volunteerCount  and  teamFor(v, teamCount) == team }
     * </pre>
     *
     * <p>Examples with three teams:</p>
     * <ul>
     *   <li>{@code teamMembers(7, 1, 3)} returns {@code [1, 4]}. The volunteers are
     *       0 through 6; volunteer 7 does not exist.</li>
     *   <li>{@code teamMembers(10, 0, 3)} returns {@code [0, 3, 6, 9]}.</li>
     *   <li>{@code teamMembers(2, 2, 3)} returns {@code []}: with only volunteers 0 and
     *       1, nobody is on team 2.</li>
     *   <li>{@code teamMembers(0, 0, 3)} returns {@code []}: there are no volunteers.</li>
     * </ul>
     *
     * @param volunteerCount how many volunteers there are, numbered 0 through
     *                       {@code volunteerCount - 1}; {@code volunteerCount >= 0}
     * @param team           the team whose roster is wanted;
     *                       {@code 0 <= team < teamCount}
     * @param teamCount      how many teams there are; {@code teamCount > 0}
     * @return a new sorted set of the numbers of all and only the volunteers on
     *         {@code team}; empty if the team has no members. Never {@code null}.
     */
    public static SortedSet<Integer> teamMembers(int volunteerCount, int team, int teamCount) {
        SortedSet<Integer> members = new TreeSet<>();
        for (int v = 0; v < volunteerCount; v++) {
            if (teamFor(v, teamCount) == team) {
                members.add(v);
            }
        }
        return members;
    }

    /**
     * Decides whether one location needs priority cleanup. The rule is: a location
     * is a priority if it has <b>at least 10</b> litter items, OR if it is near a
     * protected waterway AND has <b>at least 5</b> litter items. "At least" includes
     * the boundary value itself.
     *
     * <p>Examples:</p>
     * <ul>
     *   <li>{@code priorityCleanup(15, false)} returns {@code true}: 15 is at least 10.</li>
     *   <li>{@code priorityCleanup(6, true)} returns {@code true}: near a waterway with
     *       at least 5.</li>
     *   <li>{@code priorityCleanup(6, false)} returns {@code false}: fewer than 10 and
     *       not near a waterway.</li>
     *   <li>{@code priorityCleanup(3, true)} returns {@code false}: near a waterway but
     *       fewer than 5.</li>
     * </ul>
     *
     * @param litterCount  the number of litter items counted at the location;
     *                     {@code litterCount >= 0}
     * @param nearWaterway {@code true} if the location is near a protected waterway
     * @return {@code true} if the location meets the priority rule, {@code false} otherwise
     */
    public static boolean priorityCleanup(int litterCount, boolean nearWaterway) {
        return litterCount >= 10 || (nearWaterway && litterCount >= 5);
    }

    /**
     * Finds every surveyed location that needs priority cleanup, according to
     * {@link #priorityCleanup priorityCleanup}.
     *
     * <p>The survey is given as two maps with exactly the same location names as keys.
     * For a location named {@code loc}, {@code litterCounts.get(loc)} is its litter
     * count and {@code nearWaterway.get(loc)} says whether it is near a protected
     * waterway.</p>
     *
     * <p>Example survey:</p>
     * <pre>
     *   Location       Litter   Near waterway
     *   Bus Stop         10       no
     *   Pond Edge         5       yes
     *   Parking Lot       8       no
     *   Riverbank         2       yes
     * </pre>
     * <ul>
     *   <li>{@code priorityLocations(litter, water)} returns
     *       {@code [Bus Stop, Pond Edge]}.</li>
     *   <li>For an empty survey (two empty maps), it returns {@code []}.</li>
     * </ul>
     *
     * @param litterCounts each location's litter count; non-null, every value non-null
     *                     and {@code >= 0}
     * @param nearWaterway each location's waterway flag; non-null, every value non-null,
     *                     with exactly the same keys as {@code litterCounts}
     * @return a new sorted set of the names of all and only the priority locations;
     *         empty if none qualify. Never {@code null}. The maps are not changed.
     */
    public static SortedSet<String> priorityLocations(
            Map<String, Integer> litterCounts, Map<String, Boolean> nearWaterway) {
        SortedSet<String> result = new TreeSet<>();
        for (String location : litterCounts.keySet()) {
            if (priorityCleanup(litterCounts.get(location), nearWaterway.get(location))) {
                result.add(location);
            }
        }
        return result;
    }

    /**
     * Counts the surveyed locations that need priority cleanup. The result is the
     * cardinality of the set {@link #priorityLocations priorityLocations} returns.
     *
     * <p>Examples, using the example survey shown for {@code priorityLocations}:</p>
     * <ul>
     *   <li>{@code countPriorityLocations(litter, water)} returns {@code 2}.</li>
     *   <li>For an empty survey, it returns {@code 0}.</li>
     * </ul>
     *
     * @param litterCounts each location's litter count; same conditions as for
     *                     {@code priorityLocations}
     * @param nearWaterway each location's waterway flag; same conditions as for
     *                     {@code priorityLocations}
     * @return the number of priority locations, from 0 up to the number of
     *         locations surveyed
     */
    public static int countPriorityLocations(
            Map<String, Integer> litterCounts, Map<String, Boolean> nearWaterway) {
        return priorityLocations(litterCounts, nearWaterway).size();
    }
}
