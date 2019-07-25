/**
 * Created 7/21/19 - Brian Johncox
 *
 * This interface is used by the Firebase Listener Util to handle the query and document snapshots
 */
package com.hcrpurdue.jason.hcrhousepoints.Utils.UtilityInterfaces;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public interface SnapshotInterface {
    default void handleQuerySnapshots(QuerySnapshot queryDocumentSnapshots, Exception e){}
    default void handleDocumentSnapshot(DocumentSnapshot documentSnapshot, Exception e){}
}
