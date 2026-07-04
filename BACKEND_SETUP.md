# RCube — Backend Setup & Configuration Guide

This guide gets the RCube app talking to a **real backend + database** using only free
Google tools:

- **Database:** a Google Sheet (one tab per table).
- **Backend/API:** a Google Apps Script Web App (`backend/Code.gs`) deployed for free.
- **App:** the Android app reads one URL + one key from `android/local.properties`.

> **You do not have to do anything to keep the current demo working.** If
> `RCUBE_API_URL` is left blank, the app runs in **offline demo mode** with the seeded
> data you already saw. The steps below switch it to the live backend.

---

## 0. Accounts you need

| Service | Needed for | Cost | Do you need a new account? |
|---|---|---|---|
| **Google account** | Google Sheets + Apps Script backend | Free | You almost certainly already have one. If not, create one at [accounts.google.com](https://accounts.google.com). |

That's the only account required for the MVP. (Optional upgrades — real SMS OTP and real
payments — are covered in §6 and need extra accounts only when you get there.)

---

## 1. Architecture at a glance

```
Android app  ──HTTPS POST { action, apiKey, token, data }──►  Apps Script Web App (/exec)
     ▲                                                              │
     │                                                              ▼
     └──────────────── JSON { ok, data } ◄──────────────  Google Sheet (tabs = tables)
```

Every request is a POST to a single URL. The `action` field selects the operation
(login, search, create booking, etc.). The script reads/writes rows in the Sheet.

---

## 2. Part A — Create the backend (~10 minutes)

### Step 1 — Create the Google Sheet (your database)
1. Go to [sheets.google.com](https://sheets.google.com) → **Blank spreadsheet**.
2. Rename it to `RCube DB` (top-left). You don't need to add any columns — the script
   creates all tabs automatically.

### Step 2 — Open the bound Apps Script project
1. In that sheet, click **Extensions → Apps Script**.
   *(Opening it from inside the sheet is important — it "binds" the script to this
   sheet so it can read/write it.)*
2. A code editor opens in a new tab with a file `Code.gs`.

### Step 3 — Paste the backend code
1. Delete whatever is in `Code.gs`.
2. Open `backend/Code.gs` from this repo, copy **all** of it, and paste it in.
3. Near the top, change this line to your own random secret:
   ```js
   var API_KEY = 'REPLACE_WITH_YOUR_RCUBE_API_KEY';
   ```
   Use any hard-to-guess string, e.g. `rcube_7f3aK9x2Qm5Zt8Lp`. **Remember it** — you'll
   paste the same value into the app in Part B.
4. Click the **Save** (disk) icon.

### Step 4 — Initialize the database (run `setup` once)
1. In the editor's function dropdown (top toolbar), select **`setup`**.
2. Click **Run**.
3. The first run asks for authorization:
   - Click **Review permissions** → pick your Google account.
   - You'll see **"Google hasn't verified this app"** — this is normal for your own
     script. Click **Advanced → Go to <project> (unsafe) → Allow**.
4. Re-run **`setup`** if it didn't complete after authorizing. When done, switch back to
   the Sheet — you'll see tabs (`users`, `creator_profiles`, `services`, `bookings`,
   …) and 6 seeded demo creators.

### Step 5 — Deploy as a Web App
1. Back in the Apps Script editor, click **Deploy → New deployment**.
2. Click the **gear icon** next to "Select type" → choose **Web app**.
3. Fill in:
   - **Description:** `RCube API`
   - **Execute as:** **Me** (your account)
   - **Who has access:** **Anyone**
4. Click **Deploy**, authorize if prompted, then **copy the Web app URL**. It looks
   like:
   ```
   https://script.google.com/macros/s/AKfycb....../exec
   ```
   Keep this URL — it goes into the app next.

> **Later changes:** whenever you edit `Code.gs`, click **Deploy → Manage deployments →
> (edit / pencil) → Version: New version → Deploy** so the live URL runs your new code.

---

## 3. Part B — Connect the app

1. Open **`android/local.properties`** (create it if missing — Android Studio usually
   makes it). It already has your `sdk.dir`.
2. Set these two lines to the values from Part A:
   ```properties
   RCUBE_API_URL=https://script.google.com/macros/s/AKfycb....../exec
   RCUBE_API_KEY=rcube_7f3aK9x2Qm5Zt8Lp
   ```
   - `RCUBE_API_URL` = the `/exec` URL from Step 5.
   - `RCUBE_API_KEY` = the **exact same** string you set as `API_KEY` in `Code.gs`.
3. In Android Studio: **File → Sync Project with Gradle Files**, then **Run** (or
   `./gradlew installDebug`). The app now uses the live backend.

> `local.properties` is git-ignored, so your URL and key are never committed. 👍

---

## 4. Part C — Test the live backend

