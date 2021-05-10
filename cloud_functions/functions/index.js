const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();
const db = admin.firestore();
const FieldValue = admin.firestore.FieldValue;
const Timestamp = admin.firestore.Timestamp;

async function getUserFCMToken(docRef) {
    const userSnapshot = await docRef.get();
    const userFCMToken = userSnapshot.data()['notificationToken'];
    return userFCMToken;
}

function handleErrorMessagingToDevice(error) {
    functions.logger.error(
        'Failed to send notification on course enrollment to', userFCMToken, error
    );
    if (error.code === 'messaging/invalid-registration-token' ||
        error.code === 'messaging/registration-token-not-registered') {
        userDocRef.update({
            notificationToken: FieldValue.delete()
        });
    } 
}

async function addNewNotificationDoc(userId, payload, notificationType) {
    const response = await db.collection(`users/${userId}/notifications`)
        .add({
            hasRead: false,
            message: payload.notification.body,
            title: payload.notification.title,
            type: notificationType,
            notifiedAt: Timestamp.now()
        })
        .then(() => {
            functions.logger.log('New notification document added');
        })
        .catch(err => {
            functions.logger.err(
                'Notification sent, but new notification document failed to be added with message',
                err
            );
        });
}

exports.onEnrollFunction = functions.firestore
    .document('users/{userId}/courses/{courseId}')
    .onCreate(async (snapshot, context) => {
        const enrolled = snapshot.data()['enrolled'];
        const courseTitle = snapshot.data()['title'];
        const userId = context.params.userId;
        if (enrolled !== true) {
            functions.logger.warn('A new document without the enrolled field was created in course subcollection');
            return;
        }
        const userDocRef = snapshot.ref.parent.parent;
        const userFCMToken = await getUserFCMToken(userDocRef);
        if (userFCMToken === null || userFCMToken === '') {
            functions.logger.warn('User does not have any notification stored');
            return;
        }

        const payload = {
            notification: {
                title: 'Enroll Successful',
                body: `You have enrolled in the course '${courseTitle}' successfully`
            }
        };

        const response = await admin.messaging().sendToDevice(userFCMToken, payload);
        response.results.forEach(async (result, index) => {
            const error = result.error;
            if (error) {
                handleErrorMessagingToDevice(error);
            }
            else {
                addNewNotificationDoc(userId, payload, 'ENROLL');
            }
        })
    });

exports.onFinishedCourseFunction = functions.firestore
    .document('users/{userId}/courses/{courseId}')
    .onUpdate(async (change, context) => {
        const finishedBefore = change.before.data()['finished'];
        const finished = change.after.data()['finished'];
        const courseTitle = change.after.data()['title'];
        const userId = context.params.userId;
        if (finishedBefore === true || finished !== true) {
            functions.logger.log('Update happened, but it was not because user finished the course');
            return;
        }
        const userDocRef = change.before.ref.parent.parent;
        const userFCMToken = await getUserFCMToken(userDocRef);
        if (userFCMToken === null || userFCMToken === '') {
            functions.logger.warn('User does not have any notification stored');
            return;
        }

        const payload = {
            notification: {
                title: 'Finished Course',
                body: `Congrats! You have finished the course '${courseTitle}'`
            }
        };

        const response = await admin.messaging().sendToDevice(userFCMToken, payload);
        response.results.forEach(async (result, index) => {
            const error = result.error;
            if (error) {
                handleErrorMessagingToDevice(error);
            }
            else {
                addNewNotificationDoc(userId, payload, 'FINISH');
            }
        })
    });

exports.onCourseChapterUpdateFunction = functions.firestore
    .document('courses/{courseId}/chapters/{chapterId}')
    .onWrite(async (change, context) => {
        const courseId = context.params.courseId;
        const courseSnapshot = await db.doc(`courses/${courseId}`)
            .get();
        const courseTitle = courseSnapshot.data()['title'];
        const studentSnapshots = await db.collection('users')
            .get();
        const studentIdsAndFCMTokens = [];
        studentSnapshots.forEach(snapshot => {
            const notificationToken = snapshot.data()['notificationToken'];
            if (notificationToken === null || notificationToken === '') {
                functions.logger.log(`Student with id ${snapshot.id} does not have FCM registration token`);
                return;
            }
            else {
                studentIdsAndFCMTokens.push({
                    id: snapshot.id,
                    token: notificationToken
                });
            }
        });
        const queries = [];
        const idsAndTokens = []; // Array to store IDs and Tokens to which FCM will send a message
        studentIdsAndFCMTokens.forEach(idAndToken => {
            const query = db.doc(`users/${idAndToken.id}/courses/${courseId}`)
                .get()
                .then(courseSnapshot => {
                    if (courseSnapshot.exists) {
                        idsAndTokens.push(idAndToken);
                    }
                    else {
                        functions.logger.log(`Student with id ${idAndToken.id} has not enrolled to course with id ${courseId}`);
                    }
                })
                .catch(err => {
                    functions.logger.error(`An error occured while trying to process id and tokens of user with id ${idAndToken.id}`, err);
                })
            queries.push(query);
        })
        await Promise.all(queries);
        const payload = {
            notification: {
                title: 'Course Updated',
                body: `Your enrolled course '${courseTitle}' has been updated`
            }
        };
        const tokens = idsAndTokens.map(idAndToken => idAndToken.token);
        const ids = idsAndTokens.map(idAndToken => idAndToken.id);
        const response = await admin.messaging().sendToDevice(tokens, payload);
        response.results.forEach(async (result, index) => {
            const error = result.error;
            if (error) {
                handleErrorMessagingToDevice(error);
            }
            else {
                addNewNotificationDoc(ids[index], payload, 'UPDATE');
            }
        })
    })

exports.onDiscussionReplyFunction = functions.firestore
    .document('courses/{courseId}/chapters/{chapterId}/discussions/{discussionId}/answers/{answerId}')
    .onCreate(async (snapshot, context) => {
        const courseId = context.params.courseId;
        const courseDocRef = db.doc(`courses/${courseId}`);
        const courseSnapshot = await courseDocRef.get();
        const courseTitle = courseSnapshot.data()['title'];
        const discussionDocRef = snapshot.ref.parent.parent;
        const discussionSnapshot = await discussionDocRef.get();
        const discussionAskerId = discussionSnapshot.data()['userId'];
        const userDocRef = db.doc(`users/${discussionAskerId}`);
        const userFCMToken = await getUserFCMToken(userDocRef);
        if (userFCMToken === null || userFCMToken === '') {
            functions.logger.warn('User does not have any notification stored');
            return;
        }

        const payload = {
            notification: {
                title: 'New Discussion Reply',
                body: `Your discussion on course '${courseTitle}' has a new reply`
            }
        };

        const response = await admin.messaging().sendToDevice(userFCMToken, payload);
        response.results.forEach(async (result, index) => {
            const error = result.error;
            if (error) {
                handleErrorMessagingToDevice(error);
            }
            else {
                addNewNotificationDoc(discussionAskerId, payload, 'REPLY');
            }
        });
    });