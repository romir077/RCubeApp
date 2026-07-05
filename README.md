# RCube

**Recognition before monetization.** RCube is a marketplace that helps local hobbyist
creators — singers, guitarists, mehendi artists, photographers, dancers, and more — get
discovered and booked by organizers for events, before they ever think about going pro.

This repository contains everything for the MVP:

| Path | What it is |
|---|---|
| `android/` | Production-grade Android app — Kotlin + Jetpack Compose (Material 3). |
| `backend/Code.gs` | Google Apps Script + Google Sheets MVP backend (single Web App endpoint). |
| `BACKEND_SETUP.md` | Step-by-step guide to deploy the backend and connect the app. |
| `RCUBE_BIBLE_v2.md` | The full product + engineering spec ("the constitution of RCube"). |

## Features

- **Auth** — phone number + OTP; one account, two modes (Creator & Organizer).
- **Identity** — account-level Aadhaar verification that gates discovery and booking.
- **Creator profiles** — multiple crafts, fixed-price services, a photo/video portfolio,
  a profile photo, activate/deactivate, and an admin skill-review workflow.
- **Discovery** — browse all verified creators, search, and filter by category + radius,
  with real distances computed from the device's location (Haversine).
- **Booking lifecycle** — request → accept → pay → confirm → event OTP → in progress →
  complete. Phone numbers are shared **only** after the creator accepts and the organizer pays.
- **Ratings & reviews** — organizers rate + comment after an event; the average and all
  comments appear on the creator's profile.
- **Live UX** — notifications, pull-to-refresh on every tab, and 5-second auto-refresh.

## Tech stack

- **App:** Kotlin, Jetpack Compose (Material 3), Navigation Compose, Coroutines / StateFlow,
  OkHttp + kotlinx.serialization, Coil, Media3 ExoPlayer, Play Services Location.
- **Backend:** Google Apps Script Web App with Google Sheets as the datastore and Google
  Drive for media (Aadhaar kept private; portfolio/photos shared as public links).

## Getting started

### Android app

1. Open the `android/` folder in Android Studio (minSdk 26).
2. It runs in **offline demo mode** with rich seeded data out of the box — no backend needed.
3. To connect the live backend, set these in `android/local.properties` (git-ignored):
   ```properties
   RCUBE_API_URL=https://script.google.com/macros/s/XXXX/exec
   RCUBE_API_KEY=your-shared-secret
   ```
4. Sync + Run.

### Backend

Follow **[`BACKEND_SETUP.md`](BACKEND_SETUP.md)**: create a Google Sheet → paste
`backend/Code.gs` → run `setup` (or `reset`) → deploy as a Web App → copy the `/exec`
URL and API key into `android/local.properties`.

## Project layout

```
android/                     Android app (Compose)
  app/src/main/java/com/rcube/app/
    core/                    design system, theme, utils, location
    data/                    models, DTOs, repository, remote API, local session
    feature/                 auth, creator, organizer, account screens
    navigation/              routes + app shell
backend/Code.gs              Apps Script backend
BACKEND_SETUP.md             backend deployment guide
RCUBE_BIBLE_v2.md            product + engineering spec
```

## Status

MVP. The Bible (`RCUBE_BIBLE_v2.md`) describes the full roadmap and the production
architecture (FastAPI + PostgreSQL) for when the Sheets backend is outgrown.
