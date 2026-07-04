/**
 * RCube MVP backend — Google Apps Script + Google Sheets.
 *
 * The whole API is a single Web App endpoint. Requests are RPC-style:
 *   POST { "action": "...", "apiKey": "...", "token": "...", "data": { ... } }
 * Response envelope:
 *   { "ok": true, "data": <result> }   or   { "ok": false, "error": "message" }
 *
 * SETUP (see BACKEND_SETUP.md for the full walkthrough):
 *   1. Create a Google Sheet, then Extensions > Apps Script (this makes the script
 *      "container-bound" so getActiveSpreadsheet() works).
 *   2. Paste this file, set API_KEY below to your own random string.
 *   3. Run the `setup` function once (creates tabs + seeds demo creators).
 *   4. Deploy > New deployment > Web app > Execute as: Me, Who has access: Anyone.
 *   5. Copy the /exec URL + API_KEY into android/local.properties.
 */

// ------------------------------------------------------------------ CONFIG ----

var API_KEY = 'REPLACE_WITH_YOUR_RCUBE_API_KEY'; // must match RCUBE_API_KEY in the app
var DEV_MODE = true;            // MVP: accept any 4-6 digit OTP (no SMS provider yet)
var COMMISSION_PCT = 15;        // platform commission (Bible BR-20)
var ACCEPT_WINDOW_HOURS = 24;   // pending request expiry
var PAYMENT_WINDOW_HOURS = 24;  // accepted -> pay expiry
var SPREADSHEET_ID = '';        // only needed if the script is NOT bound to the sheet

// -------------------------------------------------------------- DATA MODEL ----

var SHEETS = {
  users: ['id', 'phone', 'displayName', 'defaultMode', 'createdAt'],
  sessions: ['token', 'userId', 'createdAt'],
  creator_profiles: ['id', 'userId', 'displayName', 'category', 'bio', 'city',
    'languages', 'status', 'instagram', 'youtube', 'completedBookings',
    'rejectionReason', 'distanceKm', 'createdAt'],
  services: ['id', 'profileId', 'title', 'pricePaise', 'durationMinutes',
    'description', 'isActive', 'createdAt'],
  bookings: ['id', 'organizerUserId', 'organizerName', 'organizerPhone',
    'creatorProfileId', 'creatorUserId', 'creatorName', 'creatorCategory',
    'creatorPhone', 'serviceId', 'serviceTitle', 'pricePaise', 'eventDate',
    'eventType', 'venue', 'notes', 'status', 'acceptExpiresAt',
    'paymentExpiresAt', 'eventOtp', 'payoutStatus', 'commissionPct', 'createdAt'],
  notifications: ['id', 'userId', 'type', 'title', 'body', 'read', 'createdAt']
};

var MUTATIONS = {
  authVerify: true, createProfile: true, addService: true, submitProfile: true,
  createBooking: true, acceptRequest: true, declineRequest: true,
  startEventWithOtp: true, payAndConfirm: true, completeBooking: true,
  cancelBooking: true, markNotificationsRead: true
};

// ---------------------------------------------------------------- ROUTING ----

function doGet(e) { return handle_(e); }
function doPost(e) { return handle_(e); }

function handle_(e) {
  var lock = null;
  try {
    var body = {};
    if (e && e.postData && e.postData.contents) {
      body = JSON.parse(e.postData.contents);
    }
    var params = (e && e.parameter) ? e.parameter : {};
    var action = body.action || params.action || 'ping';
    var apiKey = body.apiKey || params.apiKey || '';
    var token = body.token || params.token || '';
    var data = body.data || {};

    if (action !== 'ping' && apiKey !== API_KEY) {
      return json_({ ok: false, error: 'unauthorized' });
    }

    // Serialize writes to avoid concurrent-write corruption in Sheets.
    if (MUTATIONS[action]) {
      lock = LockService.getScriptLock();
      lock.waitLock(30000);
    }

    var result = route_(action, token, data);
    return json_({ ok: true, data: result });
  } catch (err) {
    return json_({ ok: false, error: (err && err.message) ? err.message : String(err) });
  } finally {
    if (lock) lock.releaseLock();
  }
}

