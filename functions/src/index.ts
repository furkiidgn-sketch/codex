import * as functions from "firebase-functions";
import * as admin from "firebase-admin";

admin.initializeApp();
const db = admin.firestore();

interface GameConfig {
  artistId: string;
  genreFilter: string[];
  era?: [number, number];
  questionCount: number;
  perQuestionSeconds: number;
}

function rateLimit(context: functions.https.CallableContext) {
  const now = Date.now();
  const lastCall = context.rawRequest?.headers["x-fn-last"];
  if (lastCall && now - Number(lastCall) < 500) {
    throw new functions.https.HttpsError("resource-exhausted", "Too many requests");
  }
}

export const generateQuestions = functions.runWith({ memory: "256MB" }).https.onCall(async (data: GameConfig, context) => {
  rateLimit(context);
  const tracksSnap = await db
    .collection("artists")
    .doc(data.artistId)
    .collection("tracks")
    .get();
  const tracks = tracksSnap.docs.map((doc) => doc.data());
  const filtered = tracks
    .filter((track) => track.previewUrl)
    .filter((track) => {
      if (!data.era) return true;
      const year = Number(track.releaseYear);
      return year >= data.era[0] && year <= data.era[1];
    })
    .filter((track) => {
      if (!data.genreFilter || data.genreFilter.length === 0) return true;
      const trackGenres = (track.genres || []).map((g: string) => g.toLowerCase());
      return data.genreFilter.some((g) => trackGenres.includes(g.toLowerCase()));
    });

  const selected = filtered.sort(() => Math.random() - 0.5).slice(0, data.questionCount);
  const questions = selected.map((track) => {
    const incorrect = filtered
      .filter((other) => other.id !== track.id)
      .sort(() => Math.random() - 0.5)
      .slice(0, 2)
      .map((other) => other.name);
    const options = [...incorrect, track.name].sort(() => Math.random() - 0.5);
    return {
      trackId: track.id,
      correctTitle: track.name,
      options,
      previewUrl: track.previewUrl,
      appleUrl: track.appleUrl || null,
    };
  });
  return { questions };
});

export const findOrCreateMatch = functions.https.onCall(async (data: GameConfig, context) => {
  rateLimit(context);
  const userId = context.auth?.uid;
  if (!userId) {
    throw new functions.https.HttpsError("unauthenticated", "Auth required");
  }
  const waitingRef = db.collection("matches").where("state", "==", "waiting").limit(1);
  const waitingSnap = await waitingRef.get();
  if (!waitingSnap.empty) {
    const doc = waitingSnap.docs[0];
    await doc.ref.update({ [`players.${userId}`]: { uid: userId, score: 0, connected: true } });
    return { matchId: doc.id };
  }
  const matchDoc = db.collection("matches").doc();
  await matchDoc.set({
    config: data,
    state: "waiting",
    createdAt: admin.firestore.FieldValue.serverTimestamp(),
    currentQuestion: 0,
    players: {
      [userId]: { uid: userId, score: 0, connected: true },
    },
  });
  return { matchId: matchDoc.id };
});

export const submitAnswer = functions.https.onCall(async (data: any, context) => {
  rateLimit(context);
  const userId = context.auth?.uid;
  if (!userId) throw new functions.https.HttpsError("unauthenticated", "Auth required");
  const { matchId, questionIndex, selected } = data;
  const matchRef = db.collection("matches").doc(matchId);
  const matchSnap = await matchRef.get();
  if (!matchSnap.exists) throw new functions.https.HttpsError("not-found", "Match missing");
  const matchData = matchSnap.data()!;
  const question = matchData.questions?.[questionIndex];
  if (!question) throw new functions.https.HttpsError("invalid-argument", "Invalid question");
  const isCorrect = question.correctTitle === selected;
  const remainingSeconds = matchData.perQuestionSeconds || 30;
  const base = isCorrect ? 500 : 0;
  const timeBonus = isCorrect ? Math.round(500 * (remainingSeconds / 30)) : 0;
  const score = base + timeBonus;
  await matchRef.set(
    {
      players: {
        [userId]: {
          uid: userId,
          score: admin.firestore.FieldValue.increment(score),
          connected: true,
        },
      },
    },
    { merge: true }
  );
  return { score };
});

export const submitSoloAnswer = functions.https.onCall(async (data: any, context) => {
  rateLimit(context);
  const { gameId, selected } = data;
  const correct = true;
  const remainingSeconds = 30;
  const base = correct ? 500 : 0;
  const timeBonus = correct ? Math.round(500 * (remainingSeconds / 30)) : 0;
  const score = base + timeBonus;
  await db.collection("soloResults").doc(gameId).set(
    {
      lastScore: score,
      updatedAt: admin.firestore.FieldValue.serverTimestamp(),
    },
    { merge: true }
  );
  return { score };
});

export const finalizeMatch = functions.https.onCall(async (data: any, context) => {
  rateLimit(context);
  const { matchId } = data;
  const matchRef = db.collection("matches").doc(matchId);
  const matchSnap = await matchRef.get();
  if (!matchSnap.exists) throw new functions.https.HttpsError("not-found", "Match missing");
  const matchData = matchSnap.data()!;
  await matchRef.update({ state: "finished" });
  const players = matchData.players || {};
  await Promise.all(
    Object.values(players).map(async (player: any) => {
      await db
        .collection("leaderboard")
        .doc("alltime")
        .collection("entries")
        .doc(player.uid)
        .set(
          {
            score: admin.firestore.FieldValue.increment(player.score || 0),
            updatedAt: admin.firestore.FieldValue.serverTimestamp(),
          },
          { merge: true }
        );
    })
  );
  return { status: "ok" };
});

export const updateLeaderboard = functions.pubsub.schedule("every monday 00:00").onRun(async () => {
  const now = admin.firestore.Timestamp.now();
  const snapshot = await db.collection("matches").where("state", "==", "finished").get();
  await Promise.all(
    snapshot.docs.map((doc) =>
      doc.ref.update({ archivedAt: now })
    )
  );
});
