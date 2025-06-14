/**
 * N-dimensional buffers.
 *
 * <p>Multidimensional data structures in Java are organized non-sequentially across fragmented
 * regions of memory, which differs from the continuous block storage method used in languages like
 * C and C++. See <a href="https://en.wikipedia.org/wiki/Row-_and_column-major_order">Row- and
 * column-major order</a> for more details (<b>ndbuffers</b> are using row-major storage order).
 *
 * <p><b>ndbuffers</b> enable processing sequential data stored within one-dimensional Java arrays
 * or {@link java.nio.ByteBuffer} as if they were multidimensional arrays. Example:
 *
 * {@snippet lang="java" :
 * var oneDimensionalArray = new double[] {
 *      1, 2, 3, 4,
 *      5, 6, 7, 8,
 *      9, 10, 11, 12,
 *      13, 14, 15, 16
 * };
 * var mx = new Matrix4d(oneDimensionalArray);
 * Assertions.assertEquals(8, mx.get(1, 3));
 * }
 *
 * <b>ndbuffers</b> do not copy or allocate any memory instead they map directly to the data which
 * is provided to them. This means that any changes made through <b>ndbuffers</b> will be reflected
 * in the original data:
 *
 * {@snippet lang="java" :
 * mx.set(1, 3, -1);
 * Assertions.assertEquals(-1, oneDimensionalArray[7]);
 * }
 *
 * <p>Another issue with Java multidimensional arrays is that they can work only with data in Java
 * heap memory and does not support native memory. <b>ndbuffers</b> support both: data stored inside
 * Java heap memory and native memory. This allow users to easily map <b>ndbuffers</b> to data
 * created in <a href="https://opencv.org/">OpenCV</a>, <a
 * href="https://eigen.tuxfamily.org/index.php?title=Main_Page">eigen</a> or other native libraries
 * without copying such data to Java heap:
 *
 * {@snippet lang="java" :
 * var opencvMat = Mat.ones(new int[] {4, 4}, CvType.CV_64F);
 * var segment =
 *      MemorySegment.ofAddress(opencvMat.dataAddr())
 *          .reinterpret(opencvMat.total() * Double.BYTES);
 * mx = new Matrix4d(segment.asByteBuffer().order(ByteOrder.nativeOrder()).asDoubleBuffer());
 * Assertions.assertEquals("""
 *      { "data" : [
 *       1, 1, 1, 1,
 *       1, 1, 1, 1,
 *       1, 1, 1, 1,
 *       1, 1, 1, 1
 *      ] }""", mx.toString());
 * mx.set(1, 3, -1.5);
 * Assertions.assertEquals("""
 *      [1, 1, 1, 1;
 *       1, 1, 1, -1.5;
 *       1, 1, 1, 1;
 *       1, 1, 1, 1]""", opencvMat.dump());
 * }
 *
 * <h2>byte[] vs ByteBuffer vs MemorySegment</h2>
 *
 * <p>Here is a comparison of {@link java.nio.ByteBuffer} which is used by <b>ndbuffers</b> with
 * other different Java memory options.
 *
 * <h3>Map data to different primitive types double/int/...</h3>
 *
 * <ul>
 *   <li>byte[] - none
 *   <li>{@link java.nio.ByteBuffer} - {@link java.nio.ByteBuffer#asDoubleBuffer()}, {@link
 *       java.nio.ByteBuffer#asIntBuffer()}, ...
 *   <li>{@link java.lang.foreign.MemorySegment} - {@link
 *       java.lang.foreign.MemorySegment#get(java.lang.foreign.ValueLayout.OfDouble, long)}, {@link
 *       java.lang.foreign.MemorySegment#get(java.lang.foreign.ValueLayout.OfInt, long)}, ...
 * </ul>
 *
 * <h3>Can be constructed from different data sources</h3>
 *
 * <ul>
 *   <li>byte[] - only from Java heap
 *   <li>ByteBuffer:
 *       <ul>
 *         <li>from Java heap - {@link java.nio.ByteBuffer#wrap(byte[])}
 *         <li>from native memory - {@link java.lang.foreign.MemorySegment#asByteBuffer()}
 *       </ul>
 *   <li>MemorySegment - only from native memory
 * </ul>
 *
 * @author lambdaprime intid@protonmail.com
 */
package id.matcv.types.ndbuffers;
