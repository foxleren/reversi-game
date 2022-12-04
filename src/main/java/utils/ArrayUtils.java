package utils;

public class ArrayUtils {
    public static int[][] copyIntArrayByValue(int[][] arr) {
        var tmp = new int[arr.length][arr.length];
        for (int i = 0; i < arr.length; i++) {
            System.arraycopy(arr[i], 0, tmp[i], 0, arr[i].length);
        }

        return tmp;
    }

    public static float[][] copyFloatArrayByValue(float[][] array) {
        var tmp = new float[array.length][array.length];
        for (int i = 0; i < array.length; i++) {
            System.arraycopy(array[i], 0, tmp[i], 0, array[i].length);
        }

        return tmp;
    }

    public static float findMaxInFloatArray(float[][] arr) {
        float max = -1000;
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                if (arr[i][j] > max) {
                    max = arr[i][j];
                }
            }
        }

        return max;
    }
}
