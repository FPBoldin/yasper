package experiments.calcite;

/**
 * Created by riccardo on 09/09/2017.
 */

/**
 * Object that will be used via reflection to create the "emps" table.
 */
public class Engineer {
    public final int empid;
    public final String name;

    public Engineer(int empid, String name) {
        this.empid = empid;
        this.name = name;
    }
}