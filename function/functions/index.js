const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);
const db = admin.firestore();

exports.leavingRoomListener = functions.firestore.document("rooms/{room_id}/users/{user_id}")
    .onDelete((snap, context) => {
        if (snap.data()["leader"]) {
            changeLeader(context);
        }

        updateRoomStatus(context.params.room_id);

        const notificationTitle = "There is someone left !";
        const notificationBody = snap.data().name + " has left";


        const payload = {
            notification: {
                title: notificationTitle,
                body: notificationBody,
                icon: "default"
            },
            data: {
                noti_type: "userState",
                user_id: snap.data().id
            }
        }

        const goesTo = context.params.room_id;

        return admin.messaging().sendToTopic(goesTo, payload).then(result => {
            console.log("Notification sent successfully " + goesTo);
        }).catch(err => {
            console.log("Error : " + err);
        });

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

async function updateRoomStatus(roomId) {
    const room = db.collection("rooms").doc(roomId).collection("users");
    const usersObjects = await room.get();
    // this code is changed and no longer delete the user from the watch room users list
    // in order to keep the invited users has the ability to access the room or be in thier history
    // let usersList = snapshot.data()["users"];
    // let userIndex = usersList.indexOf(userId);
    // let changes = {};

    // let to = 0;
    // if (userIndex == usersList.length - 1) {
    //     to = userIndex;
    // } else {
    //     to = userIndex - 1;
    // }
    // usersList.splice(userIndex, to);
    // changes.users = usersList;
    // change the state to finished
    // (2) is the id for finished state
    if (usersObjects.length == 0) {
        changes.state = 2;
    }
    let update = await room.update(changes)
    return update;
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
            icon: "default",
            click_action: "OPEN_WATCHROOM"
        },
        data: {
            noti_type: "invite",
            sendedBy_Id: sendedBy.data().id,
            sendedBy_name: sendedByName,
            room_id: snap.data().id,
            room_video_url: snap.data().mp4Url,
            room_name: snap.data().name,
            room_desc: snap.data().desc
        }
    }

    let goesTo = usersList[1]
    return admin.messaging().sendToTopic(goesTo, payload).then(result => {
        console.log("Notification sent successfully " + goesTo);
    }).catch(err => {
        console.log("Error : " + err);
    });
}

exports.notifyAboutUserStateUpdate = functions.firestore.document("rooms/{room_id}/users/{user_id}")
    .onUpdate((change, context) => {
        const oldData = change.before.data();
        const newData = change.after.data();
        const states = {
            ready: 0,
            playing: 1,
            finished: 2,
            stop: 3
        }

        let state = newData.state

        let notificationTitle = "";
        let notificationBody = "";
        let payload = {
            notification: {
                title: notificationTitle,
                body: notificationBody,
                icon: "default"
            },
            data: {
                noti_type: "userState"
            }
        };
        let goesTo = "userState";

        if (state == states.stop) {

            notificationTitle = "There is someone stopped the video"
            notificationBody = newData.name + " has stopped the video"


            payload = {
                notification: {
                    title: notificationTitle,
                    body: notificationBody,
                    icon: "default"
                },
                data: {
                    noti_type: "userState",
                    user_id: newData.id
                }
            }

            return admin.messaging().sendToTopic(goesTo, payload).then(result => {
                console.log("Notification sent successfully " + goesTo);
            }).catch(err => {
                console.log("Error : " + err);
            });
        } else if (state == states.playing) {
            if (oldData.state == states.stop) {

                notificationTitle = "Started Playing again !"
                notificationBody = newData.name + " is continuing the video"

                payload = {
                    notification: {
                        title: notificationTitle,
                        body: notificationBody,
                        icon: "default"
                    },
                    data: {
                        noti_type: "userState",
                        user_id: newData.id
                    }
                }

                return admin.messaging().sendToTopic(goesTo, payload).then(result => {
                    console.log("Notification sent successfully " + goesTo);
                }).catch(err => {
                    console.log("Error : " + err);
                });

            } else {
                return
            }
        } else {
            return
        }

    });