package com.rumaruka.powercraft.api.reflect;

public class PCSecurity {

    public static boolean allowedCaller(String funktion, Class<?>... allowedCallers){
        Class<?> caller = PCReflect.getCallerClass(1);
        for(int i=0; i<allowedCallers.length; i++){
            if(allowedCallers[i]==caller){
                return true;
            }
        }
        return false;
    }

    public static boolean allowedCallerNoException(Class<?>... allowedCallers){
        Class<?> caller = PCReflect.getCallerClass(1);
        for(int i=0; i<allowedCallers.length; i++){
            if(allowedCallers[i]==caller){
                return true;
            }
        }
        return false;
    }

    public static boolean allowedCaller(String funktion, String... allowedCallers){
        Class<?> caller = PCReflect.getCallerClass(1);
        for(int i=0; i<allowedCallers.length; i++){
            if(allowedCallers[i].equals(caller.getName())){
                return true;
            }
        }
        return false;
    }

    public static boolean allowedCallerNoException(String... allowedCallers){
        Class<?> caller = PCReflect.getCallerClass(1);
        for(int i=0; i<allowedCallers.length; i++){
            if(allowedCallers[i].equals(caller.getName())){
                return true;
            }
        }
        return false;
    }


}