1. Launch the app → enter any phone number → any 6-digit code (OTP is in dev mode, §6).
   - This creates a real `users` row + a session token in your Sheet.
2. **Organizer mode → Discover → Search** → you should see the seeded creators (Meera,
   Sam, Priya, …) coming *from the Sheet*.
3. Book one, pay (simulated), and watch a new row appear in the `bookings` tab.
4. To see the **two-sided flow** (a creator receiving a request), log in on a second
   device/emulator with a **different phone number**, create + submit a creator profile,
   have an admin approve it (set that profile's `status` to `APPROVED` in the Sheet),
   then book it from the first account.

**Quick health check (optional):** open this in a browser (replace with your URL):
```
https://script.google.com/macros/s/AKfycb....../exec?action=ping
```
You should see `{"ok":true,"data":{"pong":true,...}}`.

---

## 5. Reference — Everywhere you add keys / make changes

| What | File / Place | Value | Notes |
|---|---|---|---|
| API secret (server) | `backend/Code.gs` → `var API_KEY` | your random string | Must match the app's `RCUBE_API_KEY` |
| Backend URL (app) | `android/local.properties` → `RCUBE_API_URL` | the `/exec` URL | Blank = demo mode |
| API secret (app) | `android/local.properties` → `RCUBE_API_KEY` | same as `Code.gs` | — |
| Commission % | `backend/Code.gs` → `COMMISSION_PCT` | default `15` | Platform take-rate |
| OTP dev mode | `backend/Code.gs` → `DEV_MODE` | default `true` | `true` = accept any code (no SMS) |
| Accept/pay windows | `backend/Code.gs` → `ACCEPT_WINDOW_HOURS`, `PAYMENT_WINDOW_HOURS` | default `24` | Booking timers |
| Admin approve a creator | Google Sheet → `creator_profiles` tab | set `status` = `APPROVED` | Manual verification for MVP |
| Process a payout | Google Sheet → `bookings` tab | set `payoutStatus` = `TRANSFERRED` | Manual payout for MVP |

> After editing `local.properties`, **Sync + Rebuild**. After editing `Code.gs`,
> **redeploy a new version** (§2, Step 5 note).

---

## 6. What's real vs. mocked in this MVP (and how to upgrade)

| Area | MVP behaviour | Production upgrade | Extra account needed |
|---|---|---|---|
| **Login OTP** | `DEV_MODE=true` accepts any 4–6 digit code; the user row is real | Send real SMS OTP | **Firebase** (free tier) *or* an SMS provider like **MSG91 / Twilio** (paid) |
| **Payments** | "Pay" is simulated; booking is marked confirmed | Real UPI/card payment + escrow | **Razorpay** account (see below) |
| **Payouts** | Manual: edit `payoutStatus` in the Sheet | Automated bank transfers | **Razorpay Route / Payouts** |
| **Creator verification** | Manual: set `status=APPROVED` in the Sheet | Admin web panel | — (build the React admin from the Bible) |

### When you're ready for real SMS OTP (Firebase — free)
1. Create a project at [console.firebase.google.com](https://console.firebase.google.com).
2. Add an Android app with package `com.rcube.app` (and `com.rcube.app.debug`), download
   `google-services.json` into `android/app/`.
3. Enable **Authentication → Sign-in method → Phone**.
4. Swap the app's `verifyOtp` to use Firebase Phone Auth and pass the verified token to
   the backend. (The Bible §24.2 describes this exact flow.)

### When you're ready for real payments (Razorpay)
1. Sign up at [razorpay.com](https://razorpay.com) and complete KYC.
2. Get **Test** API keys (`Settings → API Keys`).
3. Add the `razorpay_flutter`/Razorpay Android SDK, create an order on the backend, and
   verify the payment signature server-side before confirming (Bible §24.8).

---

## 7. Troubleshooting

| Symptom | Fix |
|---|---|
| App shows demo data after adding the URL | You didn't **Sync + Rebuild**; `BuildConfig` is generated at build time. |
| `{"ok":false,"error":"unauthorized"}` | `RCUBE_API_KEY` (app) ≠ `API_KEY` (`Code.gs`). Make them identical; redeploy. |
| `No spreadsheet bound` error | The script wasn't created from **Extensions → Apps Script** inside the sheet. Recreate it there, or set `SPREADSHEET_ID` in `Code.gs`. |
| Search returns nothing | Run `setup` again (seeds creators); check the `creator_profiles` tab has `APPROVED` rows. |
| Changes to `Code.gs` have no effect | Redeploy a **new version** (Manage deployments → edit → New version). |
| Nothing loads / timeouts | Confirm the deployment "Who has access" = **Anyone**; test the `?action=ping` URL in a browser. |

---

*Built to match the RCube Bible v2.0. This Sheets + Apps Script stack is intended for the
MVP; the Bible's Part VI describes the FastAPI + PostgreSQL architecture for when you
outgrow it.*
