const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";
const ADMIN_UID = "admin_789";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

// --- User Profile Tests ---

test("Unauthenticated user: cannot read user profile", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: can create their own profile with valid schema", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      email: "alice@example.com",
      displayName: "Alice",
      currentLanguage: "bn",
      selectedCity: "ঢাকা",
      arabicFontSize: 24,
      bengaliFontSize: 16,
      isDarkMode: false,
      createdAt: new Date(),
    })
  );
});

test("Authenticated user: cannot create another user's profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).set({
      userId: BOB_UID,
      email: "bob@example.com",
      displayName: "Bob",
      createdAt: new Date(),
    })
  );
});

test("Authenticated user: cannot read another user's profile", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(BOB_UID).set({
      userId: BOB_UID,
      email: "bob@example.com",
      createdAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).get());
});

// --- Bookmarks Subcollection Tests ---

test("Authenticated user: can create and read own bookmark", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bookmarkRef = aliceDb.collection("users").doc(ALICE_UID).collection("bookmarks").doc("quran_1_1");

  await assertSucceeds(
    bookmarkRef.set({
      userId: ALICE_UID,
      type: "QURAN",
      referenceId: "1:1",
      titleBengali: "সূরা আল ফাতিহা - ১",
      arabicSnippet: "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
      bengaliSnippet: "শুরু করছি আল্লাহর নামে যিনি পরম করুণাময়, অসীম দয়ালু।",
      note: "গুরুত্বপূর্ণ আয়াত",
      createdAt: new Date(),
    })
  );

  await assertSucceeds(bookmarkRef.get());
});

test("Authenticated user: cannot read another user's bookmarks", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(BOB_UID).collection("bookmarks").doc("quran_1_1").set({
      userId: BOB_UID,
      type: "QURAN",
      referenceId: "1:1",
      titleBengali: "সূরা আল ফাতিহা - ১",
      createdAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).collection("bookmarks").doc("quran_1_1").get()
  );
});

// --- Worship Tracker Tests ---

test("Authenticated user: can update own daily worship", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const worshipRef = aliceDb.collection("users").doc(ALICE_UID).collection("worship").doc("2026-10-18");

  await assertSucceeds(
    worshipRef.set({
      userId: ALICE_UID,
      dateStr: "2026-10-18",
      fajr: true,
      dhuhr: true,
      asr: false,
      maghrib: false,
      isha: false,
      tahajjud: false,
      quranRecited: true,
      fasting: false,
      sadaqah: true,
    })
  );

  await assertSucceeds(worshipRef.get());
});

// --- Error Reports Tests ---

test("Authenticated user: can submit error report with PENDING status", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("error_reports").doc("report_001").set({
      userId: ALICE_UID,
      contentType: "QURAN",
      recordId: "1:1",
      issueCategory: "Translation",
      userExplanation: "অনুবাদে সামান্য বানান ত্রুটি রয়েছে।",
      status: "PENDING",
      createdAt: new Date(),
    })
  );
});

test("Authenticated non-admin: cannot list error reports", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("error_reports").get());
});

test("Admin: can list error reports", async () => {
  const adminDb = testEnv.authenticatedContext(ADMIN_UID, {
    email: "abirkhan33893@gmail.com",
    email_verified: true,
  }).firestore();

  await assertSucceeds(adminDb.collection("error_reports").get());
});
