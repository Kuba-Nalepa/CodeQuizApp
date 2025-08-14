import {
  onDocumentUpdated,
} from "firebase-functions/v2/firestore";
import * as admin from "firebase-admin";

admin.initializeApp();

interface FriendshipRequest {
    senderId: string;
    receiverId: string;
    status: string;
}

interface User {
    uid: string;
    name: string;
    imageUri: string;
}

interface Friend {
    uid: string;
    name: string;
    imageUri: string;
}

export const onFriendshipAccepted = onDocumentUpdated(
  "friendships/{friendshipId}",
  async (event) => {
    if (!event.data) {
      return;
    }

    const beforeData = event.data.before.data() as FriendshipRequest;
    const afterData = event.data.after.data() as FriendshipRequest;

    if (beforeData.status === "pending" && afterData.status === "accepted") {
      const senderId = afterData.senderId;
      const receiverId = afterData.receiverId;

      const firestore = admin.firestore();

      const senderRef = firestore.collection("users").doc(senderId);
      const receiverRef = firestore.collection("users").doc(receiverId);

      const [senderDoc, receiverDoc] = await Promise.all([
        senderRef.get(),
        receiverRef.get(),
      ]);

      if (!senderDoc.exists || !receiverDoc.exists) {
        console.error("Sender or receiver user document not found.");
        return;
      }

      const senderData = senderDoc.data() as User;
      const receiverData = receiverDoc.data() as User;

      const friendDataForSender: Friend = {
        uid: receiverId,
        name: receiverData.name,
        imageUri: receiverData.imageUri,
      };

      const friendDataForReceiver: Friend = {
        uid: senderId,
        name: senderData.name,
        imageUri: senderData.imageUri,
      };

      await senderRef
        .collection("friends")
        .doc(receiverId)
        .set(friendDataForSender);

      await receiverRef
        .collection("friends")
        .doc(senderId)
        .set(friendDataForReceiver);

      console.log(
        `Friendship created between ${senderId} and ${receiverId}`
      );
    }

    return;
  }
);

export const onDeleteFriendshipAfterAccept = onDocumentUpdated(
  "friendships/{friendshipId}",
  async (event) => {
    if (!event.data) {
      return;
    }

    const beforeData = event.data.before.data() as FriendshipRequest;
    const afterData = event.data.after.data() as FriendshipRequest;

    const friendshipId = event.params.friendshipId;

    if (beforeData.status === "pending" && afterData.status === "accepted") {
      const firestore = admin.firestore();

      try {
        await firestore.collection("friendships").doc(friendshipId).delete();
        console.log(
          `Friendship document ${friendshipId} successfully deleted.`
        );
      } catch (error) {
        console.error(
          `Error deleting friendship document ${friendshipId}:`, error
        );
      }
    }

    return;
  }
);
