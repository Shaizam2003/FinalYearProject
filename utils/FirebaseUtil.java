package com.example.halalcheck3.utils;


import com.google.firebase.auth.FirebaseAuth;


import java.text.SimpleDateFormat;

public class FirebaseUtil {

    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    public static boolean isLoggedIn() {
        return currentUserId() != null;
    }

  //  public static CollectionReference allChatroomCollectionReference() {
  //      return FirebaseFirestore.getInstance().collection("chatrooms");
  //  }

  //  public static DocumentReference getChatroomReference(String chatroomId) {
    //    return allChatroomCollectionReference().document(chatroomId);
//    }

   // public static CollectionReference getChatroomMessageReference(String chatroomId) {
    //    return getChatroomReference(chatroomId).collection("chats");
   // }

    public static String getChatroomId(String userId1, String userId2) {
        if (userId1.hashCode() < userId2.hashCode()) {
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
    }

  //  public static String timestampToString(Timestamp timestamp) {
   //     return new SimpleDateFormat("HH:MM").format(timestamp.toDate());
  //  }

    public static void logout() {
        FirebaseAuth.getInstance().signOut();
    }
}