function route_(action, token, data) {
  switch (action) {
    case 'ping': return { pong: true, time: Date.now() };
    case 'authVerify': return authVerify_(data);
    case 'getState': return getState_(requireUser_(token));
    case 'searchCreators': return searchCreators_(data);
    case 'getCreatorPublic': return getCreatorPublic_(data);
    case 'createProfile': return createProfile_(requireUser_(token), data);
    case 'addService': return addService_(requireUser_(token), data);
    case 'submitProfile': return submitProfile_(requireUser_(token), data);
    case 'createBooking': return createBooking_(requireUser_(token), data);
    case 'acceptRequest': return setCreatorBookingStatus_(requireUser_(token), data.bookingId, 'accept');
    case 'declineRequest': return setCreatorBookingStatus_(requireUser_(token), data.bookingId, 'decline');
    case 'startEventWithOtp': return startEventWithOtp_(requireUser_(token), data);
    case 'payAndConfirm': return payAndConfirm_(requireUser_(token), data);
    case 'completeBooking': return completeBooking_(requireUser_(token), data);
    case 'cancelBooking': return cancelBooking_(requireUser_(token), data);
    case 'markNotificationsRead': return markNotificationsRead_(requireUser_(token));
    default: throw new Error('unknown action: ' + action);
  }
}

// ------------------------------------------------------------------- AUTH ----

function authVerify_(data) {
  var phone = String(data.phone || '').trim();
  var otp = String(data.otp || '').trim();
  if (!phone) throw new Error('phone required');
  if (!DEV_MODE && otp.length < 4) throw new Error('invalid otp');

  var user = findRow_('users', 'phone', phone);
  if (!user) {
    var last4 = phone.replace(/\D/g, '').slice(-4);
    user = {
      id: 'u_' + shortId_(),
      phone: phone,
      displayName: 'User ' + last4,
      defaultMode: 'creator',
      createdAt: Date.now()
    };
    appendObj_('users', user);
  }
  var token = 'tok_' + Utilities.getUuid().replace(/-/g, '');
  appendObj_('sessions', { token: token, userId: user.id, createdAt: Date.now() });

  return {
    token: token,
    user: {
      id: user.id, phone: user.phone,
      displayName: user.displayName, defaultMode: user.defaultMode || 'creator'
    }
  };
}

function requireUser_(token) {
  if (!token) throw new Error('unauthorized');
  var session = findRow_('sessions', 'token', token);
  if (!session) throw new Error('unauthorized');
  var user = findRow_('users', 'id', session.userId);
  if (!user) throw new Error('unauthorized');
  return user;
}

// ------------------------------------------------------------------ STATE ----

function getState_(user) {
  var profiles = rows_('creator_profiles').filter(function (p) { return p.userId === user.id; });
  var services = rows_('services');
  var bookings = rows_('bookings');
  var notifs = rows_('notifications').filter(function (n) { return n.userId === user.id; });

  return {
    myProfiles: profiles.map(function (p) { return profileDto_(p, services); }),
    creatorBookings: bookings
      .filter(function (b) { return b.creatorUserId === user.id; })
      .map(function (b) { return bookingDto_(b, 'creator'); }),
    organizerBookings: bookings
      .filter(function (b) { return b.organizerUserId === user.id; })
      .map(function (b) { return bookingDto_(b, 'organizer'); }),
    notifications: notifs.map(notificationDto_)
  };
}

// -------------------------------------------------------------- DISCOVERY ----

function searchCreators_(data) {
  var category = data.category || null;
  var radius = Number(data.radiusKm || 9999);
  var services = rows_('services');
  var list = rows_('creator_profiles').filter(function (p) {
    if (p.status !== 'APPROVED') return false;
    if (category && p.category !== category) return false;
    var d = p.distanceKm === '' || p.distanceKm === null ? null : Number(p.distanceKm);
    if (d !== null && d > radius) return false;
    return true;
  });
  list.sort(function (a, b) {
    return (Number(a.distanceKm) || 9999) - (Number(b.distanceKm) || 9999);
  });
  return list.map(function (p) { return profileDto_(p, services); });
}

