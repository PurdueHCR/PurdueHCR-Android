package Utils;

import java.util.List;

import Models.PointType;

public interface FirebaseUtilInterface {
    default void onPointTypeComplete(List<PointType> data){}
    default void onUserGetSuccess(){}
    default void onSuccess(){}
    default void onError(Exception e){}
}