package Utils;

import java.util.List;

import Models.PointType;

public interface FirebaseUtilInterface {
    default void onPointTypeComplete(List<PointType> data){}
    default void onPostSuccess(){}
    default void onError(Exception e){}
}