function getCreatorPublic_(data) {
  var p = findRow_('creator_profiles', 'id', data.profileId);
  if (!p) throw new Error('profile not found');
  return profileDto_(p, rows_('services'));
}

// ------------------------------------------------------ PROFILES & SERVICES ----

function createProfile_(user, data) {
  var id = 'p_' + shortId_();
  appendObj_('creator_profiles', {
    id: id, userId: user.id, displayName: user.displayName,
    category: data.category, bio: data.bio || '', city: data.city || '',
    languages: (data.languages || []).join(','), status: 'DRAFT',
    instagram: data.instagram || '', youtube: data.youtube || '',
    completedBookings: 0, rejectionReason: '', distanceKm: '', createdAt: Date.now()
  });
  return profileDto_(findRow_('creator_profiles', 'id', id), rows_('services'));
}

function addService_(user, data) {
  var profile = ownedProfile_(user, data.profileId);
  var id = 's_' + shortId_();
  appendObj_('services', {
    id: id, profileId: profile.id, title: data.title,
    pricePaise: Number(data.pricePaise), durationMinutes: data.durationMinutes || '',
    description: data.description || '', isActive: true, createdAt: Date.now()
  });
  return profileDto_(findRow_('creator_profiles', 'id', profile.id), rows_('services'));
}

function submitProfile_(user, data) {
  var profile = ownedProfile_(user, data.profileId);
  updateRow_('creator_profiles', profile._row, { status: 'PENDING_REVIEW' });
  return profileDto_(findRow_('creator_profiles', 'id', profile.id), rows_('services'));
}

// ---------------------------------------------------------------- BOOKINGS ----

function createBooking_(user, data) {
  var profile = findRow_('creator_profiles', 'id', data.creatorProfileId);
  if (!profile) throw new Error('creator not found');
  var service = findRow_('services', 'id', data.serviceId);
  if (!service) throw new Error('service not found');
  var creatorUser = findRow_('users', 'id', profile.userId);

  var id = 'ob_' + shortId_();
  appendObj_('bookings', {
    id: id,
    organizerUserId: user.id, organizerName: user.displayName, organizerPhone: user.phone,
    creatorProfileId: profile.id, creatorUserId: profile.userId,
    creatorName: profile.displayName, creatorCategory: profile.category,
    creatorPhone: creatorUser ? creatorUser.phone : '',
    serviceId: service.id, serviceTitle: service.title, pricePaise: Number(service.pricePaise),
    eventDate: data.eventDate, eventType: data.eventType, venue: data.venue || '',
    notes: data.notes || '', status: 'PENDING',
    acceptExpiresAt: Date.now() + ACCEPT_WINDOW_HOURS * 3600000,
    paymentExpiresAt: '', eventOtp: '', payoutStatus: '',
    commissionPct: COMMISSION_PCT, createdAt: Date.now()
  });
  addNotification_(profile.userId, 'BOOKING', 'New booking request',
    user.displayName + ' wants to book your ' + service.title + '.');
  return bookingDto_(findRow_('bookings', 'id', id), 'organizer');
}

function setCreatorBookingStatus_(user, bookingId, kind) {
  var b = findRow_('bookings', 'id', bookingId);
  if (!b) throw new Error('booking not found');
  if (b.creatorUserId !== user.id) throw new Error('forbidden');
  if (b.status !== 'PENDING') throw new Error('booking not pending');

  if (kind === 'accept') {
    updateRow_('bookings', b._row, {
      status: 'PAYMENT_PENDING',
      paymentExpiresAt: Date.now() + PAYMENT_WINDOW_HOURS * 3600000
    });
    addNotification_(b.organizerUserId, 'PAYMENT', 'Accepted — pay to confirm',
      b.creatorName + ' accepted your ' + b.serviceTitle + ' request.');
  } else {
    updateRow_('bookings', b._row, { status: 'DECLINED' });
  }
  return bookingDto_(findRow_('bookings', 'id', bookingId), 'creator');
}

