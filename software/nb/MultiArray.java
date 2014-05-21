package com.rc.util;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;

import com.rc.matrix.VectorUtils;


public class MultiArray implements Cloneable, Serializable {

    //~ CONSTANT(S) ----------------------------------------------------------

    private static final long serialVersionUID = 4223563205908751611L;
    public static double DELTA = Math.pow(10.0, -10.0);
    

    //~ ATTRIBUTE(S) ---------------------------------------------------------

    // Actual data
    protected double[] data;

    // the length of this array is the rank
    protected int[] sizes;

    // factors for computing positions
    protected int[] stride;

    
    //~ CONSTRUCTOR(S) -------------------------------------------------------
    
    // Protected constructor for serialization
    protected MultiArray() { }

    public MultiArray(int[] szs) {
        int rank = szs.length;
        sizes = szs.clone();
        stride = new int[rank];
        int product = (rank == 0? 0 : 1);
        for (int i = rank - 1; i >= 0; --i) {
            stride[i] = product;
            product *= sizes[i];
        }
        data = new double[product];
    }

    public MultiArray(int[] szs, double val) {
        this(szs);
        if (data.length == 0) return;
        int[] index = new int[sizes.length];
        while (index != null) {
            set(index, val);
            index = incr(index);
        }
    }

    public MultiArray(int[] szs, double[] vals) {
        this(szs);
        if (data.length == 0 || vals == null) return;
        int[] index = new int[sizes.length];
        for (int i = 0; index != null; i++, index = incr(index)) set(index, vals[i]);
    }

    public MultiArray(double[] vals) {
        this(new int[] { vals.length });
        if (data.length == 0) return;
        int[] index = new int[sizes.length];
        for (int i0 = 0; i0 < sizes[0]; i0++) {
            set(index, vals[i0]);
            index = incr(index);
        }
    }

    public MultiArray(double[][] vals) {
        this(new int[] { vals.length, vals[0].length });
        if (data.length == 0) return;
        int[] index = new int[sizes.length];
        for (int i0 = 0; i0 < sizes[0]; i0++) {
            for (int i1 = 0; i1 < sizes[1]; i1++) {
                set(index, vals[i0][i1]);
                index = incr(index);
            }
        }
    }

    public MultiArray(double[][][] vals) {
        this(new int[] { vals.length, vals[0].length });
        if (data.length == 0) return;
        int[] index = new int[sizes.length];
        for (int i0 = 0; i0 < sizes[0]; i0++) {
            for (int i1 = 0; i1 < sizes[1]; i1++) {
                for (int i2 = 0; i2 < sizes[1]; i2++) {
                    set(index, vals[i0][i1][i2]);
                    index = incr(index);
                }
            }
        }
    }

    public MultiArray(double[][][][] vals) {
        this(new int[] { vals.length, vals[0].length });
        if (data.length == 0) return;
        int[] index = new int[sizes.length];
        for (int i0 = 0; i0 < sizes[0]; i0++) {
            for (int i1 = 0; i1 < sizes[1]; i1++) {
                for (int i2 = 0; i2 < sizes[1]; i2++) {
                    for (int i3 = 0; i3 < sizes[1]; i3++) {
                        set(index, vals[i0][i1][i2][i3]);
                        index = incr(index);
                    }
                }
            }
        }
    }

    public MultiArray(double[][][][][] vals) {
        this(new int[] { vals.length, vals[0].length });
        if (data.length == 0) return;
        int[] index = new int[sizes.length];
        for (int i0 = 0; i0 < sizes[0]; i0++) {
            for (int i1 = 0; i1 < sizes[1]; i1++) {
                for (int i2 = 0; i2 < sizes[1]; i2++) {
                    for (int i3 = 0; i3 < sizes[1]; i3++) {
                        for (int i4 = 0; i4 < sizes[1]; i4++) {
                            set(index, vals[i0][i1][i2][i3][i4]);
                            index = incr(index);
                        }
                    }
                }
            }
        }
    }

