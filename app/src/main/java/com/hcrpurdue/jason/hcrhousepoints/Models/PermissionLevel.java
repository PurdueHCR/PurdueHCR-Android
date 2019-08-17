/**
 * Enum that has the values for user permission levels
 */
package com.hcrpurdue.jason.hcrhousepoints.Models;

public enum PermissionLevel {
    RESIDENT(0),
    RHP(1),
    REC(2),
    FHP(3);

    private final int firestoreValue;

    PermissionLevel(int value) {
        this.firestoreValue = value;
    }

    public int getFirestoreValue() {
        return firestoreValue;
    }

    public static PermissionLevel getPermissionLevelFromFirestore(int value){
        switch (value){
            case 0:
                return  RESIDENT;
            case 1:
                return RHP;
            case 2:
                return REC;
            case 3:
                return FHP;
            default:
                return RESIDENT;
        }
    }
}