function payAndConfirm_(user, data) {
  var b = findRow_('bookings', 'id', data.bookingId);
  if (!b) throw new Error('booking not found');
  if (b.organizerUserId !== user.id) throw new Error('forbidden');
  if (b.status !== 'PAYMENT_PENDING') throw new Error('not payable');

  var otp = String(Math.floor(1000 + Math.random() * 9000));
  updateRow_('bookings', b._row, { status: 'CONFIRMED', eventOtp: otp });
  addNotification_(b.creatorUserId, 'BOOKING', "It's confirmed!",
    'Your ' + b.serviceTitle + ' on ' + b.eventDate + ' is confirmed. Contact shared.');
  return bookingDto_(findRow_('bookings', 'id', data.bookingId), 'organizer');
}

function startEventWithOtp_(user, data) {
  var b = findRow_('bookings', 'id', data.bookingId);
  if (!b) throw new Error('booking not found');
  if (b.creatorUserId !== user.id) throw new Error('forbidden');
  if (b.status !== 'CONFIRMED') throw new Error('not confirmed');
  if (String(b.eventOtp) !== String(data.otp).trim()) throw new Error('OTP_INVALID');

  updateRow_('bookings', b._row, { status: 'IN_PROGRESS' });
  return bookingDto_(findRow_('bookings', 'id', data.bookingId), 'creator');
}

function completeBooking_(user, data) {
  var b = findRow_('bookings', 'id', data.bookingId);
  if (!b) throw new Error('booking not found');
  if (b.organizerUserId !== user.id) throw new Error('forbidden');
  if (b.status !== 'IN_PROGRESS' && b.status !== 'CONFIRMED') throw new Error('not completable');

  updateRow_('bookings', b._row, { status: 'COMPLETED', payoutStatus: 'PENDING_TRANSFER' });
  bumpCompletedCount_(b.creatorProfileId);
  addNotification_(b.creatorUserId, 'PAYOUT', 'Completed — earning on the way',
    'Your ' + b.serviceTitle + ' is complete. Payout is pending transfer.');
  return bookingDto_(findRow_('bookings', 'id', data.bookingId), 'organizer');
}

function cancelBooking_(user, data) {
  var b = findRow_('bookings', 'id', data.bookingId);
  if (!b) throw new Error('booking not found');
  if (b.organizerUserId !== user.id) throw new Error('forbidden');
  updateRow_('bookings', b._row, { status: 'CANCELLED' });
  return bookingDto_(findRow_('bookings', 'id', data.bookingId), 'organizer');
}

// ----------------------------------------------------------- NOTIFICATIONS ----

function markNotificationsRead_(user) {
  var sh = sheet_('notifications');
  var all = rows_('notifications');
  all.forEach(function (n) {
    if (n.userId === user.id && n.read !== true) {
      updateRow_('notifications', n._row, { read: true });
    }
  });
  return { ok: true };
}

function addNotification_(userId, type, title, body) {
  appendObj_('notifications', {
    id: 'n_' + shortId_(), userId: userId, type: type,
    title: title, body: body, read: false, createdAt: Date.now()
  });
}

// ------------------------------------------------------------------- DTOS ----

function profileDto_(p, services) {
  var svc = services.filter(function (s) {
    return s.profileId === p.id && s.isActive !== false;
  }).map(serviceDto_);
  return {
    id: p.id, ownerUserId: p.userId, displayName: p.displayName,
    category: p.category, bio: p.bio || '', city: p.city || '',
    languages: p.languages ? String(p.languages).split(',').map(trim_).filter(nonEmpty_) : [],
    status: p.status, services: svc,
    instagram: p.instagram || null, youtube: p.youtube || null,
    rejectionReason: p.rejectionReason || null,
    distanceKm: (p.distanceKm === '' || p.distanceKm === null) ? null : Number(p.distanceKm),
    completedBookings: Number(p.completedBookings) || 0
  };
}

function serviceDto_(s) {
  return {
    id: s.id, title: s.title, pricePaise: Number(s.pricePaise),
    durationMinutes: (s.durationMinutes === '' || s.durationMinutes === null) ? null : Number(s.durationMinutes),
    description: s.description || null
  };
}