    public MultiArray(double[][][][][][] vals) {
        this(new int[] { vals.length, vals[0].length });
        if (data.length == 0) return;
        int[] index = new int[sizes.length];
        for (int i0 = 0; i0 < sizes[0]; i0++) {
            for (int i1 = 0; i1 < sizes[1]; i1++) {
                for (int i2 = 0; i2 < sizes[1]; i2++) {
                    for (int i3 = 0; i3 < sizes[1]; i3++) {
                        for (int i4 = 0; i4 < sizes[1]; i4++) {
                            for (int i5 = 0; i5 < sizes[1]; i5++) {
                                set(index, vals[i0][i1][i2][i3][i4][i5]);
                                index = incr(index);
                            }
                        }
                    }
                }
            }
        }
    }

    public MultiArray(double[][][][][][][] vals) {
        this(new int[] { vals.length, vals[0].length });
        if (data.length == 0) return;
        int[] index = new int[sizes.length];
        for (int i0 = 0; i0 < sizes[0]; i0++) {
            for (int i1 = 0; i1 < sizes[1]; i1++) {
                for (int i2 = 0; i2 < sizes[1]; i2++) {
                    for (int i3 = 0; i3 < sizes[1]; i3++) {
                        for (int i4 = 0; i4 < sizes[1]; i4++) {
                            for (int i5 = 0; i5 < sizes[1]; i5++) {
                                for (int i6 = 0; i6 < sizes[1]; i6++) {
                                    set(index, vals[i0][i1][i2][i3][i4][i5][i6]);
                                    index = incr(index);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    
    //~ METHOD(S) ------------------------------------------------------------

    public int capacity() { return data.length; }

    public Object clone() { return new MultiArray(sizes, data); }
    
    public boolean equals(Object obj) { return (obj instanceof MultiArray) && equals((MultiArray)obj, DELTA); }
    public boolean equals(MultiArray obj, double delta) { 
        if (!Arrays.equals(sizes, obj.sizes)) return false;
        return distance(data, obj.data) < delta;
    }
    
    public double get(int[] index) { return data[offset(index)]; }
    
    public int[] getSizes() { return sizes.clone(); }

    public int getSize(int dim) { return sizes[dim]; }

    public int dimensionCount() { return sizes.length; }

    public boolean isInside(int[] index) {
        for (int i = 0; i < sizes.length; i++) {
            if (index[i] >= sizes[i]) return false;
        }
        return true;
    }

    // public double plusEquals(int[] index, double d) { return (data[offset(index)] += d); }
    // public double minusEquals(int[] index, double d) { return (data[offset(index)] -= d); }
    
    public int[] incr(int[] index) {
        int digit = sizes.length - 1;
        while (digit >= 0) {
            index[digit]++;
            if (index[digit] < sizes[digit]) break; // normal exit
            index[digit] = 0; // else, carry
            digit--;
        }
        return digit == -1? null : index;
    }

    public int[] decr(int[] index) {
        int digit = sizes.length - 1;
        while (digit >= 0) {
            index[digit]--;
            if (index[digit] >= 0) break; // normal exit
            index[digit] = sizes[digit] - 1; // else, carry
            digit++;
        }
        return digit == -1? null : index;
    }
    
    public void resize(int[] szs, double init) {
        MultiArray a = new MultiArray(szs, init);
        
        // If there is data in object move it to 'a'
        if (data.length > 0) {
            int[] index = new int[sizes.length];
            for (int i = 0; index != null; i++, index = incr(index)) {
                if (!a.isInside(index)) continue;
                a.set(index, get(index));
            }
        }
        
        // Pick everything from 'a' and leave it to be garbage collected
        data = a.data;
        sizes = a.sizes;
        stride = a.stride;
    }
    
    public double set(int[] index, double val) { return data[offset(index)] = val; }

    public String toString() { return toString(8, 2, true); }
    public String toString(int w, int d, boolean lastDimensionAsArray) {
        DecimalFormat format = new DecimalFormat();
        format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        format.setMinimumIntegerDigits(1);
        format.setMaximumFractionDigits(d);
        format.setMinimumFractionDigits(d);
        format.setGroupingUsed(false);

        StringBuffer sb = new StringBuffer();

        int[] index = new int[sizes.length];
        for (int i = 0; index != null; i++, index = incr(index)) {
            String s = format.format(get(index));
            if (lastDimensionAsArray) {
                int lastIndex = index[index.length - 1];
                if (lastIndex == 0) sb.append(ArrayUtils.toString(index, 0, index.length - 1)).append(": {");
                sb.append(lastIndex == 0? "" : ", ").append(s);
                if (lastIndex == sizes[index.length - 1] - 1) sb.append("}\n");
            }
            else {
                sb.append(Arrays.toString(index)).append(": ").append(s).append("\n");
            }
        }
        return sb.toString();
    }


    //~ DIRECT METHOD(S) -----------------------------------------------------

    public double get(int i0) { return data[offset(i0)]; }
    public double get(int i0, int i1) { return data[offset(i0,i1)]; }
    public double get(int i0, int i1, int i2) { return data[offset(i0,i1,i2)]; }
    public double get(int i0, int i1, int i2, int i3) { return data[offset(i0,i1,i2,i3)]; }
    public double get(int i0, int i1, int i2, int i3, int i4) { return data[offset(i0,i1,i2,i3,i4)]; }
    public double get(int i0, int i1, int i2, int i3, int i4, int i5) { return data[offset(i0,i1,i2,i3,i4,i5)]; }
    public double get(int i0, int i1, int i2, int i3, int i4, int i5, int i6) { return data[offset(i0,i1,i2,i3,i4,i5,i6)]; }

    public double set(int i0, double val) { return data[offset(i0)] = val; }
    public double set(int i0, int i1, double val) { return data[offset(i0,i1)] = val; }
    public double set(int i0, int i1, int i2, double val) { return data[offset(i0,i1,i2)] = val; }
    public double set(int i0, int i1, int i2, int i3, double val) { return data[offset(i0,i1,i2,i3)] = val; }
    public double set(int i0, int i1, int i2, int i3, int i4, double val) { return data[offset(i0,i1,i2,i3,i4)] = val; }
    public double set(int i0, int i1, int i2, int i3, int i4, int i5, double val) { return data[offset(i0,i1,i2,i3,i4,i5)] = val; }
    public double set(int i0, int i1, int i2, int i3, int i4, int i5, int i6, double val) { return data[offset(i0,i1,i2,i3,i4,i5,i6)] = val; }

    
    //~ MATRIX ARITHMETIC METHOD(S) ------------------------------------------
    
    public MultiArray uminus() { return new MultiArray(sizes, VectorUtils.uminus(data, data.length)); }
    
    public MultiArray plus(double s) { MultiArray R = new MultiArray(sizes); plus(this,s,R); return R; }
    public MultiArray plusEquals(double s) { plus(this,s,this); return this; }
    public static MultiArray plus(MultiArray A, double s, MultiArray R) { VectorUtils.plus(A.data,A.data.length,s,R.data,R.data.length); return R; }
    
    public MultiArray plus(MultiArray M) { MultiArray R = new MultiArray(sizes); plus(this,M,R); return R; }
    public MultiArray plusEquals(MultiArray M) { plus(this,M,this); return this; }
    public static MultiArray plus(MultiArray A, MultiArray B, MultiArray R) { VectorUtils.plus(A.data,A.data.length,B.data,B.data.length,R.data,R.data.length); return R; }
    
    public MultiArray minus(double s) { MultiArray R = new MultiArray(sizes); minus(this,s,R); return R; }
    public MultiArray minusEquals(double s) { minus(this,s,this); return this; }
    public static MultiArray minus(MultiArray A, double s, MultiArray R) { VectorUtils.minus(A.data,A.data.length,s,R.data,R.data.length); return R; }

    public MultiArray minus(MultiArray M) { MultiArray R = new MultiArray(sizes); minus(this,M,R); return R; }
    public MultiArray minusEquals(MultiArray M) { minus(this,M,this); return this; }
    public static MultiArray minus(MultiArray A, MultiArray B, MultiArray R) { VectorUtils.minus(A.data,A.data.length,B.data,B.data.length,R.data,R.data.length); return R; }


    //~ HELPER(S) ------------------------------------------------------------

    static private double distance(double[] a1, double[] a2) {
        double d, ret = 0.0;
        int n = a1.length;
        for (int i = 0; i < n; i++) {
            d = a1[i] - a2[i];
            ret += (d * d);
        }
        return Math.sqrt(ret);
    }
    
    protected int offset(int[] index) {
        if (index.length != sizes.length) throw new IllegalArgumentException("Wrong number of indices");
        int offset = 0;
        for (int i = 0; i < sizes.length; ++i) {
            if (index[i] < 0 || index[i] >= sizes[i]) throw new ArrayIndexOutOfBoundsException();
            offset += stride[i] * index[i];
        }
        return offset;
    }
    
    public int offset(int i0) { 
        if (sizes.length != 1) throw new IllegalArgumentException("Wrong number of indices");
        if (i0 < 0 || i0 >= sizes[0]) throw new ArrayIndexOutOfBoundsException();
        return stride[0] * i0;
    }
    
    public int offset(int i0, int i1) { 
        if (sizes.length != 2) throw new IllegalArgumentException("Wrong number of indices");
        if (i0 < 0 || i0 >= sizes[0] || i1 < 0 || i1 >= sizes[1]) throw new ArrayIndexOutOfBoundsException();
        return stride[0] * i0 + stride[1] * i1;
    }
    
    public int offset(int i0, int i1, int i2) { 
        if (sizes.length != 3) throw new IllegalArgumentException("Wrong number of indices");
        if (i0 < 0 || i0 >= sizes[0] || i1 < 0 || i1 >= sizes[1] || i2 < 0 || i2 >= sizes[2]) throw new ArrayIndexOutOfBoundsException();
        return stride[0] * i0 + stride[1] * i1 + stride[2] * i2;
    }
    
    public int offset(int i0, int i1, int i2, int i3) { 
        if (sizes.length != 6) throw new IllegalArgumentException("Wrong number of indices");
        if (i0 < 0 || i0 >= sizes[0] || i1 < 0 || i1 >= sizes[1] || i2 < 0 || i2 >= sizes[1] || i3 < 0 || i3 >= sizes[3]) throw new ArrayIndexOutOfBoundsException();
        return stride[0] * i0 + stride[1] * i1 + stride[2] * i2 + stride[3] * i3;
    }
    
    public int offset(int i0, int i1, int i2, int i3, int i4) { 
        if (sizes.length != 6) throw new IllegalArgumentException("Wrong number of indices");
        if (i0 < 0 || i0 >= sizes[0] || i1 < 0 || i1 >= sizes[1] || i2 < 0 || i2 >= sizes[1] || i3 < 0 || i3 >= sizes[3] || i4 < 0 || i1 >= sizes[4]) throw new ArrayIndexOutOfBoundsException();
        return stride[0] * i0 + stride[1] * i1 + stride[2] * i2 + stride[3] * i3 + stride[4] * i4;
    }
    
    public int offset(int i0, int i1, int i2, int i3, int i4, int i5) { 
        if (sizes.length != 6) throw new IllegalArgumentException("Wrong number of indices");
        if (i0 < 0 || i0 >= sizes[0] || i1 < 0 || i1 >= sizes[1] || i2 < 0 || i2 >= sizes[1] || i3 < 0 || i3 >= sizes[3] || i4 < 0 || i1 >= sizes[4] || i5 < 0 || i5 >= sizes[5]) throw new ArrayIndexOutOfBoundsException();
        return stride[0] * i0 + stride[1] * i1 + stride[2] * i2 + stride[3] * i3 + stride[4] * i4 + stride[5] * i5;
    }
    
    public int offset(int i0, int i1, int i2, int i3, int i4, int i5, int i6) { 
        if (sizes.length != 6) throw new IllegalArgumentException("Wrong number of indices");
        if (i0 < 0 || i0 >= sizes[0] || i1 < 0 || i1 >= sizes[1] || i2 < 0 || i2 >= sizes[1] || i3 < 0 || i3 >= sizes[3] || i4 < 0 || i1 >= sizes[4] || i5 < 0 || i5 >= sizes[5] || i6 < 0 || i6 >= sizes[6]) throw new ArrayIndexOutOfBoundsException();
        return stride[0] * i0 + stride[1] * i1 + stride[2] * i2 + stride[3] * i3 + stride[4] * i4 + stride[5] * i5 + stride[6] * i6;
    }

    /*
    private static void checkMultyArrayDimensions(MultiArray A, MultiArray B) {
        if (!Arrays.equals(A.sizes, B.sizes)) { 
            throw new IllegalArgumentException("MultiArray dimensions must agree ("+A.sizes+" vs "+B.sizes+").");
        }
    }
    */
}
