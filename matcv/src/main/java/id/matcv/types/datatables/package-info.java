/**
 * Tabular data types.
 *
 * <p>{@link id.matcv.types.datatables.DataTable} models tables with fixed number of columns. Each
 * column inside {@link id.matcv.types.datatables.DataTable} represented as a {@link java.util.List}
 *
 * <p>Imagine method {@code m(List<Employee> employee, List<Short> age)} which works with two
 * "interconnected" lists, such that age of "employee[i]" is found in "age[i]". Because such lists
 * are "interconnected" then both of them always should have same size. Unfortunately, just by
 * looking at such method signature it is not possible to see that both lists are interconnected and
 * so users may try to call method "m" with lists of different sizes which can lead to undefined
 * behavior during runtime.
 *
 * <p>{@link id.matcv.types.datatables.DataTable} is an abstraction which helps to represent tabular
 * data. Such data is split into columns where all columns have equal size.
 *
 * <p>With {@link id.matcv.types.datatables.DataTable} we can declare method "m" like {@code
 * m(DataTable2<Employee, Short> dataTable)} which explicitly specifies that all lists inside method
 * "m" should always have same size.
 *
 * <p>When {@link id.matcv.types.datatables.DataTable} is created it always checks that size of all
 * lists inside it is the same. Otherwise {@link id.xfunction.PreconditionException} is thrown.
 *
 * <p>{@link id.matcv.types.datatables.DataTable} has following properties:
 *
 * <ul>
 *   <li>No null items
 *       <ul>
 *         <li>This constraint helps to avoid performing null checks every time any item "i" is
 *             accessed (ex. {@code colN().get(i)}). Doing null checks every time, increases amount
 *             of code, and it is easy to forget to perform, which can lead to {@link
 *             java.lang.NullPointerException}.
 *       </ul>
 *   <li>Size of all columns must be equal
 *       <ul>
 *         <li>For any two columns A, B following is true: colA().size() == colB().size(). This
 *             helps to guarantee that if "i" is a valid index in column A then it is also valid for
 *             column B and any other columns within the table. This means that colN().get(i) in
 *             such table will not cause {@link java.lang.ArrayIndexOutOfBoundsException}
 *       </ul>
 * </ul>
 *
 * <p>{@link id.matcv.types.datatables.DataTable} considerations:
 *
 * <ul>
 *   <li>Access to columns are done through colN() instead of col(N)
 *       <ul>
 *         <li>colN() notation is preferable as it provides compile time validation of the column
 *             index N.
 *       </ul>
 * </ul>
 *
 * @author lambdaprime intid@protonmail.com
 */
package id.matcv.types.datatables;
