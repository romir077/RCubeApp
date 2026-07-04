# The RCube Bible

### The Definitive Product, Design & Engineering Constitution

**Product:** RCube — the Recognition Cube
**Document:** RCube Bible v2.0
**Status:** Canonical / Source of Truth
**Audience:** Founders, Product, Design, Backend, Mobile, QA, DevOps, and AI coding agents (Claude, Cursor, Lovable, Windsurf)
**Classification:** Internal — Confidential
**Owner:** Founding Team

---

> "Recognition before Monetization."
>
> This is not a tagline. It is the constitution. Every decision in this document
> can be traced back to that single sentence. When in doubt, choose the path that
> maximizes a creator's recognition, not the path that maximizes short-term revenue.

---

## How To Read This Document

This is not a lightweight PRD. It is a **product bible** — the internal operating
document a founding team would keep at Airbnb, Uber, Stripe, or Notion in year one.

Every section is written in a consistent four-part structure so that anyone —
human or AI agent — can understand not just *what* to build, but *why*:

- **Vision** — the belief that motivates this section.
- **Decision** — the concrete choice we are making.
- **Reasoning** — why we chose it over alternatives.
- **Product Implication** — what this means for design, engineering, and operations.

If you are an AI coding agent: treat this document as the specification of record.
Where this document is explicit, build exactly what is written. Where this document
is silent, prefer the option most consistent with the philosophy in Part I. Do not
introduce features listed in **Out of Scope** (Part VII, "Out of Scope for MVP").

---

## Table of Contents

**PART I — WHY RCUBE EXISTS**
1. Product Vision
2. The Founder's Story
3. Mission
4. Product Philosophy
5. The Recognition Thesis

**PART II — THE MARKET**
6. Market Opportunity
7. Competitive Landscape & Positioning
8. Business Model & Unit Economics

**PART III — THE PEOPLE**
9. Personas
10. User Journeys

**PART IV — THE EXPERIENCE**
11. UX Principles & Design Language
12. Information Architecture & Navigation Map
13. Screen Inventory
14. Wireframes (ASCII)
15. Empty, Loading & Error States

**PART V — THE PRODUCT SPECIFICATION**
16. Functional Requirements
17. Non-Functional Requirements
18. Business Rules
19. State Machines
20. Notification Matrix

**PART VI — THE ENGINEERING**
21. System Architecture Overview
22. Domain-Driven Design
23. Database Design & Entity Relationships
24. API Contracts
25. Mobile App Architecture
26. Backend Architecture
27. Event-Driven Architecture
28. Admin Panel Design
29. Security Model
30. Audit Logs
31. Scalability Plan

**PART VII — THE ROAD AHEAD**
- Out of Scope for MVP (explicit exclusions)
32. Future Roadmap
33. Engineering Tradeoffs
34. Edge Cases
35. Open Questions

**APPENDICES**
- A. Glossary
- B. Enumerations & Constants
- C. Notification Copy Deck
- D. Environment & Configuration
- E. Definition of Done (MVP)

---
---

# PART I — WHY RCUBE EXISTS

---

## 1. Product Vision

**Vision.**
We believe that talent is distributed evenly across humanity, but *recognition* is
not. Millions of people quietly practice a craft — flute, guitar, mehendi, dance,
photography, storytelling — and are appreciated only by the small circle of people
who already know them. RCube exists to give every one of these people a **professional
identity** and a **path to being recognized by strangers**.

RCube is the **recognition layer for local creators**. Not a social network. Not a
gig marketplace in the extractive sense. A place where a hobbyist becomes a
*recognized creator* — someone strangers choose, book, and applaud.

**The long-term vision:**
Ten years from now, when a person in any Indian city discovers they love singing,
painting mehendi, or shooting portraits, the natural next step is not "post on
Instagram and hope." It is "create an RCube profile." RCube becomes the default
credentialing and recognition system for the informal creative economy — the
LinkedIn of local creators, but built around *being appreciated* rather than
*being employed*.

**Decision.**
The north star metric for RCube is **Recognized Bookings**: the count of completed
bookings where a creator was chosen by an organizer who did **not** previously know
them. This single metric captures the mission — a stranger recognized a stranger's
talent enough to pay for it.

**Reasoning.**
Most platforms optimize GMV (gross merchandise value) from day one. GMV is a lagging
symptom of a healthy recognition loop. If we optimize recognition first, monetization
follows durably. If we optimize monetization first, we build another extractive
marketplace and lose the soul of the product.

**Product Implication.**
Every surface — onboarding, profile, search, booking, event execution — is designed
to accumulate and display *recognition signals*, even in the MVP where the explicit
"Recognition Score" is intentionally deferred (see "Out of Scope for MVP" in Part
VII). We design the data
model now so recognition can be computed later without migration pain.

---

## 2. The Founder's Story

*(Preserved verbatim in spirit — this is the origin myth of RCube and must never be
edited out of the bible. It is the emotional root of every decision.)*

The founder plays the flute. Not professionally — as a hobby, practiced quietly for
years. His family knew. His friends knew. But no one outside that circle did.

He wanted a chance to *showcase* what he had built in private. So he did the
unglamorous thing: he contacted bars, cafés, and brewpubs, one by one, asking if he
could perform.

Most **ignored** him. Some **declined**. One brewpub said **yes**.

He performed for about **25 minutes**. The audience **applauded**. Strangers —
people who owed him nothing — appreciated something he had practiced alone for years.
The venue offered him a discount on food as a thank-you.

The food discount did not matter. The **applause** did.

That feeling — of strangers recognizing quiet, private effort — was **recognition**.
And it was addictive in the healthiest way.

Then came the realization: there are **thousands** of people exactly like him.
Guitarists. Singers. Photographers. Dancers. Painters. Storytellers. Mehendi artists.
Flautists. Most of them do **not** want fame. Most do **not** want a full-time career
change. They simply want **opportunities to showcase their talent and be recognized**.

Nobody was building for them. Instagram optimizes for reach and vanity metrics.
Fiverr and Upwork optimize for cheapest-bid freelancing. Urban Company optimizes for
standardized home services. None of them optimize for the specific, human feeling of
*a stranger appreciating your craft in a room*.

That gap became RCube.

**Why this story governs the product:**
Every time we are tempted to add a feature that increases engagement-for-engagement's
sake, or squeezes more revenue per transaction, we return to the brewpub. Would this
feature help a nervous first-time flautist get on a small stage and hear applause? If
yes, build it. If no, defer it.

---

## 3. Mission

**Mission statement.**
> Give every creator a professional identity and a path to being recognized by
> strangers — starting with local, in-person opportunities.

**The causal chain we believe in:**

```
Recognition  →  Reputation  →  Opportunities  →  Income
```

- **Recognition** is the promise. It is what we guarantee we will fight for.
- **Reputation** is what accrues as recognition repeats.
- **Opportunities** are what reputation unlocks.
- **Income** is the *consequence*, never the *headline*.

**Decision.**
Income is explicitly **not** the primary promise of RCube's marketing, product copy,
or onboarding. We will never say "earn money on RCube" as the lead message. We say
"get recognized."

**Reasoning.**
The moment income becomes the headline, we attract the wrong creators (money-first
freelancers) and the wrong organizers (lowest-price hunters), and we begin the slow
decay into a race-to-the-bottom gig marketplace. Recognition-first framing attracts
people who care about craft, which produces higher-quality supply, which produces
better experiences, which — eventually — produces more durable income than a
price-war ever could.

**Product Implication.**
Copywriting across the app is constrained by this mission (see Appendix C). Payout
and earnings features exist and must be excellent, but they are framed as
*consequences of recognition*, never as the reason to join.

---

## 4. Product Philosophy

### 4.1 What RCube is NOT

RCube is **not**:

- **Another Instagram / TikTok.** We are not in the attention/algorithmic-feed
  business. We do not reward posting frequency. There is no infinite scroll in MVP.
- **Another Fiverr / Upwork.** We are not a bid-based, race-to-the-bottom freelance
  marketplace. Creators are not commodities competing on price alone.
- **Another Urban Company.** We are not standardizing a craft into an
  interchangeable service SKU delivered by anonymous labor.

### 4.2 What RCube IS

RCube **is** the **recognition layer for local creators**.

The primary goal: **give every creator a professional identity.**

Recognition should not be reserved for celebrities and influencers with large
followings. Every hobbyist deserves recognition. RCube is the great equalizer of
recognition — a verified flautist with zero followers can be discovered and booked
on the same footing as anyone else, because discovery is based on *talent, category,
and locality*, not on a follower count.

### 4.3 The Ten Commandments of RCube

These are the invariant design principles. Any feature proposal must be checked
against them.

1. **Recognition before monetization.** Always.
2. **Every creator gets a professional identity**, regardless of following.
3. **Discovery is based on talent and locality, not popularity metrics.**
4. **Never call an organizer a "consumer."** They are Organizers or Hosts.
5. **Trust is a feature.** Verification, escrow-style payment, and OTP-gated event
   execution exist to make strangers comfortable transacting.
6. **Simplicity is a feature.** In MVP we deliberately omit calendars, chat,
   availability engines, and scores. Complexity is debt against the mission.
7. **Warmth over corporate polish.** The product should feel human, not enterprise.
8. **Protect the creator's dignity.** No public rejection, no public failure, no
   humiliating leaderboards.
9. **Privacy by default.** Contact details are revealed only at the exact moment
   trust and commitment are established (payment).
10. **Build the data model for the future, ship the feature set for today.** Defer
    features, never defer good schema.

### 4.4 Design Tenets Derived From Philosophy

| Tenet | Consequence in the product |
|---|---|
| Recognition-first | Profile is the hero object, not the feed |
| Locality-first | Search is radius + category, not global |
| Trust-first | Verification badge, escrow payment, OTP execution |
| Simplicity-first | Date-only booking, no calendar in MVP |
| Dignity-first | Rejections are private; no public reviews in MVP |

---

## 5. The Recognition Thesis

**Vision.**
Recognition is a *measurable, compounding asset*. Today it lives informally
(applause, word of mouth). RCube proposes to make it **portable, verifiable, and
cumulative**.

**Decision.**
Even though the explicit **Recognition Score is out of scope for MVP**, every event
in the system that could contribute to recognition is **logged as a first-class,
timestamped, immutable event** from day one (see Part VI, Event-Driven Architecture
and Audit Logs). We are building the ledger now; we will compute the score later.

**Reasoning.**
Recognition Score, reviews, badges, and trust scores are all *downstream computations*
over a stream of ground-truth events (bookings completed, events executed, organizers
who re-booked, etc.). If we capture those events cleanly from day one, we can turn on
recognition features later with zero backfill. If we don't, we will be forced into a
painful migration and will have permanently lost early data.

**Product Implication.**
The `audit_log` and `domain_event` tables (Part VI) are not optional
nice-to-haves. They are core MVP infrastructure precisely *because* the recognition
thesis depends on them. This is the single most important "build for the future"
decision in the bible.

**The recognition data we begin accumulating in MVP (silently):**

- Number of completed bookings per creator profile.
- Distinct organizers who booked a creator (breadth of recognition).
- Repeat organizers (depth of recognition / loyalty).
- Categories and event types a creator has performed in.
- Cities a creator has been recognized in.
- Time-to-accept and completion reliability (professionalism signals).

None of these are *shown* to users in MVP. All of them are *captured*.

---
---

# PART II — THE MARKET

---

## 6. Market Opportunity

**Vision.**
The informal creative economy in India is enormous, fragmented, and almost entirely
offline. Millions of people possess a monetizable craft they never monetize, and
millions of events happen every year that could use local talent but have no reliable
way to find it. RCube sits precisely in this gap.

### 6.1 The demand side (Organizers / Hosts)

Every one of these is a recurring, high-frequency event that often wants live or
on-site talent:

- **Birthdays & house parties** — want singers, guitarists, mehendi artists,
  photographers, magicians.
- **Weddings & pre-wedding functions** — want photographers, mehendi artists,
  performers, dancers.
- **Cafés, brewpubs, restaurants** — want live acoustic performers to create
  ambience (the exact founder use-case).
- **Corporate events & offsites** — want performers, emcees, artists for
  activities.
- **College fests & community events** — want affordable local performers.
- **Religious & cultural events** — want musicians, singers, storytellers.

### 6.2 The supply side (Creators)

The supply is vast and currently invisible:

- Hobbyist musicians (guitar, flute, keyboard, vocals) who play at home.
- Photographers who shoot as a side interest.
- Mehendi artists who currently rely on word-of-mouth in their neighborhood.
- Dancers, painters, storytellers, magicians, calligraphers.

The defining trait of RCube's supply: **they are not primarily seeking a career
change.** They want recognition and occasional, dignified paid opportunities. This is
a fundamentally different (and larger, and less contested) pool than professional
freelancers.

### 6.3 Market sizing (illustrative framing, MVP-stage)

We do not have precise figures at MVP stage and will not manufacture false precision.
We frame the opportunity structurally instead:

- **TAM (Total Addressable Market):** All local, in-person creative bookings in India
  — weddings, events, hospitality entertainment, personal celebrations. A
  multi-billion-dollar, overwhelmingly offline and cash-based market.
- **SAM (Serviceable Addressable Market):** Urban and semi-urban events in tier-1 and
  tier-2 cities where smartphone penetration, UPI payments, and event density make
  digital discovery viable.
- **SOM (Serviceable Obtainable Market) for MVP:** A single launch city (or a small
  cluster of neighborhoods), a focused set of categories (start with music,
  photography, mehendi), and a target of a **few hundred verified creators** and the
  **first hundred completed recognized bookings**.

**Decision.**
We launch **city-by-city**, not nationally. Recognition and trust are inherently
local; liquidity must be built locally before it can be built broadly.

**Reasoning.**
A radius-based, in-person product has zero value if supply and demand are geographically
sparse. One dense city with real liquidity beats ten cities with thin coverage. This
is the classic marketplace cold-start problem, and the answer is geographic focus.

**Product Implication.**
The system is multi-city capable from day one (city is a first-class field on
profiles and search), but go-to-market, admin verification capacity, and marketing
are concentrated on one launch city at a time.

### 6.4 Why now

- **UPI + Razorpay** make small-ticket digital payments frictionless in India.
- **Smartphone + cheap data** penetration reaches the hobbyist supply.
- **Post-pandemic experience economy** — demand for live, local, in-person
  experiences is structurally elevated.
- **Creator-economy cultural moment** — people increasingly identify as "creators,"
  but existing tools serve influencers, not local hobbyists.

---

## 7. Competitive Landscape & Positioning

**Vision.**
We win not by out-featuring incumbents but by occupying a position none of them can
occupy without abandoning their own model.

### 7.1 Positioning map

```
                     RECOGNITION-FIRST
                            ▲
                            │
                            │            ◎ RCube
                            │        (verified local
                            │         creators, booked
                            │         by strangers)
                            │
   LOCAL / IN-PERSON ◄──────┼──────► GLOBAL / REMOTE
                            │
      Urban Company ●       │        ● Fiverr / Upwork
      (standardized         │          (freelance gigs,
       services)            │           bid-based)
                            │
                            │        ● Instagram / TikTok
                            │          (attention / reach)
                            ▼
                     MONETIZATION / ATTENTION-FIRST
```

### 7.2 Competitor analysis

| Player | What they optimize | Why they can't be RCube |
|---|---|---|
| **Instagram / TikTok** | Attention, reach, ad revenue | Discovery is popularity-driven; no booking/trust/payment rails for local gigs; a hobbyist with 40 followers is invisible |
| **Fiverr / Upwork** | Task completion at lowest bid | Commoditizes creators; race-to-the-bottom pricing; global/remote, not local in-person recognition |
| **Urban Company** | Standardized home services | Treats providers as interchangeable labor; kills individual identity/recognition; wrong categories |
| **BookMyShow / event ticketing** | Ticketing for established acts | Serves professionals/celebrities, not hobbyists; no discovery of unknown local talent |
| **WhatsApp / word of mouth** | Nothing (default status quo) | No discovery, no verification, no trust, no payment protection, no recognition ledger |

### 7.3 Our moat (over time)

1. **The recognition ledger.** The cumulative, verified record of who was recognized,
   by whom, where, and how often. This is proprietary and compounding.
2. **Trust infrastructure.** Verification + escrow-style payments + OTP execution
   create a safety layer strangers rely on.
3. **Local liquidity.** Dense two-sided liquidity per city is hard to replicate.
4. **Brand as the home of recognition.** Owning the *meaning* of "getting recognized
   as a creator."

**Decision.**
We do not compete on price or on features. We compete on **trust** and **recognition**.

**Reasoning.**
Feature parity is copyable; a trusted recognition brand + a proprietary recognition
ledger + local liquidity is not.

---

## 8. Business Model & Unit Economics

**Vision.**
RCube is a trust intermediary. We earn by making it safe and easy for a stranger to
book a stranger. Our revenue is a *commission on recognized bookings*, aligned with
creator success.

### 8.1 Revenue model (MVP)

- **Commission on completed bookings.** The Organizer pays RCube the full service
  price. RCube deducts a **platform commission** and remits the remainder to the
  creator (manual payout in MVP).
- **Default commission rate:** configurable; assume **15%** as the MVP default
  (`platform_commission_pct`, a system config value, not hardcoded).

### 8.2 Money flow (MVP)

```
Organizer ──pays full price──► RCube (Razorpay) ──escrow-style hold──►
   on completion ──► RCube deducts commission ──► Admin manual payout ──► Creator
```

- RCube **collects** the entire amount up front (at the Payment Pending → Confirmed
  transition).
- RCube **holds** it until the event is Completed.
- After completion, the creator's earning becomes a **Pending Transfer** payout.
- Admin **manually** transfers the net amount (no automated payouts in MVP).

### 8.3 Illustrative unit economics (single booking)

Assume a ₹3,500 "60-minute live performance" booking, 15% commission:

| Line item | Amount |
|---|---|
| Service price (Organizer pays) | ₹3,500 |
| Platform commission (15%) | ₹525 |
| Payment gateway fee (~2%, borne by RCube in MVP) | ~₹70 |
| **Gross platform revenue** | ₹525 |
| **Net platform contribution** (after gateway) | ~₹455 |
| Creator payout (net) | ₹2,975 |

**Decision.**
In MVP, RCube **absorbs** the payment-gateway fee rather than passing it to either
party, to keep the pricing story clean ("the price you see is the price you pay").
This is revisited in Part VII (Tradeoffs).

