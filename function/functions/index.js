const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);
const db = admin.firestore();

exports.leavingRoomListener = functions.firestore.document("rooms/{room_id}/users/{user_id}")
    .onDelete((snap, context) => {
        if (snap.data()["leader"]) {
            return changeLeader(context)
        }
        return
    });

async function changeLeader(context) {
    const usersRef = db.collection("rooms");
    const snapshot = await usersRef.doc(context.params.room_id).collection("users").orderBy('id').limit(1).get();
    console.log(snapshot[0]);
    var firstUser = "";
    snapshot.forEach(doc => {
        console.log(doc.id, '=>', doc.data());
        firstUser = doc.id;
    });
    let data = { leader: true };
    const res = await usersRef.doc(context.params.room_id).collection("users").doc(firstUser).update(data);
    console.log("done , leader set to : " + firstUser);

    return res
}

exports.sendRoomInvitation = functions.firestore.document("rooms/{room_id}")
    .onCreate((snap, context) => {

        return sendInvitation(snap);

    });

async function sendInvitation(snap) {
    let usersList = snap.data().users

    let usersRef = db.collection("users");
    let sendedBy = await usersRef.doc(usersList[0]).get()
    let sendedByName = sendedBy.data().name

    const notificationTitle = "New invitation"
    const notificationBody = sendedByName + "Invites you to watch something"

    const payload = {
        notification: {
            title: notificationTitle,
            body: notificationBody,
            icon: "default"
        },
        data: {
            sendedBy_Id: sendedBy.data().id,
            sendedBy_name: sendedByName,
            room_desc: snap.data().desc,
            room_name: snap.data().name,
            room_id: snap.data().id
        }
    }

    let goesTo = usersList[1]
    return admin.messaging().sendToTopic(goesTo, payload).then(result => {
        console.log("Notification sent successfully " + goesTo);
    }).catch(err => {
        console.log("Error : " + err);
    });
}    