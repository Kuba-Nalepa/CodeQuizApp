import {
  onDocumentUpdated,
  onDocumentCreated,
} from "firebase-functions/v2/firestore";
import {onCall, HttpsError} from "firebase-functions/v2/https";
import * as admin from "firebase-admin";

admin.initializeApp();
const firestore = admin.firestore();

interface FriendshipRequest {
  senderId: string;
  receiverId: string;
  status: string;
}

interface User {
  uid: string;
  name: string;
  imageUri: string;
  fcmToken?: string;
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


export const createChat = onCall(async (request) => {
  const {data, auth} = request;

  if (!auth) {
    throw new HttpsError("unauthenticated", "User not authenticated.");
  }

  const myUid = auth.uid;
  const friendUid = data.friendUid;

  if (!friendUid) {
    throw new HttpsError("invalid-argument", "Missing friendUid.");
  }

  const participants = [myUid, friendUid].sort();
  const chatId = participants.join("_");
  const chatRef = firestore.collection("chats").doc(chatId);

  const chatDoc = await chatRef.get();
  if (chatDoc.exists) {
    return {chatId: chatId};
  }

  await chatRef.set({members: participants});

  return {chatId: chatId};
});

export const sendMessage = onCall(async (request) => {
  const {data, auth} = request;

  if (!auth) {
    throw new HttpsError("unauthenticated", "User not authenticated.");
  }

  const myUid = auth.uid;
  const chatId = data.chatId;
  const messageText = data.messageText;

  if (!chatId || !messageText) {
    throw new HttpsError("invalid-argument", "Missing chatId or messageText.");
  }

  const chatRef = firestore.collection("chats").doc(chatId);
  const messagesRef = chatRef.collection("messages");

  const newMessage = {
    senderId: myUid,
    text: messageText,
    timestamp: admin.firestore.FieldValue.serverTimestamp(),
  };
  await messagesRef.add(newMessage);

  return {success: true};
});

export const checkChatExists = onCall(async (request) => {
  const {data, auth} = request;
  const myUid = auth?.uid;
  const friendUid = data.friendUid;

  if (!myUid || !friendUid) {
    throw new HttpsError("invalid-argument", "Missing user UIDs.");
  }

  const participants = [myUid, friendUid].sort();
  const chatId = participants.join("_");
  const chatRef = firestore.collection("chats").doc(chatId);

  const doc = await chatRef.get();
  return {exists: doc.exists, chatId: chatId};
});

export const handleNewFriendshipRequest = onDocumentCreated(
  "friendships/{requestId}",
  async (event) => {
    if (!event.data) {
      console.log("No data found in the event.");
      return null;
    }

    const request = event.data.data();
    const receiverId = request.receiverId;
    const senderName = request.senderName;

    const userRef = admin
      .firestore()
      .collection("users")
      .doc(receiverId);

    const userDoc = await userRef.get();
    const fcmToken = userDoc.data()?.fcmToken;

    await userRef.update({
      unreadFriendInvitations: admin.firestore.FieldValue.increment(1),
    });

    console.log(
      `Friendship notification count increased for user: ${receiverId}`
    );

    if (!fcmToken) {
      console.log("No FCM token, not sending notification.");
      return null;
    }

    const payload = {
      notification: {
        title: "New invitation!",
        body: `${senderName} has sent You an invitation.`,
      },
      data: {
        type: "friend_request",
      },
      token: fcmToken,
    };

    try {
      await admin.messaging().send(payload);
      console.log("FCM notification sent successfully.");
      return null;
    } catch (error) {
      console.error("Error sending FCM notification:", error);
      return null;
    }

    return null;
  }
);

export const handleNewGameInvite = onDocumentCreated(
  "gameInvitations/{inviteId}",
  async (event) => {
    if (!event.data) {
      console.log("No data found in the event.");
      return null;
    }

    const inviteData = event.data.data();
    const receiverId = inviteData.receiverId;
    const senderName = inviteData.senderName;

    const userRef = admin
      .firestore()
      .collection("users")
      .doc(receiverId);

    const userDoc = await userRef.get();
    const fcmToken = userDoc.data()?.fcmToken;

    await userRef.update({
      unreadGameInvitations: admin.firestore.FieldValue.increment(1),
    });

    console.log(`Game invite count increased for user: ${receiverId}`);

    if (!fcmToken) {
      console.log("No FCM token, not sending notification.");
      return null;
    }

    const payload = {
      notification: {
        title: "New invitation!",
        body: `${senderName} has invited you to a game.`,
      },
      data: {
        type: "game_invite",
      },
      token: fcmToken,
    };

    try {
      await admin.messaging().send(payload);
      console.log("FCM notification sent successfully.");
      return null;
    } catch (error) {
      console.error("Error sending FCM notification:", error);
      return null;
    }

    return null;
  }
);