**Reasoning.**
Clean pricing reduces friction for first-time organizers and preserves creator
dignity (the creator's advertised price is what the transaction is about). The gateway
fee is a small, known cost we accept to protect experience quality during the trust-
building phase.

### 8.4 Cost structure (MVP)

- Cloud infra (AWS) — modest at MVP scale.
- Payment gateway fees (absorbed).
- Manual operations: creator verification + manual payouts (human admin time — the
  dominant early cost, deliberately manual to preserve quality and learning).
- Customer support.

### 8.5 Path to sustainability

Recognition-first supply → higher-quality bookings → higher completion & repeat rates
→ organic growth via word of mouth (low CAC) → commission on a growing base of
recognized bookings. We deliberately keep take-rate modest to protect the ecosystem;
volume and retention, not take-rate, are the levers.

---
---

# PART III — THE PEOPLE

---

## 9. Personas

**Vision.**
We design for specific humans, not abstract "users." The three actors in the RCube
universe are the **Creator**, the **Organizer** (Host), and the **Admin**. A single
person can be both a Creator and an Organizer with **one account** — never two.

### 9.1 The Creator (primary persona)

The Creator is the heart of RCube. Everything is designed to give the Creator
recognition and dignity.

> **A creator is a hobbyist or creator who wants to showcase talent and be recognized.**

**Examples of creators:** Singer, Guitarist, Flautist, Dancer, Photographer, Mehendi
Artist, Storyteller, Painter, Musician.

**Goals:**
- Create a professional identity.
- Showcase talent.
- Get recognized by strangers.
- Receive opportunities.
- Earn income (as a consequence, not the headline).

#### Persona 9.1.a — "Arjun, the quiet flautist" (the founder archetype)

| Attribute | Detail |
|---|---|
| Age / context | 27, software engineer, plays flute for years as a hobby |
| Following | Small; a few dozen on Instagram, mostly friends |
| Motivation | Wants strangers to hear and appreciate his music |
| Fear | Public rejection; looking desperate; being ignored by venues |
| Success | A café books him for a 30-minute set and the room applauds |
| RCube value | A verified profile + inbound booking requests replace cold-emailing venues |

#### Persona 9.1.b — "Sneha, the mehendi artist"

| Attribute | Detail |
|---|---|
| Age / context | 24, does bridal & party mehendi on weekends, relies on neighborhood word-of-mouth |
| Motivation | Reach organizers outside her immediate locality; look professional |
| Fear | No-shows, non-payment, unsafe venues |
| Success | Repeat bookings across the city; a portfolio strangers trust |
| RCube value | Verification + upfront escrow payment removes payment risk; multiple services (bridal vs party mehendi) with clear prices |

#### Persona 9.1.c — "The multi-craft creator"

Some creators have more than one craft. RCube supports **multiple creator profiles
per account** (e.g., one person with a Singer profile, a Guitarist profile, and a
Storytelling profile). Each profile is independently categorized, described, priced,
verified, and searchable.

### 9.2 The Organizer / Host (demand persona)

> **Never use the word "consumer." Always "Organizer" or "Host."**

The Organizer discovers and books local talent. The word choice matters: an Organizer
is a partner in creating an experience, not a passive consumer of a service.

**Examples of organizers:** Birthday host, Wedding planner, Event organizer, Corporate
organizer, Café owner, Restaurant owner, Community organizer.

**Goals:**
- Discover local talent.
- Book creators.
- Organize memorable events.

#### Persona 9.2.a — "Priya, the birthday host"

| Attribute | Detail |
|---|---|
| Context | Planning a surprise 30th birthday at home |
| Need | A guitarist for ~1 hour of acoustic music |
| Fear | Booking someone unknown off the internet; getting scammed; a no-show |
| Success | Finds a verified guitarist nearby, pays safely, coordinates by phone after payment, event is a hit |
| RCube value | Radius + event-type search, verification badge, escrow payment, OTP-confirmed execution |

#### Persona 9.2.b — "Rahul, the café owner" (founder's brewpub, mirrored)

| Attribute | Detail |
|---|---|
| Context | Runs a brewpub; wants live acoustic acts on weekends to build ambience |
| Need | A steady, discoverable pool of local performers |
| Fear | Unreliable performers; awkward negotiation; quality inconsistency |
| Success | Books verified local acts weekly; discovers new talent easily |
| RCube value | Category + radius discovery of verified performers with clear service pricing |

### 9.3 The Admin (internal RCube team)

The Admin is the trust operator. In MVP, humans do the heavy lifting that will later
be automated — this is intentional (see philosophy: build data model for the future,
run operations manually today to learn).

**Responsibilities:**
- **Creator verification** — approve or reject creator profiles (identity + quality).
- **Profile approval** — gate what becomes searchable.
- **Booking oversight** — monitor and intervene in disputes/edge cases.
- **Payouts** — manually transfer creator earnings.
- **Support** — resolve issues for both sides.

#### Persona 9.3.a — "The verification admin"

Reviews Aadhaar front/back, profile quality, category correctness, and pricing
sanity. Optimizes for **trust** (only real, appropriate creators become searchable)
and **speed** (creators shouldn't wait days). Target verification SLA: **< 24 hours**.

### 9.4 Persona → capability matrix

| Capability | Creator | Organizer | Admin |
|---|---|---|---|
| Phone+OTP login | ✅ | ✅ | ✅ (admin panel: separate auth) |
| Switch Creator/Organizer mode | ✅ | ✅ | n/a |
| Create creator profiles | ✅ | — | — |
| Add services | ✅ | — | — |
| Search creators | — | ✅ | ✅ (read) |
| Raise booking request | — | ✅ | — |
| Accept/decline booking | ✅ | — | — |
| Pay for booking | — | ✅ | — |
| Enter event OTP | ✅ | — | — |
| Mark event completed | — | ✅ | ✅ (override) |
| Verify creators | — | — | ✅ |
| Process payouts | — | — | ✅ |
| View analytics | own only | own only | ✅ (global) |

---

## 10. User Journeys

Each journey follows the four-part lens where relevant, and ends with the emotional
state we are engineering for.

### 10.1 Journey — Creator onboarding & first profile

**Goal:** Turn an anonymous hobbyist into a verified, searchable, recognized creator.

```
[Download app]
    → [Enter phone number]
    → [Enter OTP]  ─────────────► Account created (single account)
    → [Choose intent: "I want to be discovered" → Creator Mode]
    → [Create Creator Profile]
          • Profile photo, cover photo
          • Category (e.g., Flautist)
          • Bio / description
          • City, languages
          • Instagram / YouTube (optional)
          • Aadhaar front + back (identity)
    → [Add at least one Service]
          • Title, price (duration/description optional)
    → [Submit for review]  ──────► Status: Pending Review
    → (Admin approves within SLA)  ──────► Status: Approved
    → Push notification: "You're verified and discoverable!"
    → Profile now appears in Organizer search
```

**Emotional target:** pride and legitimacy — "I am now a *recognized* creator, not
just someone who plays at home."

**Failure branch:** Admin rejects with a reason → creator notified privately with
actionable feedback → creator edits → resubmits. **Rejection is never public.**

### 10.2 Journey — Organizer discovery & booking

**Goal:** Let a stranger confidently book a stranger.

```
[Open app in Organizer Mode]
    → [Select Event Type] (Birthday / Wedding / Café Performance / ...)
    → [Select Category] (Guitarist / Photographer / Mehendi ...)
    → [Set Radius] (e.g., within 10 km)
    → [Browse results]  (verified creators, services, prices, verification badge)
    → [Open a creator profile]  (portfolio, services, verification)
    → [Select a Service]  (e.g., "60 min live performance — ₹3,500")
    → [Enter booking details]
          • Event date (DATE only — no time slots in MVP)
          • Venue
          • Notes
    → [Submit booking request]  ──────► Status: Pending
    → Creator notified
```

**Emotional target:** confidence — "This person is verified, the price is clear, and
my money is protected."

**Note on contact:** phone numbers are **NOT** shared at this stage. See 10.4 and the
Contact Sharing Rule (Part V, §18.5).

### 10.3 Journey — Booking acceptance & payment

```
[Creator receives request]  (Pending)
    → Creator reviews (service, date, venue, notes)
    → Creator [Accepts]  ──────► Status: Accepted → Payment Pending
         (or [Declines] ──────► Declined, or ignores until Expired)
    → Organizer notified: "Accepted! Complete payment to confirm."
    → Organizer pays via Razorpay (full service price)
    → Payment success  ──────► Status: Confirmed
    → ⚡ CONTACT REVEALED to BOTH parties
         • Organizer sees creator's phone
         • Creator sees organizer's phone
    → They coordinate exact timing manually (no in-app chat in MVP)
```

**Emotional target (creator):** validation — "A stranger paid to hear me."
**Emotional target (organizer):** relief — "It's booked and protected."

### 10.4 Journey — Event execution (the applause moment)

```
Event day:
    Creator arrives at venue
    → Organizer opens app → sees a 4–6 digit Event OTP
    → Organizer reads OTP to creator
    → Creator enters OTP in app  ──────► Status: Confirmed → In Progress
    → Creator performs  🎵  (the recognition moment — the brewpub applause)
    → After performance, Organizer [Marks Completed]  ──────► In Progress → Completed
    → Creator's earning becomes: Pending Transfer
    → Both parties see a warm completion screen
```

**Emotional target:** recognition realized — the entire product exists to produce
this moment. The OTP is not just a security mechanism; it is the ceremonial
handshake that says *"the performance is beginning."*

### 10.5 Journey — Creator payout

```
Booking Completed
    → Earning: Pending Transfer  (net of commission)
    → Admin reviews pending payouts queue
    → Admin initiates manual bank transfer
    → Payout: Transfer Initiated → Transferred
    → Creator notified: "You've been paid ₹X for [service]."
    (Failure branch: Transfer Initiated → Failed → Retry)
```

**Emotional target:** trust confirmed — money promised is money received.

### 10.6 Journey — Mode switching (same account)

```
Any user
    → Profile / settings → "Switch to Organizer Mode" / "Switch to Creator Mode"
    → UI + navigation reconfigure to the chosen mode
    → No re-login, no separate account
```

**Reasoning:** a creator is often also someone who hosts events. Forcing two accounts
would fracture identity and violate the "single account" decision.

### 10.7 Journey map (end-to-end, both sides)

```
CREATOR SIDE                         ADMIN                    ORGANIZER SIDE
────────────                         ─────                    ──────────────
Sign up (phone+OTP)
Create profile ───────────────► Verify profile
Add services                    (approve/reject)
   │                                                          Sign up (phone+OTP)
   │                                                          Search (type/cat/radius)
   │                                                          Open profile
   ▼                                                          Select service
Receive request ◄───────────────────────────────────────────  Raise booking (Pending)
Accept ─────────────────────────────────────────────────────► Notified (Payment Pending)
   │                                                          Pay (Razorpay)
Contact revealed ◄──────────── (Confirmed) ──────────────────► Contact revealed
Perform → enter OTP ◄──────────────────────────────────────── Show OTP (In Progress)
   │                                                          Mark Completed
Earning: Pending Transfer ◄──── Manual payout
Paid (Transferred) ◄─────────── Initiate transfer
```

---
---

# PART IV — THE EXPERIENCE

---

## 11. UX Principles & Design Language

**Vision.**
RCube should feel like a **warm, trustworthy studio**, not a corporate SaaS dashboard
or a hyper-optimized gig app. When a nervous first-time creator opens it, they should
feel *"this is a place that respects my craft."* When an organizer opens it, they
should feel *"I can trust this."*

### 11.1 Design language

| Attribute | Direction |
|---|---|
| Overall feel | Minimal, premium, creator-first, warm, community-oriented |
| NOT | Corporate, enterprise, cluttered, aggressive, salesy |
| Emotional tone | Encouraging, dignified, calm, confident |
| Density | Generous whitespace; one primary action per screen |
| Imagery | Creator photos are heroes; large, high-quality media |
| Motion | Subtle, reassuring; celebratory at recognition moments (approval, first booking, completion) |

### 11.2 Visual system (baseline tokens — engineering-ready)

These are **starting tokens**; the design team may refine, but the *feel* is fixed.

```
COLOR
  --rc-bg:            #FBF9F6   (warm off-white, paper-like)
  --rc-surface:       #FFFFFF
  --rc-ink:           #1E1B18   (near-black warm)
  --rc-ink-muted:     #6B6560
  --rc-primary:       #E4572E   (warm terracotta/coral — energy, warmth)
  --rc-primary-ink:   #FFFFFF
  --rc-accent:        #F2A65A   (soft amber — highlights, badges)
  --rc-success:       #2E7D5B   (verified / completed)
  --rc-warning:       #C9A227   (pending states)
  --rc-danger:        #B23A48   (declined / failed / destructive)
  --rc-verified:      #2E7D5B   (verification badge)

TYPOGRAPHY
  Display / headings: a warm humanist serif or premium grotesque
                      (e.g., "Fraunces" for headings, "Inter" for body)
  Body:               Inter / system sans, 16px base, 1.5 line-height
  Numerals (prices):  tabular figures

SPACING (8pt grid)
  4, 8, 12, 16, 24, 32, 48, 64

RADIUS
  Cards: 16px    Buttons: 12px    Avatars: full    Chips: full

ELEVATION
  Soft, low shadows only (no harsh drop shadows)

ICONOGRAPHY
  Rounded, friendly line icons; 24px default
```

### 11.3 Accessibility baseline

- Minimum tap target 44×44 pt.
- Text contrast ≥ WCAG AA (4.5:1 body).
- All actionable elements have accessible labels.
- Support dynamic type / font scaling.
- Never rely on color alone (badges carry text/icon too).
- Full support for RTL is deferred (English + Indian languages; MVP is LTR).

### 11.4 Tone of voice (microcopy)

- Warm and human: "You're verified! Time to get recognized." (not "Verification
  complete.")
- Never desperate or salesy toward creators.
- Respectful in failure: "This profile needs a couple of changes before it goes
  live." (never "Rejected.")
- See Appendix C for the full copy deck.

---

## 12. Information Architecture & Navigation Map

**Decision.**
The app has **two modes** (Creator, Organizer) reachable from **one account**, plus a
shared account/profile area. Navigation is **bottom-tab** based, and the tab set
changes with the active mode.

### 12.1 Global navigation map

```
                          ┌─────────────────────────┐
                          │      RCube Mobile App    │
                          └───────────┬─────────────┘
                                      │
                     ┌────────────────┴─────────────────┐
                     │                                    │
              (not authenticated)                  (authenticated)
                     │                                    │
        ┌────────────▼───────────┐            ┌──────────▼────────────┐
        │  AUTH FLOW             │            │   MODE ROOT (toggle)   │
        │  • Splash              │            │  Creator ⇄ Organizer   │
        │  • Phone entry         │            └───────┬──────┬─────────┘
        │  • OTP verify          │                    │      │
        │  • Mode intro          │        ┌───────────┘      └───────────┐
        └────────────────────────┘        │                              │
                                    ┌──────▼───────┐              ┌───────▼──────┐
                                    │ CREATOR MODE │              │ORGANIZER MODE│
                                    │  bottom tabs │              │  bottom tabs │
                                    └──────┬───────┘              └───────┬──────┘
                                           │                              │
        ┌──────────────────────────────────┤              ┌──────────────┤
        │  Tab 1: My Profiles              │              │  Tab 1: Discover (Search)
        │  Tab 2: Requests (bookings)      │              │  Tab 2: My Bookings
        │  Tab 3: Earnings                 │              │  Tab 3: (Discover detail)
        │  Tab 4: Account                  │              │  Tab 4: Account
        └──────────────────────────────────┘              └──────────────────────────┘
```

### 12.2 Creator Mode — tab structure

| Tab | Purpose | Key screens |
|---|---|---|
| **My Profiles** | Manage creator profiles & services | Profile list, Profile editor, Service editor, Verification status |
| **Requests** | Incoming booking requests & their lifecycle | Requests list (by status), Request detail, OTP entry |
| **Earnings** | Completed bookings & payout status | Earnings summary, Payout list, Payout detail |
| **Account** | Identity, mode switch, support | Account home, Mode switch, Support, Legal |

### 12.3 Organizer Mode — tab structure

| Tab | Purpose | Key screens |
|---|---|---|
| **Discover** | Search & browse creators | Search filters, Results list, Creator profile view, Service select, Booking form |
| **My Bookings** | Bookings the organizer raised | Bookings list (by status), Booking detail, Payment, OTP display, Mark complete |
| **Account** | Identity, mode switch, support | Same shared account area |

### 12.4 Admin panel (web) navigation

```
Admin (React web)
 ├── Dashboard (KPIs)
 ├── Verifications (queue: Pending Review profiles)
 ├── Bookings (all, filterable by state)
 ├── Payments (transactions, reconciliation)
 ├── Payouts (Pending Transfer queue → process)
 ├── Users (creators & organizers, one account model)
 ├── Support (tickets / manual interventions)
 └── Analytics (recognition metrics, funnels)
```

---

## 13. Screen Inventory

**Decision.**
The following is the **complete MVP screen list**. AI agents should implement exactly
these screens. Screens are grouped by area. IDs are stable references used elsewhere.

### 13.1 Auth & shared

| ID | Screen | Notes |
|---|---|---|
| A-01 | Splash | Logo, warm loading |
| A-02 | Phone number entry | Country code (+91 default), number |
| A-03 | OTP verification | 6-digit, auto-read, resend timer |
| A-04 | Mode intro / first-run | "Get recognized" (Creator) vs "Find talent" (Organizer) |
| S-01 | Account home | Name, phone, mode toggle, support, legal, logout |
| S-02 | Mode switch sheet | Creator ⇄ Organizer |
| S-03 | Support | Contact, FAQ, raise issue |
| S-04 | Legal | Terms, Privacy, community guidelines |
| S-05 | Notifications center | List of push notifications received |

### 13.2 Creator mode

| ID | Screen | Notes |
|---|---|---|
| C-01 | My Profiles (list) | All creator profiles + status chips |
| C-02 | Profile editor | Photos, category, bio, city, languages, socials, Aadhaar |
| C-03 | Verification status | Draft / Pending / Approved / Rejected + reason |
| C-04 | Services list (per profile) | Add/edit/remove services |
| C-05 | Service editor | Title, duration (opt), description (opt), price |
| C-06 | Requests list | Booking requests grouped by state |
| C-07 | Request detail (creator view) | Service, date, venue, notes, accept/decline |
| C-08 | OTP entry (event day) | Enter organizer-provided OTP |
| C-09 | Earnings summary | Totals, pending transfer, transferred |
| C-10 | Payout detail | Per-booking payout status |
| C-11 | Profile public preview | How organizers see this profile |

### 13.3 Organizer mode

| ID | Screen | Notes |
|---|---|---|
| O-01 | Discover / search filters | Event type, category, radius |
| O-02 | Search results | Cards: creator, top service, price, verified badge |
| O-03 | Creator profile (organizer view) | Portfolio, services, verification |
| O-04 | Service selection | Choose a service to book |
| O-05 | Booking form | Date, venue, notes → submit |
| O-06 | My Bookings list | Grouped by state |
| O-07 | Booking detail (organizer view) | State, actions (pay, view OTP, complete) |
| O-08 | Payment screen | Razorpay checkout, price breakdown |
| O-09 | Event OTP display | Shows OTP on event day |
| O-10 | Mark completed | Confirm event finished |
| O-11 | Booking confirmation | Contact revealed, next steps |

### 13.4 Screen count summary

- Auth & shared: **9**
- Creator: **11**
- Organizer: **11**
- **Total mobile MVP screens: 31** (excluding admin web)

---

## 14. Wireframes (ASCII)

**Note for implementers:** these ASCII wireframes define *layout intent and content
hierarchy*, not pixel-perfect design. Follow the design language in §11.

### 14.1 A-02 — Phone number entry

```
┌───────────────────────────────┐
│                               │
│                               │
│           ◆ RCube             │
│    the recognition cube       │
│                               │
│   Get recognized for what     │
│   you quietly practice.       │
│                               │
│   ┌─────┬───────────────────┐ │
│   │ +91 │  98765 43210      │ │
│   └─────┴───────────────────┘ │
│                               │
│   ┌───────────────────────┐   │
│   │      Continue  →      │   │
│   └───────────────────────┘   │
│                               │
│   By continuing you agree to  │
│   Terms & Privacy.            │
└───────────────────────────────┘
```

### 14.2 A-03 — OTP verification

```
┌───────────────────────────────┐
│  ‹ Back                        │
│                               │
│   Enter the code              │
│   Sent to +91 98765 43210     │
│                               │
│   ┌──┐┌──┐┌──┐┌──┐┌──┐┌──┐    │
│   │ 4││ 2││ 9││ 1││  ││  │    │
│   └──┘└──┘└──┘└──┘└──┘└──┘    │
│                               │
│   Resend code in 0:24         │
│                               │
│   ┌───────────────────────┐   │
│   │       Verify          │   │
│   └───────────────────────┘   │
└───────────────────────────────┘
```

### 14.3 A-04 — Mode intro (first run)

```
┌───────────────────────────────┐
│   Welcome to RCube            │
│   How do you want to start?   │
│                               │
│   ┌───────────────────────┐   │
│   │  🎨  I'm a Creator    │   │
│   │  Showcase your talent │   │
│   │  and get recognized.  │   │
│   │            [Start →]  │   │
│   └───────────────────────┘   │
│                               │
│   ┌───────────────────────┐   │
│   │  🎉  I'm an Organizer │   │
│   │  Discover & book      │   │
│   │  local talent.        │   │
│   │            [Start →]  │   │
│   └───────────────────────┘   │
│                               │
│   You can switch anytime.     │
└───────────────────────────────┘
```

### 14.4 C-01 — My Profiles (creator)

```
┌───────────────────────────────┐
│  My Profiles          [ + ]   │
│                               │
│  ┌───────────────────────┐    │
│  │ 🎼 Flautist           │    │
│  │ Bengaluru · English   │    │
│  │ ● Approved  ✓Verified │    │
│  │ 2 services            │    │
│  └───────────────────────┘    │
│                               │
│  ┌───────────────────────┐    │
│  │ 🎸 Guitarist          │    │
│  │ Bengaluru             │    │
│  │ ● Pending Review      │    │
│  │ 1 service             │    │
│  └───────────────────────┘    │
│                               │
│  ┌───────────────────────┐    │
│  │ + Create new profile  │    │
│  └───────────────────────┘    │
│                               │
│ [Profiles][Requests][₹][Acct] │
└───────────────────────────────┘
```

### 14.5 C-02 — Profile editor (creator)

```
┌───────────────────────────────┐
│  ‹  Edit Profile      [Save]  │
│                               │
│  ┌───────────────────────┐    │
│  │   Cover photo  [＋]    │    │
│  └───────────────────────┘    │
│      (◉) Profile photo [＋]   │
│                               │
│  Category                     │
│  [ Flautist            ▼ ]    │
│                               │
│  Bio                          │
│  ┌───────────────────────┐    │
│  │ I've played flute for │    │
│  │ 8 years...            │    │
│  └───────────────────────┘    │
│                               │
│  City      [ Bengaluru    ▼]  │
│  Languages [ English, Hindi ] │
│  Instagram [ @arjun.flute   ] │
│  YouTube   [ url (optional) ] │
│                               │
│  Identity (private, for       │
│  verification only)           │
│  Aadhaar Front [＋]  Back [＋] │
│                               │
│  ┌───────────────────────┐    │
│  │  Submit for review    │    │
│  └───────────────────────┘    │
└───────────────────────────────┘
```

### 14.6 C-05 — Service editor (creator)

```
┌───────────────────────────────┐
│  ‹  Add Service       [Save]  │
│                               │
│  Title                        │
│  [ 30 Minute Live Performance]│
│                               │
│  Duration (optional)          │
│  [ 30 min                   ] │
│                               │
│  Description (optional)       │
│  ┌───────────────────────┐    │
│  │ Acoustic flute set,   │    │
│  │ 6–8 pieces.           │    │
│  └───────────────────────┘    │
│                               │
│  Price (₹)                    │
│  [ 2000                     ] │
│                               │
│  ┌───────────────────────┐    │
│  │     Save service      │    │
│  └───────────────────────┘    │
└───────────────────────────────┘
```

### 14.7 C-06 — Requests list (creator)

```
┌───────────────────────────────┐
│  Requests                     │
│  [All][Pending][Active][Done] │
│                               │
│  ┌───────────────────────┐    │
│  │ ● Pending             │    │
│  │ 60 min performance    │    │
│  │ 12 Jul · Koramangala  │    │
│  │ ₹3,500                │    │
│  │ Expires in 22h        │    │
│  └───────────────────────┘    │
│                               │
│  ┌───────────────────────┐    │
│  │ ● Payment Pending     │    │
│  │ Birthday photoshoot   │    │
│  │ 18 Jul · Indiranagar  │    │
│  │ ₹5,000  (accepted)    │    │
│  └───────────────────────┘    │
│                               │
│  ┌───────────────────────┐    │
│  │ ● Confirmed           │    │
│  │ 30 min performance    │    │
│  │ 09 Jul · HSR  ₹2,000  │    │
│  │ 📞 Contact available  │    │
│  └───────────────────────┘    │
│                               │
│ [Profiles][Requests][₹][Acct] │
└───────────────────────────────┘
```

### 14.8 C-07 — Request detail (creator view, Pending)

```
┌───────────────────────────────┐
│  ‹  Booking Request           │
│                               │
│  Status: ● Pending            │
│  Expires in 22h 14m           │
│                               │
│  Service                      │
│  60 Minute Live Performance   │
│  ₹3,500                       │
│                               │
│  Event                        │
│  Date:  12 Jul 2026           │
│  Type:  House Party           │
│  Venue: Koramangala, Blr      │
│  Notes: "Acoustic, ~20 guests"│
│                               │
│  Organizer                    │
│  Priya  (contact hidden until │
│  payment)                     │
│                               │
│  ┌─────────┐   ┌───────────┐  │
│  │ Decline │   │  Accept   │  │
│  └─────────┘   └───────────┘  │
└───────────────────────────────┘
```

### 14.9 C-08 — OTP entry (creator, event day)

```
┌───────────────────────────────┐
│  ‹  Start Event               │
│                               │
│  30 Minute Live Performance   │
│  09 Jul · HSR Layout          │
│                               │
│  Ask the organizer for the    │
│  event code and enter it to   │
│  begin.                       │
│                               │
│   ┌──┐┌──┐┌──┐┌──┐┌──┐┌──┐    │
│   │  ││  ││  ││  ││  ││  │    │
│   └──┘└──┘└──┘└──┘└──┘└──┘    │
│                               │
│   ┌───────────────────────┐   │
│   │   Start performance   │   │
│   └───────────────────────┘   │
└───────────────────────────────┘
```

### 14.10 C-09 — Earnings (creator)

```
┌───────────────────────────────┐
│  Earnings                     │
│                               │
│  ┌───────────────────────┐    │
│  │ Pending transfer      │    │
│  │ ₹4,975                │    │
│  ├───────────────────────┤    │
│  │ Transferred (total)   │    │
│  │ ₹22,300               │    │
│  └───────────────────────┘    │
│                               │
│  Recent                       │
│  • ₹2,975  30min perf  ● Paid │
│  • ₹4,975  Photoshoot ● Pending│
│  • ₹1,700  Mehendi    ● Paid  │
│                               │
│ [Profiles][Requests][₹][Acct] │
└───────────────────────────────┘
```

### 14.11 O-01 — Discover / search (organizer)

```
┌───────────────────────────────┐
│  Discover talent              │
│                               │
│  Event type                   │
│  [Birthday][Wedding][Café]    │
│  [Corporate][College][House…] │
│                               │
│  Category                     │
│  [Singer][Guitarist][Mehendi] │
│  [Photographer][Dancer]…      │
│                               │
│  Within                       │
│  �────●──────  10 km           │
│                               │
│  ┌───────────────────────┐    │
│  │   Search creators     │    │
│  └───────────────────────┘    │
│                               │
│ [Discover][Bookings][Account] │
└───────────────────────────────┘
```

### 14.12 O-02 — Search results (organizer)

```
┌───────────────────────────────┐
│  ‹  Guitarists · Birthday     │
│     within 10 km · Bengaluru  │
│                               │
│  ┌───────────────────────┐    │
│  │ (◉) Arjun  ✓Verified  │    │
│  │ Guitarist · 4 km      │    │
│  │ from ₹2,000           │    │
│  │ "8 yrs, acoustic..."  │    │
│  └───────────────────────┘    │
│                               │
│  ┌───────────────────────┐    │
│  │ (◉) Meera  ✓Verified  │    │
│  │ Guitarist · 7 km      │    │
│  │ from ₹2,500           │    │
│  └───────────────────────┘    │
│                               │
│  ┌───────────────────────┐    │
│  │ (◉) Sam    ✓Verified  │    │
│  │ Guitarist · 9 km      │    │
│  │ from ₹3,000           │    │
│  └───────────────────────┘    │
│ [Discover][Bookings][Account] │
└───────────────────────────────┘
```

### 14.13 O-03 — Creator profile (organizer view)

```
┌───────────────────────────────┐
│  ‹                     [share]│
│  ┌───────────────────────┐    │
│  │      Cover photo      │    │
│  └───────────────────────┘    │
│   (◉)  Arjun   ✓ Verified     │
│   Guitarist · Bengaluru       │
│   English, Hindi              │
│                               │
│   About                       │
│   8 years of acoustic guitar… │
│                               │
│   ▶ Instagram   ▶ YouTube     │
│                               │
│   Services                    │
│   ┌───────────────────────┐   │
│   │ 30 min performance    │   │
│   │ ₹2,000        [Book]  │   │
│   ├───────────────────────┤   │
│   │ 60 min performance    │   │
│   │ ₹3,500        [Book]  │   │
│   └───────────────────────┘   │
└───────────────────────────────┘
```

### 14.14 O-05 — Booking form (organizer)

```
┌───────────────────────────────┐
│  ‹  Book Arjun                │
│                               │
│  Service                      │
│  60 Minute Live Performance   │
│  ₹3,500                       │
│                               │
│  Event date                   │
│  [ 12 Jul 2026          📅 ]  │
│  (date only — you'll fix the  │
│   time together after paying) │
│                               │
│  Event type                   │
│  [ House Party           ▼ ]  │
│                               │
│  Venue                        │
│  [ Koramangala, Bengaluru   ] │
│                               │
│  Notes                        │
│  ┌───────────────────────┐    │
│  │ ~20 guests, acoustic  │    │
│  └───────────────────────┘    │
│                               │
│  ┌───────────────────────┐    │
│  │   Send booking request│    │
│  └───────────────────────┘    │
└───────────────────────────────┘
```

### 14.15 O-08 — Payment (organizer)

```
┌───────────────────────────────┐
│  ‹  Confirm & Pay             │
│                               │
│  Arjun · 60 min performance   │
│  12 Jul · Koramangala         │
│                               │
│  Price breakdown              │
│  Service price      ₹3,500    │
│  ─────────────────────────    │
│  Total payable      ₹3,500    │
│                               │
│  🔒 Your payment is held      │
│  safely and released to the   │
│  creator after the event.     │
│                               │
│  Contact details are shared   │
│  right after payment.         │
│                               │
│  ┌───────────────────────┐    │
│  │   Pay ₹3,500          │    │
│  └───────────────────────┘    │
└───────────────────────────────┘
```

### 14.16 O-09 — Event OTP display (organizer, event day)

```
┌───────────────────────────────┐
│  ‹  Event Code                │
│                               │
│  Share this code with the     │
│  creator to start the event.  │
│                               │
│      ┌───────────────┐        │
│      │  4  2  9  1    │        │
│      └───────────────┘        │
│                               │
│  Arjun · 60 min performance   │
│  Today · Koramangala          │
│                               │
│  After the performance:       │
│  ┌───────────────────────┐    │
│  │   Mark as completed   │    │
│  └───────────────────────┘    │
└───────────────────────────────┘
```

### 14.17 Admin — verification queue (web)

```
┌──────────────────────────────────────────────────────────┐
│ RCube Admin           Verifications ▸ Pending (12)        │
├───────────────┬──────────────────────────────────────────┤
│ Dashboard     │  ┌────────────────────────────────────┐  │
│ ▸Verifications│  │ Arjun — Flautist — Bengaluru       │  │
│  Bookings     │  │ Submitted 2h ago                   │  │
│  Payments     │  │ [Profile] [Aadhaar F] [Aadhaar B]  │  │
│  Payouts      │  │ Services: 30m ₹2000, 60m ₹3500     │  │
│  Users        │  │ ┌─────────┐  ┌──────────────────┐  │  │
│  Support      │  │ │ Approve │  │ Reject (reason…) │  │  │
│  Analytics    │  │ └─────────┘  └──────────────────┘  │  │
│               │  └────────────────────────────────────┘  │
│               │  ┌────────────────────────────────────┐  │
│               │  │ Sneha — Mehendi — Bengaluru        │  │
│               │  │ ...                                 │  │
│               │  └────────────────────────────────────┘  │
└───────────────┴──────────────────────────────────────────┘
```

### 14.18 Admin — payouts queue (web)

```
┌──────────────────────────────────────────────────────────┐
│ RCube Admin              Payouts ▸ Pending Transfer (7)   │
├───────────────┬──────────────────────────────────────────┤
│ ...           │ Booking  Creator  Net      Status  Action │
│ ▸Payouts      │ #10231   Arjun    ₹2,975   Pending [Init] │
│               │ #10233   Sneha    ₹5,950   Pending [Init] │
│               │ #10234   Meera    ₹1,700   Initiated[Mark]│
│               │ #10229   Sam      ₹2,975   Failed  [Retry]│
│               │                                           │
│               │ Selected → [Export UTR CSV]               │
└───────────────┴──────────────────────────────────────────┘
```

---

## 15. Empty, Loading & Error States

**Vision.**
Empty and error states are where warmth and trust are won or lost. We treat them as
first-class design, never afterthoughts. Every list and async surface must define all
three states.

### 15.1 State matrix (per key surface)

| Surface | Empty | Loading | Error |
|---|---|---|---|
| C-01 My Profiles | "Create your first profile and get recognized." + CTA | Skeleton cards | "Couldn't load your profiles." + Retry |
| C-06 Requests | "No requests yet. They'll appear here when organizers book you." | Skeleton list | Retry |
| C-09 Earnings | "Your earnings will show here after your first completed event." | Skeleton | Retry |
| O-02 Search results | "No verified creators match yet. Try a wider radius or another category." | Skeleton cards | Retry |
| O-06 My Bookings | "You haven't booked anyone yet. Discover local talent." + CTA | Skeleton | Retry |
| A-03 OTP | n/a | Spinner on verify | "That code didn't work. Try again or resend." |
| O-08 Payment | n/a | Razorpay processing overlay | "Payment didn't go through. You weren't charged. Try again." |

### 15.2 Empty state pattern (ASCII)

```
┌───────────────────────────────┐
│                               │
│           ◇  ◇  ◇             │
│                               │
│   No requests yet             │
│   When an organizer books     │
│   you, it shows up here.      │
│                               │
│   ┌───────────────────────┐   │
│   │  Preview your profile │   │
│   └───────────────────────┘   │
│                               │
└───────────────────────────────┘
```

### 15.3 Error state pattern (ASCII)

```
┌───────────────────────────────┐
│                               │
│            ⚠                  │
│   Something went wrong        │
│   We couldn't load this.      │
│  Please check your connection.│
│                               │
│   ┌───────────┐               │
│   │   Retry   │               │
│   └───────────┘               │
└───────────────────────────────┘
```

### 15.4 Loading conventions

- **Skeletons** for content lists (cards, rows), not spinners.
- **Inline spinners** for button actions (Accept, Pay, Verify).
- **Optimistic UI** is avoided for money/state-changing actions (booking accept,
  payment, completion) — always confirm with the server before showing success.
- **Full-screen blocking overlays** only for payment processing.

### 15.5 State transition feedback (celebration moments)

Design must include subtle celebratory feedback at these recognition milestones:

- Profile **Approved** → confetti-lite + "You're verified!"
- **First booking request** received → warm highlight.
- Booking **Confirmed** (paid) → "It's on! 🎉 Contact shared."
- Event **Completed** → "Nicely done. Your earning is on the way."

---
---

# PART V — THE PRODUCT SPECIFICATION

---

## 16. Functional Requirements

**Convention.** Each requirement has an ID (`FR-<area>-<n>`), a priority (`P0` = MVP
must-have, `P1` = MVP should-have, `P2` = post-MVP), and acceptance criteria. AI
agents must implement all `P0` requirements to satisfy the MVP Definition of Done
(Appendix E).

### 16.1 Authentication & account (FR-AUTH)

| ID | Priority | Requirement |
|---|---|---|
| FR-AUTH-1 | P0 | Users authenticate with **phone number + OTP only**. No email/password. |
| FR-AUTH-2 | P0 | OTP delivery and verification via **Firebase Phone Auth**. |
| FR-AUTH-3 | P0 | A verified phone number maps to exactly **one account** (`user`). |
| FR-AUTH-4 | P0 | A single account can operate in **Creator Mode and Organizer Mode** without separate accounts. |
| FR-AUTH-5 | P0 | Mode is a UI/session concept; both capability sets are always available to the account. |
| FR-AUTH-6 | P0 | Sessions use backend-issued JWT (access + refresh) after Firebase token exchange. |
| FR-AUTH-7 | P0 | Logout revokes the refresh token server-side. |
| FR-AUTH-8 | P1 | OTP resend allowed after a cooldown (e.g., 30s), rate-limited per number. |

**Acceptance (FR-AUTH-1..4):** New phone → OTP → account created → user can toggle to
either mode from Account without re-auth.

### 16.2 Creator profiles (FR-PROF)

| ID | Priority | Requirement |
|---|---|---|
| FR-PROF-1 | P0 | A user can create **multiple creator profiles** (e.g., Singer + Guitarist). |
| FR-PROF-2 | P0 | A creator profile has: profile photo, cover photo, category, bio/description, city, languages, Instagram URL (opt), YouTube URL (opt), Aadhaar front, Aadhaar back. |
| FR-PROF-3 | P0 | Profile status lifecycle: **Draft → Pending Review → Approved / Rejected**. |
| FR-PROF-4 | P0 | Only **Approved** profiles are searchable by organizers. |
| FR-PROF-5 | P0 | Rejected profiles include an admin-provided **reason** shown privately to the creator. |
| FR-PROF-6 | P0 | Editing an Approved profile's material fields (category, photos, Aadhaar) sends it back to Pending Review (re-verification). Non-material edits (bio typo) configurable. |
| FR-PROF-7 | P0 | Aadhaar images are **private**, never shown to organizers, stored encrypted. |
| FR-PROF-8 | P0 | Category is chosen from a controlled list (see Appendix B). |
| FR-PROF-9 | P1 | A creator can preview exactly how their profile appears to organizers (C-11). |

### 16.3 Services (FR-SVC)

| ID | Priority | Requirement |
|---|---|---|
| FR-SVC-1 | P0 | A profile can contain **multiple services**. |
| FR-SVC-2 | P0 | A service has: title, price (required); duration, description (optional). |
| FR-SVC-3 | P0 | **No hourly pricing.** Pricing is per-service, fixed amount in INR. |
| FR-SVC-4 | P0 | A booking always **references exactly one service** (price snapshot captured at booking time). |
| FR-SVC-5 | P0 | Services can be added/edited/removed while the profile is Draft or Approved. |
| FR-SVC-6 | P1 | Editing/removing a service does not affect existing bookings (price is snapshotted). |
| FR-SVC-7 | P0 | A profile must have **at least one service** to be submitted for review. |

### 16.4 Discovery / search (FR-SEARCH)

| ID | Priority | Requirement |
|---|---|---|
| FR-SEARCH-1 | P0 | Organizers search by **Event Type**, **Category**, and **Radius**. |
| FR-SEARCH-2 | P0 | Only **Approved** creator profiles appear in results. |
| FR-SEARCH-3 | P0 | Radius filtering is based on the organizer's location vs the creator profile's city/location. |
| FR-SEARCH-4 | P0 | Results show: profile summary, top/starting service price, verification status. |
| FR-SEARCH-5 | P1 | Results are ordered by relevance (proximity first in MVP; recognition-based ranking later). |
| FR-SEARCH-6 | P1 | Empty results suggest widening radius or changing category. |
| FR-SEARCH-7 | P2 | Event Type maps to suggested categories (guidance), but does not hard-filter in MVP. |

### 16.5 Booking (FR-BOOK)

| ID | Priority | Requirement |
|---|---|---|
| FR-BOOK-1 | P0 | Organizer creates a booking request with: creator profile, service, **event date (DATE only)**, venue, notes. |
| FR-BOOK-2 | P0 | **No time-slot booking, no calendar, no availability engine** in MVP. Date only. |
| FR-BOOK-3 | P0 | Booking follows the state machine in §19.1. |
| FR-BOOK-4 | P0 | A creator can **Accept** or **Decline** a Pending request. |
| FR-BOOK-5 | P0 | A Pending request **auto-expires** after a configurable window (default 24h). |
| FR-BOOK-6 | P0 | After acceptance, organizer must pay within a configurable window (default 24h) or the booking hits **Payment Expired**. |
| FR-BOOK-7 | P0 | **Contact details are revealed only after Accept + Pay** (Confirmed). See §18.5. |
| FR-BOOK-8 | P0 | On the event day, the **OTP handshake** transitions Confirmed → In Progress. |
| FR-BOOK-9 | P0 | Organizer marks the event **Completed**; this creates the creator earning. |
| FR-BOOK-10 | P1 | A booking references an immutable snapshot of service title + price at creation. |
| FR-BOOK-11 | P1 | Multiple concurrent bookings for the same creator on the same date are **allowed** (no availability blocking in MVP — see Edge Cases §34). |

### 16.6 Payments (FR-PAY)

| ID | Priority | Requirement |
|---|---|---|
| FR-PAY-1 | P0 | Organizer pays the **full service price** to RCube via **Razorpay**. |
| FR-PAY-2 | P0 | Payment success transitions Payment Pending → **Confirmed**. |
| FR-PAY-3 | P0 | RCube **holds** funds (escrow-style) until completion. |
| FR-PAY-4 | P0 | Payment state machine per §19.4; verified via Razorpay signature + webhook. |
| FR-PAY-5 | P0 | RCube deducts a configurable **platform commission** on completion. |
| FR-PAY-6 | P0 | Idempotent handling of Razorpay webhooks (no double-confirm). |
| FR-PAY-7 | P1 | Failed/abandoned payments are recoverable (retry) until Payment Expired. |
| FR-PAY-8 | P2 | Refunds handled manually by Admin in MVP (documented flow, not automated). |

### 16.7 Event execution / OTP (FR-OTP)

| ID | Priority | Requirement |
|---|---|---|
| FR-OTP-1 | P0 | Each Confirmed booking has an **Event OTP** (distinct from login OTP). |
| FR-OTP-2 | P0 | Organizer app **displays** the Event OTP on/near the event day. |
| FR-OTP-3 | P0 | Creator **enters** the OTP to transition Confirmed → In Progress. |
| FR-OTP-4 | P0 | OTP state machine per §19.5 (Not Generated → Generated → Verified → Expired). |
| FR-OTP-5 | P1 | OTP is regenerable by Admin in edge cases (dispute/support). |

### 16.8 Payouts / earnings (FR-PAYOUT)

| ID | Priority | Requirement |
|---|---|---|
| FR-PAYOUT-1 | P0 | On Completion, a creator **earning** is created as **Pending Transfer** (net of commission). |
| FR-PAYOUT-2 | P0 | **Payouts are manual** in MVP (Admin initiates bank transfer). |
| FR-PAYOUT-3 | P0 | Payout state machine per §19.3 (Pending Transfer → Transfer Initiated → Transferred / Failed → Retry). |
| FR-PAYOUT-4 | P0 | Creator can see earnings: pending transfer total, transferred total, per-booking status. |
| FR-PAYOUT-5 | P1 | Admin records a payout reference (UTR) when marking Transferred. |

### 16.9 Notifications (FR-NOTIF)

| ID | Priority | Requirement |
|---|---|---|
| FR-NOTIF-1 | P0 | Push notifications via **Firebase Cloud Messaging (FCM)**. |
| FR-NOTIF-2 | P0 | Notifications fire on all state transitions in the Notification Matrix (§20). |
| FR-NOTIF-3 | P0 | Notifications are also persisted (in-app notification center S-05). |
| FR-NOTIF-4 | P1 | Notification preferences (mute categories) — deferred config, at least on/off. |

### 16.10 Admin (FR-ADMIN)

| ID | Priority | Requirement |
|---|---|---|
| FR-ADMIN-1 | P0 | Admin can view a **verification queue** and Approve/Reject profiles with reason. |
| FR-ADMIN-2 | P0 | Admin can view all **bookings** filterable by state. |
| FR-ADMIN-3 | P0 | Admin can view **payments** and reconcile against Razorpay. |
| FR-ADMIN-4 | P0 | Admin can process **payouts** (initiate, mark transferred, mark failed/retry). |
| FR-ADMIN-5 | P0 | Admin has a **dashboard** of KPIs (see §28). |
| FR-ADMIN-6 | P0 | Admin can access **support** context for any user/booking. |
| FR-ADMIN-7 | P1 | Admin can view **analytics** including (silent) recognition metrics. |
| FR-ADMIN-8 | P0 | Admin actions are **audit-logged** (§30). |
| FR-ADMIN-9 | P0 | Admin panel uses **separate, stronger authentication** than the consumer app (email+password+2FA or SSO). |

---

## 17. Non-Functional Requirements

**Convention.** `NFR-<area>-<n>`. These define quality attributes. They are testable.

### 17.1 Performance

| ID | Requirement | Target |
|---|---|---|
| NFR-PERF-1 | Search response (P95) | < 800 ms server-side |
| NFR-PERF-2 | Core API reads (P95) | < 300 ms |
| NFR-PERF-3 | App cold start (P95) | < 3 s on mid-range Android |
| NFR-PERF-4 | Image load (profile media) | Progressive, CDN-served, < 1.5 s perceived |
| NFR-PERF-5 | Push delivery latency | < 10 s from event to device (best-effort via FCM) |

### 17.2 Scalability

| ID | Requirement |
|---|---|
| NFR-SCALE-1 | Stateless API services, horizontally scalable behind a load balancer. |
| NFR-SCALE-2 | Support 10k creators / 100k organizers / 1k concurrent without redesign. |
| NFR-SCALE-3 | Read-heavy search path cacheable (Redis) and independently scalable. |
| NFR-SCALE-4 | DB designed with indices for search, booking lookups, payout queues. |

### 17.3 Availability & reliability

| ID | Requirement | Target |
|---|---|---|
| NFR-AVAIL-1 | Core booking/payment API uptime | 99.5% MVP |
| NFR-AVAIL-2 | No data loss on payments/bookings | RPO ≈ 0 for financial data (durable writes + audit) |
| NFR-AVAIL-3 | Graceful degradation | Search may degrade before booking/payment does |
| NFR-AVAIL-4 | Idempotency on all money-affecting operations | Required |

### 17.4 Security & privacy (summary — full model in §29)

| ID | Requirement |
|---|---|
| NFR-SEC-1 | All traffic over TLS 1.2+. |
| NFR-SEC-2 | Aadhaar & PII encrypted at rest; access strictly controlled & audit-logged. |
| NFR-SEC-3 | Contact info withheld until Confirmed (enforced server-side, not just UI). |
| NFR-SEC-4 | Payment verification via Razorpay signature; secrets in a secrets manager. |
| NFR-SEC-5 | Principle of least privilege for admin roles. |

### 17.5 Usability & accessibility

| ID | Requirement |
|---|---|
| NFR-UX-1 | WCAG AA color contrast for text. |
| NFR-UX-2 | Tap targets ≥ 44×44 pt. |
| NFR-UX-3 | Support device font scaling. |
| NFR-UX-4 | All async surfaces implement empty/loading/error states (§15). |

### 17.6 Observability & maintainability

| ID | Requirement |
|---|---|
| NFR-OBS-1 | Structured JSON logging with correlation/request IDs. |
| NFR-OBS-2 | Metrics on booking funnel, payment success rate, payout latency. |
| NFR-OBS-3 | Error tracking (e.g., Sentry) on mobile + backend. |
| NFR-OBS-4 | All domain events emitted to an event log (§27) for recognition + audit. |

### 17.7 Localization & compliance

| ID | Requirement |
|---|---|
| NFR-LOC-1 | English MVP; i18n-ready string catalog (no hardcoded UI strings). |
| NFR-COMP-1 | INR currency; amounts stored in **paise (integer)** to avoid float errors. |
| NFR-COMP-2 | Aadhaar handling complies with applicable Indian data norms; store minimally, encrypt, restrict. |
| NFR-COMP-3 | Payment handling within Razorpay's PCI-compliant flow (no card data touches RCube). |

---

## 18. Business Rules

**Vision.** Business rules are the *laws of physics* of RCube. They are enforced
**server-side** and are the same regardless of client. Each rule states the rule and
the reasoning.

### 18.1 Account & identity rules

- **BR-1.** One verified phone number = one account. *Why: identity integrity; the
  recognition ledger must attach to a single person.*
- **BR-2.** Modes (Creator/Organizer) are not separate accounts. *Why: a person's
  recognition and their hosting activity belong to one identity.*
- **BR-3.** A user acting as Organizer cannot book **their own** creator profile.
  *Why: prevents self-dealing / fake recognition.*

### 18.2 Profile & verification rules

- **BR-4.** A creator profile is searchable **only** when status = Approved. *Why:
  trust — organizers only ever see verified talent.*
- **BR-5.** A profile requires profile photo, category, bio, city, Aadhaar front +
  back, and ≥ 1 service to be submitted. *Why: minimum bar for a credible,
  bookable identity.*
- **BR-6.** Aadhaar images are never exposed to any non-admin user or in any API
  response consumed by the mobile app for another user. *Why: PII protection.*
- **BR-7.** Editing material fields of an Approved profile reverts it to Pending
  Review. *Why: verification must reflect current reality.*
- **BR-8.** A profile can be **suspended** by Admin (removed from search) without
  deletion. *Why: trust & safety response without losing history/ledger.*

### 18.3 Service & pricing rules

- **BR-9.** Prices are fixed per service, in INR, stored in **paise**. *Why:
  precision; no floating-point money.*
- **BR-10.** No hourly pricing. *Why: philosophy — creators sell recognizable
  experiences ("a 30-minute set"), not commoditized hours.*
- **BR-11.** A booking snapshots the service title + price at creation; later edits
  to the service don't change existing bookings. *Why: contractual integrity.*

### 18.4 Booking rules

- **BR-12.** A booking references exactly one creator profile + one service. *Why:
  clarity of what was booked.*
- **BR-13.** Pending requests expire after `booking_accept_window` (default 24h).
  *Why: organizers deserve timely answers; stale requests hurt experience.*
- **BR-14.** Accepted (Payment Pending) bookings expire after `payment_window`
  (default 24h) if unpaid → Payment Expired. *Why: creators shouldn't be held by
  non-committing organizers.*
- **BR-15.** Booking date must be **today or in the future** at creation. *Why: no
  booking the past.*
- **BR-16.** MVP does **not** block double-booking a creator on the same date. *Why:
  no availability engine in MVP; coordination is manual post-payment. (See Edge
  Cases §34 for creator guidance.)*
- **BR-17.** Only the creator who owns the profile may Accept/Decline; only the
  organizer who created the booking may Pay/Complete. *Why: authorization
  integrity.*

### 18.5 Contact-sharing rule (a constitutional rule)

> **BR-18 (CONTACT SHARING RULE).** Phone numbers are **NOT** revealed when a request
> is raised. Phone numbers are **NOT** revealed when the creator accepts. Phone
> numbers are revealed **ONLY AFTER** the creator has **Accepted** *AND* the organizer
> has **Paid** (i.e., booking = **Confirmed**). At that point:
>
> - The **Organizer** receives the **creator's** phone number.
> - The **Creator** receives the **organizer's** phone number.
>
> Both then coordinate timing **manually**. There is **no in-app chat**, **no
> availability engine**, **no calendar**, and **no scheduling complexity** in MVP.

- **Enforcement:** contact fields are stripped from all API responses until the
  booking's server-side status is Confirmed (or later). This is enforced in the
  serialization layer and re-checked at the endpoint. *Why: privacy + commitment.
  Contact is a reward for mutual commitment (accept + pay), which prevents spam,
  off-platform leakage before payment, and protects both parties.*

### 18.6 Payment & commission rules

- **BR-19.** The organizer pays the full service price to RCube before Confirmation.
  *Why: escrow-style trust; the creator knows the money is secured.*
- **BR-20.** Commission (`platform_commission_pct`, default 15%) is deducted on
  completion to compute the creator's net earning. *Why: aligned incentives; we earn
  when the creator succeeds.*
- **BR-21.** Funds are only released to payout after the event is **Completed**.
  *Why: protects the organizer against no-shows.*
- **BR-22.** All payment/webhook processing is **idempotent**. *Why: financial
  correctness under retries.*

### 18.7 Event execution rules

- **BR-23.** Confirmed → In Progress requires a **valid Event OTP** entered by the
  creator. *Why: proof the creator actually showed up; ceremonial start.*
- **BR-24.** Only the **organizer** can mark a booking Completed (In Progress →
  Completed). Admin may override in support cases. *Why: the organizer is the party
  who witnesses the performance.*
- **BR-25.** Completion is the sole trigger that creates a payout earning. *Why:
  single, auditable source of truth for money release.*

### 18.8 Payout rules

- **BR-26.** Payouts are manual in MVP; Admin records a reference (UTR) on transfer.
  *Why: learn the operational reality before automating.*
- **BR-27.** A failed payout can be retried; it never silently disappears. *Why:
  creators must always be paid; money owed is sacred.*

### 18.9 Trust & safety rules

- **BR-28.** Admin can suspend users/profiles and cancel bookings with reason
  (audit-logged). *Why: safety.*
- **BR-29.** Rejections and suspensions are communicated **privately** with a reason.
  *Why: dignity (philosophy §4.3.8).*

---

## 19. State Machines

**Vision.** RCube's correctness lives in its state machines. They are the backbone of
the booking economy. Each machine below is authoritative: states, transitions,
guards (conditions), actors, side effects, and notifications. Illegal transitions
must be rejected server-side with a clear error.

### 19.1 Booking state machine (the core machine)

**States:** `PENDING`, `ACCEPTED`, `PAYMENT_PENDING`, `CONFIRMED`, `IN_PROGRESS`,
`COMPLETED`, `DECLINED`, `EXPIRED`, `PAYMENT_EXPIRED`, `CANCELLED`.

> Note on ACCEPTED vs PAYMENT_PENDING: acceptance immediately opens the payment
> window. We model `ACCEPTED` as a transient state that auto-advances to
> `PAYMENT_PENDING` in the same transaction (acceptance = "accepted, awaiting
> payment"). Both are retained as distinct statuses for auditability and clarity;
> implementations may collapse the UI representation but must record both events.

```
                         ┌─────────────┐
        (organizer       │   PENDING   │
         raises request) └──────┬──────┘
                                │
        ┌───────────────┬───────┼───────────────┐
        │               │       │               │
   (creator        (creator  (24h no        (admin/system
    declines)       accepts)  action)         cancel)
        │               │       │               │
        ▼               ▼       ▼               ▼
  ┌──────────┐   ┌──────────┐ ┌─────────┐  ┌──────────┐
  │ DECLINED │   │ ACCEPTED │ │ EXPIRED │  │CANCELLED │
  └──────────┘   └────┬─────┘ └─────────┘  └──────────┘
                      │ (auto, same txn)
                      ▼
              ┌────────────────┐
              │PAYMENT_PENDING │
              └───────┬────────┘
              ┌───────┼─────────────────┐
        (organizer   (payment window     (admin
          pays OK)    elapses, unpaid)    cancel)
              │            │                │
              ▼            ▼                ▼
        ┌──────────┐ ┌───────────────┐ ┌──────────┐
        │CONFIRMED │ │PAYMENT_EXPIRED│ │CANCELLED │
        └────┬─────┘ └───────────────┘ └──────────┘
             │  ⚡ CONTACT REVEALED to both parties here
             │
       (creator enters valid Event OTP on event day)
             ▼
      ┌─────────────┐
      │ IN_PROGRESS │
      └──────┬──────┘
             │ (organizer marks completed)
             ▼
      ┌─────────────┐
      │  COMPLETED  │  → creates payout earning (Pending Transfer)
      └─────────────┘
```

**Transition table (authoritative):**

| # | From | To | Actor | Guard / condition | Side effects | Notifies |
|---|---|---|---|---|---|---|
| T1 | (none) | PENDING | Organizer | Valid creator+service+date(≥today)+venue | Create booking; snapshot price; set accept-expiry | Creator: new request |
| T2 | PENDING | ACCEPTED | Creator | Creator owns profile; still PENDING | Set payment-expiry | Organizer: accepted |
| T3 | ACCEPTED | PAYMENT_PENDING | System | Immediately after T2 (same txn) | Create payment intent (Razorpay order) | Organizer: pay now |
| T4 | PENDING | DECLINED | Creator | Creator owns profile | Close request | Organizer: declined |
| T5 | PENDING | EXPIRED | System | Now > accept-expiry | Close request | Organizer: expired |
| T6 | PAYMENT_PENDING | CONFIRMED | Organizer | Verified successful payment (signature+webhook) | Capture funds (escrow); **reveal contacts**; generate Event OTP (Not Generated→Generated) | Both: confirmed + contact |
| T7 | PAYMENT_PENDING | PAYMENT_EXPIRED | System | Now > payment-expiry; unpaid | Void order | Both: payment expired |
| T8 | CONFIRMED | IN_PROGRESS | Creator | Valid Event OTP; on/after event date window | Mark OTP Verified | Organizer: event started |
| T9 | IN_PROGRESS | COMPLETED | Organizer | Booking IN_PROGRESS | Compute commission; create earning (Pending Transfer) | Creator: completed + earning |
| T10 | PENDING/ACCEPTED/PAYMENT_PENDING/CONFIRMED | CANCELLED | Admin (or organizer pre-payment) | Policy-permitted | If paid: mark refund-required (manual) | Both: cancelled + reason |

**Terminal states:** `COMPLETED`, `DECLINED`, `EXPIRED`, `PAYMENT_EXPIRED`,
`CANCELLED`.

**Guards & rules recap:**
- Only PENDING can go to ACCEPTED/DECLINED/EXPIRED.
- CONFIRMED requires a verified payment; never trust the client.
- Contacts revealed exactly at CONFIRMED (T6), never earlier.
- Event OTP required for IN_PROGRESS (T8).
- Completion (T9) is the only earning-creating transition.

**Timers:**
- `accept-expiry = created_at + booking_accept_window` (default 24h).
- `payment-expiry = accepted_at + payment_window` (default 24h).
- Enforced by a scheduled job (§27) that emits T5/T7 as needed.

### 19.2 Creator profile state machine

**States:** `DRAFT`, `PENDING_REVIEW`, `APPROVED`, `REJECTED`, `SUSPENDED`.

```
   ┌───────┐  submit   ┌───────────────┐  approve  ┌──────────┐
   │ DRAFT │──────────►│ PENDING_REVIEW│──────────►│ APPROVED │
   └───┬───┘           └──────┬────────┘           └────┬─────┘
       ▲                      │ reject                  │ edit material fields
       │                      ▼                         │ (re-review)
       │                 ┌──────────┐                   ▼
       │  edit & resubmit│ REJECTED │            ┌───────────────┐
       └─────────────────┴────┬─────┘            │ PENDING_REVIEW│
                              │ edit & resubmit   └───────────────┘
                              └───────────────────────────►
       (any non-draft) ── admin suspend ──► ┌───────────┐
                                            │ SUSPENDED │── admin reinstate ──► APPROVED/PENDING
                                            └───────────┘
```

| From | To | Actor | Guard | Notifies |
|---|---|---|---|---|
| DRAFT | PENDING_REVIEW | Creator | Meets BR-5 minimum | Admin queue |
| PENDING_REVIEW | APPROVED | Admin | Verification passes | Creator: verified 🎉 |
| PENDING_REVIEW | REJECTED | Admin | With reason | Creator: needs changes (private) |
| REJECTED | PENDING_REVIEW | Creator | Edited & resubmitted | Admin queue |
| APPROVED | PENDING_REVIEW | System | Material edit (BR-7) | Creator: back to review |
| any | SUSPENDED | Admin | T&S with reason | Creator: suspended (private) |
| SUSPENDED | APPROVED/PENDING | Admin | Reinstated | Creator: reinstated |

### 19.3 Payout state machine

**States:** `PENDING_TRANSFER`, `TRANSFER_INITIATED`, `TRANSFERRED`, `FAILED`.

```
   Completed booking
        │
        ▼
 ┌──────────────────┐  admin initiates  ┌────────────────────┐
 │ PENDING_TRANSFER │──────────────────►│ TRANSFER_INITIATED │
 └──────────────────┘                   └─────────┬──────────┘
                                    success │      │ failure
                                            ▼      ▼
                                   ┌────────────┐ ┌────────┐
                                   │ TRANSFERRED│ │ FAILED │
                                   └────────────┘ └───┬────┘
                                        (terminal)    │ retry
                                                       ▼
                                          ┌────────────────────┐
                                          │ TRANSFER_INITIATED │
                                          └────────────────────┘
```

| From | To | Actor | Guard | Side effects | Notifies |
|---|---|---|---|---|---|
| (booking completed) | PENDING_TRANSFER | System | On T9 | Compute net = price − commission | Creator: earning pending |
| PENDING_TRANSFER | TRANSFER_INITIATED | Admin | Bank transfer started | Record initiator, timestamp | — |
| TRANSFER_INITIATED | TRANSFERRED | Admin | Transfer confirmed | Record UTR/reference | Creator: paid ✅ |
| TRANSFER_INITIATED | FAILED | Admin/System | Transfer failed | Record failure reason | Admin alert |
| FAILED | TRANSFER_INITIATED | Admin | Retry | New attempt | — |

**Creator earnings view mapping:** `Completed Booking → Pending Transfer →
Transferred` (as specified in the brief).

### 19.4 Payment state machine

**States:** `CREATED`, `PENDING`, `PAID`, `FAILED`, `EXPIRED`, `REFUND_REQUIRED`,
`REFUNDED`.

```
 ┌─────────┐ order created ┌─────────┐ user pays ┌────────┐
 │ CREATED │──────────────►│ PENDING │──────────►│  PAID  │→ booking CONFIRMED
 └─────────┘               └────┬────┘  verified └────────┘
                                │ fail / window elapse
                         ┌──────┼───────┐
                         ▼              ▼
                    ┌────────┐    ┌─────────┐
                    │ FAILED │    │ EXPIRED │
                    └────┬───┘    └─────────┘
                         │ retry (new order)
                         ▼
                    ┌─────────┐
                    │ PENDING │
                    └─────────┘

  PAID ── admin refund (cancellation) ──► REFUND_REQUIRED ── manual ──► REFUNDED
```

| From | To | Trigger | Guard | Notes |
|---|---|---|---|---|
| (none) | CREATED | T3 (booking accepted) | — | Razorpay order created |
| CREATED | PENDING | Checkout opened | — | User in Razorpay flow |
| PENDING | PAID | Payment success | **Signature verified + webhook** | Idempotent; drives T6 |
| PENDING | FAILED | Payment failure | — | Retryable |
| PENDING/CREATED | EXPIRED | payment_window elapsed | — | Drives T7 |
| PAID | REFUND_REQUIRED | Admin cancels confirmed booking | Policy | Manual refund in MVP |
| REFUND_REQUIRED | REFUNDED | Admin refund done | — | Record reference |

### 19.5 Event OTP state machine

**States:** `NOT_GENERATED`, `GENERATED`, `VERIFIED`, `EXPIRED`.

```
 ┌───────────────┐ booking CONFIRMED ┌───────────┐ creator enters ┌──────────┐
 │ NOT_GENERATED │──────────────────►│ GENERATED │───────────────►│ VERIFIED │
 └───────────────┘                   └─────┬─────┘  valid + window └──────────┘
                                           │ event window elapses      (→ IN_PROGRESS)
                                           ▼
                                      ┌─────────┐
                                      │ EXPIRED │  (admin can regenerate → GENERATED)
                                      └─────────┘
```

| From | To | Actor | Guard |
|---|---|---|---|
| NOT_GENERATED | GENERATED | System | On booking CONFIRMED (T6) |
| GENERATED | VERIFIED | Creator | Correct OTP within event window → triggers T8 |
| GENERATED | EXPIRED | System | Event window passed without verification |
| EXPIRED | GENERATED | Admin | Regenerate for support/dispute (FR-OTP-5) |

**OTP rules:** 4–6 digit numeric; rate-limit wrong attempts (e.g., 5 then lockout for
support); OTP value never returned to the creator's client — only displayed on the
organizer's client (organizer reads it out).

### 19.6 State ownership summary

| Machine | Source of truth | Who can advance |
|---|---|---|
| Booking | `booking.status` | Creator, Organizer, System, Admin (per table) |
| Profile | `creator_profile.status` | Creator, Admin |
| Payment | `payment.status` | System (Razorpay), Admin |
| Payout | `payout.status` | System (create), Admin |
| Event OTP | `event_otp.status` | System, Creator, Admin |

---

## 20. Notification Matrix

**Vision.** Notifications are how RCube keeps two strangers coordinated without chat.
Every state change that matters to a human triggers a notification. Copy is warm
(Appendix C). Channel: **FCM push + persisted in-app** (S-05).

| # | Event / transition | Recipient(s) | Channel | Push title (short) | Deep link |
|---|---|---|---|---|---|
| N1 | Booking created (T1) | Creator | Push+InApp | "New booking request 🎵" | C-07 request detail |
| N2 | Booking accepted (T2/T3) | Organizer | Push+InApp | "Accepted — pay to confirm" | O-08 payment |
| N3 | Booking declined (T4) | Organizer | Push+InApp | "Request not accepted" | O-06 bookings |
| N4 | Booking expired (T5) | Organizer | Push+InApp | "Your request expired" | O-01 discover |
| N5 | Payment success / Confirmed (T6) | Both | Push+InApp | "It's confirmed! 🎉 Contact shared" | booking detail |
| N6 | Payment expired (T7) | Both | Push+InApp | "Payment window closed" | booking detail |
| N7 | Event started via OTP (T8) | Organizer | Push+InApp | "Performance started" | O-07 |
| N8 | Event completed (T9) | Creator | Push+InApp | "Completed — earning on the way" | C-09 earnings |
| N9 | Profile approved | Creator | Push+InApp | "You're verified! ✓" | C-03 status |
| N10 | Profile rejected | Creator | Push+InApp | "Profile needs a few changes" | C-02 editor |
| N11 | Profile suspended/reinstated | Creator | Push+InApp | "Profile update" | C-03 |
| N12 | Payout transferred | Creator | Push+InApp | "You've been paid ₹X ✅" | C-10 payout |
| N13 | Booking cancelled (T10) | Both | Push+InApp | "Booking cancelled" | booking detail |
| N14 | Payment failed | Organizer | Push+InApp | "Payment didn't go through" | O-08 |
| N15 | Reminder: payment pending (T+Xh) | Organizer | Push | "Complete payment to confirm" | O-08 |
| N16 | Reminder: event tomorrow | Both | Push | "Your event is tomorrow" | booking detail |
| N17 | Reminder: pending request (creator, T+Xh) | Creator | Push | "A request is waiting" | C-07 |

**Notification rules:**
- Every notification is also stored in `notification` (S-05 list), so nothing is lost
  if push fails.
- Reminders (N15–N17) are scheduled jobs (§27), configurable, and idempotent.
- No notification ever contains a phone number before Confirmed (BR-18).

---
---

# PART VI — THE ENGINEERING

---

## 21. System Architecture Overview

**Vision.** A boring, reliable, horizontally-scalable architecture. Nothing exotic.
The cleverness is in the domain model and the recognition ledger, not the infra.

### 21.1 High-level diagram

```
        ┌──────────────────┐        ┌────────────────────┐
        │  Flutter App     │        │  React Admin (web) │
        │ (Creator +       │        │  (internal team)   │
        │  Organizer modes)│        └─────────┬──────────┘
        └────────┬─────────┘                  │
                 │ HTTPS (JWT)                 │ HTTPS (admin JWT/SSO)
                 ▼                             ▼
        ┌───────────────────────────────────────────────┐
        │            API Gateway / Load Balancer         │  (AWS ALB)
        └───────────────────────┬───────────────────────┘
                                │
        ┌───────────────────────▼───────────────────────┐
        │        FastAPI application (stateless)         │
        │  ┌─────────┬─────────┬─────────┬────────────┐  │
        │  │ Auth    │ Profiles│ Booking │ Payments   │  │
        │  │ Search  │ Payouts │ Notif   │ Admin      │  │
        │  └─────────┴─────────┴─────────┴────────────┘  │
        │        Domain services + Event bus (outbox)    │
        └───┬─────────────┬────────────┬──────────┬──────┘
            │             │            │          │
            ▼             ▼            ▼          ▼
     ┌───────────┐ ┌───────────┐ ┌─────────┐ ┌──────────┐
     │PostgreSQL │ │  Redis    │ │  AWS S3 │ │ Workers  │
     │(primary)  │ │(cache/    │ │(media,  │ │(scheduler│
     │           │ │ queues)   │ │ Aadhaar)│ │ + events)│
     └───────────┘ └───────────┘ └─────────┘ └──────────┘
            │
   ┌────────┴─────────────────────────────────────┐
   │ External services                            │
   │  • Firebase Auth (phone OTP)                 │
   │  • Firebase Cloud Messaging (push)           │
   │  • Razorpay (payments + webhooks)            │
   └──────────────────────────────────────────────┘
```

### 21.2 Component responsibilities

| Component | Responsibility |
|---|---|
| Flutter app | Both user modes; talks only to FastAPI (never DB/Razorpay secrets) |
| React admin | Internal ops (verification, bookings, payouts, analytics) |
| FastAPI | All business logic, state machines, authz, serialization (contact-stripping) |
| PostgreSQL | System of record (users, profiles, services, bookings, payments, payouts, events, audit) |
| Redis | Search cache, rate limiting, ephemeral locks, scheduled-job coordination |
| S3 | Media (profile/cover), Aadhaar (encrypted, private bucket) |
| Workers | Timers (expiries), reminders, event/outbox dispatch, FCM sends |
| Firebase Auth | Phone OTP issuance/verification |
| FCM | Push delivery |
| Razorpay | Payment collection + webhooks |

### 21.3 Environments

`local` → `staging` → `production`. Each with isolated DB, S3 bucket, Razorpay keys
(test vs live), Firebase project. Config via environment variables / AWS Secrets
Manager (Appendix D).

---

## 22. Domain-Driven Design

**Vision.** We model RCube as a set of **bounded contexts** with clear ownership. This
keeps the codebase legible and lets teams (and AI agents) work in parallel without
stepping on each other.

### 22.1 Bounded contexts

```
┌───────────────────────────────────────────────────────────────┐
│                         RCube Domain                           │
│                                                               │
│  ┌──────────────┐  ┌──────────────┐  ┌───────────────────┐    │
│  │  Identity &  │  │  Creator     │  │   Discovery       │    │
│  │  Access      │  │  Catalog     │  │   (Search)        │    │
│  │  (users,     │  │  (profiles,  │  │                   │    │
│  │   sessions,  │  │   services,  │  │                   │    │
│  │   modes)     │  │   verify)    │  │                   │    │
│  └──────────────┘  └──────────────┘  └───────────────────┘    │
│                                                               │
│  ┌──────────────┐  ┌──────────────┐  ┌───────────────────┐    │
│  │  Booking     │  │  Payments &  │  │   Recognition     │    │
│  │  (lifecycle, │  │  Payouts     │  │   Ledger          │    │
│  │   OTP,       │  │  (escrow,    │  │  (events, future  │    │
│  │   contact    │  │   commission,│  │   score) — SILENT │    │
│  │   reveal)    │  │   payouts)   │  │   in MVP          │    │
│  └──────────────┘  └──────────────┘  └───────────────────┘    │
│                                                               │
│  ┌──────────────┐  ┌──────────────┐                          │
│  │ Notifications│  │  Admin &     │                          │
│  │              │  │  Trust/Safety│                          │
│  └──────────────┘  └──────────────┘                          │
└───────────────────────────────────────────────────────────────┘
```

### 22.2 Context responsibilities & core aggregates

| Context | Aggregate root(s) | Owns | Key invariants |
|---|---|---|---|
| Identity & Access | `User`, `Session` | Phone identity, JWT, mode | One phone = one user (BR-1) |
| Creator Catalog | `CreatorProfile` (with `Service` entities) | Profiles, services, verification lifecycle | Approved-only searchable (BR-4); Aadhaar private (BR-6) |
| Discovery | (read model over Catalog) | Search index/cache | Only Approved surfaced |
| Booking | `Booking` (with `EventOtp`) | Booking lifecycle, contact reveal | State machine (§19.1); reveal at Confirmed (BR-18) |
| Payments & Payouts | `Payment`, `Payout` | Razorpay orders, escrow, commission, payouts | Idempotent (BR-22); pay before confirm (BR-19) |
| Recognition Ledger | `DomainEvent` | Immutable event stream | Append-only; never mutated |
| Notifications | `Notification` | Push + in-app records | Never leak contact pre-confirm |
| Admin & Trust/Safety | `AdminUser`, `AuditLog` | Verification, payouts, moderation | All actions audited (§30) |

### 22.3 Ubiquitous language (canonical terms)

- **Creator** — a person offering a craft (never "provider," "seller," "vendor").
- **Organizer / Host** — a person booking a creator (never "consumer," "buyer,"
  "customer").
- **Profile** — a single creator identity (a user may have several).
- **Service** — a fixed-price offering on a profile (never "gig," "listing," "hour").
- **Booking** — a request for a service on a date (never "order," "job").
- **Recognition** — appreciation by strangers; the ledgered outcome.
- **Event OTP** — the code that starts a booked event (distinct from login OTP).

### 22.4 Context map (relationships)

```
Identity&Access ──(user id)──► Creator Catalog ──(profile/service)──► Discovery
       │                              │
       │                              ▼
       └──(user id)──► Booking ◄──(references)── Payments&Payouts
                          │
                          ├──emits──► Recognition Ledger (events)
                          └──emits──► Notifications
Admin&TrustSafety ──oversees──► (all contexts, audited)
```

---

## 23. Database Design & Entity Relationships

**Vision.** PostgreSQL is the system of record. The schema is designed so the
recognition ledger and future features (reviews, scores, calendars) can be added
**without migration pain** (philosophy §5). Money is stored in **paise (BIGINT)**.
UUIDs are primary keys. Every table has `created_at`, `updated_at`.

### 23.1 ER diagram (logical)

```
┌─────────────┐        ┌────────────────────┐        ┌──────────────┐
│    user     │1      *│  creator_profile   │1      *│   service    │
│─────────────│────────│────────────────────│────────│──────────────│
│ id (PK)     │        │ id (PK)            │        │ id (PK)      │
│ phone (uq)  │        │ user_id (FK)       │        │ profile_id FK│
│ display_name│        │ category           │        │ title        │
│ ...         │        │ bio, city, ...     │        │ price_paise  │
└──────┬──────┘        │ status             │        │ duration_min │
       │               │ aadhaar refs (enc) │        │ description  │
       │               └─────────┬──────────┘        └──────┬───────┘
       │                         │                          │
       │ (organizer)             │ (creator side)           │
       │                         │                          │
       │            ┌────────────▼───────────┐              │
       │           *│        booking         │*─────────────┘
       └────────────│────────────────────────│ (service snapshot)
       (organizer   │ id (PK)                │
        user_id FK) │ organizer_user_id FK   │
                    │ creator_profile_id FK  │
                    │ service_id FK (nullable on delete)│
                    │ service_title_snap     │
                    │ price_paise_snap       │
                    │ event_date             │
                    │ event_type             │
                    │ venue, notes           │
                    │ status                 │
                    └───┬───────┬────────┬───┘
                        │1     1│        │1
              ┌─────────▼─┐ ┌───▼──────┐ │ ┌──────────────┐
              │ event_otp │ │ payment  │ └►│    payout    │
              │───────────│ │──────────│   │──────────────│
              │ booking FK│ │ booking  │   │ booking FK   │
              │ code_hash │ │ razorpay │   │ creator_prof │
              │ status    │ │ order_id │   │ net_paise    │
              └───────────┘ │ status   │   │ commission   │
                            │ amount   │   │ status, utr  │
                            └──────────┘   └──────────────┘

┌──────────────┐   ┌───────────────┐   ┌──────────────┐   ┌───────────────┐
│ notification │   │  domain_event │   │  audit_log   │   │  admin_user   │
│──────────────│   │───────────────│   │──────────────│   │───────────────│
│ user_id FK   │   │ type          │   │ actor_type   │   │ email (uq)    │
│ type,title   │   │ aggregate_id  │   │ actor_id     │   │ role          │
│ body,payload │   │ payload(jsonb)│   │ action       │   │ pw_hash / SSO │
│ read_at      │   │ occurred_at   │   │ entity, diff │   │ ...           │
└──────────────┘   └───────────────┘   └──────────────┘   └───────────────┘

┌──────────────────┐   ┌───────────────┐
│ device_token     │   │ system_config │
│ (FCM per user)   │   │ (key/value)   │
└──────────────────┘   └───────────────┘
```

### 23.2 Table catalog

Full DDL is in §23.4. Summary:

| Table | Purpose |
|---|---|
| `user` | One per phone; both modes |
| `session` | Refresh tokens / device sessions |
| `device_token` | FCM tokens per user/device |
| `creator_profile` | A creator identity; verification lifecycle |
| `service` | Fixed-price offerings under a profile |
| `booking` | Core lifecycle object |
| `event_otp` | Event-start OTP per confirmed booking |
| `payment` | Razorpay order + verification state |
| `payout` | Manual payout tracking |
| `notification` | In-app + push record |
| `domain_event` | Append-only recognition/event ledger |
| `audit_log` | Admin & sensitive-action audit trail |
| `admin_user` | Internal admin accounts (separate auth) |
| `system_config` | Tunables (windows, commission %) |

### 23.3 Key design decisions

- **UUID PKs** (`uuid` / `gen_random_uuid()`), not serial. *Why: safe to expose,
  merge-friendly, no enumeration.*
- **Money in paise (BIGINT).** *Why: precision (NFR-COMP-1).*
- **Price snapshot on booking** (`service_title_snap`, `price_paise_snap`). *Why:
  BR-11 contractual integrity even if service edited/deleted.*
- **`service_id` FK is `ON DELETE SET NULL`**; snapshots preserve the contract.
- **Enum columns** stored as `TEXT` with `CHECK` constraints (portable, readable) —
  not native PG enums (easier to evolve). Values in Appendix B.
- **`domain_event` append-only** — no updates/deletes; the recognition ledger.
- **Soft-delete / status** over hard delete for profiles/users (trust & ledger).
- **Timestamps in UTC** (`timestamptz`).
- **Indices** on all FKs, `booking.status`, `booking.event_date`,
  `creator_profile.status`, `creator_profile.city`, `(category, city)` for search,
  and geospatial (see §23.5).

### 23.4 Schema DDL (PostgreSQL)

```sql
-- Extensions
CREATE EXTENSION IF NOT EXISTS "pgcrypto";      -- gen_random_uuid()
CREATE EXTENSION IF NOT EXISTS "postgis";       -- radius search (optional; see §23.5)

-- ─────────────────────────────────────────────
-- Identity & Access
-- ─────────────────────────────────────────────
CREATE TABLE app_user (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    phone           VARCHAR(20) NOT NULL UNIQUE,        -- E.164, e.g. +919876543210
    display_name    VARCHAR(120),
    firebase_uid    VARCHAR(128) UNIQUE,                -- from Firebase Auth
    default_mode    TEXT NOT NULL DEFAULT 'organizer'   -- 'creator' | 'organizer'
                    CHECK (default_mode IN ('creator','organizer')),
    status          TEXT NOT NULL DEFAULT 'active'
                    CHECK (status IN ('active','suspended','deleted')),
    -- organizer-side location (for search origin), optional until first search
    last_lat        DOUBLE PRECISION,
    last_lng        DOUBLE PRECISION,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE session (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    refresh_token_hash TEXT NOT NULL,
    device_info     TEXT,
    revoked_at      TIMESTAMPTZ,
    expires_at      TIMESTAMPTZ NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_session_user ON session(user_id);

CREATE TABLE device_token (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    fcm_token       TEXT NOT NULL,
    platform        TEXT CHECK (platform IN ('android','ios')),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (fcm_token)
);
CREATE INDEX idx_device_token_user ON device_token(user_id);

-- ─────────────────────────────────────────────
-- Creator Catalog
-- ─────────────────────────────────────────────
CREATE TABLE creator_profile (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    category        TEXT NOT NULL,                      -- see Appendix B
    display_name    VARCHAR(120) NOT NULL,
    bio             TEXT,
    city            VARCHAR(120) NOT NULL,
    lat             DOUBLE PRECISION,
    lng             DOUBLE PRECISION,
    languages       TEXT[],                             -- e.g. {'English','Hindi'}
    profile_photo_url TEXT,
    cover_photo_url TEXT,
    instagram_url   TEXT,
    youtube_url     TEXT,
    -- private identity (encrypted at rest; store S3 keys, not public URLs)
    aadhaar_front_key TEXT,
    aadhaar_back_key  TEXT,
    status          TEXT NOT NULL DEFAULT 'draft'
                    CHECK (status IN ('draft','pending_review','approved','rejected','suspended')),
    rejection_reason TEXT,
    reviewed_by     UUID REFERENCES admin_user(id),
    reviewed_at     TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_profile_user     ON creator_profile(user_id);
CREATE INDEX idx_profile_status   ON creator_profile(status);
CREATE INDEX idx_profile_search   ON creator_profile(category, city) WHERE status = 'approved';

CREATE TABLE service (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    profile_id      UUID NOT NULL REFERENCES creator_profile(id) ON DELETE CASCADE,
    title           VARCHAR(160) NOT NULL,
    duration_min    INTEGER,                            -- optional
    description     TEXT,                               -- optional
    price_paise     BIGINT NOT NULL CHECK (price_paise > 0),
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_service_profile ON service(profile_id);

-- ─────────────────────────────────────────────
-- Booking
-- ─────────────────────────────────────────────
CREATE TABLE booking (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organizer_user_id   UUID NOT NULL REFERENCES app_user(id) ON DELETE RESTRICT,
    creator_profile_id  UUID NOT NULL REFERENCES creator_profile(id) ON DELETE RESTRICT,
    creator_user_id     UUID NOT NULL REFERENCES app_user(id) ON DELETE RESTRICT, -- denormalized for notifs/payouts
    service_id          UUID REFERENCES service(id) ON DELETE SET NULL,
    service_title_snap  VARCHAR(160) NOT NULL,          -- snapshot (BR-11)
    price_paise_snap    BIGINT NOT NULL CHECK (price_paise_snap > 0),
    event_date          DATE NOT NULL,                  -- DATE ONLY (BR-15, FR-BOOK-2)
    event_type          TEXT NOT NULL,                  -- see Appendix B
    venue               TEXT NOT NULL,
    notes               TEXT,
    status              TEXT NOT NULL DEFAULT 'pending'
                        CHECK (status IN ('pending','accepted','payment_pending',
                                          'confirmed','in_progress','completed',
                                          'declined','expired','payment_expired','cancelled')),
    accept_expires_at   TIMESTAMPTZ,                    -- created_at + accept window
    payment_expires_at  TIMESTAMPTZ,                    -- accepted_at + payment window
    accepted_at         TIMESTAMPTZ,
    confirmed_at        TIMESTAMPTZ,                    -- contact reveal time
    in_progress_at      TIMESTAMPTZ,
    completed_at        TIMESTAMPTZ,
    cancelled_reason    TEXT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    CHECK (creator_user_id <> organizer_user_id)        -- BR-3 no self-booking
);
CREATE INDEX idx_booking_organizer ON booking(organizer_user_id);
CREATE INDEX idx_booking_creator   ON booking(creator_user_id);
CREATE INDEX idx_booking_profile   ON booking(creator_profile_id);
CREATE INDEX idx_booking_status    ON booking(status);
CREATE INDEX idx_booking_eventdate ON booking(event_date);
CREATE INDEX idx_booking_expiries  ON booking(status, accept_expires_at, payment_expires_at);

CREATE TABLE event_otp (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    booking_id      UUID NOT NULL UNIQUE REFERENCES booking(id) ON DELETE CASCADE,
    code_hash       TEXT NOT NULL,                      -- hashed OTP, never plaintext at rest
    status          TEXT NOT NULL DEFAULT 'generated'
                    CHECK (status IN ('not_generated','generated','verified','expired')),
    attempts        INTEGER NOT NULL DEFAULT 0,
    verified_at     TIMESTAMPTZ,
    expires_at      TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ─────────────────────────────────────────────
-- Payments & Payouts
-- ─────────────────────────────────────────────
CREATE TABLE payment (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    booking_id          UUID NOT NULL REFERENCES booking(id) ON DELETE RESTRICT,
    razorpay_order_id   VARCHAR(64) UNIQUE,
    razorpay_payment_id VARCHAR(64) UNIQUE,
    razorpay_signature  TEXT,
    amount_paise        BIGINT NOT NULL CHECK (amount_paise > 0),
    currency            VARCHAR(3) NOT NULL DEFAULT 'INR',
    status              TEXT NOT NULL DEFAULT 'created'
                        CHECK (status IN ('created','pending','paid','failed',
                                          'expired','refund_required','refunded')),
    idempotency_key     VARCHAR(80) UNIQUE,
    failure_reason      TEXT,
    paid_at             TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_payment_booking ON payment(booking_id);
CREATE INDEX idx_payment_status  ON payment(status);

CREATE TABLE payout (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    booking_id          UUID NOT NULL UNIQUE REFERENCES booking(id) ON DELETE RESTRICT,
    creator_profile_id  UUID NOT NULL REFERENCES creator_profile(id) ON DELETE RESTRICT,
    creator_user_id     UUID NOT NULL REFERENCES app_user(id) ON DELETE RESTRICT,
    gross_paise         BIGINT NOT NULL,                -- = price_paise_snap
    commission_paise    BIGINT NOT NULL,                -- gross * commission_pct
    net_paise           BIGINT NOT NULL,                -- gross - commission
    status              TEXT NOT NULL DEFAULT 'pending_transfer'
                        CHECK (status IN ('pending_transfer','transfer_initiated','transferred','failed')),
    utr_reference       VARCHAR(64),                    -- bank transfer ref
    initiated_by        UUID REFERENCES admin_user(id),
    failure_reason      TEXT,
    initiated_at        TIMESTAMPTZ,
    transferred_at      TIMESTAMPTZ,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_payout_status  ON payout(status);
CREATE INDEX idx_payout_creator ON payout(creator_user_id);

-- ─────────────────────────────────────────────
-- Notifications
-- ─────────────────────────────────────────────
CREATE TABLE notification (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    type            TEXT NOT NULL,                      -- N1..N17 codes
    title           VARCHAR(160) NOT NULL,
    body            TEXT,
    payload         JSONB,                              -- deep-link data (never contains phone pre-confirm)
    read_at         TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_notification_user ON notification(user_id, created_at DESC);

-- ─────────────────────────────────────────────
-- Recognition Ledger (append-only) & Audit
-- ─────────────────────────────────────────────
CREATE TABLE domain_event (
    id              BIGSERIAL PRIMARY KEY,
    event_type      TEXT NOT NULL,                      -- e.g. 'booking.completed'
    aggregate_type  TEXT NOT NULL,                      -- 'booking','profile',...
    aggregate_id    UUID NOT NULL,
    payload         JSONB NOT NULL,
    occurred_at     TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_event_aggregate ON domain_event(aggregate_type, aggregate_id);
CREATE INDEX idx_event_type_time ON domain_event(event_type, occurred_at);

CREATE TABLE outbox (
    id              BIGSERIAL PRIMARY KEY,
    event_type      TEXT NOT NULL,
    payload         JSONB NOT NULL,
    dispatched_at   TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_outbox_undispatched ON outbox(created_at) WHERE dispatched_at IS NULL;

CREATE TABLE audit_log (
    id              BIGSERIAL PRIMARY KEY,
    actor_type      TEXT NOT NULL CHECK (actor_type IN ('user','admin','system')),
    actor_id        UUID,
    action          TEXT NOT NULL,                      -- 'profile.approve', etc.
    entity_type     TEXT NOT NULL,
    entity_id       UUID,
    diff            JSONB,                              -- before/after
    ip_address      INET,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_audit_entity ON audit_log(entity_type, entity_id);
CREATE INDEX idx_audit_actor  ON audit_log(actor_type, actor_id);

-- ─────────────────────────────────────────────
-- Admin & Config
-- ─────────────────────────────────────────────
CREATE TABLE admin_user (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email           VARCHAR(160) NOT NULL UNIQUE,
    password_hash   TEXT,                               -- or SSO subject
    role            TEXT NOT NULL DEFAULT 'ops'
                    CHECK (role IN ('superadmin','ops','finance','support','readonly')),
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    twofa_secret    TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE system_config (
    key             VARCHAR(80) PRIMARY KEY,
    value           JSONB NOT NULL,
    updated_by      UUID REFERENCES admin_user(id),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);
-- Seed:
-- ('platform_commission_pct', '15'),
-- ('booking_accept_window_hours', '24'),
-- ('payment_window_hours', '24'),
-- ('otp_length', '4'),
-- ('otp_max_attempts', '5')
```

### 23.5 Radius search implementation options

**Decision.** Two acceptable implementations; choose per team capability:

- **Option A (recommended for scale): PostGIS.** Add a `geography(Point,4326)` column
  to `creator_profile`, GiST index it, and query with `ST_DWithin`. Precise, fast.
- **Option B (MVP-simple): bounding box + Haversine.** Store `lat/lng`, pre-filter by
  a lat/lng bounding box (indexed), then compute Haversine distance in SQL/app for
  final radius + sort. Simpler ops, fine at MVP scale.

Either way, search is cached in Redis keyed by `(category, event_type, geo-cell,
radius)` with a short TTL.

### 23.6 Data retention & privacy

- Aadhaar S3 keys reference objects in a **private, encrypted** bucket; access only
  via short-lived signed URLs to **admin** during verification. Never returned to any
  consumer client. Consider deleting Aadhaar images after successful verification and
  retaining only a verified-flag + hash (see Open Questions §35).
- `domain_event` and `audit_log` are retained long-term (ledger + compliance).

---

## 24. API Contracts

**Vision.** A clean, versioned REST API. JSON everywhere. The API is the **only**
place business rules and state machines are enforced. Clients are dumb; the server is
the source of truth. Contact-stripping (BR-18) happens in the serialization layer.

### 24.1 Conventions

- Base URL: `https://api.rcube.app/v1`
- Auth: `Authorization: Bearer <access_jwt>` (except auth endpoints).
- Content type: `application/json; charset=utf-8`.
- Money: integer **paise** in all payloads (`price_paise`, `amount_paise`).
- IDs: UUID strings.
- Timestamps: ISO-8601 UTC (`2026-07-12T10:30:00Z`); `event_date` is `YYYY-MM-DD`.
- Idempotency: money-affecting POSTs accept `Idempotency-Key` header.
- Pagination: cursor-based — `?limit=20&cursor=<opaque>` → `{ items, next_cursor }`.
- Errors: consistent envelope (§24.11).

### 24.2 Auth

```
POST /v1/auth/otp/start
  body: { "phone": "+919876543210" }
  → 200 { "request_id": "…", "resend_in_seconds": 30 }
  (delegates OTP send to Firebase; or returns Firebase session info for client SDK)

POST /v1/auth/otp/verify
  body: { "phone": "+919876543210", "firebase_id_token": "<token>" }
  → 200 {
      "access_token": "<jwt>",
      "refresh_token": "<jwt>",
      "user": { "id","phone","display_name","default_mode","is_new": true }
    }

POST /v1/auth/refresh
  body: { "refresh_token": "<jwt>" }
  → 200 { "access_token": "<jwt>", "refresh_token": "<jwt>" }

POST /v1/auth/logout            (auth)
  → 204   (revokes refresh token)

POST /v1/me/device-tokens       (auth)
  body: { "fcm_token": "…", "platform": "android" }
  → 204
```

> **Design note:** OTP is issued/verified by Firebase on the client; the backend
> verifies the Firebase ID token and mints its own JWTs (FR-AUTH-6). This keeps
> RCube's session model independent of Firebase after login.

### 24.3 Me / account

```
GET  /v1/me                     (auth)
  → 200 { "id","phone","display_name","default_mode","status" }

PATCH /v1/me                    (auth)
  body: { "display_name"?, "default_mode"?, "last_lat"?, "last_lng"? }
  → 200 { ...user }

GET  /v1/me/notifications       (auth)   ?limit&cursor
  → 200 { "items": [ { id,type,title,body,payload,read_at,created_at } ], "next_cursor" }

POST /v1/me/notifications/{id}/read  (auth)  → 204
```

### 24.4 Creator profiles

```
POST /v1/creator/profiles                       (auth)
  body: { category, display_name, bio?, city, lat?, lng?, languages?[],
          instagram_url?, youtube_url? }
  → 201 { ...profile (status:'draft') }

GET  /v1/creator/profiles                       (auth)     -- my profiles
  → 200 { "items": [ { id,category,display_name,city,status,rejection_reason?,
                       service_count } ] }

GET  /v1/creator/profiles/{id}                  (auth, owner)
  → 200 { ...full profile incl. services, status }

PATCH /v1/creator/profiles/{id}                 (auth, owner)
  body: { any editable fields }
  -- material-field edits on an approved profile revert status to 'pending_review' (BR-7)
  → 200 { ...profile }

POST /v1/creator/profiles/{id}/media            (auth, owner)   -- get S3 upload URL
  body: { "kind": "profile_photo|cover_photo|aadhaar_front|aadhaar_back",
          "content_type": "image/jpeg" }
  → 200 { "upload_url": "<presigned>", "object_key": "…" }
  -- client PUTs the file to upload_url, then PATCHes the profile with the key

POST /v1/creator/profiles/{id}/submit           (auth, owner)
  -- guard: BR-5 satisfied (photos, category, bio, city, aadhaar, >=1 service)
  → 200 { ...profile (status:'pending_review') }
  -- 422 if requirements unmet: { error: { code:'PROFILE_INCOMPLETE', fields:[…] } }
```

**Serialization rule:** Aadhaar keys are **never** returned in any profile response to
a non-admin. Admin uses a separate admin endpoint (§28) to get signed URLs.

### 24.5 Services

```
POST   /v1/creator/profiles/{profileId}/services      (auth, owner)
  body: { title, price_paise, duration_min?, description? }
  → 201 { ...service }

PATCH  /v1/creator/services/{id}                       (auth, owner)
  body: { title?, price_paise?, duration_min?, description?, is_active? }
  → 200 { ...service }

DELETE /v1/creator/services/{id}                       (auth, owner)
  -- soft: is_active=false; existing bookings unaffected (BR-11)
  → 204
```

### 24.6 Discovery / search (organizer)

```
GET /v1/search/creators                               (auth)
  query:
    event_type   (required)  e.g. 'birthday'
    category     (required)  e.g. 'guitarist'
    lat,lng      (required)  organizer origin
    radius_km    (default 10)
    limit,cursor
  → 200 {
      "items": [
        {
          "profile_id","display_name","category","city",
          "distance_km": 4.2,
          "profile_photo_url","cover_photo_url",
          "is_verified": true,
          "starting_price_paise": 200000,
          "bio_snippet"
        }
      ],
      "next_cursor"
    }
  -- only status='approved' profiles (BR-4)

GET /v1/search/creators/{profileId}                   (auth)
  → 200 {
      "profile_id","display_name","category","city","bio","languages",
      "profile_photo_url","cover_photo_url","instagram_url","youtube_url",
      "is_verified": true,
      "services": [ { id,title,price_paise,duration_min?,description? } ]
      -- NO contact, NO aadhaar, NO user_id exposed
    }
```

### 24.7 Bookings

```
POST /v1/bookings                                     (auth, organizer)
  headers: Idempotency-Key
  body: {
    "creator_profile_id","service_id",
    "event_date":"2026-07-12","event_type":"house_party",
    "venue":"Koramangala, Bengaluru","notes":"~20 guests"
  }
  guards: profile approved; service belongs to profile; date>=today; not self (BR-3)
  → 201 { ...booking (status:'pending'),
          "service_title_snap","price_paise_snap","accept_expires_at" }

GET  /v1/bookings                                     (auth)
  query: role='creator'|'organizer' (default: current mode), status?, limit, cursor
  → 200 { "items": [ booking summaries ], "next_cursor" }

GET  /v1/bookings/{id}                                (auth, participant)
  → 200 { ...booking,
          "contact": null | { "counterparty_phone": "+91…", "counterparty_name": "…" } }
  -- contact populated ONLY when status in ('confirmed','in_progress','completed')  (BR-18)

-- Creator actions
POST /v1/bookings/{id}/accept                         (auth, creator-owner)
  guard: status='pending'
  → 200 { ...booking (status:'payment_pending'), "payment_expires_at",
          "payment": { "razorpay_order_id","amount_paise" } }

POST /v1/bookings/{id}/decline                        (auth, creator-owner)
  guard: status='pending'
  → 200 { ...booking (status:'declined') }

POST /v1/bookings/{id}/start                          (auth, creator-owner)
  body: { "otp": "4291" }
  guard: status='confirmed'; valid OTP; within event window
  → 200 { ...booking (status:'in_progress') }
  -- 422 { error:{ code:'OTP_INVALID' | 'OTP_EXPIRED' | 'TOO_MANY_ATTEMPTS' } }

-- Organizer actions
POST /v1/bookings/{id}/complete                       (auth, organizer-owner)
  guard: status='in_progress'
  → 200 { ...booking (status:'completed') }   -- creates payout (Pending Transfer)

POST /v1/bookings/{id}/cancel                         (auth, organizer-owner pre-payment | admin)
  body: { "reason" }
  → 200 { ...booking (status:'cancelled') }

-- Organizer: view event OTP (event day)
GET  /v1/bookings/{id}/event-otp                      (auth, organizer-owner)
  guard: status='confirmed'; within display window
  → 200 { "otp": "4291", "expires_at" }
  -- ONLY the organizer can read the OTP; never returned to creator
```

### 24.8 Payments

```
POST /v1/payments/{bookingId}/order                   (auth, organizer-owner)
  guard: booking status='payment_pending'
  → 200 { "razorpay_order_id","amount_paise","currency":"INR","key_id":"rzp_…" }
  -- idempotent: returns existing open order if present

POST /v1/payments/{bookingId}/verify                  (auth, organizer-owner)
  body: { "razorpay_order_id","razorpay_payment_id","razorpay_signature" }
  -- server verifies HMAC signature; idempotent
  → 200 { "payment_status":"paid", "booking_status":"confirmed" }
  -- on success: reveal contacts (T6), generate Event OTP

POST /v1/webhooks/razorpay                            (no user auth; signature-verified)
  -- Razorpay → RCube. Idempotent. Authoritative confirmation of payment.
  -- handles payment.captured / payment.failed
  → 200 { "received": true }
```

> **Dual confirmation:** both the client `verify` call and the server `webhook` can
> confirm payment; both are idempotent and converge on the same `payment.status=paid`
> and `booking.status=confirmed`. The webhook is authoritative if the client drops.

### 24.9 Earnings & payouts (creator read)

```
GET /v1/creator/earnings                              (auth, creator)
  → 200 {
      "pending_transfer_paise": 497500,
      "transferred_total_paise": 2230000,
      "items": [
        { "booking_id","service_title_snap","net_paise","status",
          "event_date","transferred_at?" }
      ]
    }
```

### 24.10 Admin API (separate auth realm — see §28)

```
POST /v1/admin/auth/login                 body:{ email,password,totp? } → tokens
GET  /v1/admin/verifications              ?status=pending_review        → queue
GET  /v1/admin/profiles/{id}              → full incl. signed Aadhaar URLs
POST /v1/admin/profiles/{id}/approve      → status approved
POST /v1/admin/profiles/{id}/reject       body:{ reason } → status rejected
POST /v1/admin/profiles/{id}/suspend      body:{ reason }
GET  /v1/admin/bookings                   ?status&from&to&cursor
POST /v1/admin/bookings/{id}/cancel       body:{ reason }
POST /v1/admin/bookings/{id}/regenerate-otp
GET  /v1/admin/payments                   ?status&cursor  (+ reconcile)
GET  /v1/admin/payouts                    ?status=pending_transfer
POST /v1/admin/payouts/{id}/initiate      → transfer_initiated
POST /v1/admin/payouts/{id}/complete      body:{ utr_reference } → transferred
POST /v1/admin/payouts/{id}/fail          body:{ reason } → failed
GET  /v1/admin/analytics/overview         → KPIs (see §28.3)
GET  /v1/admin/users                      ?query&cursor
```

All admin endpoints require admin JWT + role check and are **audit-logged** (§30).

### 24.11 Error envelope

```json
{
  "error": {
    "code": "BOOKING_INVALID_STATE",
    "message": "This booking can no longer be accepted.",
    "details": { "current_status": "expired" },
    "request_id": "req_01H…"
  }
}
```

**Standard error codes (non-exhaustive):**

| HTTP | Code | Meaning |
|---|---|---|
| 400 | `VALIDATION_ERROR` | Malformed input |
| 401 | `UNAUTHENTICATED` | Missing/invalid token |
| 403 | `FORBIDDEN` | Not the owner/participant |
| 404 | `NOT_FOUND` | Resource absent |
| 409 | `IDEMPOTENCY_CONFLICT` | Reused key, different body |
| 422 | `BOOKING_INVALID_STATE` | Illegal state transition |
| 422 | `PROFILE_INCOMPLETE` | Submit guard failed |
| 422 | `OTP_INVALID` / `OTP_EXPIRED` / `TOO_MANY_ATTEMPTS` | Event OTP |
| 402 | `PAYMENT_FAILED` | Payment could not be verified |
| 429 | `RATE_LIMITED` | Too many requests |
| 500 | `INTERNAL` | Server error (with request_id) |

### 24.12 OpenAPI

The backend must expose an **OpenAPI 3.1** spec (FastAPI auto-generates it at
`/openapi.json`, docs at `/docs`). This spec is the machine-readable contract AI
agents and client generators consume. The tables above are the human-readable summary;
the OpenAPI file is authoritative for exact schemas.

---

## 25. Mobile App Architecture (Flutter)

**Vision.** A single Flutter codebase serving both modes, with a clean, testable
architecture. State management is predictable; the network layer is the only source
of truth mirror; no business rules live on the client.

### 25.1 Architectural style

- **Layered / Clean-ish architecture:** `presentation` → `application (state)` →
  `data (repositories)` → `services (api/storage)`.
- **State management:** **Riverpod** (recommended) or Bloc. Riverpod for its
  testability and compile-safe providers. (Decision: Riverpod; a team may substitute
  Bloc if it prefers, keeping the same layering.)
- **Navigation:** `go_router` with mode-aware shell routes (Creator shell vs
  Organizer shell) and an auth-gated redirect.
- **Networking:** `dio` with interceptors (auth token, refresh, logging, error
  mapping to the standard envelope).
- **Serialization:** `freezed` + `json_serializable` for immutable models.
- **Local storage:** `flutter_secure_storage` (tokens), lightweight cache for
  last-known lists.
- **Payments:** `razorpay_flutter` SDK for checkout; verification via backend.
- **Push:** `firebase_messaging`; token registered via `/me/device-tokens`.
- **Auth OTP:** `firebase_auth` phone flow; exchange ID token with backend.

### 25.2 Folder structure

```
rcube_app/
├── lib/
│   ├── main.dart
│   ├── app.dart                      # MaterialApp.router, theme, mode shell
│   ├── core/
│   │   ├── theme/                    # tokens from §11.2 (colors, type, spacing)
│   │   ├── router/                   # go_router config, guards, mode shells
│   │   ├── network/                  # dio client, interceptors, error mapping
│   │   ├── storage/                  # secure storage, cache
│   │   ├── config/                   # env, constants (Appendix B/D)
│   │   ├── error/                    # failure types, error UI helpers
│   │   └── widgets/                  # shared UI: buttons, cards, empty/error/loading
│   ├── features/
│   │   ├── auth/
│   │   │   ├── data/ (repo, dtos)
│   │   │   ├── application/ (providers/state)
│   │   │   └── presentation/ (A-01..A-04 screens, widgets)
│   │   ├── account/                  # S-01..S-05
│   │   ├── creator_profile/          # C-01..C-05, C-11
│   │   ├── creator_requests/         # C-06..C-08 (bookings, OTP entry)
│   │   ├── creator_earnings/         # C-09, C-10
│   │   ├── discovery/                # O-01..O-04 (search, profile view)
│   │   ├── booking_organizer/        # O-05..O-11 (form, pay, otp display, complete)
│   │   └── notifications/            # S-05 center + FCM handling
│   ├── models/                       # shared freezed models (Booking, Service, ...)
│   └── l10n/                         # i18n string catalogs (NFR-LOC-1)
├── test/                             # unit + widget tests
├── integration_test/                 # end-to-end flows
└── pubspec.yaml
```

### 25.3 Mode-aware navigation shell

```
GoRouter
 ├── /auth/*                     (unauthenticated)
 ├── ShellRoute (authenticated)
 │    ├── mode == creator  → CreatorShell (bottom tabs: profiles/requests/earnings/account)
 │    └── mode == organizer→ OrganizerShell (bottom tabs: discover/bookings/account)
 └── redirect: if !authed → /auth ; if new user → /auth/mode-intro
```

Mode is held in a `modeProvider`; switching mode swaps the shell without re-auth
(FR-AUTH-4).

### 25.4 Data flow (example: accept a booking)

```
UI (C-07 Accept button)
  → requestsController.accept(bookingId)      [application layer]
  → bookingRepository.accept(bookingId)       [data layer]
  → dio POST /bookings/{id}/accept            [network]
  → on 200: update local booking state → UI shows Payment Pending
  → on 422 BOOKING_INVALID_STATE: show friendly error (state changed elsewhere)
```

### 25.5 Client-side rules (non-negotiable)

- Never assume a state transition succeeded; wait for server confirmation.
- Never store or display counterparty phone unless the API returns it (BR-18 is
  enforced server-side, but the client must also not cache stale contact).
- Never hold Razorpay secrets; only `key_id` + order id come from the backend.
- All monetary display converts paise → ₹ with tabular figures.
- Handle token refresh transparently via interceptor; on refresh failure → logout.

### 25.6 Offline & resilience

- Read-only lists (profiles, bookings) show last cached data with a "stale" hint when
  offline; write actions are disabled offline with a clear message.
- Payment and OTP flows require connectivity (blocked offline with guidance).

---

## 26. Backend Architecture (FastAPI)

**Vision.** A modular monolith organized by bounded context (§22), ready to split
into services later if needed. Domain logic is isolated from framework and I/O.

### 26.1 Architectural style

- **Modular monolith**, domain-oriented packages (one per bounded context).
- **Layering per module:** `api` (routers/schemas) → `service` (use cases, state
  machines) → `domain` (entities, rules) → `repository` (DB access).
- **Transactional outbox** for reliable event emission (§27).
- **Async FastAPI** with SQLAlchemy 2.x (async) + Alembic migrations.
- **Pydantic v2** for request/response schemas (and contact-stripping serializers).
- **Background workers** (e.g., Celery/RQ/arq or a simple async scheduler) for
  timers, reminders, outbox dispatch, FCM sends.

### 26.2 Folder structure

```
rcube_backend/
├── app/
│   ├── main.py                       # FastAPI app factory, router mounting
│   ├── core/
│   │   ├── config.py                 # settings (env, secrets) — Appendix D
│   │   ├── security.py               # JWT mint/verify, password hashing
│   │   ├── db.py                     # async engine, session
│   │   ├── errors.py                 # error envelope, exception handlers
│   │   ├── deps.py                   # FastAPI dependencies (current_user, etc.)
│   │   ├── idempotency.py            # Idempotency-Key handling
│   │   └── logging.py                # structured logging, request IDs
│   ├── modules/
│   │   ├── identity/                 # users, sessions, auth
│   │   │   ├── api.py  service.py  domain.py  repository.py  schemas.py
│   │   ├── catalog/                  # creator_profile, service, verification
│   │   ├── discovery/                # search (read model over catalog)
│   │   ├── booking/                  # booking state machine, event_otp, contact reveal
│   │   ├── payments/                 # razorpay orders, verify, webhook, escrow
│   │   ├── payouts/                  # payout lifecycle
│   │   ├── notifications/            # notification records + FCM
│   │   ├── ledger/                   # domain_event append + (future) recognition
│   │   └── admin/                    # admin auth, verification, payouts, analytics
│   ├── workers/
│   │   ├── scheduler.py              # expiries (T5/T7), reminders (N15-N17)
│   │   ├── outbox_dispatcher.py      # publish domain_event → handlers
│   │   └── fcm_sender.py             # push delivery
│   ├── integrations/
│   │   ├── firebase.py               # verify ID token
│   │   ├── razorpay_client.py        # orders, signature verify, webhooks
│   │   ├── s3.py                     # presigned uploads, private Aadhaar bucket
│   │   └── fcm.py                    # FCM client
│   └── shared/
│       ├── enums.py                  # canonical enums (Appendix B)
│       ├── money.py                  # paise helpers
│       └── events.py                 # event type constants
├── alembic/                          # migrations
├── tests/                            # unit + integration + state-machine tests
├── pyproject.toml
└── Dockerfile
```

### 26.3 State machine implementation pattern

Each state machine (§19) is implemented as an explicit **transition function** in the
module's `service` layer, guarded and transactional:

```python
# booking/service.py (illustrative)
ALLOWED = {
    ("pending","accepted"): _guard_creator_owns,
    ("pending","declined"): _guard_creator_owns,
    ("payment_pending","confirmed"): _guard_payment_verified,
    ("confirmed","in_progress"): _guard_valid_event_otp,
    ("in_progress","completed"): _guard_organizer_owns,
    # ...
}

async def transition(booking, to_status, actor, ctx):
    guard = ALLOWED.get((booking.status, to_status))
    if guard is None:
        raise InvalidState(booking.status, to_status)
    await guard(booking, actor, ctx)
    async with uow():                      # single transaction
        booking.status = to_status
        _apply_side_effects(booking, to_status, ctx)   # contact reveal, otp gen, payout create
        append_domain_event(booking, to_status)         # ledger (outbox)
        write_audit(actor, booking, to_status)
    enqueue_notifications(booking, to_status)
```

**Why:** a single, testable place per machine; illegal transitions are impossible to
trigger accidentally; side effects and events are atomic with the state change.

### 26.4 Serialization / contact-stripping layer

A dedicated response serializer for `Booking` computes the `contact` field:

```python
def serialize_booking(b, viewer) -> dict:
    contact = None
    if b.status in ("confirmed","in_progress","completed"):
        contact = counterparty_contact(b, viewer)   # phone + name of the other party
    return { ...base_fields, "contact": contact }
```

This is the **only** path that ever emits a counterparty phone (BR-18). Unit tests
assert `contact is None` for every pre-confirmed state.

### 26.5 Idempotency & concurrency

- Money POSTs require `Idempotency-Key`; stored in a short-TTL table/Redis; identical
  key + body → cached response; different body → 409.
- Booking transitions use row-level locking (`SELECT ... FOR UPDATE`) to avoid double
  transitions under race.
- Razorpay webhook + client verify both converge idempotently (§24.8).

### 26.6 Configuration & tunables

Business timing/commission come from `system_config` (DB) with env fallbacks, so ops
can tune without deploys:

| Key | Default | Effect |
|---|---|---|
| `platform_commission_pct` | 15 | Payout commission (BR-20) |
| `booking_accept_window_hours` | 24 | Pending expiry (BR-13) |
| `payment_window_hours` | 24 | Payment expiry (BR-14) |
| `otp_length` | 4 | Event OTP digits |
| `otp_max_attempts` | 5 | Lockout threshold |

---

## 27. Event-Driven Architecture

**Vision.** RCube is choreographed by **domain events**. Events are the seams between
contexts, the source of the recognition ledger, and the driver of notifications and
future analytics. We use the **transactional outbox** pattern so events are never lost
and never emitted without the corresponding state change committing.

### 27.1 Event flow

```
State change (in a DB transaction)
   ├── writes the aggregate (e.g., booking.status = completed)
   ├── appends to domain_event (the ledger)
   ├── writes to outbox (undispatched)
   └── writes audit_log
        │  (all commit atomically)
        ▼
outbox_dispatcher (worker) polls undispatched rows
   ├── → notifications handler (creates notification rows + FCM push)
   ├── → ledger/recognition handler (future score computation — no-op in MVP)
   └── → analytics handler (metrics)
   marks outbox row dispatched
```

### 27.2 Canonical event catalog

| Event type | Emitted on | Consumers |
|---|---|---|
| `booking.created` | T1 | notifications(N1), analytics |
| `booking.accepted` | T2/T3 | notifications(N2), payments (create order) |
| `booking.declined` | T4 | notifications(N3) |
| `booking.expired` | T5 | notifications(N4) |
| `booking.confirmed` | T6 | notifications(N5), otp gen, ledger, analytics |
| `booking.payment_expired` | T7 | notifications(N6) |
| `booking.started` | T8 | notifications(N7), ledger |
| `booking.completed` | T9 | payouts (create), notifications(N8), **ledger (recognition)**, analytics |
| `booking.cancelled` | T10 | notifications(N13), payments (refund_required) |
| `profile.submitted` | submit | admin queue |
| `profile.approved` | admin | notifications(N9), analytics |
| `profile.rejected` | admin | notifications(N10) |
| `payment.paid` | webhook/verify | booking.confirm |
| `payment.failed` | webhook | notifications(N14) |
| `payout.transferred` | admin | notifications(N12), ledger |

### 27.3 Scheduled jobs (the "clock")

| Job | Frequency | Action |
|---|---|---|
| Expiry sweeper | every 1–5 min | PENDING past accept-expiry → EXPIRED (T5); PAYMENT_PENDING past payment-expiry → PAYMENT_EXPIRED (T7); GENERATED OTPs past window → EXPIRED |
| Payment reminder | hourly | N15 to organizers with unpaid accepted bookings |
| Event reminder | hourly | N16 to both parties for events tomorrow/today |
| Pending-request reminder | hourly | N17 to creators with waiting requests |
| Outbox dispatcher | continuous | publish undispatched events |

**Why event-driven + outbox:** it decouples contexts, guarantees the recognition
ledger is complete (philosophy §5), makes notifications reliable, and lets us add new
consumers (reviews, scores) later by simply subscribing to existing events — **zero
change to producers**.

### 27.4 Recognition ledger (silent in MVP, foundational)

Every `booking.completed`, `booking.confirmed`, `payout.transferred`, and
`profile.approved` event is permanently recorded in `domain_event`. In MVP we compute
nothing from it. Post-MVP, the Recognition Score, reviews, badges, and trust score are
all pure functions over this stream — computed without any backfill. This is the
single most important architectural bet in the bible.

---

## 28. Admin Panel Design (React)

**Vision.** The admin panel is RCube's **operations cockpit**. In MVP, humans run
verification and payouts manually — this is deliberate (learn before automating). The
panel must make those manual tasks fast, safe, and fully audited.

### 28.1 Tech & structure

- **React + TypeScript + Vite**, component library (e.g., MUI or shadcn/ui), data via
  React Query against the `/v1/admin/*` API.
- **Separate auth realm** (email + password + TOTP 2FA), separate JWT, RBAC by role
  (`superadmin`, `ops`, `finance`, `support`, `readonly`).
- Every mutating action shows a confirm dialog and is audit-logged.

```
admin_web/
├── src/
│   ├── app/ (router, layout, auth guard)
│   ├── features/
│   │   ├── dashboard/       # KPIs
│   │   ├── verifications/   # queue + review (Aadhaar via signed URLs)
│   │   ├── bookings/        # list/detail/cancel/regenerate-otp
│   │   ├── payments/        # reconciliation
│   │   ├── payouts/         # initiate/complete/fail/retry, UTR
│   │   ├── users/           # search, suspend
│   │   ├── support/         # per-user/booking context
│   │   └── analytics/       # funnels + recognition metrics
│   ├── api/ (typed client from OpenAPI)
│   └── components/ (tables, filters, badges, confirm dialogs)
└── package.json
```

### 28.2 Admin capabilities (mapped to FR-ADMIN)

| Area | Capability |
|---|---|
| **Creator Verification** | Review queue; view profile + Aadhaar (signed, time-limited); **Approve** / **Reject** (with reason); **Suspend** |
| **Bookings** | List/filter by state; view detail; cancel (reason); regenerate Event OTP for support |
| **Payments** | View transactions; reconcile against Razorpay; flag disputes; initiate manual refund (refund_required → refunded) |
| **Payouts** | Pending Transfer queue; **Initiate**; **Mark Transferred** (record UTR); **Mark Failed**; **Retry**; export CSV for banking |
| **Dashboard** | Live KPIs (§28.3) |
| **Support** | Look up any user/booking; timeline of events (from `domain_event`/`audit_log`) |
| **Analytics** | Funnels; silent recognition metrics (§28.4) |
| **Users** | Search; suspend/reinstate; view mode activity |

### 28.3 Dashboard KPIs (MVP)

```
┌──────────────────────────────────────────────────────────┐
│  RCube Ops — Today                                        │
│  ┌────────────┬────────────┬────────────┬──────────────┐ │
│  │ Verified   │ Pending    │ Bookings   │ Recognized   │ │
│  │ creators   │ reviews    │ (7d)       │ bookings ★   │ │
│  │   214      │    12      │    58      │     41       │ │
│  ├────────────┼────────────┼────────────┼──────────────┤ │
│  │ Payments   │ Escrow     │ Payouts    │ GMV (7d)     │ │
│  │ success %  │ held (₹)   │ pending    │ ₹1,84,500    │ │
│  │   96%      │ ₹62,300    │    7       │              │ │
│  └────────────┴────────────┴────────────┴──────────────┘ │
│  Funnel: request→accept→pay→confirm→complete             │
│  100% ─► 71% ─► 63% ─► 63% ─► 58%                        │
└──────────────────────────────────────────────────────────┘
```

★ **Recognized bookings** = completed bookings where organizer had not previously
booked this creator (the north-star metric, §1).

### 28.4 Silent recognition metrics (admin-only, MVP)

Even though creators don't see scores, admins track (from the ledger):
- Distinct organizers per creator (breadth of recognition).
- Repeat-organizer rate (depth/loyalty).
- Completion reliability, time-to-accept.
- Category/city recognition heatmaps.

These validate the recognition thesis (§5) before we surface it to users.

### 28.5 Verification review screen (operational spec)

1. Open a `pending_review` profile.
2. See: photos, category, bio, city, services (titles + prices), and Aadhaar front/
   back via **signed, short-TTL URLs** (viewing is audit-logged).
3. Checklist: identity legible? category appropriate? content safe/genuine? prices
   sane?
4. **Approve** → profile becomes searchable; creator notified (N9).
5. **Reject** → mandatory reason; creator notified privately (N10); profile returns to
   creator to edit.
6. Target SLA < 24h (persona 9.3.a).

---

## 29. Security Model

**Vision.** Trust is the product. Security is not a checklist bolted on; it is a
first-class feature that lets strangers transact safely.

### 29.1 Authentication & sessions

- **Consumer:** Firebase Phone OTP → backend verifies Firebase ID token → mints
  RCube JWT (short-lived access ~15 min) + refresh (rotating, revocable, stored
  hashed).
- **Admin:** separate realm; email + password (Argon2/bcrypt) + **TOTP 2FA**; short
  sessions; RBAC.
- Refresh-token rotation; reuse detection → revoke session family.

### 29.2 Authorization

- Every endpoint checks: authenticated? correct role (user/admin)? **resource
  ownership/participation**?
  - Creator actions require the creator owns the profile/booking.
  - Organizer actions require the organizer created the booking.
  - Admin actions require role permission.
- Enforced in `deps.py` dependencies + per-service guards (defense in depth).

### 29.3 Data protection

| Data | Protection |
|---|---|
| **Aadhaar images** | Private S3 bucket, **SSE-KMS encryption**, no public URLs; access only via short-TTL signed URLs to authenticated admins; every access audited; consider deletion post-verification (§35) |
| **Phone numbers** | Stored normally but **stripped from all cross-user API responses** until booking Confirmed (BR-18); enforced server-side |
| **Payment data** | Card data never touches RCube (Razorpay-hosted); we store only order/payment IDs + status |
| **JWT secrets, API keys** | AWS Secrets Manager / SSM; never in code or client |
| **Passwords (admin)** | Argon2id / bcrypt with per-user salt |
| **OTP (event)** | Stored **hashed**; plaintext only transiently to organizer's client |

### 29.4 Transport & network

- TLS 1.2+ everywhere; HSTS.
- API behind AWS ALB + WAF; rate limiting (per IP + per user) via Redis.
- Private subnets for DB/Redis; no public DB access; security groups least-privilege.

### 29.5 Input validation & abuse prevention

- Strict Pydantic schema validation on all inputs.
- Rate limits: OTP requests (per phone), login attempts, booking creation, search.
- OTP brute-force protection: max attempts → lockout (config).
- File uploads: content-type + size limits; virus/type checks; only via presigned
  URLs to designated buckets.
- Prevent enumeration: UUIDs, generic 404s, no user existence leaks on auth.

### 29.6 Payment security

- Verify Razorpay **signature** (HMAC-SHA256) on client callback.
- Verify **webhook signature**; treat webhook as authoritative; idempotent.
- Never confirm a booking on client claim alone.
- Reconciliation job compares RCube payments vs Razorpay settlements.

### 29.7 Privacy & trust rules (recap, enforced)

- Contact reveal only at Confirmed (BR-18) — server-enforced, unit-tested.
- No self-booking (BR-3).
- Rejections/suspensions private (BR-29).
- Minimal PII collection; purpose-limited Aadhaar (verification only).

### 29.8 Threat model highlights

| Threat | Mitigation |
|---|---|
| Contact scraping before payment | Server-side stripping; no phone in search/booking pre-confirm |
| Payment spoofing | Signature + webhook verification; idempotency |
| Account takeover | Short JWTs, refresh rotation, reuse detection; admin 2FA |
| Aadhaar/PII leak | Encrypted private bucket, signed URLs, audit, minimal retention |
| No-show fraud / fake completion | OTP-gated start; only organizer completes; escrow release only on completion; admin dispute tools |
| OTP brute force | Hashing, attempt limits, lockout |
| Fake profiles | Manual human verification (Aadhaar + review) |

---

## 30. Audit Logs

**Vision.** Trust requires accountability. Every sensitive and financial action is
recorded immutably so we can investigate disputes, satisfy compliance, and protect
both creators and organizers.

### 30.1 What is audited (`audit_log`)

- **Admin actions:** approve/reject/suspend profiles, cancel bookings, regenerate OTP,
  initiate/complete/fail payouts, refunds, config changes, Aadhaar views.
- **Financial events:** payment confirmations, payout status changes.
- **Sensitive user actions:** login, logout, profile submission, booking state
  transitions (also in `domain_event`).
- **Access to PII:** every signed-URL issuance for Aadhaar.

### 30.2 Record shape

```
audit_log(actor_type, actor_id, action, entity_type, entity_id, diff(jsonb), ip, created_at)
```

- `diff` captures before/after for mutations.
- Append-only; no updates/deletes; retained long-term.
- Correlated with `request_id` from structured logs (NFR-OBS-1).

### 30.3 Relationship to the ledger

- `domain_event` = **business** truth (recognition + choreography).
- `audit_log` = **accountability** truth (who did what, especially admins/PII).
- They overlap intentionally; both are append-only and durable.

### 30.4 Access & integrity

- Audit logs viewable only by `superadmin`/`finance` roles.
- Consider write-once storage / periodic export to immutable storage for high-value
  financial audit.

---

## 31. Scalability Plan

**Vision.** Start boring and monolithic; scale by well-understood, incremental moves.
Never pre-optimize; design so scaling is *possible* without rewrites.

### 31.1 Scaling stages

| Stage | Scale | Moves |
|---|---|---|
| **0 — MVP** | 1 city, ≤ few thousand creators | Single FastAPI (2+ instances), single Postgres, Redis, S3, one worker |
| **1 — Traction** | Several cities | Horizontal scale API behind ALB; Postgres read replicas for search; CDN for media; separate worker fleet |
| **2 — Growth** | Many cities, high concurrency | Dedicated search service/index (PostGIS tuned or OpenSearch); Redis cluster; partition hot tables; queue-backed workers |
| **3 — Platform** | National | Split bounded contexts into services along existing seams (§22); event bus (Kafka/SNS-SQS) replacing outbox polling; regional media |

### 31.2 Bottlenecks & mitigations

| Bottleneck | Mitigation |
|---|---|
| Search read load | Redis cache (short TTL) + geo-indexed read replica; later dedicated index |
| Booking write contention | Row locks are per-booking (naturally sharded); index on status/expiry |
| Expiry sweeper scanning | Indexed partial scans on `(status, *_expires_at)`; batch; move to queue timers at scale |
| Media bandwidth | S3 + CloudFront CDN; responsive image variants |
| Notification fan-out | Worker fleet; FCM batching; outbox decouples from request path |
| DB size (events/audit) | Partition `domain_event`/`audit_log` by month; archive cold partitions |

### 31.3 Stateless-by-design

- API carries no session state (JWT); any instance serves any request.
- All shared state in Postgres/Redis/S3.
- Workers are idempotent and horizontally scalable.

### 31.4 Cost discipline

At MVP, the dominant cost is **human ops** (verification + payouts), not infra —
intentionally. Infra stays small (a couple of API instances, one DB, Redis, S3). We
scale infra only when metrics demand it.

### 31.5 What we deliberately DON'T build yet

Microservices, Kafka, sharded multi-region DBs, dedicated search clusters,
auto-scaling groups tuned to the minute — all deferred. The seams (bounded contexts,
events, outbox) are in place so these are *additive* later, never rewrites.

---
---

# PART VII — THE ROAD AHEAD

---

## OS. Out of Scope for MVP (explicit exclusions)

**Vision.** Discipline is the feature. Every excluded item below is excluded on
purpose to keep the MVP focused on the single question: *can hobbyists get booked by
strangers because of their talent?* Building any of these now would dilute that proof.

**The following are explicitly NOT in the MVP. AI agents must not build them:**

| Excluded | Why deferred |
|---|---|
| **AI** (any ML/AI features) | Not needed to prove the recognition loop; adds complexity |
| **In-app chat / messaging** | Contact is shared post-payment; coordination is manual (BR-18) |
| **Social feed** | We are not an attention platform (philosophy §4.1) |
| **Reviews / ratings** | Deferred; but events are ledgered now for future computation |
| **Recognition Score** | Deferred; ledger built now, score computed later (§5) |
| **Calendar / availability engine / scheduling** | Date-only booking; no complexity (FR-BOOK-2) |
| **Time-slot booking** | Date-only in MVP |
| **Auto payouts** | Manual payouts to learn ops first (BR-26) |
| **Custom quotes / negotiation** | Fixed-price services only |
| **Community features** | Not needed for the core loop |
| **Gamification / badges / achievements / trust score** | Deferred to roadmap |

**Reasoning (the meta-rule):** we defer features, never good schema. The data model
(ledger, events, audit) already anticipates all of the above so they become additive.

---

## 32. Future Roadmap

**Vision.** The roadmap is the *unfolding* of the recognition thesis. Each phase turns
accumulated ledger data into visible recognition, and each is designed as an additive
consumer of events we already emit (§27). Ordering is by dependency and value, not by
date.

### 32.1 Phase 2 — Recognition surfaces

- **Reviews & ratings.** Post-completion, the organizer can leave a review. Feeds the
  recognition ledger; visible on profiles. *(Depends on: `booking.completed` events —
  already emitted.)*
- **Recognition Score.** A computed, transparent score over the ledger (completed
  bookings, distinct organizers, repeat rate, reliability). The flagship post-MVP
  feature — the whole point of building the ledger now. Shown as a dignified badge,
  never a humiliating leaderboard (philosophy §4.3.8).
- **Achievements & badges.** "First applause," "10 recognized bookings," "Recognized in
  3 cities." Milestone-based, positive-only.
- **Trust score.** Composite of verification depth, reliability, dispute history.

### 32.2 Phase 3 — Reducing coordination friction

- **Availability calendar.** Creators mark available dates; search respects it. Removes
  the double-booking edge case (§34).
- **Time-slot booking.** Beyond date-only, once availability exists.
- **In-app chat.** Optional, post-confirmation coordination without leaving the app.
- **Custom quotes / negotiation.** For bespoke events (weddings) where fixed services
  don't fit.

### 32.3 Phase 4 — Intelligence

- **AI portfolio review.** Suggests better photos/bios to creators.
- **AI suggestions.** Recommends services/pricing based on category + city data.
- **Creator analytics.** Views, conversion, recognition trends for creators.
- **Smart discovery ranking.** Recognition-weighted search ranking.

### 32.4 Phase 5 — Automation & scale

- **Automated payouts.** Razorpay Route / payouts API; replaces manual transfers.
- **Automated refunds.**
- **Community features.** Local creator communities, showcases, collaborative events.
- **Multi-city expansion tooling.** Self-serve creator onboarding at scale with
  assisted verification.

### 32.5 Roadmap dependency map

```
MVP (ledger + events)
   │
   ├─► Reviews ──► Recognition Score ──► Badges/Achievements ──► Trust Score
   │
   ├─► Availability ──► Time slots ──► In-app chat ──► Custom quotes
   │
   ├─► AI portfolio/suggestions ──► Creator analytics ──► Smart ranking
   │
   └─► Auto payouts/refunds ──► Community ──► National scale
```

Everything hangs off the MVP ledger. **Build the ledger right, and the future is
cheap.**

---

## 33. Engineering Tradeoffs

**Vision.** Great teams make tradeoffs explicitly and record them. Here are the
decisions we made, the alternatives, and why.

### 33.1 Date-only booking (no calendar)

- **Decision:** book by date only; coordinate time manually after payment.
- **Alternative:** full availability + time-slot engine.
- **Why:** the availability engine is the single biggest complexity sink in
  marketplaces. It is not needed to prove the recognition loop. Manual coordination is
  acceptable at MVP volume. **Cost:** double-booking is possible (§34) — accepted risk.

### 33.2 Manual payouts

- **Decision:** admin manually transfers earnings.
- **Alternative:** automated payouts via Razorpay Route.
- **Why:** at MVP volume, manual payouts let us learn edge cases (disputes, failures,
  KYC) cheaply before encoding them. **Cost:** ops time + payout latency — acceptable,
  and a forcing function to keep volume honest.

### 33.3 Escrow-style hold + commission-on-completion

- **Decision:** collect full price up front, release (net commission) after completion.
- **Alternative:** collect on completion, or split payments.
- **Why:** protects both sides (creator sees money is secured; organizer protected
  against no-show). Simplest trust mechanism. **Cost:** we hold funds and bear gateway
  fees; refund flow is manual in MVP.

### 33.4 Modular monolith (not microservices)

- **Decision:** one FastAPI app organized by bounded context.
- **Alternative:** microservices from day one.
- **Why:** microservices add distributed-systems tax with no MVP benefit. Bounded
  contexts + events give us clean seams to split later. **Cost:** must maintain
  module discipline to keep future splits cheap.

### 33.5 Transactional outbox (not direct event bus)

- **Decision:** write events to an outbox table in the same transaction; a worker
  dispatches.
- **Alternative:** publish to Kafka/SNS directly from request handlers.
- **Why:** guarantees no lost events and no events without committed state (crucial
  for the recognition ledger). No new infra needed. **Cost:** polling latency (small);
  swap to a real bus at Stage 3.

### 33.6 Firebase Auth + own JWT

- **Decision:** Firebase issues OTP; backend verifies and mints its own JWTs.
- **Alternative:** use Firebase tokens directly everywhere; or build SMS OTP ourselves.
- **Why:** Firebase gives reliable OTP delivery cheaply; owning our JWT keeps our
  session model independent and portable. **Cost:** dependency on Firebase for login.

### 33.7 Money in paise (integer)

- **Decision:** store all money as integer paise.
- **Alternative:** decimals/floats.
- **Why:** eliminates floating-point rounding errors in financial calculations. **Cost:**
  minor conversion at display — trivial.

### 33.8 Enum-as-text + CHECK (not native PG enums)

- **Decision:** status columns are `TEXT` with `CHECK` constraints.
- **Alternative:** native Postgres `ENUM` types.
- **Why:** easier to evolve (adding a value to native enums is painful); readable in
  queries. **Cost:** slightly larger storage — negligible.

### 33.9 Absorb payment-gateway fee (MVP)

- **Decision:** RCube absorbs the ~2% gateway fee.
- **Alternative:** pass to organizer or creator.
- **Why:** clean pricing story and creator dignity during trust-building. **Cost:**
  margin — revisit once volume justifies passing it through transparently.

### 33.10 Riverpod (client state)

- **Decision:** Riverpod for Flutter state.
- **Alternative:** Bloc / Provider / GetX.
- **Why:** testable, compile-safe, scales well. **Cost:** team learning curve —
  acceptable; Bloc is a permitted substitute if the team prefers.

---

## 34. Edge Cases

**Vision.** The difference between a demo and a product is how it behaves at the edges.
Each edge case has a **defined behavior**. AI agents must implement these behaviors.

### 34.1 Booking & scheduling

| Edge case | Defined behavior |
|---|---|
| **Double-booking** (two organizers book same creator, same date) | Allowed in MVP (BR-16). Creator sees both; coordinates/declines manually. Post-confirmation conflicts handled via admin cancel + manual refund. Roadmap availability fixes this. Surface a gentle in-app hint to creators about overlapping dates. |
| Organizer books own profile | Blocked server-side (BR-3), 422. |
| Event date in the past at creation | Blocked (BR-15), 422. |
| Creator accepts then becomes unavailable | Before payment: organizer simply may not pay → payment expires. After payment (confirmed): handled as cancellation + manual refund by admin; creator reliability noted in ledger. |
| Creator ignores request | Auto-expires after accept window (T5); organizer notified (N4). |
| Organizer accepts price then abandons payment | Payment expires (T7); booking closes; creator freed. |

### 34.2 Payments

| Edge case | Defined behavior |
|---|---|
| Payment succeeds but client crashes before `verify` | Webhook confirms authoritatively; booking becomes Confirmed regardless (§24.8). |
| Duplicate webhook / double `verify` | Idempotent; single Confirmed transition (BR-22). |
| Payment succeeds after payment window elapsed | If webhook arrives post-expiry: treat as paid → confirm if booking still cancellable-to-confirmed; else auto-refund-required (admin). Prefer honoring a real successful payment; reconcile. |
| Partial/failed payment | Booking stays Payment Pending; retry allowed until expiry; N14 sent. |
| Refund needed (cancellation of confirmed booking) | payment → refund_required → admin manual refund → refunded; booking cancelled; audit-logged. |
| Gateway downtime | Organizer sees friendly error; booking remains Payment Pending; can retry. |

### 34.3 Event OTP / execution

| Edge case | Defined behavior |
|---|---|
| Creator enters wrong OTP repeatedly | Attempts capped (`otp_max_attempts`); lockout → contact support; admin can regenerate. |
| OTP expired (event window passed, never started) | OTP → Expired; admin can regenerate for genuine cases; otherwise dispute flow. |
| Organizer never marks completed after event | Admin can complete on evidence (override, audited). Reminder N16. Consider auto-complete after N days post-event as a future rule (Open Question §35). |
| Creator no-show (never enters OTP) | Booking stays Confirmed; organizer contacts support; admin cancels + refunds; creator reliability noted. |
| OTP requested by wrong party | Only organizer can read OTP; only creator can submit it (§24.7). |

### 34.4 Profiles & verification

| Edge case | Defined behavior |
|---|---|
| Profile edited while pending review | Allowed; stays pending; admin reviews latest. |
| Approved profile edited (material field) | Reverts to Pending Review (BR-7); existing bookings unaffected. |
| Aadhaar unreadable | Admin rejects with reason "identity not legible"; creator re-uploads. |
| Duplicate person, multiple accounts | One phone = one account (BR-1); same Aadhaar across accounts flagged for admin review (Open Question §35). |
| Service deleted after a booking exists | Booking keeps snapshot (BR-11); no impact. |

### 34.5 Auth & account

| Edge case | Defined behavior |
|---|---|
| Same phone, new device | Login works (single account); new device token registered; old sessions optionally revoked. |
| Lost phone number / number reassigned | Support flow (out of automated MVP scope; admin-assisted). |
| Rapid OTP requests | Rate-limited per phone (FR-AUTH-8). |
| Token expired mid-action | Silent refresh; if refresh fails, re-auth; action retried by user. |

### 34.6 Search

| Edge case | Defined behavior |
|---|---|
| No results in radius | Suggest widening radius / changing category (FR-SEARCH-6). |
| Organizer location unavailable | Prompt for location; cannot radius-search without origin. |
| Creator in different city than organizer's radius | Simply not returned; expected. |

### 34.7 Money integrity invariants (must always hold)

- Sum of a completed booking's `payout.gross + (organizer-paid − gross)` reconciles;
  `gross = price_paise_snap`, `commission = round(gross × pct)`, `net = gross −
  commission`.
- No payout exists without a Completed booking.
- No Confirmed booking without a verified Paid payment.
- No contact revealed without Confirmed status.

---

## 35. Open Questions

**Vision.** A living list of decisions we've consciously deferred. Each has an owner
and a recommended default so the MVP is never blocked. AI agents should implement the
**recommended default** unless told otherwise.

| # | Question | Recommended default (MVP) | Owner |
|---|---|---|---|
| OQ-1 | Delete Aadhaar images after verification? | **Yes** — after approval, delete images; retain verified flag + hash + timestamp. Minimizes PII risk. | Security/Legal |
| OQ-2 | Auto-complete bookings N days after event if organizer inactive? | Defer; add reminder now, revisit auto-complete (e.g., +3 days) once dispute patterns are known. | Product |
| OQ-3 | Same Aadhaar across multiple accounts — block or flag? | **Flag for admin review**, don't hard-block (edge: shared household). | Trust & Safety |
| OQ-4 | Commission rate & whether to pass gateway fee | Start 15%, absorb gateway fee; revisit at volume. | Founders/Finance |
| OQ-5 | Booking accept & payment windows | 24h each; tune from data. | Product |
| OQ-6 | Multiple bookings same date — warn creator? | Show gentle hint; do not block (BR-16). | Product |
| OQ-7 | Cancellation/refund policy specifics (who bears fee, cutoffs) | Full manual refund of service price on admin cancel; RCube absorbs gateway fee in MVP. Formalize policy pre-scale. | Founders/Legal |
| OQ-8 | Launch city & seed categories | One dense city; music + photography + mehendi first. | Founders/GTM |
| OQ-9 | Do organizers need any verification? | Not in MVP (payment is the trust signal); revisit if abuse appears. | Trust & Safety |
| OQ-10 | Event OTP length (4 vs 6) | 4 digits (easier to read aloud); configurable. | Product |
| OQ-11 | Handling creators across multiple cities | City is per-profile; a creator can create profiles per city if needed. Revisit multi-city per profile later. | Product |
| OQ-12 | Notification preferences granularity | On/off master toggle in MVP; per-category later. | Product |

---
---

# APPENDICES

---

## Appendix A — Glossary

| Term | Meaning |
|---|---|
| **Creator** | A hobbyist/creator offering a craft on RCube. Never "provider/seller/vendor." |
| **Organizer / Host** | A person booking a creator. **Never "consumer."** |
| **Admin** | Internal RCube ops team member. |
| **Profile** | One creator identity; a user may have several. |
| **Service** | A fixed-price offering under a profile. |
| **Booking** | A request for a service on a date. |
| **Event OTP** | Code that starts a booked event (≠ login OTP). |
| **Recognition** | Appreciation by strangers; RCube's core promise. |
| **Recognized booking** | A completed booking by an organizer new to that creator (north-star). |
| **Recognition Ledger** | The append-only `domain_event` stream of recognition-relevant events. |
| **Escrow-style hold** | RCube holds the organizer's payment until completion. |
| **Paise** | 1/100 of a rupee; the unit all money is stored in. |
| **Mode** | Creator or Organizer view within one account. |

---

## Appendix B — Enumerations & Constants

**Creator categories** (`creator_profile.category`):
```
singer, guitarist, flautist, musician, dancer, photographer,
mehendi_artist, storyteller, painter, magician, calligrapher, other
```

**Event types** (`booking.event_type`):
```
birthday, wedding, house_party, corporate_event, cafe_performance,
college_fest, religious_event, community_event, other
```

**Booking statuses:**
```
pending, accepted, payment_pending, confirmed, in_progress, completed,
declined, expired, payment_expired, cancelled
```

**Profile statuses:** `draft, pending_review, approved, rejected, suspended`
**Payment statuses:** `created, pending, paid, failed, expired, refund_required, refunded`
**Payout statuses:** `pending_transfer, transfer_initiated, transferred, failed`
**Event OTP statuses:** `not_generated, generated, verified, expired`
**Admin roles:** `superadmin, ops, finance, support, readonly`
**Modes:** `creator, organizer`

**System config defaults:**
```
platform_commission_pct      = 15
booking_accept_window_hours  = 24
payment_window_hours         = 24
otp_length                   = 4
otp_max_attempts             = 5
default_search_radius_km     = 10
currency                     = INR
```

---

## Appendix C — Notification Copy Deck

Warm, dignified, recognition-first. `{name}`, `{service}`, `{amount}`, `{date}` are
placeholders.

| Code | Title | Body |
|---|---|---|
| N1 | New booking request 🎵 | "{name} wants to book your {service} on {date}. Take a look." |
| N2 | Accepted — pay to confirm | "Great news — your request was accepted! Pay to confirm and get {name}'s contact." |
| N3 | Request not accepted | "This request wasn't accepted. Plenty more talent nearby — keep exploring." |
| N4 | Your request expired | "Your request expired. Send a fresh one whenever you're ready." |
| N5 | It's confirmed! 🎉 | "You're all set for {date}. Contact details are now shared — coordinate the timing together." |
| N6 | Payment window closed | "The payment window closed, so this booking didn't go through." |
| N7 | Performance started | "The event has started. Enjoy!" |
| N8 | Completed — earning on the way | "Nicely done! ₹{amount} is on its way to you for {service}." |
| N9 | You're verified! ✓ | "Your {category} profile is live. Time to get recognized." |
| N10 | Profile needs a few changes | "Almost there — a couple of tweaks and you'll be live. Tap to see what to update." |
| N11 | Profile update | "There's an update to your profile status. Tap for details." |
| N12 | You've been paid ✅ | "₹{amount} has been transferred to you for {service}. Well earned." |
| N13 | Booking cancelled | "This booking was cancelled. Tap for details." |
| N14 | Payment didn't go through | "Your payment didn't complete — you weren't charged. Try again." |
| N15 | Complete payment to confirm | "{name} is holding your date. Complete payment to lock it in." |
| N16 | Your event is tomorrow | "Reminder: your {service} event is on {date}. All set?" |
| N17 | A request is waiting | "Someone would love to book you. A request is waiting for your reply." |

**Copy rules:** never say "earn money" as a lead to creators; never reveal a phone
number in copy before Confirmed; never use the word "consumer"; failures are framed
warmly and privately.

---

## Appendix D — Environment & Configuration

**Backend environment variables:**
```
# App
APP_ENV=production|staging|local
API_BASE_URL=https://api.rcube.app
JWT_ACCESS_SECRET=***            # Secrets Manager
JWT_REFRESH_SECRET=***
ACCESS_TOKEN_TTL_MIN=15
REFRESH_TOKEN_TTL_DAYS=30

# Database / cache
DATABASE_URL=postgresql+asyncpg://...
REDIS_URL=redis://...

# Firebase (auth + FCM)
FIREBASE_PROJECT_ID=...
FIREBASE_CREDENTIALS_JSON=***     # service account (Secrets Manager)

# Razorpay
RAZORPAY_KEY_ID=***
RAZORPAY_KEY_SECRET=***
RAZORPAY_WEBHOOK_SECRET=***

# AWS / S3
AWS_REGION=ap-south-1
S3_MEDIA_BUCKET=rcube-media
S3_AADHAAR_BUCKET=rcube-aadhaar-private   # SSE-KMS, private
S3_KMS_KEY_ID=***

# Observability
SENTRY_DSN=...
LOG_LEVEL=INFO
```

**Mobile config:** `API_BASE_URL`, Firebase config files
(`google-services.json` / `GoogleService-Info.plist`), Razorpay `key_id` (fetched
from backend, not hardcoded for secret parts).

**Admin config:** `ADMIN_API_BASE_URL`, admin auth settings, TOTP issuer.

**Infrastructure (AWS):** ALB + WAF → ECS/EKS (or EC2) FastAPI service; RDS
PostgreSQL (Multi-AZ in prod); ElastiCache Redis; S3 (+ CloudFront for media);
Secrets Manager; CloudWatch; region `ap-south-1` (Mumbai).

---

## Appendix E — Definition of Done (MVP)

The MVP is **done** when all `P0` requirements pass and the following end-to-end
scenario works in production for real users:

**The Golden Path (must pass):**
1. A new user signs up with phone + OTP. ✅
2. Switches to Creator Mode, creates a profile (photos, category, bio, city, Aadhaar),
   adds a service, submits for review. ✅
3. Admin approves the profile within SLA; creator is notified. ✅
4. A **different** user (Organizer) searches by event type + category + radius, finds
   the creator, opens the profile. ✅
5. Organizer selects a service, submits a booking request (date only). ✅
6. Creator receives the request, accepts. Organizer is prompted to pay. ✅
7. Organizer pays via Razorpay; booking becomes Confirmed; **both parties now see each
   other's phone number** (and not a moment before). ✅
8. On event day, organizer shows the Event OTP; creator enters it → In Progress. ✅
9. Organizer marks the event Completed → creator earning becomes Pending Transfer. ✅
10. Admin processes the payout → Transferred; creator is notified they've been paid. ✅

**Non-functional gates:**
- All money in paise; commission computed correctly; escrow held until completion.
- Contact never revealed before Confirmed (unit-tested).
- Every state transition emits a domain event (ledger) and audit entry where relevant.
- Push + in-app notifications fire per the matrix (§20).
- Aadhaar stored privately/encrypted; never exposed to consumers.
- Idempotent payments/webhooks; no double-confirm, no double-payout.

**Success criteria (from the mission):**
- Verified creators onboarded.
- Booking requests raised by strangers.
- Bookings completed.
- Creators earning money.
- **Creators receiving recognition** — the whole point.

---
---

> ## Closing note — The Constitution of RCube
>
> This document is not a feature list; it is a constitution. Features will come and go,
> the tech stack may evolve, the UI will be redesigned many times. But the one thing
> that must never change is the first line of this bible:
>
> **Recognition before Monetization.**
>
> Somewhere right now, a person is quietly practicing something beautiful, seen only by
> the few who already know them. RCube exists so that one day, a room full of strangers
> can applaud them too.
>
> Build for that room. Build for that applause.
>
> — *The RCube Founding Team*

*End of RCube Bible v2.0.*