function bookingDto_(b, perspective) {
  var revealed = (b.status === 'CONFIRMED' || b.status === 'IN_PROGRESS' || b.status === 'COMPLETED');
  var counterpartyPhone = null;
  if (revealed) counterpartyPhone = (perspective === 'creator') ? b.organizerPhone : b.creatorPhone;
  var eventOtp = (perspective === 'organizer' && b.eventOtp) ? String(b.eventOtp) : null;
  return {
    id: b.id, creatorProfileId: b.creatorProfileId, creatorName: b.creatorName,
    creatorCategory: b.creatorCategory, organizerName: b.organizerName,
    serviceTitle: b.serviceTitle, pricePaise: Number(b.pricePaise),
    eventDate: dateStr_(b.eventDate), eventType: b.eventType,
    venue: b.venue || '', notes: b.notes || '', status: b.status,
    createdAt: Number(b.createdAt) || Date.now(),
    acceptExpiresAt: b.acceptExpiresAt ? Number(b.acceptExpiresAt) : null,
    paymentExpiresAt: b.paymentExpiresAt ? Number(b.paymentExpiresAt) : null,
    eventOtp: eventOtp, counterpartyPhone: counterpartyPhone || null,
    payoutStatus: b.payoutStatus || null,
    commissionPct: Number(b.commissionPct) || COMMISSION_PCT
  };
}

function notificationDto_(n) {
  return {
    id: n.id, type: n.type, title: n.title, body: n.body,
    read: n.read === true, createdAt: Number(n.createdAt) || Date.now()
  };
}

// ---------------------------------------------------------- SHEET HELPERS ----

function ss_() {
  var ss = SpreadsheetApp.getActiveSpreadsheet();
  if (ss) return ss;
  if (SPREADSHEET_ID) return SpreadsheetApp.openById(SPREADSHEET_ID);
  throw new Error('No spreadsheet bound. Create the script from the Sheet (Extensions > Apps Script) or set SPREADSHEET_ID.');
}

function sheet_(name) {
  var ss = ss_();
  var sh = ss.getSheetByName(name);
  if (!sh) {
    sh = ss.insertSheet(name);
    sh.appendRow(SHEETS[name]);
  }
  return sh;
}

function rows_(name) {
  var sh = sheet_(name);
  var values = sh.getDataRange().getValues();
  if (values.length < 2) return [];
  var headers = values[0];
  var out = [];
  for (var i = 1; i < values.length; i++) {
    var obj = {};
    for (var j = 0; j < headers.length; j++) obj[headers[j]] = values[i][j];
    obj._row = i + 1;
    out.push(obj);
  }
  return out;
}

function findRow_(name, field, value) {
  var all = rows_(name);
  for (var i = 0; i < all.length; i++) {
    if (String(all[i][field]) === String(value)) return all[i];
  }
  return null;
}

function appendObj_(name, obj) {
  var sh = sheet_(name);
  var headers = SHEETS[name];
  var row = headers.map(function (h) {
    return (obj[h] === undefined || obj[h] === null) ? '' : obj[h];
  });
  sh.appendRow(row);
}

function updateRow_(name, rowIndex, patch) {
  var sh = sheet_(name);
  var headers = SHEETS[name];
  for (var j = 0; j < headers.length; j++) {
    if (patch.hasOwnProperty(headers[j])) {
      var v = patch[headers[j]];
      sh.getRange(rowIndex, j + 1).setValue(v === null ? '' : v);
    }
  }
}

function ownedProfile_(user, profileId) {
  var p = findRow_('creator_profiles', 'id', profileId);
  if (!p) throw new Error('profile not found');
  if (p.userId !== user.id) throw new Error('forbidden');
  return p;
}

function bumpCompletedCount_(profileId) {
  var p = findRow_('creator_profiles', 'id', profileId);
  if (p) updateRow_('creator_profiles', p._row, { completedBookings: (Number(p.completedBookings) || 0) + 1 });
}

// ----------------------------------------------------------------- UTILS ----

