/**
 * Converters between different data types.
 *
 * <h2>Methods naming convention
 *
 * <ul>
 *   <li>toXXX, copyToXXX - create a full copy. Example {@link
 *       id.matcv.converters.MatConverters#copyToMat(byte...)}
 *   <li>mapToXXX - create a view to the underlying data, no data is copied. Example {@link
 *       id.matcv.converters.MatConverters#mapToListOfMat(java.util.List)}
 * </ul>
 */
package id.matcv.converters;
