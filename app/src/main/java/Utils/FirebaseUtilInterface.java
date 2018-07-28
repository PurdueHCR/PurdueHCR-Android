package Utils;

import java.util.List;

import Models.PointType;

public interface FirebaseUtilInterface {
    default void onComplete(List<PointType> data){}
}