function json_(obj) {
  return ContentService.createTextOutput(JSON.stringify(obj))
    .setMimeType(ContentService.MimeType.JSON);
}
function shortId_() { return Utilities.getUuid().replace(/-/g, '').slice(0, 8); }
function trim_(s) { return String(s).trim(); }
function nonEmpty_(s) { return s && s.length > 0; }
function dateStr_(v) {
  if (v instanceof Date) {
    return Utilities.formatDate(v, Session.getScriptTimeZone() || 'Asia/Kolkata', 'yyyy-MM-dd');
  }
  return String(v);
}

// ------------------------------------------------------ SETUP / SEED (once) ----

/** Run this ONCE from the Apps Script editor after pasting the code. */
function setup() {
  ensureSheets_();
  seedDirectory_();
  return 'RCube backend ready. Tabs created and demo creators seeded.';
}

function ensureSheets_() {
  Object.keys(SHEETS).forEach(function (name) { sheet_(name); });
}

function seedDirectory_() {
  if (rows_('creator_profiles').length > 0) return; // idempotent

  var seed = [
    { name: 'Meera', cat: 'GUITARIST', dist: 4.2, bio: 'Acoustic guitarist & vocalist. Soulful covers for warm evenings.', ig: '@meera.strings', langs: 'English,Kannada', done: 27, svc: [['30 Minute Live Performance', 250000, 30, ''], ['60 Minute Live Performance', 400000, 60, 'Covers + requests.']] },
    { name: 'Sam', cat: 'GUITARIST', dist: 8.9, bio: 'Rock & blues guitarist. High energy sets for parties and fests.', ig: '@sam.plays', langs: 'English,Hindi', done: 15, svc: [['45 Minute Set', 300000, 45, ''], ['90 Minute Set', 550000, 90, '']] },
    { name: 'Priya', cat: 'PHOTOGRAPHER', dist: 3.1, bio: 'Candid & portrait photographer. I chase real moments, not poses.', ig: '@priya.frames', langs: 'English', done: 41, svc: [['Birthday Photoshoot', 500000, 120, '2 hours, edited gallery.'], ['Wedding Photography', 1800000, '', 'Full day coverage.']] },
    { name: 'Sneha', cat: 'MEHENDI_ARTIST', dist: 6.4, bio: 'Bridal & party mehendi. Intricate, long-lasting designs.', ig: '@sneha.mehendi', langs: 'Hindi,English', done: 33, svc: [['Party Mehendi', 250000, '', 'Up to 5 guests.'], ['Bridal Mehendi', 700000, '', 'Full bridal, both hands & feet.']] },
    { name: 'Kabir', cat: 'SINGER', dist: 11.2, bio: 'Playback-style vocalist. Bollywood, ghazals and unplugged.', ig: '', langs: 'Hindi,Urdu,English', done: 19, svc: [['Unplugged Evening', 450000, 60, '']] },
    { name: 'Rhea', cat: 'DANCER', dist: 5.5, bio: 'Contemporary & semi-classical performances for stage events.', ig: '@rhea.moves', langs: 'English,Tamil', done: 22, svc: [['Solo Performance', 400000, 15, 'One choreographed piece.'], ['Event Set', 700000, 40, 'Three pieces.']] }
  ];

  seed.forEach(function (c, idx) {
    var uid = 'seed_u_' + idx;
    appendObj_('users', {
      id: uid, phone: '+91 900000000' + idx, displayName: c.name,
      defaultMode: 'creator', createdAt: Date.now()
    });
    var pid = 'seed_p_' + idx;
    appendObj_('creator_profiles', {
      id: pid, userId: uid, displayName: c.name, category: c.cat, bio: c.bio,
      city: 'Bengaluru', languages: c.langs, status: 'APPROVED',
      instagram: c.ig, youtube: '', completedBookings: c.done,
      rejectionReason: '', distanceKm: c.dist, createdAt: Date.now()
    });
    c.svc.forEach(function (s, si) {
      appendObj_('services', {
        id: 'seed_s_' + idx + '_' + si, profileId: pid, title: s[0],
        pricePaise: s[1], durationMinutes: s[2], description: s[3],
        isActive: true, createdAt: Date.now()
      });
    });
  });
}
