package com.rumaruka.powercraft.api;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class PCMathHelper {

    private static int[] multiplyDeBruijnBitPosition = {0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};

    public static final float sin(float value) {
        return MathHelper.sin(value);
    }

    public static final float cos(float value) {
        return MathHelper.cos(value);
    }

    public static final float sqrt_float(float value) {
        return (float) Math.sqrt(value);
    }

    public static final float sqrt_double(double value) {
        return (float) Math.sqrt(value);
    }

    public static int floor_float(float value) {
        int var1 = (int) value;
        return value < var1 ? var1 - 1 : var1;
    }

    public static int truncateDoubleToInt(double value) {
        return (int) (value + 1024.0D) - 1024;
    }

    public static int floor_double(double value) {
        int var2 = (int) value;
        return value < var2 ? var2 - 1 : var2;
    }

    public static long floor_double_long(double value) {
        long var2 = (long) value;
        return value < var2 ? var2 - 1L : var2;
    }

    public static float abs(float value) {
        return value >= 0.0F ? value : -value;
    }

    public static int abs_int(int value) {
        return value >= 0 ? value : -value;
    }

    public static double abs_double(double value) {
        return value >= 0 ? value : -value;
    }

    public static int ceiling_float_int(float value) {
        int var1 = (int) value;
        return value > var1 ? var1 + 1 : var1;
    }

    public static int ceiling_double_int(double value) {
        int var2 = (int) value;
        return value > var2 ? var2 + 1 : var2;
    }

    public static int clamp_int(int value, int min, int max) {
        return value < min ? min : (value > max ? max : value);
    }

    public static float clamp_float(float value, float min, float max) {
        return value < min ? min : (value > max ? max : value);
    }

    public static double abs_max(double value, double max) {
        return MathHelper.absMax(value, max);
    }

    public static int bucketInt(int value, int times) {
        return value < 0 ? -((-value - 1) / times) - 1 : value / times;
    }

    public static boolean stringNullOrLengthZero(String value) {
        return value == null || value.isEmpty();
    }

    public static int getRandomIntegerInRange(Random random, int min, int max) {
        return min >= max ? min : random.nextInt(max - min + 1) + min;
    }

    public static double getRandomDoubleInRange(Random random, double min, double max) {
        return min >= max ? min : random.nextDouble() * (max - min) + min;
    }

    public static double average(long[] array) {
        return MathHelper.average(array);
    }

    public static float wrapAngleTo180_float(float angle) {
        return MathHelper.wrapDegrees(angle);
    }

    public static double wrapAngleTo180_double(double angle) {
        return MathHelper.wrapDegrees(angle);
    }

    public static int getIntDefault(String value, int defaultValue){
        return MathHelper.getInt(value, defaultValue);
    }

    public static int getIntDefaultAndMax(String value, int defaultValue, int max){
        return MathHelper.getInt(value, defaultValue, max);
    }

    public static double getDoubleDefault(String value, double defaultValue) {
        return MathHelper.getDouble(value, defaultValue);
    }

    public static double getDoubleDefaultAndMax(String value, double defaultValue, double max){
        return MathHelper.getDouble(value, defaultValue, max);
    }

    public static int roundUpToPowerOfTwo(int value){
        int j = value - 1;
        j |= j >> 1;
        j |= j >> 2;
        j |= j >> 4;
        j |= j >> 8;
        j |= j >> 16;
        return j + 1;
    }

    private static boolean isPowerOfTwo(int value){
        return value != 0 && (value & value - 1) == 0;
    }

    private static int calculateLogBaseTwoDeBruijn(int value){
        int index = isPowerOfTwo(value) ? value : roundUpToPowerOfTwo(value);
        return multiplyDeBruijnBitPosition[(int)(index * 125613361L >> 27) & 31];
    }

    public static int calculateLogBaseTwo(int value){
        return calculateLogBaseTwoDeBruijn(value) - (isPowerOfTwo(value) ? 0 : 1);
    }

    public static double max(double... nums){
        double max = nums[0];
        for(int i=1; i<nums.length; i++){
            max = Math.max(max, nums[i]);
        }
        return max;
    }

    public static double min(double... nums){
        double min = nums[0];
        for(int i=1; i<nums.length; i++){
            min = Math.min(min, nums[i]);
        }
        return min;
    }

    public static double minAbs(double... nums){
        double min = nums[0];
        double minAbs = abs_double(min);
        double tmpAbs;
        for(int i=1; i<nums.length; i++){
            tmpAbs = abs_double(nums[i]);
            if(minAbs>tmpAbs){
                min = nums[i];
                minAbs = tmpAbs;
            }
        }
        return min;
    }


    public static double length(double ... v) {
        double vv=0;
        for(int i=0; i<v.length; i++){
            vv += v[i]*v[i];
        }
        return sqrt_double(vv);
    }

    public static PCVec3 to45Angles(PCVec3 vec) {
        double x = vec.x;
        if(x>0.5){
            x=1;
        }else if(x<-0.5){
            x=-1;
        }else{
            x=0;
        }
        double y = vec.y;
        if(y>0.5){
            y=1;
        }else if(y<-0.5){
            y=-1;
        }else{
            y=0;
        }
        double z = vec.z;
        if(z>0.5){
            z=1;
        }else if(z<-0.5){
            z=-1;
        }else{
            z=0;
        }
        return new PCVec3(x, y, z).normalize();
    }

    public static boolean xInsideY(AxisAlignedBB x, AxisAlignedBB y) {
        return x.minX>=y.minX && x.minY>=y.minY && x.minZ>=y.minZ && x.maxX<=y.maxX && x.maxY<=y.maxY && x.maxZ<=y.maxZ;
    }
}
