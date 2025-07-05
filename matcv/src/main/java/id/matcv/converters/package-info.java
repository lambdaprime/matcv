/**
 * Converters between different data types.
 *
 * <h2>Converters separation
 *
 * <p>Converters organized in classes based on destination type to which type conversion happens.
 * For example, all methods which convert to {@link id.ndbuffers.NdBuffer} are gathered inside
 * {@link id.matcv.converters.ConvertersToNdBuffers}.
 *
 * <p>This helps to prevent confusion for methods which could belong to different converters. For
 * example, it may be not clear if method like "NdBuffer toNdBuffer(Mat m)" should belong to Mat
 * (OpenCv) converters or NdBuffer converters. But using destination as a criteria such converter
 * should belong to NdBuffer converters.
 *
 * <h2>Methods naming convention
 *
 * <ul>
 *   <li>toXXX, copyToXXX - create a full copy. Example {@link
 *       id.matcv.converters.ConvertersToOpenCv#copyToMat(byte...)}
 *   <li>mapToXXX - create a view to the underlying data, no data is copied. Example {@link
 *       id.matcv.converters.ConvertersToOpenCv#mapToListOfMat(java.util.List)}
 * </ul>
 */
package id.matcv.converters;
