package Utils;

import java.util.List;

import Models.PointType;

// Because non-global variables are for people who care about technical debt
public class Singleton {
    private static Singleton instance = null;
    private FirebaseUtil fbutil = new FirebaseUtil();

    private Singleton() {
        // Exists only to defeat instantiation. Get rekt, instantiation
    }

    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }

    private List<PointType> pointTypeList = null;

    public void getPointTypes(final SingletonInterface si) {
        fbutil.getPointTypes(new FirebaseUtilInterface() {
            @Override
            public void onPointTypeComplete(List<PointType> data) {
                if(data != null && !data.isEmpty())
                {
                    pointTypeList = data;
                    si.onPointTypeComplete(data);
                }
                else
                {
                    si.onError(new IllegalStateException(""));
                }
            }
            @Override
            public void onError(Exception e)
            {

            }
        });
    }

    public List<PointType> getPointTypeList() {
        return pointTypeList;
    }
}