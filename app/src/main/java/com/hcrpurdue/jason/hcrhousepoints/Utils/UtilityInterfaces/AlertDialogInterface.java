package com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces;

public interface AlertDialogInterface {

    default void onPositiveButtonListener(){}
    default void onPositiveButtonWithTextListener(String text){}

    default void onNegativeButtonListener(){}
}
